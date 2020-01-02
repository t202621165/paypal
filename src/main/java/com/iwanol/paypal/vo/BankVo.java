package com.iwanol.paypal.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.until.CommonUtil;
import com.iwanol.paypal.until.DateUtil;

public class BankVo extends BaseVo implements Serializable{
	
	private static final long serialVersionUID = 1L;

	public final static StringBuffer SUM_BUF =  new StringBuffer("SELECT new com.iwanol.paypal.vo.BankVo("
			+ "SUM(t.over_money),SUM(t.blockMoney) FROM(");
	
	public final static StringBuffer SELECT_BUF = new StringBuffer("SELECT new com.iwanol.paypal.domain.Bank(temp.id,temp.bankName,"
			+ "temp.bankNumber,temp.realName,temp.bankBranch,m.id,temp.overMoney,(IFNULL(SUM(o.merchantProfits+o.merTailProfit),0.00) + 0.00) AS blockMoney)");
	
	public final static StringBuffer FROM_BUF = new StringBuffer(" FROM Bank temp INNER JOIN temp.merchant m "
			+ "LEFT JOIN m.platformOrders o WITH o.orderDate >= ? "
			+ "AND o.orderDate <= ? WHERE m.settlementType = ? AND temp.bankNumber IS NOT NULL");
	
	private StringBuffer where_buf = new StringBuffer("");

	private Long id;
	
	private BigDecimal amount;
	
	private BigDecimal cost;
	
	private Long merchantId;
	
	private Integer settlementType;
	
	private BigDecimal startAmount;
	
	private BigDecimal endAmount;
	
	private BigDecimal totalOverMoney = new BigDecimal(0.00);
	
	private BigDecimal totalSettleMoney = new BigDecimal(0.00);
	
	private BigDecimal totalBlockMoney = new BigDecimal(0.00);
	
	public BankVo() {
		super.SELECT_BUF = SELECT_BUF;
		super.FROM_BUF = FROM_BUF;
		super.SUM_BUF = SUM_BUF;
	}
	
	public BankVo(EntityManager entityManager) {
		setEntityManager(entityManager);
	}
	
	public BankVo(BigDecimal totalOverMoney, BigDecimal totalBlockMoney) {
		this.totalOverMoney = totalOverMoney;
		this.totalBlockMoney = totalBlockMoney;
		this.totalSettleMoney = totalOverMoney.subtract(totalBlockMoney);
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

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		if (merchantId != null) {
			where_buf.append(" AND m.id = ").append(merchantId);
		}
		this.merchantId = merchantId;
	}

	public Integer getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(Integer settlementType) {
		this.settlementType = settlementType;
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

	public BigDecimal getTotalOverMoney() {
		return totalOverMoney;
	}

	public void setTotalOverMoney(BigDecimal totalOverMoney) {
		this.totalOverMoney = totalOverMoney;
	}

	public BigDecimal getTotalSettleMoney() {
		return totalSettleMoney;
	}

	public void setTotalSettleMoney(BigDecimal totalSettleMoney) {
		this.totalSettleMoney = totalSettleMoney;
	}

	public BigDecimal getTotalBlockMoney() {
		return totalBlockMoney;
	}

	public void setTotalBlockMoney(BigDecimal totalBlockMoney) {
		this.totalBlockMoney = totalBlockMoney;
	}
	
	@Override
	public String toString() {
		return "BankVo [where_buf=" + where_buf + ", id=" + id + ", amount=" + amount + ", cost=" + cost
				+ ", merchantId=" + merchantId + ", settlementType=" + settlementType + ", startAmount=" + startAmount
				+ ", endAmount=" + endAmount + ", totalOverMoney=" + totalOverMoney + ", totalSettleMoney="
				+ totalSettleMoney + ", totalBlockMoney=" + totalBlockMoney + "]";
	}

	/**
	 * 结算校验
	 * @return
	 */
	public boolean settlementCheck() {
		if (id == null || amount == null || 
				amount.compareTo(new BigDecimal(0.00)) < 1) {
			return false;
		}
		this.endAmount = null;
		this.merchantId = null;
		if (cost.compareTo(new BigDecimal(0.00)) < 1) {
			setStartAmount(amount);
		}else{
			setStartAmount(amount.add(cost));
		}
		resetWhereBuf();
		return true;
	}
	
	public Bank findByIdAndOverMoney() {
		StringBuffer qlBuf = new StringBuffer(SELECT_BUF)
				.append(FROM_BUF).append(where_buf)
				.append(" AND temp.id = ").append(this.id)
				.append(" GROUP BY temp.merchant");
		return (Bank) findOneByQl(qlBuf.toString());
	}
	
	public int updateOverMoneyByIds(StringBuffer when_buf,StringBuffer where_buf) {
		if (!"".equals(when_buf) && !"".equals(where_buf)) {
			StringBuffer qlBuf = new StringBuffer("UPDATE Bank b SET b.overMoney = CASE b.id");
			qlBuf.append(when_buf).append(" END WHERE b.id IN(").append(where_buf).append(")");
			String ql = qlBuf.toString();
			return updateByQl(ql.replace(", )", ")"));
		}
		return 0;
	}
	
	public int updateOverMoneyByMerchant(List<Bank> banks) {
		StringBuffer when_buf = new StringBuffer();
		StringBuffer where_buf = new StringBuffer();
		for (Bank bank : banks) {
			when_buf.append(CommonUtil.getBuffer(" WHEN ",bank.getMerId()," THEN ","over_money+"+bank.getOverMoney()));
			where_buf.append(CommonUtil.getBuffer(bank.getMerId(),", "));
		}
		if (!"".equals(when_buf) && !"".equals(where_buf)) {
			StringBuffer qlBuf = new StringBuffer("UPDATE bank SET over_money = CASE merchant_id");
			qlBuf.append(when_buf).append(" END WHERE merchant_id IN(").append(where_buf).append(") AND bank_type = 1");
			String ql = qlBuf.toString();
			int i = updateByNativeQl(ql.replace(", )", ")"));
			return i;
		}
		return 0;
	}
	
	public void resetWhereBuf() {
		this.where_buf = new StringBuffer();
		this.setMerchantId(merchantId);
		this.setStartAmount(startAmount);
		this.setEndAmount(endAmount);
	}

	@Override
	public void setParameter(Query query) {
		query.setParameter(0, DateUtil.getDateZeroPoint(new Date()));
		query.setParameter(1, new Date());
		query.setParameter(2, this.settlementType);
	}

	@Override
	public Class<?> getListObjClass() {
		return Bank.class;
	}

	@Override
	public StringBuffer getQueryWhereBuf() {
		return new StringBuffer("").append(this.where_buf).append(" GROUP BY temp.merchant");
	}

	@Override
	public StringBuffer getQueryGroupByBuf() {
		return new StringBuffer("");
	}

	@Override
	public StringBuffer getQuerySumQl() {
		StringBuffer bf = new StringBuffer();
		bf.append(SUM_BUF);
		bf.append(SELECT_BUF);
		bf.append(FROM_BUF);
		bf.append(where_buf);
		bf.append("))t");
		return bf;
	}
	
}
