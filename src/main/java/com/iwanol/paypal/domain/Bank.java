package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Transient;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.BanksEnum;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.until.VerifyUtil;

/**
 * 商户银行卡实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "bankWithMerchantAndOrder",
		attributeNodes = {@NamedAttributeNode(value = "merchant", subgraph = "merchantWithOrder")},
		subgraphs = {
			@NamedSubgraph(
				name = "merchantWithOrder", 
				attributeNodes = {
					@NamedAttributeNode(value = "platformOrders")
				}
			)
		}
	)
})
public class Bank implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	private String bankName; //开户行

	private String bankMark; // 银行标识
	
	private String realName; // 银行卡绑定的身份证姓名
	
	@Column(length = 18)
	private String idNumber; // 银行卡绑定的身份证号
	
	private String bankBranch; //开户支行
	
	private String bankNumber; // 银行卡账号、支付宝账号

	@Column(nullable=false)
	private BigDecimal overMoney; //账户余额

	@Column(nullable=false)
	private BigDecimal settleMoney; //可结算余额   t1只能结算当天以前的订单利润

	@Column(nullable=false)
	private BigDecimal allDeposit; //总存入金额

	@Column(nullable=false)
	private BigDecimal allPay; //总支出金额
	
	@Column(nullable=false)
	private Boolean bankType; //银行卡类型 true/1 主卡 false/0 副卡
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(updatable = false)
	private Merchant merchant;
	
	@Column(nullable = false)
	private Boolean payeeState; // 是否允许代付
	
	@Transient
	private Long merId;
	
	@Transient
	private BigDecimal blockMoney = new BigDecimal(0.00);
	
	@Transient
	private boolean checked;

	public Bank() {
		super();
	}
	
	public Bank(Long id) {
		this.id = id;
	}
	
	public Bank(Long merId, double overMoney) {
		this.merId = merId;
		this.merchant = new Merchant(merId);
		this.overMoney = new BigDecimal(overMoney).
				setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public Bank(Long id,String bankName,String bankNumber,String realName,
			String bankBranch,Long merchantId,BigDecimal overMoney,double blockMoney) {
		this.id = id;
		this.bankName = bankName;
		this.bankNumber = bankNumber;
		this.realName = realName;
		this.bankBranch = bankBranch;
		this.merId = merchantId;
		this.overMoney = overMoney == null?new BigDecimal(0.00):overMoney;
		this.blockMoney = new BigDecimal(blockMoney);
		this.blockMoney = this.blockMoney.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.settleMoney = this.overMoney.subtract(this.blockMoney);
	}
	
	public ReturnMsgEnum validate() {
		if (!BanksEnum.isPresent(bankMark))
			return ReturnMsgEnum.error.setMsg("当前银行不可用！");
		
		if (StringUtils.isEmpty(bankNumber))
			return ReturnMsgEnum.error.setMsg("银行账号不能为空！");
		
		if (bankBranch ==null || "".equals(bankBranch))
			return ReturnMsgEnum.error.setMsg("开户地区不能为空！");
		
		if (realName ==null || "".equals(realName))
			return ReturnMsgEnum.error.setMsg("账户名不能为空！");
		
		if (!VerifyUtil.isIdNumber(idNumber))
			return ReturnMsgEnum.error.setMsg("身份证号码格式错误！");
		
		this.bankType = Boolean.FALSE;
		this.payeeState = Boolean.FALSE;
		this.bankName = BanksEnum.getBankNameByMark(bankMark);
		this.overMoney = new BigDecimal(0);
		this.settleMoney = new BigDecimal(0);
		this.allDeposit = new BigDecimal(0);
		this.allPay = new BigDecimal(0);
		return ReturnMsgEnum.success.setMsg("校验通过");
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getBankBranch() {
		return bankBranch;
	}

	public void setBankBranch(String bankBranch) {
		this.bankBranch = bankBranch;
	}

	public String getBankNumber() {
		return bankNumber;
	}

	public void setBankNumber(String bankNumber) {
		this.bankNumber = bankNumber;
	}

	public BigDecimal getOverMoney() {
		return overMoney;
	}

	public void setOverMoney(BigDecimal overMoney) {
		this.overMoney = overMoney;
	}

	public BigDecimal getAllDeposit() {
		return allDeposit;
	}

	public void setAllDeposit(BigDecimal allDeposit) {
		this.allDeposit = allDeposit;
	}

	public BigDecimal getAllPay() {
		return allPay;
	}

	public void setAllPay(BigDecimal allPay) {
		this.allPay = allPay;
	}

	public Boolean getBankType() {
		return bankType;
	}

	public void setBankType(Boolean bankType) {
		this.bankType = bankType;
	}
	@JsonIgnore
	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public BigDecimal getSettleMoney() {
		return settleMoney;
	}

	public void setSettleMoney(BigDecimal settleMoney) {
		this.settleMoney = settleMoney;
	}

	public Boolean getPayeeState() {
		return payeeState;
	}

	public void setPayeeState(Boolean payeeState) {
		this.payeeState = payeeState;
	}

	public Long getMerId() {
		return merId;
	}

	public void setMerId(Long merId) {
		this.merId = merId;
		this.merchant = new Merchant(merId);
	}

	public String getBankMark() {
		return bankMark;
	}

	public void setBankMark(String bankMark) {
		this.bankMark = bankMark;
		this.bankName = BanksEnum.getBankNameByMark(bankMark);
	}

	public BigDecimal getBlockMoney() {
		return blockMoney;
	}

	public void setBlockMoney(BigDecimal blockMoney) {
		this.blockMoney = blockMoney;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public String toString() {
		return "Bank [id=" + id + ", bankName=" + bankName + ", realName=" + realName + ", idNumber=" + idNumber
				+ ", bankBranch=" + bankBranch + ", bankNumber=" + bankNumber + ", overMoney=" + overMoney
				+ ", settleMoney=" + settleMoney + ", allDeposit=" + allDeposit + ", allPay=" + allPay + ", bankType="
				+ bankType + ", payeeState=" + payeeState + "]";
	}

}
