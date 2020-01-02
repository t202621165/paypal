package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

/**
 * 通道产品成本实体
 * @author leo
 *
 */
@Entity
public class GalleryProductCost implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(scale=2,nullable=false)
	private BigDecimal payRate; //通道费率
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Gallery gallery; //通道产品id
	
	@Column(nullable=false)
	private Long  productId; //产品id
	
	@Transient
	private Long galleryId;
	
	
	public GalleryProductCost() {
		super();
	}
	
	public GalleryProductCost(Long id, Long galleryId, Long productId, BigDecimal payRate) {
		this.id = id;
		this.galleryId = galleryId;
		this.productId = productId;
		this.payRate = payRate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public BigDecimal getPayRate() {
		return payRate;
	}

	public void setPayRate(BigDecimal payRate) {
		this.payRate = payRate;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(Long galleryId) {
		this.galleryId = galleryId;
	}

	@Override
	public String toString() {
		return "GalleryProductCost [id=" + id + ", payRate=" + payRate + ", productId=" + productId + ", galleryId="
				+ galleryId + "]";
	}

}
