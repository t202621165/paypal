package com.iwanol.paypal.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.vo.BaseVo;

public class FundsVo extends BaseVo implements Serializable{

	private static final long serialVersionUID = 1L;

	public final static StringBuffer SUM_BUF =  new StringBuffer("");

	public final static StringBuffer SELECT_BUF = new StringBuffer("SELECT new com.iwanol.paypal.vo.FundsVo("
			+ "m.id, m.account, temp.bankName, temp.realName,  temp.bankNumber,  temp.overMoney,  IFNULL(SUM(o.amount),0.0)+0.0, "
			+ "IFNULL(SUM(o.merchantProfits),0.0)+0.0, IFNULL(SUM(o.platformProfits),0.0)+0.0,  p.id, p.productMark, p.type)");

	private final static StringBuffer FROM_BUF = new StringBuffer(" FROM Bank temp LEFT JOIN temp.merchant m LEFT JOIN m.platformOrders o WITH (o.state != 0 "
			+ "AND o.orderDate >= ? AND o.orderDate <= ?) LEFT JOIN o.product p WHERE temp.bankType = 1 AND temp.bankNumber IS NOT NULL");
	
	private StringBuffer group_by_buf = new StringBuffer("");
	
	private StringBuffer where_buf = new StringBuffer("");
	
	@JsonIgnore
	private BigDecimal startAmount;
	
	@JsonIgnore
	private BigDecimal endAmount;

	private Long merchantId;
	
	private String account;
	
	private String bankName;
	
	private String realName;
	
	private String bankNumber;
	
	private BigDecimal overMoney = new BigDecimal(0.00);
	
	private List<ProductVo> list = new ArrayList<ProductVo>();
	
	@JsonIgnore
	private final static List<FundsVo> FUNDS_LIST = new ArrayList<FundsVo>();
	
	public FundsVo() {
		super.SELECT_BUF = SELECT_BUF;
		super.FROM_BUF = FROM_BUF;
		super.SUM_BUF = SUM_BUF;
	}
	
	public FundsVo(Long merchantId,String account,String bankName,String realName,
			String bankNumber,BigDecimal overMoney,double amount,double merchantProfits,
			double platformProfits,Long productId,String productMark,Integer type) {
		this.merchantId = merchantId;
		this.account = account;
		this.bankName = bankName;
		this.realName = realName;
		this.bankNumber = bankNumber;
		this.overMoney = overMoney;
		if (productId != null) {
			ProductVo vo = new ProductVo(productId, productMark, type, 
					amount, merchantProfits, platformProfits);
			this.list.add(vo);
		}
		listVo();
	}
	
	public void listVo() {
		boolean isAdd = false;
		for (FundsVo vo : FUNDS_LIST) {
			if (vo.merchantId.equals(this.merchantId)) {
				isAdd = true;
				vo.list.addAll(this.list);
				continue;
			}
		}
		if (!isAdd)
			FUNDS_LIST.add(this);
	}
	
	public BigDecimal getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(BigDecimal startAmount) {
		startAmount = startAmount == null?new BigDecimal(0.00):startAmount;
		if (startAmount.compareTo(new BigDecimal(0.00)) > 0) {
			where_buf.append(" AND temp.overMoney >= ").append(startAmount);
		}else{
			where_buf.append(" AND temp.overMoney > ").append(startAmount);
		}
		this.startAmount = startAmount;
	}

	public BigDecimal getEndAmount() {
		return endAmount;
	}

	public void setEndAmount(BigDecimal endAmount) {
		if (endAmount != null) {
			where_buf.append(" AND temp.overMoney <= ").append(endAmount);
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

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
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

	public BigDecimal getOverMoney() {
		return overMoney;
	}

	public void setOverMoney(BigDecimal overMoney) {
		this.overMoney = overMoney;
	}

	public List<ProductVo> getList() {
		return list;
	}

	public void setList(List<ProductVo> list) {
		this.list = list;
	}

	@JsonIgnore
	public List<FundsVo> getFundsList() {
		List<FundsVo> listVo = new ArrayList<FundsVo>();
		Integer start = super.getStart();
		Integer length = super.getLength();
		int size = start + length;
		size = size > listSize()?listSize():size;
		for (int i = start; i < size; i++) {
			listVo.add(FUNDS_LIST.get(i));
		}
		return listVo;
	}
	
	public Integer listSize() {
		return FUNDS_LIST.size();
	}
	
	public static void setEmpty() {
		FUNDS_LIST.clear();
	}

	@Override
	public String toString() {
		return "FundsVo [merchantId=" + merchantId + ", account=" + account + ", bankName=" + bankName + ", realName="
				+ realName + ", bankNumber=" + bankNumber + ", overMoney=" + overMoney + "]";
	}

	@Override
	public void setParameter(Query query) {
		query.setParameter(0, getStartDate());
		query.setParameter(1, getEndDate());
	}

	@Override
	public Class<?> getListObjClass() {
		return this.getClass();
	}

	@Override
	public StringBuffer getQueryWhereBuf() {
		return new StringBuffer(this.where_buf).append(" GROUP BY m.id,p.id");
	}

	@Override
	public StringBuffer getQueryGroupByBuf() {
		return this.group_by_buf;
	}
	
}
