package com.iwanol.paypal.service;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.AccountDetails;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.repository.AccountDetailsRepository;
import com.iwanol.paypal.repository.BankRepository;
import com.iwanol.paypal.repository.MerchantRepository;
import com.iwanol.paypal.repository.SettleMentRepository;
import com.iwanol.paypal.until.CommonUtil;
import com.iwanol.paypal.until.PageData;
import com.iwanol.paypal.vo.BankVo;
import com.iwanol.paypal.vo.FundsVo;

@Service
public class BankService extends BaseServiceImpl<Bank,BankVo> {
	
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private SettleMentService settleMentService;
	@Autowired
	private SettleMentRepository settleMentRepository;
	@Autowired
	private AccountDetailsRepository accountDetailsRepository;
	@Autowired
	private JdbcTemplate jdbcTemplate;
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public BaseRepository<Bank, Long> getRepository() {
		return bankRepository;
	}
	
	@SuppressWarnings("serial")
	@Override
	public Specification<Bank> getSpecification(BankVo vo) {
		return new Specification<Bank>() {

			@Override
			public Predicate toPredicate(Root<Bank> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				// 根据商户查询
				Long merchantId = vo.getMerchantId();
				if (merchantId != null) {
					list.add(cb.equal(root.get("merchant").as(Merchant.class), new Merchant(merchantId)));
				}
				// 根据商户结算类型查询
				Integer settlementType = vo.getSettlementType();
				if (settlementType != null) {
					list.add(cb.equal(root.get("merchant").get("settlementType").as(Integer.class), settlementType));
				}
				Predicate[] predicates = new Predicate[list.size()];
				predicates = list.toArray(predicates);
				return cb.and(predicates);
			}
			
		};
	}
	
	@Override
	public Map<String, Object> findEntityBySpec(PageData pageData, BankVo v) {
		Page<Bank> page = getRepository().findAll(getSpecification(v), pageData.initPageable());
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("draw", pageData.getDraw());
		map.put("recordsTotal", page.getTotalElements());//数据总条数
		map.put("recordsFiltered", page.getTotalElements());//显示的条数
		map.put("data", page.getContent());//数据集合
		return map;
	}

	@Override
	public JSONObject saveEntity(Bank t) {
		t.validate();
		if (!ReturnMsgEnum.isSucc)
			return ReturnMsgEnum.returnMsg();
		
		Optional<Merchant> optional = merchantRepository.findById(t.getMerId());
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("当前商户不存在！").toJson();
		
		bankRepository.save(t);
		return ReturnMsgEnum.success.setMsg("添加成功！").toJson();
	}

	public Optional<Bank> findBankByMerchantIdAndBankType(Long merchantId, int type) {
		return bankRepository.findByMerchantIdAndBankType(merchantId,type);
	}
	
	/**
	 * 商户结算
	 * @param banks
	 * @param settlementType
	 * @return
	 */
	@Transactional
	public JSONObject findMerchantSettlementCounts(List<BankVo> banks, Integer settlementType) {
		if (banks != null && banks.size() > 0) {
			List<SettleMent> list = new ArrayList<SettleMent>();
			List<AccountDetails> detailsList = new ArrayList<AccountDetails>();
			StringBuffer when_buf = new StringBuffer();
			StringBuffer where_buf = new StringBuffer();
			for (int i = 0; i < banks.size(); i++) {
				BankVo vo = banks.get(i);
				if (vo.settlementCheck()) {
					vo.setSettlementType(settlementType);
					Bank entity = vo.findByIdAndOverMoney();
					if (entity != null) {
						// 账户余额
						BigDecimal overMoney = entity.getOverMoney();
						// 可结算金额
						BigDecimal settleMoney = entity.getSettleMoney();
						// 待结算金额 + 手续费
						BigDecimal startAmount = vo.getStartAmount();
						if (settlementType == 0) {
							settleMoney = entity.getOverMoney();
						}
						if (settleMoney.compareTo(startAmount) >= 0) {
							// 结算后账户余额 = 结算前账户余额 - 结算金额
							BigDecimal money = overMoney.subtract(startAmount);
							when_buf.append(CommonUtil.getBuffer(" WHEN ",entity.getId()," THEN ",money));
							where_buf.append(CommonUtil.getBuffer(entity.getId(),", "));
							
							// 结算记录
							BigDecimal amount = vo.getAmount();
							BigDecimal cost = vo.getCost();
							Long merchantId = entity.getMerId();
							SettleMent settleMent = new SettleMent(amount, cost, entity.getMerId(), 
									entity.getId(), settlementType);
							list.add(settleMent);
							// 账户明细
							String recordNumber = settleMent.getSerialNumber();
							AccountDetails details = new AccountDetails(amount, cost, money, recordNumber, true, "提现", merchantId);
							detailsList.add(details);
						}
					}
				}
				
			}
			if (list.size() > 0) {
				BankVo vo = new BankVo(entityManager);
				// 批量更新账户余额
				vo.updateOverMoneyByIds(when_buf,where_buf);
				// 批量添加结算记录
				settleMentRepository.saveAll(list);
				// 批量添加账户明细
				accountDetailsRepository.saveAll(detailsList);
				ReturnMsgEnum.success.setMsg("结算成功！");
				return ReturnMsgEnum.returnMsg();
			}
			
		}
		ReturnMsgEnum.error.setMsg("结算失败，没有可结算记录！");
		return ReturnMsgEnum.returnMsg();
	}
	
