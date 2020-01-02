package com.iwanol.paypal.payee.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.until.CFCAUtil;
import com.iwanol.paypal.until.DateUtil;
import com.iwanol.paypal.until.HttpSender;

public class Pay_ShuangQian extends Pay_Payee{
	
	private String url = "http://218.4.234.150:9600/merchant/numberPaidSingle.action";
	
	private String merno;
	
	private String time;
	
	private String content;
	
	private String signature;

	@Override
	public void setRequestParam(Payee payee) {
		// TODO Auto-generated method stub
		this.merno = payee.getAccount();
		this.time = DateUtil.getDate("yyyyMMddHHmmss");
		this.content = this.content();
		this.signature = this.sign(payee.getPrivateKey(),payee.getSignKey());
	}
	
	private String content(){
		StringBuffer buffer = new StringBuffer();
		buffer.append(getRealName()).append("|");
		buffer.append(getBankMark()).append("|");
		buffer.append(getAccount()).append("|");
		buffer.append("1").append("|");
		buffer.append(String.format("%.2f", getAmount().doubleValue())).append("|");
		buffer.append(getPayeeNumber()).append("|");
		buffer.append("000").append("|");
		return buffer.toString();
	}
	
	private String sign(String pfxPath,String passWord){
		StringBuffer buffer = new StringBuffer();
		buffer.append("merno="+getMerno()).append("&time="+getTime()).append("&content="+getContent());
		logger.info("【验签参数:{}】",buffer.toString());
		try {
			return CFCAUtil.signMessageByP1(buffer.toString(), pfxPath, passWord);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ReturnMsgEnum pay(Payee payee) {
		// TODO Auto-generated method stub
		Map<String,String> params = new HashMap<String,String>();
		params.put("merno", getMerno());
		params.put("time", getTime());
		params.put("content", getContent());
		params.put("signature", getSignature());
		logger.info("【请求参数:{}】",JSONObject.toJSONString(params));
		String result = HttpSender.getHttpSender().connection(url, "POST", params);
		JSONObject ret = JSONObject.parseObject(result);
		logger.info("【代付结果参数:{}】",result);
		if (!StringUtils.isEmpty(result) && result.contains("status=success")){
			return ReturnMsgEnum.success.setMsg("提交成功,等待出款");
		}
		return ReturnMsgEnum.error.setMsg("代付失败【"+ret.getString("remark")+"】");
	}

	public String getMerno() {
		return merno;
	}

	public void setMerno(String merno) {
		this.merno = merno;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}
