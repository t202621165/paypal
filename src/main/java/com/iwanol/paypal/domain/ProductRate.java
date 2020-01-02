package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.Transient;

/**
 * 产品出售费率实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "rateWithProduct",
		attributeNodes = {@NamedAttributeNode(value = "product", subgraph = "productWithGallery")},
		subgraphs = {
				@NamedSubgraph(
					name = "productWithGallery", 
					attributeNodes = {
						@NamedAttributeNode(value = "gallerys")
					}
				)
			}
	)
})
public class ProductRate implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; // 主键id
	
	@Column(scale=2)
	private BigDecimal productRate; //产品费率
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Product product;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Merchant merchant;
	
	@Transient
	private Long merchantId;
	
	@Transient
	private Long productId;
	
	@Transient
	private String productMark;
	
	@Transient
	private String productName;
	
	@Transient
	private Integer type;

	public ProductRate() {
		super();
	}
	
	public ProductRate(Long id,Long merchantId,Long productId,BigDecimal productRate) {
		this.id = id;
		this.merchantId = merchantId;
		this.productId = productId;
		this.productRate = productRate;
	}
	
	public ProductRate(Long id,BigDecimal productRate,Long productId,
			String productMark,String productName,Integer type) {
		this.id = id;
		this.productId = productId;
		this.productRate = productRate;
		this.productMark = productMark;
		this.productName = productName;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProducts(Product product) {
		this.product = product;
	}

	public BigDecimal getProductRate() {
		return productRate;
	}

	public void setProductRate(BigDecimal productRate) {
		this.productRate = productRate;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "ProductRate [id=" + id + ", productRate=" + productRate + ", merchantId=" + merchantId + ", productId="
				+ productId + ", productMark=" + productMark + ", productName=" + productName + ", type=" + type + "]";
	}

}
