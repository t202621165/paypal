package com.iwanol.paypal.domain;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * 账户明细
 * @author Administrator
 * 2018年6月1日 上午10:11:46
 */
@Entity
public class AccountDetails {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private BigDecimal amount; // 收入/支出 金额
	
	private BigDecimal cost; // 手续费
	
	@Column(nullable = false)
	private BigDecimal overMoney; // 余额
	
	@Column(nullable = false)
	private String recordNumber; // 订单号、结算流水号
	
	@Column(nullable = false)
	private Boolean type; // true/1: 收入 false/0: 支出
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date recordDate; // 记录时间
	
	@Column(nullable = false)
	private String details; // 详情：订单、提现、退款
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Merchant merchant;
	
	@Transient
	private Long merchantId;
	
	public AccountDetails() {
		
	}
	
	public AccountDetails(BigDecimal amount, BigDecimal cost, BigDecimal overMoney, 
			String recordNumber, Boolean type, String details, Long merchantId) {
		this.amount = amount;
		this.cost = cost;
		this.overMoney = overMoney;
		this.recordNumber = recordNumber;
		this.type = type;
		this.details = details;
		this.merchant = new Merchant(merchantId);
		this.recordDate = new Date();
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getCost() {
		return cost;
	}

	public void setCost(BigDecimal cost) {
		this.cost = cost;
	}

	public BigDecimal getOverMoney() {
		return overMoney;
	}

	public void setOverMoney(BigDecimal overMoney) {
		this.overMoney = overMoney;
	}

	public String getRecordNumber() {
		return recordNumber;
	}

	public void setRecordNumber(String recordNumber) {
		this.recordNumber = recordNumber;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public Date getRecordDate() {
		return recordDate;
	}

	public void setRecordDate(Date recordDate) {
		this.recordDate = recordDate;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	@Override
	public String toString() {
		return "AccountDetails [id=" + id + ", amount=" + amount + ", cost=" + cost + ", overMoney=" + overMoney
				+ ", recordNumber=" + recordNumber + ", type=" + type + ", recordDate=" + recordDate + ", details="
				+ details + ", merchantId=" + merchantId + "]";
	}

}
