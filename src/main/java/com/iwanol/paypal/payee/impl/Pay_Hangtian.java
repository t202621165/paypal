package com.iwanol.paypal.payee.impl;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;
import com.iwanol.paypal.third.hangtian.ContextUtil;
import com.iwanol.paypal.third.hangtian.HttpUtil;
import com.iwanol.paypal.third.hangtian.MySecurity;
import com.iwanol.paypal.third.hangtian.PPSecurity;
import com.iwanol.paypal.until.DateUtil;
import com.iwanol.paypal.until.XmlUtil;

import cn.hutool.core.lang.UUID;

public class Pay_Hangtian extends Pay_Payee{
	
	private String opcode;
	
	private String productid;
	
	private String merorderno;
	
	private String merorderdate;
	
	private String meruserid;
	
	private String tranamt;
	
	private String orderdesc;
	
	private String bankcard;
	
	private String cardusername;
	
	private String notifyurl;

	@Override
	public void setRequestParam(Payee payee) {
		// TODO Auto-generated method stub
		this.opcode = "MA2001";
		this.productid = "LCDF_ht0202";
		this.merorderno = getPayeeNumber();
		this.merorderdate = DateUtil.getDate(new Date(),"yyyyMMdd");
		this.meruserid = UUID.fastUUID().toString(true);
		this.tranamt = String.format("%.0f", getAmount().doubleValue() * 100);
		this.orderdesc = "settlement";
		this.bankcard = getAccount();
		this.cardusername = getRealName();
		this.notifyurl = "http://api.iwanol.com/funds/hangtian/notify";
	}

	@Override
	public ReturnMsgEnum pay(Payee payee) {
		// TODO Auto-generated method stub
		try{
			PPSecurity ppSecurity = MySecurity.getPPSecurity();
			String url = "https://www.paypalm.cn/cloud-moneymgr/xml";
			Map<String,String> req = new HashMap<String, String>();
			req.put("opcode", getOpcode());
			req.put("productid", getProductid());
			req.put("merorderno", getMerorderno());
			req.put("merorderdate", getMerorderdate());
			req.put("meruserid", getMeruserid());
			req.put("tranamt", getTranamt());
			req.put("orderdesc", getOrderdesc());
			req.put("notifyurl", getNotifyurl());
			byte[] bankcardB = ppSecurity.PPFEncryptSensitiveData(getBankcard().getBytes("gbk"));
			req.put("bankcard", new String(bankcardB,"gbk"));
			byte[] cardusernameB = ppSecurity.PPFEncryptSensitiveData(getCardusername().getBytes("gbk"));
			req.put("cardusername", new String(cardusernameB,"gbk"));
			String xml = XmlUtil.map2Xml(req);
			logger.info("航天代付请求xml:{}",xml);
			byte[] encodeDataByte = ppSecurity.PPFCommDataEncode(xml.getBytes("gbk"));
			xml = new String(encodeDataByte,"gbk");
			xml = URLEncoder.encode(xml,"gbk");
			String merid = ContextUtil.getValue("merid");
			String data = "merid="+merid+"&transdata="+xml;
			logger.info("最终请求数据:{}",data);
			//发送请求
			String res = HttpUtil.request(url,data,true);
			//加密响应数据
			byte[] resB = ppSecurity.PPFCommDataDecode(res.getBytes("gbk"));
			res = new String(resB,"gbk");
			logger.info("响应数据明文:{}",res);
			Map<String,String> resp = XmlUtil.toMap(resB, "gbk");
			logger.info("航天代付响应码:{}",resp);
			if (resp.get("rspcode").equals("000000")){
				return ReturnMsgEnum.success.setMsg("航天代付受理成功,等待航天出款");
			}
			return ReturnMsgEnum.error.setMsg("代付失败【"+resp.get("rspdesc")+"】");
		}catch (Exception e) {
			// TODO: handle exception
			return ReturnMsgEnum.error.setMsg("代付失败【"+e.getMessage()+"】");
		}
	}

	public String getOpcode() {
		return opcode;
	}

	public void setOpcode(String opcode) {
		this.opcode = opcode;
	}

	public String getProductid() {
		return productid;
	}

	public void setProductid(String productid) {
		this.productid = productid;
	}

	public String getMerorderno() {
		return merorderno;
	}

	public void setMerorderno(String merorderno) {
		this.merorderno = merorderno;
	}

	public String getMerorderdate() {
		return merorderdate;
	}

	public void setMerorderdate(String merorderdate) {
		this.merorderdate = merorderdate;
	}

	public String getMeruserid() {
		return meruserid;
	}

	public void setMeruserid(String meruserid) {
		this.meruserid = meruserid;
	}

	public String getTranamt() {
		return tranamt;
	}

	public void setTranamt(String tranamt) {
		this.tranamt = tranamt;
	}

	public String getOrderdesc() {
		return orderdesc;
	}

	public void setOrderdesc(String orderdesc) {
		this.orderdesc = orderdesc;
	}

	public String getBankcard() {
		return bankcard;
	}

	public void setBankcard(String bankcard) {
		this.bankcard = bankcard;
	}

	public String getCardusername() {
		return cardusername;
	}

	public void setCardusername(String cardusername) {
		this.cardusername = cardusername;
	}

	public String getNotifyurl() {
		return notifyurl;
	}

	public void setNotifyurl(String notifyurl) {
		this.notifyurl = notifyurl;
	}
	
}
