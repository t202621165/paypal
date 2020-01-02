package com.iwanol.paypal.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.tuckey.web.filters.urlrewrite.utils.StringUtils;

import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.GalleryProductCost;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.ProductRate;
import com.iwanol.paypal.domain.Route;

public class RateArray {
	
	private Long galleryId;
	
	private Long productId;
	
	private Long merchantId;
	
	private String productName;
	
	private String productMark;
	
	private Integer productType;
	
	private Boolean state;
	
	private String url;
	
	private BigDecimal rate;
	
	private List<Long> productIds = new ArrayList<Long>();
	
	private List<Product> products = new ArrayList<Product>();
	
	public RateArray() {
	}
	
	public RateArray(Long productId,String productName,String productMark,
			Integer productType,Boolean state,String url,BigDecimal rate) {
		
		this.productId = productId;
		
		this.productName = productName;
		
		this.productMark = productMark;
		
		this.productType = productType;
		
		this.state = state;
		
		this.url = url;
		
		this.rate = rate;
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

	public Long getMerchantId() {
		return merchantId;
	}

	public void setMerchantId(Long merchantId) {
		this.merchantId = merchantId;
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

	public Integer getProductType() {
		return productType;
	}

	public void setProductType(Integer productType) {
		this.productType = productType;
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

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}
	
	public List<Long> getProductIds() {
		return productIds;
	}

	public void setProductIds(List<Long> productIds) {
		this.productIds = productIds;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public GalleryProductCost getGalleryProductCost(GalleryProductCost cost) {
		if (cost == null) {
			cost = new GalleryProductCost();
		}
		cost.setProductId(this.productId);
		cost.setGallery(new Gallery(this.galleryId));
		cost.setPayRate(this.rate);
		return cost;
	}
	
	public Route getRoute(Route route) {
		if (route == null) {
			route = new Route();
		}
		route.setUrl(this.url);
		route.setState(this.state);
		route.setProduct(new Product(this.productId));
		route.setGallery(new Gallery(this.galleryId));
		return route;
	}
	
	public ProductRate getProductRate(ProductRate rate) {
		if (rate == null) {
			rate = new ProductRate();
		}
		rate.setProduct(new Product(this.productId));
		rate.setMerchant(new Merchant(this.merchantId));
		rate.setProductRate(this.rate);
		return rate;
	}
	
	/**
	 * 通道费率、通道路由参数校验
	 * @return
	 */
	public boolean validationRateAndRoute() {
		if (galleryId == null) {
			return false;
		}
		if (productId == null) {
			return false;
		}
		if (rate == null || rate.compareTo(new BigDecimal(0)) < 0) {
			return false;
		}
		if (state == null) {
			return false;
		}
		if (StringUtils.isBlank(url)) {
			return false;
		}
		return true;
	}
	
	/**
	 * 商户产品费率参数校验
	 * @return
	 */
	public boolean validationRate() {
		if (merchantId == null) {
			return false;
		}
		if (productId == null) {
			return false;
		}
		if (rate == null || rate.compareTo(new BigDecimal(0)) < 0) {
			return false;
		}
		return true;
	}
	
	public void galleryProductAndIds(List<RateArray> rates) {
		if (rates != null && rates.size() > 0) {
			this.galleryId = rates.get(0).galleryId;
			rates.stream().filter(rate -> rate.validationRateAndRoute()).forEach(rate -> {
				Long productId = rate.getProductId();
				Product product = new Product(productId);
				this.getProductIds().add(productId);
				this.getProducts().add(product);
			});
		}
	}
	
	public void merchantProductAndIds(List<RateArray> rates) {
		if (rates != null && rates.size() > 0) {
			this.merchantId = rates.get(0).merchantId;
			rates.stream().filter(rate -> rate.validationRate()).forEach(rate -> {
				Long productId = rate.getProductId();
				Product product = new Product(productId);
				this.getProductIds().add(productId);
				this.getProducts().add(product);
			});
		}
	}
	
	@Override
	public String toString() {
		return "GalleryDetails [productId=" + productId + ", productName=" + productName
				+ ", productMark=" + productMark + ", state=" + state + ", url=" + url + ", rate="
				+ rate + "]";
	}
	
}
