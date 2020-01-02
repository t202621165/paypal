package com.iwanol.paypal.vo;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.Merchant;

public class MerchantPayeeVo {
	private String url;
	private String merchantId;
	private String totalAmount;
	private String bankCode;
	private String bankName;
	private String realName;
	private String bankNumber;
	
	public JSONObject valid(Merchant merchant){
		BigDecimal amount = new BigDecimal("0.00");
		Bank bank = merchant.getBanks().stream().filter(b -> b.getBankType()).findFirst().get();
		if (StringUtils.isEmpty(url))
			return ReturnMsgEnum.error.setMsg("请填写代付网关").toJson();
		if (StringUtils.isEmpty(merchantId))
			return ReturnMsgEnum.error.setMsg("请填写商户账户").toJson();
		if (StringUtils.isEmpty(totalAmount))
			return ReturnMsgEnum.error.setMsg("请填写代付金额").toJson();
		if (StringUtils.isEmpty(bankCode))
			return ReturnMsgEnum.error.setMsg("请选择收款银行").toJson();
		if (StringUtils.isEmpty(bankName))
			return ReturnMsgEnum.error.setMsg("请填写开户行").toJson();
		if (StringUtils.isEmpty(realName))
			return ReturnMsgEnum.error.setMsg("请填写收款人姓名").toJson();
		if (StringUtils.isEmpty(bankNumber))
			return ReturnMsgEnum.error.setMsg("请填写银行卡号").toJson();
		amount = new BigDecimal(totalAmount).add(merchant.getFee());
		if (amount.compareTo(bank.getOverMoney()) > 0)
			return ReturnMsgEnum.error.setMsg("账户余额不足").toJson();
		return ReturnMsgEnum.success.setMsg("校验成功").toJson();
	}
	
	public JSONObject batchValid(Merchant merchant,List<MerchantPayeeVo> list){
		int i = 0;
		Bank bank = merchant.getBanks().stream().filter(b -> b.getBankType()).findFirst().get();
		for (MerchantPayeeVo v : list){
			if (StringUtils.isEmpty(v.getTotalAmount()))
				break;
			if (StringUtils.isEmpty(v.getBankCode()))
				break;
			if (StringUtils.isEmpty(v.getBankName()))
				break;
			if (StringUtils.isEmpty(v.getRealName()))
				break;
			if (StringUtils.isEmpty(v.getBankNumber()))
				break;
			i++;
		}
		System.out.println("=============|"+i);
		if (i == list.size()){
			BigDecimal amount = new BigDecimal("0.00");
			for (MerchantPayeeVo v : list){
				amount = amount.add(new BigDecimal(v.getTotalAmount()));
			}
			amount = amount.add(merchant.getFee());
			if (amount.compareTo(bank.getOverMoney()) > 0)
				return ReturnMsgEnum.error.setMsg("账户余额不足").toJson();
			return ReturnMsgEnum.success.setMsg("校验成功").toJson();
		}
		return ReturnMsgEnum.success.setMsg("Excel模板校验失败").toJson();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public String getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(String totalAmount) {
		this.totalAmount = totalAmount;
	}

	public String getBankCode() {
		return bankCode;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

}
