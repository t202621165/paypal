package com.iwanol.paypal.payee;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.domain.Payee;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.until.DateUtil;
import com.iwanol.paypal.vo.PayeeVo;

public abstract class Pay_Payee {
	
	protected Logger logger = LoggerFactory.getLogger(this.getClass());
	
	@JsonIgnore
	private String payeeNumber;
	
	@JsonIgnore
	private BigDecimal amount; // 付款金额
	
	@JsonIgnore
	private String realName; //开户名称
	
	@JsonIgnore
	private String bankMark; //开户行代码
	
	@JsonIgnore
	private String account; // 收款账号
	
	@JsonIgnore
	protected SettleMent settleMent;
	
	/**
	 * 设置代付基本参数
	 * @param payee
	 * @param vo
	 */
	public void setBaseParam(Payee payee, PayeeVo vo) {
		this.payeeNumber = DateUtil.getTimeStampNumber("DS_", 5);
		this.amount = vo.getAmount();
		this.realName = vo.getBank().getRealName();
		this.bankMark = vo.getBank().getBankMark();
		this.account = vo.getBank().getBankNumber();
		settleMent = new SettleMent(payeeNumber, amount, "代付("+payee.getName()+")");
		settleMent.setBank(vo.getBank());
		
		setRequestParam(payee);
	}
	
	/**
	 * 设置代付基本参数
	 * @param payee
	 * @param vo
	 */
	public void setBaseParam(Payee payee, SettleMent settleMent) {
		this.payeeNumber = settleMent.getSerialNumber();
		this.amount = settleMent.getAmount();
		this.account = settleMent.getBankNumber();
		this.realName = settleMent.getRealName();
		this.bankMark = settleMent.getBankMark();
		setRequestParam(payee);
	}
	
	/**设置请求参数*/
	public abstract void setRequestParam(Payee payee); // 子类实现
	
	/**发起付款*/
	public abstract ReturnMsgEnum pay(Payee payee); // 子类实现

	public String getPayeeNumber() {
		return payeeNumber;
	}

	public void setPayeeNumber(String payeeNumber) {
		this.payeeNumber = payeeNumber;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankMark() {
		return bankMark;
	}

	public void setBankMark(String bankMark) {
		this.bankMark = bankMark;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public SettleMent getSettleMent() {
		return settleMent;
	}

	public void setSettleMent(SettleMent settleMent) {
		this.settleMent = settleMent;
	}
	
}
