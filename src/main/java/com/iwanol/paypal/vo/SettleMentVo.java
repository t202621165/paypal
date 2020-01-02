package com.iwanol.paypal.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.domain.SettleMentReply;

public class SettleMentVo extends BaseVo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public final static StringBuffer SUM_BUF =  new StringBuffer("SELECT new com.iwanol.paypal.vo.SettleMentVo("
			+ "IFNULL(SUM(temp.amount),0)+0.00,IFNULL(SUM(temp.cost),0)+0.00,count(temp.id))");

	public final static StringBuffer SELECT_BUF = new StringBuffer("SELECT new com.iwanol.paypal.domain.SettleMent("
			+ "temp.id,temp.applyDate,temp.serialNumber,temp.amount,temp.cost,temp.state,temp.replyState,temp.type,"
			+ "temp.discription,temp.complateDate,"
			+ "m.id,b.bankName,b.realName,b.bankNumber)");
	
	public final static StringBuffer FROM_BUF = new StringBuffer(" FROM SettleMent temp "
			+ "left join temp.merchant m left join temp.bank b "
			+ "WHERE 1 = 1");
	
	//----------------------------------------------商户代付-----------------------------------------------------------//
	public final static StringBuffer SELECT_BUF_DF = new StringBuffer("SELECT new com.iwanol.paypal.domain.SettleMent("
			+ "temp.id,temp.applyDate,temp.serialNumber,temp.amount,temp.cost,temp.state,temp.replyState,temp.type,"
			+ "temp.discription,temp.complateDate,"
			+ "m.id,temp.bankName,temp.realName,temp.bankNumber)");
	
	public final static StringBuffer FROM_BUF_DF = new StringBuffer(" FROM SettleMent temp "
			+ "left join temp.merchant m "
			+ "WHERE 1 = 1");
	
	@JsonIgnore
	public StringBuffer where_buf = new StringBuffer("");
	
	@JsonIgnore
	private String serialNumber;
	
	@JsonIgnore
	private BigDecimal startAmount;
	
	@JsonIgnore
	private BigDecimal endAmount;
	
	@JsonIgnore
	private Long merchantId;
	
	@JsonIgnore
	private Long replyId;
	
	@JsonIgnore
	private Integer state;
	
	@JsonIgnore
	private Integer type;
	
	@JsonIgnore
	private Boolean replyState;
	
	private BigDecimal totalAmount;
	
	private BigDecimal totalCost;
	
	private Long totalCount;
	
	public SettleMentVo() {
		super.SELECT_BUF = SELECT_BUF;
		super.FROM_BUF = FROM_BUF;
		super.SUM_BUF = SUM_BUF;
	}
	
	public SettleMentVo(double totalAmount, double totalCost,Long totalCount) {
		this.totalAmount = new BigDecimal(totalAmount)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.totalCost = new BigDecimal(totalCost)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.totalCount = totalCount;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		if (serialNumber != null && !"".equals(serialNumber)) {
			where_buf.append(" AND temp.serialNumber = '").append(serialNumber).append("'");
		}
		this.serialNumber = serialNumber;
	}

	public BigDecimal getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(BigDecimal startAmount) {
		startAmount = startAmount == null?new BigDecimal(0.00):startAmount;
		if (startAmount.compareTo(new BigDecimal(0.00)) > 0) {
			where_buf.append(" AND temp.amount >= ").append(startAmount);
		}else{
			where_buf.append(" AND temp.amount > ").append(startAmount);
		}
		this.startAmount = startAmount;
	}

	public BigDecimal getEndAmount() {
		return endAmount;
	}

	public void setEndAmount(BigDecimal endAmount) {
		if (endAmount != null) {
			where_buf.append(" AND temp.amount <= ").append(endAmount);
		}
		this.endAmount = endAmount;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		if (merchantId != null) {
			this.where_buf.append(" AND m.id = ").append(merchantId);
		}
		this.merchantId = merchantId;
	}

	public Long getReplyId() {
		return replyId;
	}

	public void setReplyId(Long replyId) {
		if (replyId != null) {
			this.where_buf.append(" AND temp.settleMentReply = ?");
		}
		this.replyId = replyId;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		if (state != null){
			where_buf.append(" AND temp.state = " + state);
			if (state == 0)
				super.FROM_BUF = FROM_BUF;
			if (state == 1) {
				if (replyId == null && this.replyState == null) {
					super.FROM_BUF = new StringBuffer(FROM_BUF).append(" AND temp.complateDate >= ? AND temp.complateDate <= ?");
				}else{
					super.FROM_BUF = FROM_BUF;
				}
			}
			if (state == 2)
				super.FROM_BUF = FROM_BUF;
			if (state == -3 || state == -6 || state == -3 || state == 4){
				super.SELECT_BUF = SELECT_BUF_DF;
				if (state == 4){	
					super.FROM_BUF = new StringBuffer(FROM_BUF_DF).append(" AND temp.complateDate >= ? AND temp.complateDate <= ?");
				}else if(state == -3){
					super.FROM_BUF = new StringBuffer(FROM_BUF_DF).append(" AND temp.applyDate >= ? AND temp.applyDate <= ?");
				}else{
					super.FROM_BUF = FROM_BUF_DF;
				}
			}
			
			
		}else{
			super.FROM_BUF = new StringBuffer(FROM_BUF).append(" AND temp.applyDate >= ? AND temp.applyDate <= ?");
		}
		this.state = state;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		if (type != null) {
			this.where_buf.append(" AND temp.type = ").append(type);
		}
		this.type = type;
	}

	public Boolean getReplyState() {
		return replyState;
	}

	public void setReplyState(Boolean replyState) {
		if (replyState != null) {
			this.where_buf.append(" AND temp.replyState = ").append(replyState);
		}
		this.replyState = replyState;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	public BigDecimal getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(BigDecimal totalCost) {
		this.totalCost = totalCost;
	}

	@Override
	public String toString() {
		return "SettleMentVo [serialNumber=" + serialNumber + ", startAmount=" + startAmount + ", endAmount="
				+ endAmount + ", merchantId=" + merchantId + ", replyId=" + replyId + ", state=" + state
				+ ", totalAmount=" + totalAmount + ", totalCost=" + totalCost + "]";
	}

	@Override
	public void setParameter(Query query) {
		if (replyId != null) {
			query.setParameter(0, new SettleMentReply(replyId));
		}else {
			if (state == null || state == 1 || state == 4 || state == -3){
				query.setParameter(0, getStartDate());
				query.setParameter(1, getEndDate());
			}
		}
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	@Override
	public Class<?> getListObjClass() {
		return SettleMent.class;
	}

	@Override
	public StringBuffer getQueryWhereBuf() {
		return this.where_buf;
	}

	@Override
	public StringBuffer getQueryGroupByBuf() {
		return new StringBuffer("");
	}

}
