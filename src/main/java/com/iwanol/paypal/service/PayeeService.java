package com.iwanol.paypal.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.domain.SystemSet;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.payee.PayeeFactory;
import com.iwanol.paypal.repository.BankRepository;
import com.iwanol.paypal.repository.PayeeRepository;
import com.iwanol.paypal.repository.SettleMentRepository;
import com.iwanol.paypal.until.SMS;
import com.iwanol.paypal.vo.PayeeVo;

@Service
public class PayeeService extends BaseServiceImpl<Payee, Long>{
	
	@Autowired
	private BankRepository bankRepository;
	@Autowired
	private PayeeRepository payeeRepository;
	@Autowired
	private SystemSetService systemSetService;
	@Autowired
	private SettleMentRepository settleMentRepository;

	/**
	 * 代付-参数校验
	 * @param vo
	 * @return
	 */
	public JSONObject payeeValidate(PayeeVo vo) {
		try {
			// 验证码校验
			SMS.verify(vo.getSmsCode());
			if (!ReturnMsgEnum.isSucc)
				return ReturnMsgEnum.returnMsg();
			
			SystemSet system = systemSetService.findByMark();
			Merchant merchant = system.getMerchant();
			
			// 根据id查询当前使用的代付方式
			Long payeeId = vo.getPayeeId();
			Optional<Payee> optional1 = payeeRepository.findById(payeeId);
			if (!optional1.isPresent())
				return ReturnMsgEnum.error.setMsg("付款方式不存在或不可用").toJson();
			
			// 校验付款金额
			Payee payee = optional1.get();
			BigDecimal amount = vo.getAmount();
			ReturnMsgEnum result = payee.amountValidate(amount);
			if (!ReturnMsgEnum.isSucc)
				return result.toJson();

			// 根据商户和银行账户id查询收款账号
			Optional<Bank> optional2 = bankRepository
					.findByIdAndMerchantAndPayeeState(vo.getBankId(),merchant, true);
			if (!optional2.isPresent())
				return ReturnMsgEnum.error.setMsg("收款账号不存在或不可用").toJson();
			
			// 校验通过-付款
			vo.setBank(optional2.get());
			Pay_Payee pay = PayeeFactory.getInstance(payee);
			pay.setBaseParam(payee, vo);
			ReturnMsgEnum response = pay.pay(payee);
			
			// 付款完成-添加结算记录(自提不需要更新商户账户余额)
			SettleMent settleMent = pay.getSettleMent();
			settleMent.setMerchant(merchant);
			settleMent.setReplyState(response.getState());
			settleMentRepository.save(settleMent);
			
			return response.toJson();
		} catch (Exception e) {
			e.printStackTrace();
			ReturnMsgEnum.error.setMsg("系统异常！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	@Override
	public BaseRepository<Payee, Long> getRepository() {
		return payeeRepository;
	}

	@Override
	public boolean addValidate(Payee t) {
		Optional<Payee> optional = payeeRepository.findByMark(t.getMark());
		if (optional.isPresent()) {
			if (optional.get().getId().equals(t.getId()))
				return false;
			return true;
		}
		return false;
	}

	@Override
	public Specification<Payee> getSpecification(Long v) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * 设置默认代付通道
	 * @param payee
	 * @return
	 */
	public JSONObject updateDefault(Payee payee) {
		payeeRepository.payeeRepository(payee.getId());
		return ReturnMsgEnum.success.setMsg("设置成功！").toJson();
	}
}
