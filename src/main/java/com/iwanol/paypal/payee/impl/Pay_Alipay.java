package com.iwanol.paypal.payee.impl;

import java.math.BigDecimal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.payee.Pay_Payee;

/**
 * 支付宝代付
 * @author Administrator
 * 2018年6月28日 下午5:10:08
 */
public class Pay_Alipay extends Pay_Payee {
	
	@JsonIgnore
	private final static String FORMAT = "json";
	@JsonIgnore
	private final static String CHARSET = "UTF-8";
	@JsonIgnore
	private final static String SIGN_TYPE = "RSA2";
	@JsonIgnore
	private final static String URL = "https://openapi.alipay.com/gateway.do";
	
	private String out_biz_no;
	
	private String payee_account;
	
	private BigDecimal amount;
	
	private String remark;
	
	private String payee_type;
	
	/**
	 * 支付宝代付-设置请求参数并校验参数
	 * @param out_biz_no
	 * @param payee_account
	 * @param amount
	 * @return
	 */
	@Override
	public void setRequestParam(Payee payee) {
		this.out_biz_no = getPayeeNumber();
		this.payee_account = getAccount();
		this.amount = super.getAmount();
		this.payee_type = "ALIPAY_LOGONID";
		this.remark = payee.getRemark();
	}
	
	/**
	 * 支付宝代付-发起代付请求，同步返回处理结果
	 * @return
	 */
	@Override
	public ReturnMsgEnum pay(Payee payee) {
		try {
			AlipayClient alipay = new DefaultAlipayClient
					(URL,payee.getAccount(),payee.getPrivateKey(),
							FORMAT,CHARSET,payee.getPublicKey(),SIGN_TYPE);
			
			AlipayFundTransToaccountTransferRequest request = 
					new AlipayFundTransToaccountTransferRequest();
			
			request.setBizContent(this.toString());
			AlipayFundTransToaccountTransferResponse response = alipay.execute(request);
			
			if (response.isSuccess()) {
				ReturnMsgEnum.success.setMsg("支付宝代付成功！向账号："+getAccount()+"付款 "+super.getAmount()+"元！");
			}else{
				ReturnMsgEnum.error.setMsg(response.getSubCode()+"："+response.getSubMsg());
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			ReturnMsgEnum.error.setMsg(e.getErrMsg());
		}
		return ReturnMsgEnum.returnInstance();
	}

	public String getOut_biz_no() {
		return out_biz_no;
	}

	public void setOut_biz_no(String out_biz_no) {
		this.out_biz_no = out_biz_no;
	}

	public String getPayee_account() {
		return payee_account;
	}

	public void setPayee_account(String payee_account) {
		this.payee_account = payee_account;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getPayee_type() {
		return payee_type;
	}

	public void setPayee_type(String payee_type) {
		this.payee_type = payee_type;
	}

	@Override
	public String toString() {
		return "{\"out_biz_no\":\"" + out_biz_no + "\", \"payee_account\":\"" 
				+ payee_account + "\", \"amount\":\"" + String.format("%.2f", amount)
				+ "\", \"remark\":\"" + remark + "\", \"payee_type\":\"" + payee_type + "\"}";
	}

}