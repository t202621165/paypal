package com.iwanol.paypal.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.util.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.domain.PlatformOrder;
import com.iwanol.paypal.until.DateUtil;

public class OrderVo extends BaseVo implements Serializable{

	private static final long serialVersionUID = 1L;

	public final static StringBuffer SUM_BUF =  new StringBuffer("SELECT new com.iwanol.paypal.vo.OrderVo("
			+ "DATE_FORMAT(temp.orderDate,'%Y-%m-%d'),SUM(temp.amount) AS amount,SUM(temp.merchantProfits),SUM(temp.platformProfits),SUM(temp.agencyProfits),SUM(temp.tailAmount),SUM(temp.tailProfit),SUM(temp.merTailProfit),"
			+ "COUNT(*),temp.state,p.productName,p.type)");

	public final static StringBuffer SELECT_BUF = new StringBuffer("SELECT new com.iwanol.paypal.domain.PlatformOrder("
			+ "temp.id,temp.sysOrderNumber,temp.merchantOrderNumber,temp.partyOrderNumber,temp.state,"
			+ "temp.orderDate,temp.completeDate,temp.amount,temp.tailAmount,temp.merchantProfits,temp.platformProfits,"
			+ "temp.agencyProfits,temp.tailProfit,temp.merTailProfit,temp.clientIp,temp.attach,temp.reqParam,m.id,m.account,p.productMark,g.galleryName,temp.platformProfits+temp.tailProfit,temp.merchantProfits+temp.merTailProfit)");

	private final static StringBuffer FROM_BUF = new StringBuffer(" FROM PlatformOrder temp "
			+ "left join temp.merchant m left join temp.product p left join "
			+ "temp.gallery g WHERE temp.orderDate >= ? AND temp.orderDate <= ?");
	
	private StringBuffer group_by_buf = new StringBuffer(" GROUP BY temp.state");
	
	private StringBuffer where_buf = new StringBuffer("");
	
	private String sysOrderNumber;
	
	private String merchantOrderNumber;
	
	private String partyOrderNumber;
	
	private String productName;
	
	private String orderDate;
	
	@JsonIgnore
	private Long merchantId;
	
	@JsonIgnore
	private Long agencyId;
	
	@JsonIgnore
	private String agencyAccount;
	
	@JsonIgnore
	private Long galleryId;
	
	@JsonIgnore
	private Long productId;
	
	private Integer type;
	
	@JsonIgnore
	private BigDecimal startAmount;
	
	@JsonIgnore
	private BigDecimal endAmount;
	
	private BigDecimal totalAmount = new BigDecimal(0.00); // 订单总金额
	
	private BigDecimal successAmount = new BigDecimal(0.00); // 成功订单总金额
	
	private BigDecimal waitAmount = new BigDecimal(0.00); // 待付款订单总金额
	
	private BigDecimal totalMerchantProfits = new BigDecimal(0.00); // 商户总利润
	
	private BigDecimal totalPlatformProfits = new BigDecimal(0.00); // 平台总利润
	
	private BigDecimal totalAgencyProfits = new BigDecimal(0.00); //代理总利润;
	
	private BigDecimal totalTailAmount = new BigDecimal("0.00"); //订单总尾额
	
	private BigDecimal totalTailProfit = new BigDecimal("0.00"); //订单总尾额利润
	
	private BigDecimal totalMerTailProfit = new BigDecimal("0.00"); //商户订单总尾额利润
	
	private Long totalCount = 0L; // 总订单笔数
	
	private Long successCount = 0L; // 成功订单笔数
	
	private Long waitCount = 0L; // 等待付款订单笔数
	
	private Integer state;
	
	public OrderVo() {
		super.SELECT_BUF = SELECT_BUF;
		super.FROM_BUF = FROM_BUF;
		super.SUM_BUF = SUM_BUF;
	}
	
	public OrderVo(Integer state) {
		this();
		super.setStartDate(null);
		super.setEndDate(null);
		setState(state);
	}
	
	public OrderVo(Long merchantId) {
		this();
		super.setStartDate(null);
		super.setEndDate(null);
		setMerchantId(merchantId);
	}
	
	/**
	 * 
	 * @param merchantId
	 * 			商户ID
	 * @param galleryId
	 * 			通道ID
	 * @param productId
	 * 			产品ID
	 */
	public OrderVo(Long merchantId,Long galleryId,Long productId) {
		// 获取三个月以前的日期
		String date = DateUtil.getBeforeOrAfterDateByDate("yyyy-MM-dd HH:mm:ss", Calendar.MONTH, -3);
		super.setStartDate(date);
		super.setEndDate(null);
		this.merchantId = merchantId;
		this.galleryId = galleryId;
		this.productId = productId;
	}
	
