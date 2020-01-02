package com.iwanol.paypal.vo;

import java.math.BigDecimal;

public class ProductVo {
	
	private Long productId;
	
	private String productMark;
	
	private Integer type;

	private BigDecimal amount = new BigDecimal(0.00);
	
	private BigDecimal merchantProfits = new BigDecimal(0.00);
	
	private BigDecimal platformProfits = new BigDecimal(0.00);
	
	public ProductVo() {
		
	}
	public ProductVo(Long productId,String productMark,Integer type,
			double amount,double merchantProfits,double platformProfits) {
		this.productId = productId;
		this.productMark = productMark;
		this.type = type;
		this.amount = new BigDecimal(amount)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.merchantProfits = new BigDecimal(merchantProfits)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		this.platformProfits = new BigDecimal(platformProfits)
				.setScale(2, BigDecimal.ROUND_HALF_UP);
		
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductMark() {
		return productMark;
	}
	public void setProductMark(String productMark) {
		this.productMark = productMark;
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

	@Override
	public String toString() {
		return "ProductVo [productId=" + productId + ", productMark=" + productMark + ", type=" + type + ", amount="
				+ amount + ", merchantProfits=" + merchantProfits + ", platformProfits=" + platformProfits + "]";
	}
	
}
