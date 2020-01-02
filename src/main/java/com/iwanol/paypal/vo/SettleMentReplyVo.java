package com.iwanol.paypal.vo;

import java.math.BigDecimal;

import javax.persistence.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.domain.SettleMentReply;

public class SettleMentReplyVo extends BaseVo{
	
	public final static StringBuffer SUM_BUF =  new StringBuffer("SELECT new com.iwanol.paypal.vo.SettleMentReplyVo(IFNULL(SUM(s.amount),0)+0.0)");

	public final static StringBuffer SELECT_BUF = new StringBuffer("SELECT new com.iwanol.paypal.domain.SettleMentReply("
			+ "temp.id, temp.serialNumber, temp.replyDate, temp.state, COUNT(s), IFNULL(SUM(s.amount),0)+0.0, u.userName)");
	
	public final static StringBuffer FROM_BUF = new StringBuffer(" FROM SettleMentReply temp "
			+ "LEFT JOIN temp.user u LEFT JOIN temp.settleMents s "
			+ "WHERE temp.replyDate >= ? AND temp.replyDate <= ?");
	
	@JsonIgnore
	public StringBuffer where_buf = new StringBuffer("");
	
	private String serialNumber;
	
	@JsonIgnore
	private Long userId;
	
	@JsonIgnore
	private Boolean state;
	
	private BigDecimal totalAmount;
	
	public SettleMentReplyVo() {
		super.SELECT_BUF = SELECT_BUF;
		super.FROM_BUF = FROM_BUF;
		super.SUM_BUF = SUM_BUF;
	}
	
	public SettleMentReplyVo(double totalAmount) {
		this.totalAmount = new BigDecimal(totalAmount)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
	}
	
	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		if (serialNumber != null && !"".equals(serialNumber)) {
			this.where_buf.append(" AND temp.serialNumber = '").append(serialNumber).append("'");
		}
		this.serialNumber = serialNumber;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		if (userId != null ) {
			this.where_buf.append(" AND u.id = ").append(userId);
		}
		this.userId = userId;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		if (state != null ) {
			this.where_buf.append(" AND temp.state = ").append(state);
		}
		this.state = state;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}

	@Override
	public void setParameter(Query query) {
		query.setParameter(0, getStartDate());
		query.setParameter(1, getEndDate());
	}

	@Override
	public Class<?> getListObjClass() {
		return SettleMentReply.class;
	}

	@Override
	public StringBuffer getQueryWhereBuf() {
		return new StringBuffer("").append(this.where_buf).append(" GROUP BY temp.id");
	}
	
	@Override
	public StringBuffer getQuerySumQl() {
		StringBuffer bf = new StringBuffer();
		bf.append(SUM_BUF);
		bf.append(FROM_BUF);
		bf.append(this.where_buf);
		return bf;
	}
	
	@Override
	public StringBuffer getQueryGroupByBuf() {
		return new StringBuffer("");
	}

}