	@Override
	@Transactional
	public JSONObject deleteEntity(Long id) {
		Optional<Bank> optional = bankRepository.findById(id);
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("账户不存在或已被删除！").toJson();
		
		Bank bank = optional.get();
		if (bank.getBankType())
			return ReturnMsgEnum.error.setMsg("当前账户为主账户(主卡)，不可删除！").toJson();
		BigDecimal overMoney = bank.getOverMoney();
		if (overMoney != null && overMoney.compareTo(new BigDecimal(0))>0)
			return ReturnMsgEnum.error.setMsg("当前账户有可用余额，不可删除！").toJson();
		
		Long unsettledCount = settleMentService.findUnsettledCount(id, null);
		if (unsettledCount > 0)
			return ReturnMsgEnum.error.setMsg("当前账户有未结算记录，不可删除！").toJson();
		settleMentRepository.deleteByBank(bank);
		bankRepository.deleteById(id);
		return ReturnMsgEnum.success.setMsg("删除成功！").toJson();
	}
	
	public JSONObject updateEntity(Bank t) {
		t.validate();
		if (!ReturnMsgEnum.isSucc)
			return ReturnMsgEnum.returnMsg();
		
		return ReturnMsgEnum.success.setMsg("修改成功！").toJson();
		
	}

	/**
	 * 设置当前银行卡为主卡
	 * @param bank
	 * @return
	 */
	@Transactional
	public JSONObject updateBankType(Bank bank) {
		bankRepository.updateBankType(bank.getId(), bank.getMerId());
		ReturnMsgEnum.success.setMsg("主卡设置成功！");
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 查询商户所有可代付银行账户
	 * @param merchant
	 * @param payeeState
	 * @return
	 */
	public List<Bank> findByMerchantAndPayeeState(Merchant merchant, Boolean payeeState) {
		return bankRepository.findByMerchantAndPayeeState(merchant, payeeState);
	}

	@Override
	public boolean addValidate(Bank t) {
		
		return false;
	}

	/**
	 * 商户资金列表查询
	 * @param vo
	 * @return
	 */
	public Map<String, Object> findFundsList(FundsVo vo) {
		FundsVo.setEmpty();
		vo.setEntityManager(entityManager);
		vo.findListByQl(false);
		Map<String, Object> map = new HashMap<String, Object>();
		Integer size = vo.listSize();
		map.put("draw", vo.getDraw());
		map.put("recordsTotal", size);//数据总条数
		map.put("recordsFiltered", size);//显示的条数
		map.put("data", vo.getFundsList());//数据集合
		return map;
	}

	/**
	 * 开启/关闭银行卡代付功能
	 * @param bank
	 * @return
	 */
	public JSONObject updateBankPayeeState(Bank bank) {
		bankRepository.updateBankPayeeState(bank.getId());
		ReturnMsgEnum.success.setMsg(bank.isChecked()?"已开启代付！":"已关闭代付！");
		return ReturnMsgEnum.returnMsg();
	}
	
	
	public JSONObject daifuManualRefund(String data){
		return rollback(data);
	}
	
	public JSONObject rollback(String data) {
		String[] arrays = data.split(",");
		Long merId = Long.valueOf(arrays[0]);
		BigDecimal amount = new BigDecimal(arrays[1]).add(new BigDecimal(arrays[2]));
		String serialNumber = arrays[3];
		if (arrays[4].contains("代付金额已回退"))
			return ReturnMsgEnum.success.setMsg("资金已经回退成功,系统拒绝此次操作").toJson(); 
		String sql = "update bank set over_money = over_money + ?,all_pay = all_pay - ? where merchant_id = ? and bank_type = 1";
		int i = jdbcTemplate.update(sql, new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				// TODO Auto-generated method stub
				ps.setBigDecimal(1, amount);
				ps.setBigDecimal(2, amount);
				ps.setLong(3, merId);
			}
		});
		if (i > 0) {
			logger.info("余额回退成功");
			String discription = "代付失败(代付金额已回退)";
			Long mId = Long.valueOf(merId);
			sql = "update settle_ment set discription = ? where serial_number = ? and merchant_id = ? and state = -3";
			jdbcTemplate.update(sql,new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					// TODO Auto-generated method stub
					ps.setString(1, discription);
					ps.setString(2, serialNumber);
					ps.setLong(3, mId);
				}
			});
			return ReturnMsgEnum.success.setMsg("资金回退成功").toJson();
		}
		logger.info("余额回退失败");
		return ReturnMsgEnum.error.setMsg("资金回退失败").toJson();
	}
}
