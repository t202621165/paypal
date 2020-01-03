package com.iwanol.paypal.vo;

import java.io.Serializable;

import javax.persistence.Query;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.domain.CongKou;

public class CongKouVo extends BaseVo implements Serializable{

	private static final long serialVersionUID = 1L;
	
	public final static StringBuffer SUM_BUF =  new StringBuffer("SELECT new com.iwanol.paypal.vo.CongKouVo("
			+ "IFNULL(SUM(temp.amount),0)+0.00,count(temp.id))");

	public final static StringBuffer SELECT_BUF = new StringBuffer("SELECT new com.iwanol.paypal.domain.CongKou("
			+ "temp.id,temp.date,temp.type,temp.amount,temp.merId,temp.merName,temp.realName,temp.bankName,"
			+ "temp.bankNumber,temp.state,temp.discription)");
	
	public final static StringBuffer FROM_BUF = new StringBuffer(" FROM CongKou temp WHERE 1 = 1 AND temp.date >= ? AND temp.date <= ?");
	
	@JsonIgnore
	public StringBuffer where_buf = new StringBuffer("");
	
	private double totalAmount;
	
	private Long totalCount;
	
	@JsonIgnore
	private String merId;
	
	@JsonIgnore
	private Integer state;
	
	public CongKouVo() {
		super.SELECT_BUF = SELECT_BUF;
		super.FROM_BUF = FROM_BUF;
		super.SUM_BUF = SUM_BUF;
	}

	public CongKouVo(double totalAmount, Long totalCount) {
		super();
		this.totalAmount = totalAmount;
		this.totalCount = totalCount;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	
	

	public String getMerId() {
		return merId;
	}

	public void setMerId(String merId) {
		if (!StringUtils.isEmpty(merId)){
			where_buf.append(" AND temp.merId = '").append(merId).append("'");
		}
		this.merId = merId;
	}
	
	

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		if (state != null){
			where_buf.append(" AND temp.state = " + state);
		}
		this.state = state;
	}

	@Override
	public void setParameter(Query query) {
		// TODO Auto-generated method stub
		query.setParameter(0, getStartDate());
		query.setParameter(1, getEndDate());
	}

	@Override
	public Class<?> getListObjClass() {
		// TODO Auto-generated method stub
		return CongKou.class;
	}

	@Override
	public StringBuffer getQueryWhereBuf() {
		// TODO Auto-generated method stub
		return this.where_buf;
	}

	@Override
	public StringBuffer getQueryGroupByBuf() {
		// TODO Auto-generated method stub
		return new StringBuffer("");
	}

}
