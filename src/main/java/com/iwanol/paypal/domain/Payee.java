package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.iwanol.paypal.base.enums.ReturnMsgEnum;

@Entity
public class Payee implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private Boolean type; // 代付方式 true：支付宝 false： 银联
	
	@Column(nullable = false)
	private Boolean isDefault = Boolean.FALSE; // 商户资金代付默认付款通道
	
	@Column(nullable = false, unique = true)
	private String mark; // 标识
	
	private String name;
	
	private String account;
	
	private String signKey;
	
	@Column(columnDefinition="text")
	private String publicKey;
	
	@Column(columnDefinition="text")
	private String privateKey;
	
	@Column(nullable = false)
	private BigDecimal minAmount;
	
	@Column(nullable = false)
	private BigDecimal maxAmount;
	
	private String remark; // 付款备注
	
	public ReturnMsgEnum amountValidate(BigDecimal amount) {
		if (amount.compareTo(minAmount) < 0)
			return ReturnMsgEnum.error.setMsg("允许最小付款金额："+minAmount+"元！");
		
		if (amount.compareTo(maxAmount) > 0)
			return ReturnMsgEnum.error.setMsg("允许最大付款金额："+maxAmount+"元！");
		
		return ReturnMsgEnum.success.setMsg("校验通过！");
	}
	
	public void init() {
		if (this.minAmount == null)
			this.minAmount = new BigDecimal(0);
		if (this.maxAmount == null)
			this.maxAmount = new BigDecimal(0);
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public Boolean getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(Boolean isDefault) {
		this.isDefault = isDefault;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getSignKey() {
		return signKey;
	}

	public void setSignKey(String signKey) {
		this.signKey = signKey;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}

	public String getPrivateKey() {
		return privateKey;
	}

	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "Payee [id=" + id + ", type=" + type + ", mark=" + mark + ", name=" + name + ", account=" + account
				+ ", signKey=" + signKey + ", publicKey=" + publicKey + ", privateKey=" + privateKey + ", minAmount="
				+ minAmount + ", maxAmount=" + maxAmount + ", remark=" + remark + "]";
	}

}
