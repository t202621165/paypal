package com.iwanol.paypal.payee.impl;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.until.MD5Util;

import cn.hutool.core.lang.UUID;
import cn.hutool.http.HttpRequest;

public class Pay_LongBao extends Pay_Payee{
	
	private String parter;
	
	private String orderid;
	
	private String accout_type = "0";
	
	private String amount;
	
	private String account_no;
	
	private String account_name;
	
	private String bank_code;
	
	private String nonce_str;
	
	private String clientip;
	
	private String sign;

	@Override
	public void setRequestParam(Payee payee) {
		// TODO Auto-generated method stub
		this.parter = payee.getAccount();
		this.orderid = getPayeeNumber();
		this.amount = String.format("%.2f", getAmount().doubleValue());
		this.account_no = getAccount();
		this.account_name = getRealName();
		this.bank_code = getBankMark();
		this.nonce_str = UUID.fastUUID().toString().replace("-","");
		this.clientip = "116.62.111.54";
		this.bank_code = getBankCode(getBankMark());
		this.sign = sign(payee.getSignKey());
	}

	@Override
	public ReturnMsgEnum pay(Payee payee) {
		// TODO Auto-generated method stub
		String url = "https://gate.longpay.com/agentpay/index";
		StringBuffer buffer = new StringBuffer();
		buffer.append("parter="+this.parter).append("&orderid="+this.orderid).append("&accout_type=0").append("&amount="+this.amount)
		.append("&account_no="+this.account_no).append("&account_name="+this.account_name).append("&bank_code="+this.bank_code)
		.append("&nonce_str="+this.nonce_str).append("&clientip="+this.clientip).append("&sign="+this.sign);
		logger.info("龙宝代付请求报文串【{}】",buffer.toString());
		try{
			String result = HttpRequest.post(url).body(buffer.toString().getBytes("GB2312")).charset("GB2312").execute().body();
			logger.info("龙宝代付响应参数:{}",result);
			if (!StringUtils.isEmpty(result)){
				JSONObject resp = JSONObject.parseObject(result);
				if (resp.getString("code").equals("0000")){
					return ReturnMsgEnum.success.setMsg("龙宝代付受理成功,等待龙宝出款");		
				}else{
					return ReturnMsgEnum.error.setMsg("龙宝代付失败【"+resp.getString("msg")+"】");
				}
			}
			return null;
		}catch (Exception e) {
			// TODO: handle exception
			return ReturnMsgEnum.error.setMsg("代付失败【"+e.getMessage()+"】");
		}
	}
	
	private String sign(String key){
		StringBuffer buffer = new StringBuffer();
		buffer.append("parter="+getParter()).append("&orderid="+getOrderid())
		.append("&amount="+ amount).append("&accout_type="+getAccout_type())
		.append("&account_no="+getAccount_no()).append("&account_name="+getAccount_name())
		.append("&nonce_str="+getNonce_str());
		logger.info(String.format("请求签名串：%s",buffer.toString())+key);
		return MD5Util.sign(buffer.toString(), key, "GB2312");
	}
	
	private String getBankCode(String mark){
		
		if (mark.equals("CITIC")) return "ECITIC";
		
		if (mark.equals("COMM")) return "BOCOM";
		
		if (mark.equals("PINGAN")) return "PAB";
		
		if (mark.equals("CGB")) return "HXB";
		
		return mark;
	}

	public String getParter() {
		return parter;
	}

	public void setParter(String parter) {
		this.parter = parter;
	}

	public String getOrderid() {
		return orderid;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public String getAccout_type() {
		return accout_type;
	}

	public void setAccout_type(String accout_type) {
		this.accout_type = accout_type;
	}

	public String getAccount_no() {
		return account_no;
	}

	public void setAccount_no(String account_no) {
		this.account_no = account_no;
	}

	public String getAccount_name() {
		return account_name;
	}

	public void setAccount_name(String account_name) {
		this.account_name = account_name;
	}

	public String getBank_code() {
		return bank_code;
	}

	public void setBank_code(String bank_code) {
		this.bank_code = bank_code;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

}
