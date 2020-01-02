package com.iwanol.paypal.vo;

import java.util.ArrayList;
import java.util.List;

import com.iwanol.paypal.domain.Gallery;

public class MerchantProductGalleryVo {

	private Long productId;
	
	private String productName;
	
	private String productMark;
	
	private Long galleryId;
	
	private String galleryName;
	
	private Long defaultGalleryId;
	
	private Integer type;
	
	private List<Gallery> gallerys = new ArrayList<Gallery>();
	
	public MerchantProductGalleryVo() {}
	
	public MerchantProductGalleryVo(Long productId,Long defaultGalleryId,String productName,
			String productMark,Integer type,Long galleryId,String galleryName) {
		this.productId = productId;
		
		this.defaultGalleryId = defaultGalleryId;
		
		this.productName = productName;
		
		this.productMark = productMark;
		
		this.type = type;
		
		this.galleryId = galleryId;
		
		this.galleryName = galleryName;
		
	}
	
	public List<MerchantProductGalleryVo> listVo(List<MerchantProductGalleryVo> list) {
		List<MerchantProductGalleryVo> voList = new ArrayList<MerchantProductGalleryVo>();
		for (int i = 0; i < list.size(); i++) {
			boolean isAdd = false;
			MerchantProductGalleryVo vo = list.get(i);
			Long productId = vo.getProductId();
			Long galleryId = vo.getGalleryId();
			String galleryName = vo.getGalleryName();
			for (int j = 0; j < voList.size(); j++) {
				MerchantProductGalleryVo mpg = voList.get(j);
				if (mpg.getProductId().equals(productId)) {
					isAdd = true;
					mpg.gallerys.add(new Gallery(galleryId, galleryName));
				}
			}
			if (!isAdd) {
				vo.gallerys.add(new Gallery(galleryId, galleryName));
				voList.add(vo);
			}
		}
		return voList;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductMark() {
		return productMark;
	}

	public void setProductMark(String productMark) {
		this.productMark = productMark;
	}

	public Long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(Long galleryId) {
		this.galleryId = galleryId;
	}

	public String getGalleryName() {
		return galleryName;
	}

	public void setGalleryName(String galleryName) {
		this.galleryName = galleryName;
	}

	public Long getDefaultGalleryId() {
		return defaultGalleryId;
	}

	public void setDefaultGalleryId(Long defaultGalleryId) {
		this.defaultGalleryId = defaultGalleryId;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public List<Gallery> getGallerys() {
		return gallerys;
	}

	public void setGallerys(List<Gallery> gallerys) {
		this.gallerys = gallerys;
	}

	@Override
	public String toString() {
		return "MerchantProductGalleryVo [productId=" + productId + ", productName=" + productName + ", productMark="
				+ productMark + ", galleryId=" + galleryId + ", galleryName=" + galleryName + ", defaultGalleryId="
				+ defaultGalleryId + ", type=" + type + "]";
	}

}