	public OrderVo(Long galleryId, EntityManager entityManager) {
		this();
		super.setStartDate(null);
		super.setEndDate(null);
		setGalleryId(galleryId);
		setEntityManager(entityManager);
	}
	
	public OrderVo(EntityManager entityManager) {
		this();
		super.setStartDate(null);
		super.setEndDate(null);
		setEntityManager(entityManager);
	}
	
	public OrderVo(String startDate, String endDate, EntityManager entityManager) {
		this();
		super.setStartDate(startDate);
		super.setEndDate(endDate);
		setEntityManager(entityManager);
	}
	
	public OrderVo(String orderDate,BigDecimal totalAmount,BigDecimal totalMerchantProfits,
			BigDecimal totalPlatformProfits,BigDecimal totalAgencyProfits,BigDecimal totalTailAmount,BigDecimal totalTailProfit,BigDecimal totalMerTailProfit,Long totalCount,Integer state,
			String productName,Integer type) {
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		this.totalMerchantProfits = totalMerchantProfits;
		this.totalPlatformProfits = totalPlatformProfits;
		this.totalAgencyProfits = totalAgencyProfits;
		this.totalTailAmount = totalTailAmount;
		this.totalTailProfit = totalTailProfit;
		this.totalMerTailProfit = totalMerTailProfit;
		this.totalCount = totalCount;
		this.state = state;
		this.productName = productName;
		this.type = type;
	}
	
	/**
	 * 获取订单统计数据-根据产品类型分组统计
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<OrderVo> findOrderSumDate() {
		return findSumDataByQl();
	}
	
	/**
	 * 获取订单统计数据-根据订单状态分组统计
	 */
	@SuppressWarnings("unchecked")
	public void findOrderSumDateByState() {
		List<OrderVo> list = findSumDataByQl();
		if (list != null && !list.isEmpty()) {
			for (OrderVo vo : list) {
				Integer state = vo.getState();
				Long count = vo.getTotalCount();
				BigDecimal amount = vo.getTotalAmount();
				BigDecimal merProfits = vo.getTotalMerchantProfits();
				BigDecimal plaProfits = vo.getTotalPlatformProfits();
				BigDecimal agencyProfits= vo.getTotalAgencyProfits();
				if (state == 0) {
					this.waitAmount = amount;
					this.waitCount = count;
				}else {
					this.successAmount = this.successAmount.add(amount);
					this.successCount += count;
				}
				this.totalCount += count;
				this.totalAmount = this.totalAmount.add(amount);
				this.totalMerchantProfits = this.totalMerchantProfits.add(merProfits);
				this.totalPlatformProfits = this.totalPlatformProfits.add(plaProfits);
				this.totalAgencyProfits = this.totalAgencyProfits.add(agencyProfits);
			}
		}
	}
	
	public String getSysOrderNumber() {
		return sysOrderNumber;
	}

	public void setSysOrderNumber(String sysOrderNumber) {
		if (sysOrderNumber != null && !"".equals(sysOrderNumber)) {
			where_buf.append(" AND temp.sysOrderNumber = '").append(sysOrderNumber).append("'");
		}
		this.sysOrderNumber = sysOrderNumber;
	}

	public String getMerchantOrderNumber() {
		return merchantOrderNumber;
	}

	public void setMerchantOrderNumber(String merchantOrderNumber) {
		if (merchantOrderNumber != null && !"".equals(merchantOrderNumber)) {
			where_buf.append(" AND temp.merchantOrderNumber = '").append(merchantOrderNumber).append("'");
		}
		this.merchantOrderNumber = merchantOrderNumber;
	}

	public String getPartyOrderNumber() {
		return partyOrderNumber;
	}

