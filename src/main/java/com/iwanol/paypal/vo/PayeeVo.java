package com.iwanol.paypal.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.iwanol.paypal.domain.Bank;

public class PayeeVo implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long payeeId;
	
	private Long bankId;
	
	private BigDecimal amount;
	
	private String phoneNumber;
	
	private String smsCode;
	
	private Bank bank;

	public Long getPayeeId() {
		return payeeId;
	}

	public void setPayeeId(Long payeeId) {
		this.payeeId = payeeId;
	}

	public Long getBankId() {
		return bankId;
	}

	public void setBankId(Long bankId) {
		this.bankId = bankId;
	}

	public BigDecimal getAmount() {
		return amount == null?new BigDecimal(0.00):amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getSmsCode() {
		return smsCode;
	}

	public void setSmsCode(String smsCode) {
		this.smsCode = smsCode;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	@Override
	public String toString() {
		return "PayeeVo [payeeId=" + payeeId + ", bankId=" + bankId + ", amount=" + amount + ", phoneNumber="
				+ phoneNumber + ", smsCode=" + smsCode + "]";
	}
	
}