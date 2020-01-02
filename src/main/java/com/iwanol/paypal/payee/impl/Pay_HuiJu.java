package com.iwanol.paypal.payee.impl;

import org.springframework.util.DigestUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.until.DateUtil;

import cn.hutool.http.HttpRequest;

public class Pay_HuiJu extends Pay_Payee{
	
	private String userNo;
	
	private String productCode = "BANK_PAY_DAILY_ORDER";
	
	private String requestTime;
	
	private String merchantOrderNo;
	
	private String receiverAccountNoEnc;
	
	private String receiverNameEnc;
	
	private String receiverAccountType = "201";
	
	private String paidAmount;
	
	private String currency = "201";
	
	private String isChecked = "202";
	
	private String paidDesc = "资金结算";
	
	private String paidUse = "201";
	
	private String callbackUrl;
			
    private String hmac;
    
	@Override
	public void setRequestParam(Payee payee) {
		// TODO Auto-generated method stub
		this.userNo = payee.getAccount();
		this.requestTime = DateUtil.getDate("yyyy-MM-dd HH:mm:ss");
		this.merchantOrderNo = getPayeeNumber();
		this.receiverAccountNoEnc = getAccount();
		this.receiverNameEnc = getRealName();
		this.paidAmount = String.format("%.2f", getAmount().doubleValue());
		this.callbackUrl = "http://api.iwanol.com/funds/huiju/notify";
		this.hmac = this.sign(payee.getSignKey());
	}
	
	private String sign(String key) {
		String content = getUserNo().concat(getProductCode()).concat(getRequestTime()).concat(getMerchantOrderNo())
				.concat(getReceiverAccountNoEnc()).concat(getReceiverNameEnc()).concat(getReceiverAccountType())
				.concat(getPaidAmount()).concat(getCurrency()).concat(getIsChecked()).concat(getPaidDesc())
				.concat(getPaidUse()).concat(getCallbackUrl()).concat(key);
		try{
			return DigestUtils.md5DigestAsHex(content.getBytes("UTF-8")).toUpperCase();
		}catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@Override
	public ReturnMsgEnum pay(Payee payee) {
		// TODO Auto-generated method stub
		String url = "https://www.joinpay.com/payment/pay/singlePay";
		JSONObject json = new JSONObject();
		json.put("userNo", getUserNo());
		json.put("productCode", getProductCode());
		json.put("requestTime", getRequestTime());
		json.put("merchantOrderNo", getMerchantOrderNo());
		json.put("receiverAccountNoEnc", getReceiverAccountNoEnc());
		json.put("receiverNameEnc", getReceiverNameEnc());
		json.put("receiverAccountType",getReceiverAccountType());
		json.put("paidAmount", getPaidAmount());
		json.put("currency",getCurrency());
		json.put("isChecked",getIsChecked());
		json.put("paidDesc", getPaidDesc());
		json.put("paidUse",getPaidUse());
		json.put("callbackUrl", getCallbackUrl());
		json.put("hmac",getHmac());
		logger.info("汇聚代付请求参数:{}",json.toJSONString());
		String result = HttpRequest.post(url).body(json.toJSONString()).charset("UTF-8").execute().body();
		logger.info("汇聚代付响应结果:{}",result);
		JSONObject resp = JSONObject.parseObject(result);
		if (resp.getString("statusCode").equals("2001")){
			return ReturnMsgEnum.success.setMsg("汇聚代付受理成功,等待汇聚出款");
		}
		return ReturnMsgEnum.error.setMsg("代付失败【"+resp.getString("message")+"】");
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(String requestTime) {
		this.requestTime = requestTime;
	}

	public String getMerchantOrderNo() {
		return merchantOrderNo;
	}

	public void setMerchantOrderNo(String merchantOrderNo) {
		this.merchantOrderNo = merchantOrderNo;
	}

	public String getReceiverAccountNoEnc() {
		return receiverAccountNoEnc;
	}

	public void setReceiverAccountNoEnc(String receiverAccountNoEnc) {
		this.receiverAccountNoEnc = receiverAccountNoEnc;
	}

	public String getReceiverNameEnc() {
		return receiverNameEnc;
	}

	public void setReceiverNameEnc(String receiverNameEnc) {
		this.receiverNameEnc = receiverNameEnc;
	}

	public String getReceiverAccountType() {
		return receiverAccountType;
	}

	public void setReceiverAccountType(String receiverAccountType) {
		this.receiverAccountType = receiverAccountType;
	}

	public String getPaidAmount() {
		return paidAmount;
	}

	public void setPaidAmount(String paidAmount) {
		this.paidAmount = paidAmount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getIsChecked() {
		return isChecked;
	}

	public void setIsChecked(String isChecked) {
		this.isChecked = isChecked;
	}

	public String getPaidDesc() {
		return paidDesc;
	}

	public void setPaidDesc(String paidDesc) {
		this.paidDesc = paidDesc;
	}

	public String getPaidUse() {
		return paidUse;
	}

	public void setPaidUse(String paidUse) {
		this.paidUse = paidUse;
	}

	public String getCallbackUrl() {
		return callbackUrl;
	}

	public void setCallbackUrl(String callbackUrl) {
		this.callbackUrl = callbackUrl;
	}

	public String getHmac() {
		return hmac;
	}

	public void setHmac(String hmac) {
		this.hmac = hmac;
	}

}
