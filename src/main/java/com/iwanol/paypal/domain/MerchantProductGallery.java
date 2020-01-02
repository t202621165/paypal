package com.iwanol.paypal.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
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
 * 商户产品通道实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "withProductAndGallery",
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
public class MerchantProductGallery {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id; //主键id
	
	@ManyToOne
	private Product product;
	
	@ManyToOne
	private Gallery gallery;
	
	@ManyToOne
	private Merchant merchant;
	
	@Transient
	private Long productId; //产品Id
	
	@Transient
	private Long galleryId; //通道Id 0关闭商户产品通道 
	
	@Transient
	private Long merchantId; 
	
	public MerchantProductGallery() {
		super();
	}
	
	public MerchantProductGallery(Long id, Long productId, Long galleryId) {
		this.id = id;
		this.productId = productId;
		this.product = new Product(productId);
		this.galleryId = galleryId;
		this.gallery = new Gallery(galleryId);
	}
	
	public MerchantProductGallery(Merchant merchant, Product product) {
		this.merchant = merchant;
		this.product = product;
	}
	
	public List<MerchantProductGallery> listByProduct(List<Merchant> merchants, Product product) {
		List<MerchantProductGallery> list = new ArrayList<MerchantProductGallery>();
		merchants.stream().forEach(merchant -> {
			list.add(new MerchantProductGallery(merchant, product));
		});
		return list;
	}
	
	public MerchantProductGallery newEntityByProduct(Product product) {
		MerchantProductGallery mpg = new MerchantProductGallery();
		mpg.setProduct(product);
		mpg.setGallery(this.gallery);
		mpg.setMerchant(this.merchant);
		return mpg;
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

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
		if (productId != null)
			this.product = new Product(productId);
	}

	public Long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(Long galleryId) {
		this.galleryId = galleryId;
		if (galleryId != null)
			this.gallery = new Gallery(galleryId);
	}

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
		if (merchantId != null)
			this.merchant = new Merchant(merchantId);
	}

	@Override
	public String toString() {
		return "MerchantProductGallery [id=" + id + ", productId=" + productId + ", galleryId=" + galleryId
				+ ", merchantId=" + merchantId + "]";
	}

}