	public void setPartyOrderNumber(String partyOrderNumber) {
		if (partyOrderNumber != null && !"".equals(partyOrderNumber)) {
			where_buf.append(" AND temp.partyOrderNumber = '").append(partyOrderNumber).append("'");
		}
		this.partyOrderNumber = partyOrderNumber;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(String orderDate) {
		this.orderDate = orderDate;
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

	public Long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(Long galleryId) {
		if (galleryId != null) {
			where_buf.append(" AND g.id = ").append(galleryId);
		}
		this.galleryId = galleryId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		if (productId != null) {
			where_buf.append(" AND (p.id = ").append(productId)
			.append(" OR p.parent.id = ").append(productId).append(")");
		}
		this.productId = productId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		if (type != null) {
			where_buf.append(" AND p.type = ").append(type);
		}
		this.type = type;
	}

	public BigDecimal getStartAmount() {
		return startAmount;
	}

	public void setStartAmount(BigDecimal startAmount) {
		if (startAmount != null) {
			where_buf.append(" AND temp.amount >= ").append(startAmount);
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
	
	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		if (state != null && state != -1) {
			where_buf.append(" AND temp.state = ").append(state);
		}
		this.state = state;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}
	
	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
	}
	
	public BigDecimal getTotalMerchantProfits() {
		return totalMerchantProfits;
	}
	
	public void setTotalMerchantProfits(BigDecimal totalMerchantProfits) {
		this.totalMerchantProfits = totalMerchantProfits;
	}
	
	public BigDecimal getTotalPlatformProfits() {
		return totalPlatformProfits;
	}
	
	public void setTotalPlatformProfits(BigDecimal totalPlatformProfits) {
		this.totalPlatformProfits = totalPlatformProfits;
	}
	
	public Long getTotalCount() {
		return totalCount;
	}
	
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}
	
	public BigDecimal getSuccessAmount() {
		return successAmount;
	}

	public void setSuccessAmount(BigDecimal successAmount) {
		this.successAmount = successAmount;
	}

	public BigDecimal getTotalTailAmount() {
		return totalTailAmount;
	}

	public void setTotalTailAmount(BigDecimal totalTailAmount) {
		this.totalTailAmount = totalTailAmount;
	}

	public BigDecimal getWaitAmount() {
		return waitAmount;
	}

	public void setWaitAmount(BigDecimal waitAmount) {
		this.waitAmount = waitAmount;
	}

	public Long getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(Long successCount) {
		this.successCount = successCount;
	}

	public Long getWaitCount() {
		return waitCount;
	}

	public void setWaitCount(Long waitCount) {
		this.waitCount = waitCount;
	}
	
	public BigDecimal getTotalMerTailProfit() {
		return totalMerTailProfit;
	}

	public void setTotalMerTailProfit(BigDecimal totalMerTailProfit) {
		this.totalMerTailProfit = totalMerTailProfit;
	}

	public void reSetGroupByBuf(StringBuffer group_by_buf) {
		this.group_by_buf = group_by_buf;
	}

	@Override
	public void setParameter(Query query) {
		query.setParameter(0, getStartDate());
		query.setParameter(1, getEndDate());
	}
	

	public BigDecimal getTotalAgencyProfits() {
		return totalAgencyProfits;
	}

	public void setTotalAgencyProfits(BigDecimal totalAgencyProfits) {
		this.totalAgencyProfits = totalAgencyProfits;
	}

	@Override
	public Class<?> getListObjClass() {
		return PlatformOrder.class;
	}

	@Override
	public StringBuffer getQueryWhereBuf() {
		return this.where_buf;
	}

	@Override
	public StringBuffer getQueryGroupByBuf() {
		return this.group_by_buf;
	}

	public BigDecimal getTotalTailProfit() {
		return totalTailProfit;
	}

	public void setTotalTailProfit(BigDecimal totalTailProfit) {
		this.totalTailProfit = totalTailProfit;
	}

	public Long getAgencyId() {
		return agencyId;
	}

	public void setAgencyId(Long agencyId) {
		if (agencyId != null){
			where_buf.append(" AND m.merchant.id = ").append(agencyId);
		}
		this.agencyId = agencyId;
	}

	public String getAgencyAccount() {
		return agencyAccount;
	}

	public void setAgencyAccount(String agencyAccount) {
		if (!StringUtils.isEmpty(agencyAccount)){
			where_buf.append(" AND temp.agencyAccount = ").append(agencyAccount);
		}
		this.agencyAccount = agencyAccount;
	}

	@Override
	public String toString() {
		return "OrderVo [sysOrderNumber=" + sysOrderNumber + ", merchantOrderNumber=" + merchantOrderNumber
				+ ", partyOrderNumber=" + partyOrderNumber + ", merchantId=" + merchantId + ", galleryId=" + galleryId
				+ ", productId=" + productId + ", startAmount=" + startAmount + ", endAmount=" + endAmount
				+ ", totalAmount=" + totalAmount + ", successAmount=" + successAmount + ", waitAmount=" + waitAmount
				+ ", totalMerchantProfits=" + totalMerchantProfits + ", totalPlatformProfits=" + totalPlatformProfits
				+ ", totalCount=" + totalCount + ", successCount=" + successCount + ", waitCount=" + waitCount
				+ ", state=" + state + "]" + super.toString();
	}
	
}
