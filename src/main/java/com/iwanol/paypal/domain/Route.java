package com.iwanol.paypal.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

@Entity
public class Route implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Product product;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(nullable=false)
	private Gallery gallery;
	
	@Column(nullable=false)
	private Boolean state;
	
	private String url;
	
	private Boolean isRisk = Boolean.FALSE;
	
	private String typeMark;
	
	@Transient
	private Long galleryId;
	
	@Transient
	private Long productId;
	
	public Route() {
		
	}
	
	public Route(Long id,Long galleryId,Long productId,Boolean state,String url,Boolean isRisk,String typeMark) {
		this.id = id;
		this.galleryId = galleryId;
		this.productId = productId;
		this.state = state;
		this.url = url;
		this.isRisk = isRisk;
		this.typeMark = typeMark;
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

	public void setProduct(Product product) {
		this.product = product;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(Long galleryId) {
		this.galleryId = galleryId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Boolean getIsRisk() {
		return isRisk;
	}

	public void setIsRisk(Boolean isRisk) {
		this.isRisk = isRisk;
	}

	public String getTypeMark() {
		return typeMark;
	}

	public void setTypeMark(String typeMark) {
		this.typeMark = typeMark;
	}

	@Override
	public String toString() {
		return "Route [id=" + id + ", state=" + state + ", url=" + url + ", galleryId=" + galleryId + ", productId="
				+ productId + "]";
	}

}
