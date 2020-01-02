package com.iwanol.paypal.payee.impl;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.third.zhangling.AesEncryptUtil;
import com.iwanol.paypal.third.zhangling.HttpSendUtil;
import com.iwanol.paypal.third.zhangling.SignUtil;

public class Pay_ZhangLing_new extends Pay_Payee{

	@Override
	public void setRequestParam(Payee payee) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ReturnMsgEnum pay(Payee payee) {
		// TODO Auto-generated method stub
		String url = "http://api.palmf.cn/api/tx/apply";
		String privateKey = payee.getPrivateKey();
		String key = payee.getSignKey().substring(0,16);
		try{
			if (getBankMark().equals("BOB") || getBankMark().equals("BOSC"))
				return ReturnMsgEnum.error.setMsg("代付失败【暂不支持"+getBankMark()+"该银行代付】");
			Map<String,Object> map=new HashMap<String, Object>();
	        map.put("service","service.api.tx.apply");//接口类型
	        map.put("mchntId",payee.getAccount());//商户id  由平台分配
	        map.put("payeeName",AesEncryptUtil.aesEncrypt(getRealName(), key));//收款人姓名  需要进行aes加密
	        map.put("payeeBankcardNo",AesEncryptUtil.aesEncrypt(getAccount(), key));//银行卡号  需要进行aes加密
	        map.put("payeeBankName",AesEncryptUtil.aesEncrypt(getBank(getBankMark()), key));//银行编号  结合文档查看对应的值  需要进行aes加密
	        map.put("txAmount",String.format("%.0f", getAmount().doubleValue() * 100));//代付金额  大于1元
	        map.put("txMerNo",getPayeeNumber());//商户代付订单号  商户自己生成
	        map.put("txAccountType","0");//账户类型  0代表对私  1代表对公
	        map.put("payeeBankCode",AesEncryptUtil.aesEncrypt(getBankCode(getBank(getBankMark())), key));
	        map.put("version", "v1.2");
	        String sign=SignUtil.doEncrypt(map,privateKey);//rsa签名
	        map.put("signature", sign);//签名值
	        String jsonObject= JSONObject.toJSONString(map);//最终http post请求参数 
	        logger.info("请求参数={}",jsonObject);
	        String result= HttpSendUtil.doHttpAndHttps(url, jsonObject);
	        logger.info("请求返回参数:{}",result);
	        JSONObject resp = JSONObject.parseObject(result);
	        if (resp.getString("code").equals("10000")){
	        	return ReturnMsgEnum.success.setMsg("提交成功【"+resp.getString("msg")+"】");
	        }else{
	        	return ReturnMsgEnum.error.setMsg("代付失败【"+resp.getString("msg")+"】");
	        }
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return ReturnMsgEnum.error.setMsg("代付失败【"+e.getMessage()+"】");
		}
	}

	private String getBank(String mark){
		if (mark.equals("COMM")){return "BCM";}
		if (mark.equals("PINGAN")){return "PAB";}
		if (mark.equals("CITIC")){return "CNCB";}
		return mark;
	}
	
	private String getBankCode(String  payeeBankName){
		if (payeeBankName.equals("ICBC")) { return "102100099996"; }
		if (payeeBankName.equals("ABC")) { return "103100000026"; }
		if (payeeBankName.equals("BOC")) { return "104100000004"; }
		if (payeeBankName.equals("CCB")) { return "105100000017"; }
		if (payeeBankName.equals("BCM")) { return "301290000007"; }
		if (payeeBankName.equals("CMB")) { return "308584000013"; }
		if (payeeBankName.equals("GDB")) { return "306581000003"; }
		if (payeeBankName.equals("CNCB")) { return "302100011000"; }
		if (payeeBankName.equals("CMBC")) { return "305100000013"; }
		if (payeeBankName.equals("CEB")) { return "303100000006"; }
		if (payeeBankName.equals("PAB")) { return "307584007998"; }
		if (payeeBankName.equals("SPDB")) { return "310290000013"; }
		if (payeeBankName.equals("PSBC")) { return "403100000004"; }
		if (payeeBankName.equals("HXB")) { return "304100040000"; }
		if (payeeBankName.equals("CIB")) { return "309391000011"; }
		return "";
	}
	
}
