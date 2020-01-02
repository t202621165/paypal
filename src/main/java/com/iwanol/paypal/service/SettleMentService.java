package com.iwanol.paypal.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.domain.SettleMentReply;
import com.iwanol.paypal.domain.User;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.payee.PayeeFactory;
import com.iwanol.paypal.repository.PayeeRepository;
import com.iwanol.paypal.repository.SettleMentReplyRepository;
import com.iwanol.paypal.repository.SettleMentRepository;
import com.iwanol.paypal.security.SecurityManager;
import com.iwanol.paypal.vo.BankVo;

@Service
public class SettleMentService extends BaseServiceImpl<SettleMent, Object>{

	@Autowired
	private EntityManager entityManager;
	@Autowired
	private PayeeRepository payeeRepository;
	@Autowired
	private SettleMentRepository settleMentRepository;
	@Autowired
	private SettleMentReplyRepository settleMentReplyRepository;
	
	@Override
	public BaseRepository<SettleMent, Long> getRepository() {
		return settleMentRepository;
	}

	@Override
	public Specification<SettleMent> getSpecification(Object v) {
		
		return null;
	}

	@Override
	public boolean addValidate(SettleMent t) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 查询账户未结算记录
	 * @return
	 */
	@SuppressWarnings("serial")
	public Long findUnsettledCount(Long bankId, Long merchantId) {
		return settleMentRepository.count(new Specification<SettleMent>() {

			@Override
			public Predicate toPredicate(Root<SettleMent> root, CriteriaQuery<?> query,
					CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				if (bankId != null)
					list.add(cb.equal(root.get("bank").get("id").as(Long.class), bankId));
				
				if (merchantId != null)
					list.add(cb.equal(root.get("merchant").get("id").as(Long.class), merchantId));
					
				list.add(cb.equal(root.get("state").as(Integer.class), 0));
				list.add(cb.or(cb.equal(root.get("state").as(Integer.class), 2)));
				Predicate[] predicates = new Predicate[list.size()];
				predicates = list.toArray(predicates);
				return cb.and(predicates);
			}
		});
	}

	/**
	 * 商户提现-通过/拒绝 审核
	 * @param settleMents
	 * @param check
	 * @return
	 */
	@Transactional
	public JSONObject updateSettlementCheck(List<SettleMent> settleMents, boolean check) {
		if (!check) { // 拒绝审核 ：修改状态值为-1、更新账户余额
			List<Bank> banks = settleMentRepository.findAmountAndCostByIds(settleMents, 0);
			if (banks != null && banks.size() > 0){
				BankVo vo = new BankVo(entityManager);
				int i = vo.updateOverMoneyByMerchant(banks);
				if (i > 0) {
					settleMentRepository.updateByIds(0, -1, settleMents);
					ReturnMsgEnum.success.setMsg("已拒绝审核！");
					return ReturnMsgEnum.returnMsg();
				}
			}
		}else{
			settleMentRepository.updateByIds(0, 2, settleMents);
			ReturnMsgEnum.success.setMsg("审核通过，请尽快完成付款！");
			return ReturnMsgEnum.returnMsg();
		}
		ReturnMsgEnum.error.setMsg("审核失败！");
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 商户提现-完成/拒绝 付款
	 * @param settleMents
	 * @param pay
	 * @return
	 */
	@Transactional
	public JSONObject updateSettlementPay(List<SettleMent> settleMents, boolean pay) {
		List<Bank> banks = settleMentRepository.findAmountAndCostByIds(settleMents,2);
		if (banks == null || banks.isEmpty())
			return ReturnMsgEnum.error.setMsg("暂无待付款记录！").toJson();
		if (pay) { // 完成付款：修改状态值为1、插入批付记录
			// 插入批付记录
			User user = SecurityManager.getUser();
			SettleMentReply reply = new SettleMentReply(user);
			settleMentReplyRepository.save(reply);
			// 修改状态值为1、更新批付ID
			settleMentRepository.updateByIds(2, 1, settleMents, reply);
			ReturnMsgEnum.success.setMsg("付款成功！");
			return ReturnMsgEnum.returnMsg();
		}else{ // 拒绝付款 ：修改状态值为-2、更新账户余额
				BankVo vo = new BankVo(entityManager);
				int i = vo.updateOverMoneyByMerchant(banks);
				if (i > 0){
					settleMentRepository.updateByIds(2, -2, settleMents);
					ReturnMsgEnum.success.setMsg("已拒绝付款！");
					return ReturnMsgEnum.returnMsg();
				}
		}
		ReturnMsgEnum.error.setMsg("付款失败！");
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 商户提现-支付宝代付
	 * @param settleMents
	 * @param pay
	 * @return
	 */
	@Transactional
	public JSONObject updateSettlementPayee(List<SettleMent> settleMents) {
		
		Optional<Payee> optional = payeeRepository.findByIsDefault(Boolean.TRUE);
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("请前往‘系统设置’配置商户代付默认通道！").toJson();
		
		Payee payee = optional.get();
		Pay_Payee pay = PayeeFactory.getInstance(payee);
		
		return daifu(settleMents, payee, pay);
	}
	
	public JSONObject daifu(List<SettleMent> settleMents,Payee payee,Pay_Payee pay){
		// 查询可代付记录(结算账户为支付宝)
		List<SettleMent> list = null;
		String description = "支付宝代付";
		if (payee.getType()){
			list = settleMentRepository.findWaitPayeeRecorde(settleMents);
			if (list == null || list.isEmpty())
				return ReturnMsgEnum.error.setMsg("没有可代付记录，只允许支付宝账户使用此功能！").toJson();	
			if (!payee.getType())
				return ReturnMsgEnum.error.setMsg("请使用‘支付宝代付通道’完成此操作！").toJson();
		}else{
			description = "银联代付";
			list = settleMentRepository.findWaitPayeeRecordeByBank(settleMents);
			if (list == null || list.isEmpty())
				return ReturnMsgEnum.error.setMsg("没有可代付记录，只允许银行卡账户使用此功能！").toJson();	
			if (payee.getType())
				return ReturnMsgEnum.error.setMsg("请使用‘银联代付通道’完成此操作！").toJson();
		}
		
		List<Long> ids = new ArrayList<Long>();
		for (SettleMent settleMent : list) {
			// 设置代付参数
			pay.setBaseParam(payee, settleMent);
			// 发起付款
			pay.pay(payee);
			
			// 支付宝付款失败，终止循环
			if (!ReturnMsgEnum.isSucc)
				break;
			ids.add(settleMent.getId());
		}
		if (ids.isEmpty())
			return ReturnMsgEnum.returnMsg();
		
		
		// 修改状态值为1、更新批付ID
		if (payee.getType() || payee.getMark().contains("longbao") || payee.getMark().contains("zhangling")){
			settleMentRepository.updateStateAndDesc(ids, description,new Date());
		}
		
		return ReturnMsgEnum.success.setMsg("代付受理成功,预计两小时内到账！").toJson();
	}

	public void updateStateAndDescBySerialNumber(String serialNumber,String description){
		settleMentRepository.updateStateAndDescBySerialNumber(serialNumber, description, new Date());
	}
	
	public List<SettleMent> findWaitAuditPayeeRecorde(List<SettleMent> list){
		return settleMentRepository.findWaitAuditPayeeRecorde(list);
	}
}
