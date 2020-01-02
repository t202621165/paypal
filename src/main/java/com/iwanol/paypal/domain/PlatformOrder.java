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
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.until.DateUtil;

/**
 * 商户订单实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraph(
	name = "orderWithMerchantAndGalleryAndProduct",
	attributeNodes = {
			@NamedAttributeNode(value = "merchant"),
			@NamedAttributeNode(value = "gallery"),
			@NamedAttributeNode(value = "product")
	}
)
public class PlatformOrder implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(length=30)
	private String sysOrderNumber; //平台订单号  年+月+日+时+分+秒+毫秒+4位随机数+服务器标识
	
	@Column(length=40)
	private String merchantOrderNumber; //商户订单号
	
	@Column(length=40)
	private String partyOrderNumber; //第三方订单号
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date orderDate; //订单提交日期
	
	@Column(nullable = false)
	private BigDecimal amount; //订单金额
	
	@Column(length=15)
	private String clientIp; //下单ip
	
	@Column(nullable = false)
	private BigDecimal merchantProfits; //商户利润
	
	@Column(nullable = false)
	private BigDecimal platformProfits; //平台利润
	
	@Column(nullable = false)
	private BigDecimal agencyProfits; //代理利润
	
	private BigDecimal tailAmount; //订单尾额
	
	private BigDecimal tailProfit = new BigDecimal("0.00"); //尾额利润
	
	private BigDecimal merTailProfit = new BigDecimal("0.00"); //商户尾额利润
	
	private Integer state; //订单状态  0 下单待支付  1支付成功  2成功待下发
	
	private String attach; //订单扩展参数 原路返回给下单商户
	
	@Column(columnDefinition="TEXT")
	private String reqParam; //下单请求参数
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date completeDate; //订单完成时间
	
	private String orderDiscription; //订单描述
	
	private String agencyAccount;
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	private Merchant merchant; //一共商户多个订单
	
	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	private Gallery gallery; //一个通道多个订单

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	private Product product; //一个产品有多个订单
	
	@Transient
	private Long merchantId;
	
	@Transient
	private String merchantAccount;
	
	@Transient
	private String productMark;
	
	@Transient
	private String galleryName;
	
	@Transient
	private BigDecimal platAllProfit;
	
	@Transient
	private BigDecimal merAllProfit;
	
	public PlatformOrder(Long id,String sysOrderNumber,String merchantOrderNumber,String partyOrderNumber,
			Integer state,Date orderDate,Date completeDate,BigDecimal amount,BigDecimal tailAmount,BigDecimal merchantProfits,
			BigDecimal platformProfits,BigDecimal agencyProfits,BigDecimal tailProfit,BigDecimal merTailProfit,String clientIp,String attach,String reqParam,
			Long merchantId,String merchantAccount,String productMark,String galleryName,BigDecimal platAllProfit,BigDecimal merAllProfit) {
		this.id = id;
		this.sysOrderNumber = sysOrderNumber;
		this.merchantOrderNumber = merchantOrderNumber;
		this.partyOrderNumber = partyOrderNumber;
		this.state = state;
		this.orderDate = orderDate;
		this.completeDate = completeDate;
		this.amount = amount;
		this.tailAmount = tailAmount;
		this.merchantProfits = merchantProfits;
		this.platformProfits = platformProfits;
		this.agencyProfits = agencyProfits;
		this.tailProfit = tailProfit;
		this.merTailProfit = merTailProfit;
		this.clientIp = clientIp;
		this.attach = attach;
		this.reqParam = reqParam;
		this.merchantId = merchantId;
		this.merchantAccount = merchantAccount;
		this.productMark = productMark;
		this.galleryName = galleryName;
		this.platAllProfit = platAllProfit;
		this.merAllProfit = merAllProfit;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getClientIp() {
		return clientIp;
	}

	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}

	public BigDecimal getMerchantProfits() {
		return merchantProfits;
	}

	public void setMerchantProfits(BigDecimal merchantProfits) {
		this.merchantProfits = merchantProfits;
	}

	public BigDecimal getPlatformProfits() {
		return platformProfits;
	}

	public void setPlatformProfits(BigDecimal platformProfits) {
		this.platformProfits = platformProfits;
	}

	public BigDecimal getAgencyProfits() {
		return agencyProfits;
	}

	public void setAgencyProfits(BigDecimal agencyProfits) {
		this.agencyProfits = agencyProfits;
	}

	public BigDecimal getTailAmount() {
		return tailAmount;
	}

	public void setTailAmount(BigDecimal tailAmount) {
		this.tailAmount = tailAmount;
	}

	public Integer getState() {
		return state;
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getReqParam() {
		return reqParam;
	}

	public void setReqParam(String reqParam) {
		this.reqParam = reqParam;
	}

	public String getCompleteDate() {
		return completeDate==null?null:DateUtil.getDate(completeDate, "yyyy-MM-dd HH:mm:ss");
	}

	public void setCompleteDate(Date completeDate) {
		this.completeDate = completeDate;
	}

	public PlatformOrder() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSysOrderNumber() {
		return sysOrderNumber;
	}

	public void setSysOrderNumber(String sysOrderNumber) {
		this.sysOrderNumber = sysOrderNumber;
	}

	public String getMerchantOrderNumber() {
		return merchantOrderNumber;
	}

	public void setMerchantOrderNumber(String merchantOrderNumber) {
		this.merchantOrderNumber = merchantOrderNumber;
	}

	public String getPartyOrderNumber() {
		return partyOrderNumber;
	}

	public void setPartyOrderNumber(String partyOrderNumber) {
		this.partyOrderNumber = partyOrderNumber;
	}

	public String getOrderDate() {
		return DateUtil.getDate(orderDate, "yyyy-MM-dd HH:mm:ss");
	}

	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
	}

	public String getMerchantAccount() {
		return merchantAccount;
	}

	public void setMerchantAccount(String merchantAccount) {
		this.merchantAccount = merchantAccount;
	}

	public String getProductMark() {
		return productMark;
	}

	public void setProductMark(String productMark) {
		this.productMark = productMark;
	}

	public String getGalleryName() {
		return galleryName;
	}

	public void setGalleryName(String galleryName) {
		this.galleryName = galleryName;
	}	

	public String getOrderDiscription() {
		return orderDiscription;
	}

	public void setOrderDiscription(String orderDiscription) {
		this.orderDiscription = orderDiscription;
	}

	public BigDecimal getTailProfit() {
		return tailProfit;
	}

	public void setTailProfit(BigDecimal tailProfit) {
		this.tailProfit = tailProfit;
	}

	public BigDecimal getPlatAllProfit() {
		return platAllProfit;
	}

	public void setPlatAllProfit(BigDecimal platAllProfit) {
		this.platAllProfit = platAllProfit;
	}
	

	public BigDecimal getMerTailProfit() {
		return merTailProfit;
	}

	public void setMerTailProfit(BigDecimal merTailProfit) {
		this.merTailProfit = merTailProfit;
	}

	public BigDecimal getMerAllProfit() {
		return merAllProfit;
	}

	public void setMerAllProfit(BigDecimal merAllProfit) {
		this.merAllProfit = merAllProfit;
	}

	public String getAgencyAccount() {
		return agencyAccount;
	}

	public void setAgencyAccount(String agencyAccount) {
		this.agencyAccount = agencyAccount;
	}

	@Override
	public String toString() {
		return "PlatformOrder [id=" + id + ", sysOrderNumber=" + sysOrderNumber + ", merchantOrderNumber="
				+ merchantOrderNumber + ", partyOrderNumber=" + partyOrderNumber + ", orderDate=" + orderDate
				+ ", amount=" + amount + ", clientIp=" + clientIp + ", merchantProfits=" + merchantProfits
				+ ", platformProfits=" + platformProfits + ", agencyProfits=" + agencyProfits + ", state=" + state
				+ ", attach=" + attach + ", reqParam=" + reqParam + ", completeDate=" + completeDate + ", merchantId="
				+ merchantId + ", productMark=" + productMark + ", galleryName=" + galleryName + "]";
	}

}
