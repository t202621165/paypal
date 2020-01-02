package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.SettlementTypeEnum;
import com.iwanol.paypal.until.DateUtil;

/**
 * 结算表实体
 * @author leo
 *
 */
@Entity
public class SettleMent implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date applyDate; //业务申请时间
	
	@Column(length=25)
	private String serialNumber; //业务流水号  提现审核：(check)C_+年+月+日+时+分+秒+毫秒+4位随机数+服务器标识; 等待付款：(pay)P_+年+月+日+时+分+秒+毫秒+4位随机数+服务器标识
	
	@Column(nullable=false)
	private BigDecimal amount; // 结算金额
	
	@Column(nullable=false)
	private BigDecimal cost; // 手续费
	
	private Integer state; //-2拒绝出款 -1审核失败 0 等待审核 1 出款成功 2等待出款  3 商户代付 -3商户代付失败  4商户代付成功  -6代付审核中  -7代付审核驳回
	
	private Integer type; //业务类型 falset/0 普通业务结算   true/1资金自提
	
	private String discription; //业务描述
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date complateDate; //业务完成时间
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Merchant merchant; //出款商户
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Bank bank; //收款银行账户
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private SettleMentReply settleMentReply;//出款批复
	
	@Column(nullable = false)
	private Boolean replyState = false;
	
	@Transient
	private Long merchantId;
	
	@Transient
	private Long bId;
	
	@Transient
	private String merAccount;
	
	@Transient
	private String securityKey;
	
	private String bankName; //开户行
	
	private String realName; //银行卡真实姓名
	
	private String bankNumber; //银行卡账号
	
	private String bankMark; //银行卡标识
	
	/**
	 * 清除实体关系 做级联删除
	 */
	public void clearSettleMent(){
		this.settleMentReply.getSettleMents().remove(this);
	}

	public SettleMent() {
		super();
	}
	
	public SettleMent(Long id, String serialNumber, BigDecimal amount, String bankNumber) {
		this.id = id;
		this.amount = amount;
		this.serialNumber = serialNumber;
		this.bankNumber = bankNumber;
	}
	
	public SettleMent(Long id, String serialNumber, BigDecimal amount, String bankNumber,String realName,String bankMark) {
		this.id = id;
		this.amount = amount;
		this.serialNumber = serialNumber;
		this.bankNumber = bankNumber;
		this.realName = realName;
		this.bankMark = bankMark;
	}
	
	public SettleMent(Long id, String serialNumber, BigDecimal amount,String bankName, String bankNumber,String realName,String bankMark,String merAccount,String securityKey) {
		this.id = id;
		this.amount = amount;
		this.serialNumber = serialNumber;
		this.bankName = bankName;
		this.bankNumber = bankNumber;
		this.realName = realName;
		this.bankMark = bankMark;
		this.merAccount = merAccount;
		this.securityKey = securityKey;
	}
	
	public SettleMent(Long id) {
		this.id = id;
	}
	
	/**
	 * 创建等待付款(普通结算)实例
	 * @param amount
	 * @param cost
	 * @param merchantId
	 * @param bankId
	 * @param replyId
	 */
	public SettleMent(BigDecimal amount, BigDecimal cost, 
			Long merchantId, Long bankId, Integer settlementType) {
		this.amount = amount;
		this.cost = cost;
		this.merchant = new Merchant(merchantId);
		this.bank = new Bank(bankId);
		this.state = 2;
		this.type = 0;
		this.applyDate = new Date();
		this.serialNumber = DateUtil.getTimeStampNumber("P_", 4);
		this.discription = SettlementTypeEnum.getDescByType(settlementType);
	}
	
	public SettleMent(Long id,Date applyDate,String serialNumber,BigDecimal amount,
			BigDecimal cost,Integer state,Boolean replyState,Integer type,String discription,Date completeDate,
			Long merchantId,String bankName,String realName,String bankNumber) {
		this.id = id;
		this.applyDate = applyDate;
		this.serialNumber = serialNumber;
		this.amount = amount;
		this.cost =  cost;
		this.state = state;
		this.replyState = replyState;
		this.type = type;
		this.discription = discription;
		this.complateDate = completeDate;
		this.merchantId = merchantId;
		this.bankName = bankName;
		this.realName = realName;
		this.bankNumber = bankNumber;
	}
	
	public SettleMent(String payeeNumber, BigDecimal amount, String discription) {
		this.serialNumber = payeeNumber;
		this.amount = amount;
		this.discription = discription;
		this.type = 1;
		this.cost = new BigDecimal(0);
		this.applyDate = new Date();
		this.state = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getApplyDate() {
		return applyDate==null?null:DateUtil.getDate(applyDate, "yyyy-MM-dd HH:mm:ss");
	}

	public void setApplyDate(Date applyDate) {
		this.applyDate = applyDate;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
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

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDiscription() {
		return discription;
	}

	public void setDiscription(String discription) {
		this.discription = discription;
	}

	public String getComplateDate() {
		return complateDate==null?null:DateUtil.getDate(complateDate, "yyyy-MM-dd HH:mm:ss");
	}

	public void setComplateDate(Date complateDate) {
		this.complateDate = complateDate;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public SettleMentReply getSettleMentReply() {
		return settleMentReply;
	}

	public void setSettleMentReply(SettleMentReply settleMentReply) {
		this.settleMentReply = settleMentReply;
	}

	public Boolean getReplyState() {
		return replyState;
	}

	public void setReplyState(Boolean replyState) {
		this.replyState = replyState;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public Long getbId() {
		return bId;
	}

	public void setbId(Long bId) {
		this.bId = bId;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	public String getBankMark() {
		return bankMark;
	}

	public void setBankMark(String bankMark) {
		this.bankMark = bankMark;
	}

	public String getMerAccount() {
		return merAccount;
	}

	public void setMerAccount(String merAccount) {
		this.merAccount = merAccount;
	}
	

	public String getSecurityKey() {
		return securityKey;
	}

	public void setSecurityKey(String securityKey) {
		this.securityKey = securityKey;
	}

	@Override
	public String toString() {
		return "SettleMent [id=" + id + ", applyDate=" + applyDate + ", serialNumber=" + serialNumber + ", amount="
				+ amount + ", cost=" + cost + ", state=" + state + ", type=" + type + ", discription=" + discription
				+ ", complateDate=" + complateDate + ", merchantId=" + merchantId + ", bId=" + bId + ", bankName="
				+ bankName + ", realName=" + realName + ", bankNumber=" + bankNumber + "]";
	}

}
