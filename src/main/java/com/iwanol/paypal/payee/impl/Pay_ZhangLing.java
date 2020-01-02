package com.iwanol.paypal.payee.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.until.AlgorithmUtil;
import com.iwanol.paypal.until.CertificateUtils;
import com.iwanol.paypal.until.DateUtil;
import com.iwanol.paypal.until.HttpSender;
import com.iwanol.paypal.until.SignUtils;

public class Pay_ZhangLing extends Pay_Payee{

	@Override
	public void setRequestParam(Payee payee) {
		// TODO Auto-generated method stub
		
	}
	

	@Override
	public ReturnMsgEnum pay(Payee payee) {
		// TODO Auto-generated method stub
		String url = "https://jsh.sumpay.cn/interface/deputy/zeroPay";
		//String url = "http://124.160.28.138:58082/interface/deputy/zeroPay";
		try{
			if (getBankMark().equals("SPDB") || getBankMark().equals("GDB") || getBankMark().equals("CITIC"))
				return ReturnMsgEnum.error.setMsg("代付失败【暂不支持"+getBankMark()+"该银行代付】");
			Map<String,String> map = new HashMap<String,String>();
			String aesKey = AlgorithmUtil.getKey();
			map.put("service", "service.deputy.zeroPay");
			map.put("timestamp", DateUtil.getDate("yyyyMMddHHmmss"));
			map.put("mchntId",payee.getAccount());
			map.put("transAmt",String.format("%.0f", getAmount().doubleValue() * 100));
			map.put("orderNo", getPayeeNumber());
			map.put("accountBank",AlgorithmUtil.encode(aesKey,getBank(getBankMark())));
			map.put("accountName", AlgorithmUtil.encode(aesKey, getRealName()));
			map.put("accountNo", AlgorithmUtil.encode(aesKey, getAccount()));
			map.put("aesKey",CertificateUtils.encryptByPublicKey(aesKey, payee.getPublicKey()));
			map.put("sign", SignUtils.generateSign(map, payee.getPrivateKey(), payee.getSignKey()));
			map.put("signType", "CERT");
			String param = JSONObject.toJSONString(map);
			logger.info("【请求参数:{}】",param);
			String result = HttpSender.getHttpSender().doHttpAndHttps(url, param);
			JSONObject ret = JSONObject.parseObject(result);
			logger.info("【代付结果参数:{}】",result);
			if (!StringUtils.isEmpty(result) && ret.getString("code").equals("10000")){
				String deputyJson = ret.getString("deputyJson");
				JSONObject json = JSONObject.parseObject(deputyJson);
				if (json.getString("status").equals("1") || json.getString("status").equals("2") || json.getString("status").equals("8")){
					return ReturnMsgEnum.success.setMsg("提交成功【"+json.getString("returnMsg")+"】");
				}else{
					return ReturnMsgEnum.error.setMsg("代付失败【"+json.getString("returnMsg")+"】");
				}
			}
			return ReturnMsgEnum.error.setMsg("代付失败【"+ret.getString("msg")+"】");
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	private String getBank(String mark){
		if (mark.equals("COMM")){return "BCM";}
		if (mark.equals("PINGAN")){return "PAB";}
		if (mark.equals("BOB")){return "BCCB";}
		if (mark.equals("BOSC")){return "BOS";}
		if (mark.equals("BOSC")){return "BOS";}
		return mark;
	}

}
