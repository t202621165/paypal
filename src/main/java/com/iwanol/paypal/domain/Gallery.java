package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 产品通道实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "galleryWithRouteAndProduct",
		attributeNodes = {@NamedAttributeNode(value = "routes", subgraph = "routeWithProduct")},
		subgraphs = {
			@NamedSubgraph(
				name = "routeWithProduct", 
				attributeNodes = {
					@NamedAttributeNode(value = "product")
				}
			)
		}
	)
})
public class Gallery implements Serializable{
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(length=15,nullable=false,unique=true)
	private String galleryMark; //通道标识
	
	@Column(length=15)
	private String galleryName; //通道名称
	
	@Column
	private String galleryAccount; //通道账号
	
	private String appId; // 微信app_id
	
	@Column(length=64)
	private String galleryMD5Key; //通道md5密钥
	
	@Column(columnDefinition="text")
	private String galleryPrivateKey; //RSA私钥
	
	@Column(columnDefinition="text")
	private String galleryPubKey; //RSA公钥

	@Column(nullable=false)
	private Boolean riskState; //是否启动风控 0不启动 1启动
	
	private String riskDomain; //风控域名
	
	private String galleryDescription; //通道描述

	@Column(nullable = false )
	private Boolean state; //通道状态false/ 0禁用 true/1启用
	
	private Integer sort;  //排序号
	
	private Integer weight = 0; //通道权重 用于通道轮询
	
	private BigDecimal minAmount = new BigDecimal(0.00); //下单提交最小金额（左开）用于按金额分流
	
	private BigDecimal maxAmount = new BigDecimal(0.00); //下单提交最大金额（右闭）用于按金额分流
	
	@JsonIgnore
	@ManyToMany
	private Set<Product> products = new HashSet<Product>(); //一个通道支持多种产品

	@JsonIgnore
	@OneToMany(mappedBy="gallery")
	private Set<GalleryProductCost> galleryProductCosts = new HashSet<GalleryProductCost>();

	@JsonIgnore
	@OneToMany(mappedBy="gallery")
	private Set<PlatformOrder> platformOrders = new HashSet<PlatformOrder>();
	
	@JsonIgnore
	@OneToMany(mappedBy="gallery")
	private Set<Route> routes = new HashSet<Route>();
	
	@Transient
	private Long productId;
	
	@Transient
	private Long[] productIds;

	@Transient
	private boolean checked;
	
	public Gallery() {
		super();
	}
	
	public Gallery(Long id) {
		this.id = id;
	}
	
	public Gallery(Long id,String galleryName) {
		this.id = id;
		this.galleryName = galleryName;
	}
	
	public Gallery(Long id,String galleryName,Long productId) {
		this.id = id;
		this.galleryName = galleryName;
		this.productId = productId;
	}
	
	public void init(){
		if (this.id == null){
			if (this.state == null) {
				this.state = Boolean.FALSE;
			}
			if (this.riskState == null) {
				this.riskState = Boolean.FALSE;
			}
			if (this.sort == null) {
				this.sort = 0;
			}
			if (this.weight == null) {
				this.weight = 0;
			}
		}
	}
	
	public void reSetProduct(Product product) {
		if (productIds != null && productIds.length > 0) {
			this.products = new HashSet<Product>();
			Long id = product.getId();
			for (Long productId : productIds) {
				if (id == productId) {
					this.products.addAll(product.getChildren());
				}
				this.products.add(new Product(productId,this));
			}
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGalleryMark() {
		return galleryMark;
	}

	public void setGalleryMark(String galleryMark) {
		this.galleryMark = galleryMark;
	}

	public String getGalleryName() {
		return galleryName;
	}

	public void setGalleryName(String galleryName) {
		this.galleryName = galleryName;
	}


	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}
	
	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getGalleryAccount() {
		return galleryAccount;
	}

	public void setGalleryAccount(String galleryAccount) {
		this.galleryAccount = galleryAccount;
	}

	public String getGalleryMD5Key() {
		return galleryMD5Key;
	}

	public void setGalleryMD5Key(String galleryMD5Key) {
		this.galleryMD5Key = galleryMD5Key;
	}

	public String getGalleryPrivateKey() {
		return galleryPrivateKey;
	}

	public void setGalleryPrivateKey(String galleryPrivateKey) {
		this.galleryPrivateKey = galleryPrivateKey;
	}

	public String getGalleryPubKey() {
		return galleryPubKey;
	}

	public void setGalleryPubKey(String galleryPubKey) {
		this.galleryPubKey = galleryPubKey;
	}

	public Boolean getRiskState() {
		return riskState;
	}

	public void setRiskState(Boolean riskState) {
		this.riskState = riskState;
	}

	public String getRiskDomain() {
		return riskDomain;
	}

	public void setRiskDomain(String riskDomain) {
		this.riskDomain = riskDomain;
	}

	public String getGalleryDescription() {
		return galleryDescription;
	}

	public void setGalleryDescription(String galleryDescription) {
		this.galleryDescription = galleryDescription;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Integer getWeight() {
		return weight;
	}

	public void setWeight(Integer weight) {
		this.weight = weight;
	}

	public Set<Product> getProducts() {
		return products;
	}

	public void setProducts(Set<Product> products) {
		this.products = products;
	}

	public Set<GalleryProductCost> getGalleryProductCosts() {
		return galleryProductCosts;
	}

	public void setGalleryProductCosts(Set<GalleryProductCost> galleryProductCosts) {
		this.galleryProductCosts = galleryProductCosts;
	}

	public Set<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(Set<Route> routes) {
		this.routes = routes;
	}

	public Set<PlatformOrder> getPlatformOrders() {
		return platformOrders;
	}

	public void setPlatformOrders(Set<PlatformOrder> platformOrders) {
		this.platformOrders = platformOrders;
	}

	public BigDecimal getMinAmount() {
		return minAmount;
	}

	public void setMinAmount(BigDecimal minAmount) {
		this.minAmount = minAmount;
	}

	public BigDecimal getMaxAmount() {
		return maxAmount;
	}

	public void setMaxAmount(BigDecimal maxAmount) {
		this.maxAmount = maxAmount;
	}
	
	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long[] getProductIds() {
		return productIds;
	}

	public void setProductIds(Long[] productIds) {
		this.productIds = productIds;
		if (productIds != null && productIds.length > 0) {
			this.products = new HashSet<Product>();
			for (Long productId : productIds) {
				this.products.add(new Product(productId,this));
			}
		}
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	@Override
	public String toString() {
		return "Gallery [id=" + id + ", galleryMark=" + galleryMark + ", galleryName=" + galleryName
				+ ", galleryAccount=" + galleryAccount + ", appId=" + appId + ", galleryMD5Key=" + galleryMD5Key
				+ ", galleryPrivateKey=" + galleryPrivateKey + ", galleryPubKey=" + galleryPubKey + ", riskState="
				+ riskState + ", riskDomain=" + riskDomain + ", galleryDescription=" + galleryDescription + ", state="
				+ state + ", sort=" + sort + ", weight=" + weight + ", minAmount=" + minAmount + ", maxAmount="
				+ maxAmount + ", productId=" + productId + "]";
	}
}
