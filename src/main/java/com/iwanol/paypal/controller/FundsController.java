package com.iwanol.paypal.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.domain.SettleMentReply;
import com.iwanol.paypal.service.BankService;
import com.iwanol.paypal.service.SettleMentReplyService;
import com.iwanol.paypal.service.SettleMentService;
import com.iwanol.paypal.until.PageData;
import com.iwanol.paypal.vo.BankVo;
import com.iwanol.paypal.vo.FundsVo;
import com.iwanol.paypal.vo.SettleMentReplyVo;
import com.iwanol.paypal.vo.SettleMentVo;

@RestController
@RequestMapping(value = "funds", method = RequestMethod.POST)
public class FundsController {

	@Autowired
	private SettleMentService settleMentService;
	@Autowired
	private SettleMentReplyService settleMentReplyService;
	@Autowired
	private BankService bankService;
	
	@PostMapping("settlement")
	@PreAuthorize("hasAuthority('funds_pay')")
	public Map<String, Object> settlementList(SettleMentVo vo){
		if (vo.getState() == null) {
			vo.setState(0);
		}
		vo.setType(0);
		return settleMentService.findPageVo(vo, Boolean.TRUE);
	}
	
	@PostMapping("daifu")
	@PreAuthorize("hasAuthority('funds_pay')")
	public Map<String, Object> daifuList(SettleMentVo vo){
		if (vo.getState() == null) {
			vo.setState(3);
		}
		vo.setType(0);
		return settleMentService.findPageVo(vo, Boolean.TRUE);
	}
	
	/**
	 * 批付列表
	 * @param vo
	 * @return
	 */
	@PostMapping("reply")
	@PreAuthorize("hasAuthority('reply.html')")
	public Map<String, Object> settlementReply(SettleMentReplyVo vo){
		return settleMentReplyService.findPageVo(vo, Boolean.TRUE);
	}
	
	/**
	 * 商户资金列表
	 * @param vo
	 * @return
	 */
	@PostMapping("list")
	@PreAuthorize("hasAuthority('funds.html')")
	public Map<String, Object> list(FundsVo vo){
		return bankService.findFundsList(vo);
	}
	
	/**
	 * T+0结算列表
	 * @param vo
	 * @return
	 */
	@PostMapping("t0_list")
	@PreAuthorize("hasAuthority('funds_t0')")
	public Map<String, Object> t0(PageData pageData, BankVo vo){
		vo.setSettlementType(0);
		return bankService.findPageVo(vo, Boolean.FALSE);
	}
	
	/**
	 * T+1结算列表
	 * @param vo
	 * @return
	 */
	@PostMapping("t1_list")
	@PreAuthorize("hasAuthority('funds_t1')")
	public Map<String, Object> t1(BankVo vo){
		vo.setSettlementType(1);
		return bankService.findPageVo(vo, Boolean.FALSE);
	}
	
	/**
	 * T+0结算
	 * @param vo
	 * @return
	 */
	@PostMapping("t0_settlement")
	@PreAuthorize("hasAuthority('funds/t0_settlement')")
	public JSONObject t0(@RequestBody List<BankVo> banks){
		return bankService.findMerchantSettlementCounts(banks, 0);
	}
	
	
	/**
	 * T+1结算
	 * @param vo
	 * @return
	 */
	@PostMapping("t1_settlement")
	@PreAuthorize("hasAuthority('funds/t1_settlement')")
	public JSONObject t1(@RequestBody List<BankVo> banks){
		return bankService.findMerchantSettlementCounts(banks, 1);
	}
	
	/**
	 * 商户提现-审核通过
	 * @param vo
	 * @return
	 */
	@PostMapping("check")
	@PreAuthorize("hasAuthority('funds/check')")
	public JSONObject check(@RequestBody List<SettleMent> SettleMents){
		return settleMentService.updateSettlementCheck(SettleMents, true);
	}
	
	
	/**
	 * 商户提现-拒绝审核
	 * @param vo
	 * @return
	 */
	@PostMapping("check_refuse")
	@PreAuthorize("hasAuthority('funds/check_refuse')")
	public JSONObject refuseCheck(@RequestBody List<SettleMent> SettleMents){
		return settleMentService.updateSettlementCheck(SettleMents, false);
	}
	
	/**
	 * 商户提现-完成付款
	 * @param vo
	 * @return
	 */
	@PostMapping("pay")
	@PreAuthorize("hasAuthority('funds/pay')")
	public JSONObject pay(@RequestBody List<SettleMent> SettleMents){
		return settleMentService.updateSettlementPay(SettleMents, true);
	}
	
	/**
	 * 商户提现-拒绝付款
	 * @param vo
	 * @return
	 */
	@PostMapping("pay_refuse")
	@PreAuthorize("hasAuthority('funds/pay_refuse')")
	public JSONObject refusePay(@RequestBody List<SettleMent> SettleMents){
		return settleMentService.updateSettlementPay(SettleMents, false);
	}
	
	/**
	 * 支付宝代付
	 * @param vo
	 * @return
	 */
	@PostMapping("payee")
	@PreAuthorize("hasAuthority('funds/pay')")
	public JSONObject payee(@RequestBody List<SettleMent> SettleMents){
		return settleMentService.updateSettlementPayee(SettleMents);
	}
	
	/**
	 * 批付-确定付款
	 * @param vo
	 * @return
	 */
	@PostMapping("reply_pay")
	@PreAuthorize("hasAuthority('funds/reply_pay')")
	public JSONObject replyPay(SettleMentReply vo){
		return settleMentReplyService.updateReplyState(vo);
	}
	
	@PostMapping("pay_back")
	@PreAuthorize("hasAuthority('funds/pay')")
	public JSONObject payeeBack(HttpServletRequest request){
		String data = request.getParameter("data");
		if (!StringUtils.isEmpty(data)){
			return bankService.daifuManualRefund(data);
		}else{
			return ReturnMsgEnum.error.setMsg("驳回失败").toJson();
		}
	}
	
	/**
	 * 删除批付记录
	 * @param vo
	 * @return
	 */
	@PostMapping("reply_delete")
	@PreAuthorize("hasAuthority('funds/reply_delete')")
	public JSONObject replyDelete(SettleMentReply vo){
		return settleMentReplyService.deleteEntity(vo.getId());
	}
	
	/**
	 * 资金自提列表
	 * @param vo
	 * @return
	 */
	@PostMapping("self")
	@PreAuthorize("hasAuthority('funds_self')")
	public Map<String, Object> selfList(SettleMentVo vo){
		vo.setState(null);
		vo.setReplyId(null);
		if (vo.getType() == null) {
			vo.setType(1);
		}
		return settleMentService.findPageVo(vo, Boolean.TRUE);
	}
	
}
