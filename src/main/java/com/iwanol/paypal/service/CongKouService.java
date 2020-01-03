package com.iwanol.paypal.service;

import java.math.BigDecimal;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.AccountDetails;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.CongKou;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.repository.AccountDetailsRepository;
import com.iwanol.paypal.repository.BankRepository;
import com.iwanol.paypal.repository.CongKouRepository;
import com.iwanol.paypal.repository.MerchantRepository;
import com.iwanol.paypal.until.CommonUtil;
import com.iwanol.paypal.until.DateUtil;

@Service
public class CongKouService extends BaseServiceImpl<CongKou, Object>{
	
	@Autowired
	private CongKouRepository congKouRepository;
	@Autowired
	private MerchantRepository merchantRepository;
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private AccountDetailsRepository accountDetailsRepository;
	

	@Override
	public BaseRepository<CongKou, Long> getRepository() {
		// TODO Auto-generated method stub
		return congKouRepository;
	}

	@Override
	public boolean addValidate(CongKou t) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Specification<CongKou> getSpecification(Object v) {
		// TODO Auto-generated method stub
		return null;
	}

	@Transactional
	public JSONObject congkou(CongKou v){
		if (StringUtils.isEmpty(v.getMerId()))
			return ReturnMsgEnum.error.setMsg("请输入商户ID").toJson();
		if (v.getAmount().compareTo(BigDecimal.ZERO) < 0)
			return ReturnMsgEnum.error.setMsg("充扣款金额无效").toJson();
		Optional<Merchant> optional = merchantRepository.findById(Long.valueOf(v.getMerId()));
		if (optional.isPresent()){
			Merchant merchant = optional.get();
			Bank bank = merchant.getBanks().stream().filter(b -> b.getBankType()).findFirst().get();
			v.setBankName(bank.getBankName());
			v.setMerName(merchant.getNickName());
			v.setRealName(bank.getRealName());
			v.setBankNumber(bank.getBankNumber());
			if (v.getType() == 0){
				bank.setOverMoney(bank.getOverMoney().add(v.getAmount()));
				bank.setAllDeposit(bank.getAllDeposit().add(v.getAmount()));
			}else{
				bank.setOverMoney(bank.getOverMoney().subtract(v.getAmount()));
				bank.setAllDeposit(bank.getAllDeposit().subtract(v.getAmount()));
			}
			Bank b = bankRepository.save(bank);
			if (!StringUtils.isEmpty(b))
				v.setState(1);
			else
				v.setState(-1);
			v.setSerialNumber(DateUtil.getDate("yyyyMMddHHmmssSSS").concat(CommonUtil.getRandom(4)));
			congKouRepository.save(v);
			//保存到资金明细
			Boolean adType = Boolean.TRUE;
			if (v.getType() == 1)
				adType = Boolean.FALSE;
			AccountDetails ad = new AccountDetails(v.getAmount(), BigDecimal.ZERO, bank.getOverMoney(), v.getSerialNumber(), adType, v.getDiscription(), Long.valueOf(v.getMerId()));
			accountDetailsRepository.save(ad);
		}else{
			return ReturnMsgEnum.error.setMsg("商户不存在").toJson();
		}
		return ReturnMsgEnum.success.setMsg("充扣款业务操作成功").toJson();
	}
}
