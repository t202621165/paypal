package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 充扣款实体
 * @author iwano
 *
 */
@Entity
public class CongKou implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Temporal(TemporalType.TIMESTAMP)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
	private Date date = new Date();
	
	private String serialNumber; //序列号
	
	private Integer type = 0; // 0:充款  1：扣款
	
	private BigDecimal amount; //业务金额
	
	private String merId; //充扣款商户ID;
	
	private String merName; //充扣款商户名;
	
	private String realName; //充扣款真实姓名
	
	private String bankName; //充扣款银行名称
	
	private String bankNumber; //充扣款卡号
	
	private Integer state; //状态 1 成功  -1 失败
	
	private String discription; //业务描述
	
	public CongKou(){};

	public CongKou(Long id, Date date, Integer type, BigDecimal amount, String merId, String merName, String realName,
			String bankName, String bankNumber, Integer state, String discription) {
		super();
		this.id = id;
		this.date = date;
		this.type = type;
		this.amount = amount;
		this.merId = merId;
		this.merName = merName;
		this.realName = realName;
		this.bankName = bankName;
		this.bankNumber = bankNumber;
		this.state = state;
		this.discription = discription;
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		this.merId = merId;
	}

	public String getMerName() {
		return merName;
	}

	public void setMerName(String merName) {
		this.merName = merName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}
	
}
