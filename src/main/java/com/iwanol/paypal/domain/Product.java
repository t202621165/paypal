package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.ProductsEnum;

/**
 * 平台产品实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "productWithGallery",
		attributeNodes = {@NamedAttributeNode(value = "gallery")}
	),
	@NamedEntityGraph(
		name = "productWithGallerys",
		attributeNodes = {@NamedAttributeNode(value = "gallerys")}
	),
	@NamedEntityGraph(
		name = "productWithChildren",
		attributeNodes = {@NamedAttributeNode(value = "children")}
	),
	@NamedEntityGraph(
		name = "productWithGalleryAndGallerys",
		attributeNodes = {
			@NamedAttributeNode(value = "gallery"),
			@NamedAttributeNode(value = "gallerys")
		}
	)
})
public class Product implements Serializable{

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(length=15,nullable=false,unique=true,updatable=false)
	private String productMark;//产品标识
	
	@Column(length=15)
	private String productName; //产品名称
	
	private Integer type; // 0 普通产品  1 网银产品
	
	@JsonIgnore
	@OneToMany(mappedBy="product")
	Set<PlatformOrder> platformOrders = new HashSet<PlatformOrder>();
	
	@JsonIgnore
	@OneToMany(mappedBy="product")
	Set<MerchantProductGallery> merProGallerys = new HashSet<MerchantProductGallery>();

	@JsonIgnore
	@ManyToOne(fetch=FetchType.LAZY)
	private Gallery gallery; //一个产品有一个默认通道
	
	@JsonIgnore
	@ManyToMany(mappedBy="products",cascade={CascadeType.REMOVE,CascadeType.MERGE})
	@OrderBy("sort ASC")
	private Set<Gallery> gallerys = new HashSet<Gallery>(); //一个产品支持多个通道
	
	@JsonIgnore
	@OneToMany(mappedBy="product")
	private Set<Route> routes = new HashSet<Route>();

	@Column(nullable=false)
	private Boolean state; //启用状态 0禁用 1启用
	
	private Integer sort; //排序号
	
	@Column(name = "product_desc")
	private String desc;
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(updatable =false)
	private Product parent;
	
	@JsonIgnore
	@OneToMany(mappedBy = "parent",cascade = CascadeType.PERSIST)
	@OrderBy("sort ASC")
	private List<Product> children = new ArrayList<Product>();
	
	@Transient
	private Long parentId;
	
	@Transient
	private Long galleryId;
	
	@Transient
	private Long[] galleryIds;

	@Transient
	private boolean checked;
	
	public Product() {
		super();
	}
	
	public Product(Long id) {
		this.id = id;
	}
	
	public Product(Long id,String productName) {
		this.id = id;
		this.productName = productName;
	}
	
	public Product(Long id,Gallery gallery) {
		this.id = id;
		this.gallerys.add(gallery);
	}
	
	public Product(Long id,Integer type,String productMark,String productName,Long galleryId) {
		this.id = id;
		this.type = type;
		this.productMark = productMark;
		this.productName = productName;
		this.galleryId = galleryId;
	}
	
	public void init() {
		if (this.id == null){
			if (this.state == null){
				this.state = Boolean.FALSE;
			}
			if (this.type == null){
				this.type = 0;
			}
			if (this.sort == null) {
				this.sort = 0;
			}
		}
	}
	
	public static Product getProductByEnum(ProductsEnum pEnum) {
		if (pEnum == null)
			return null;
		Product product = new Product();
		product.setProductMark(pEnum.toString());
		product.setProductName(pEnum.getName());
		product.setDesc(pEnum.getDesc());
		product.setState(Boolean.TRUE);
		product.setSort(pEnum.getSort());
		product.setType(pEnum.getType());
		return product;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Set<PlatformOrder> getPlatformOrders() {
		return platformOrders;
	}

	public void setPlatformOrders(Set<PlatformOrder> platformOrders) {
		this.platformOrders = platformOrders;
	}

	public Set<MerchantProductGallery> getMerProGallerys() {
		return merProGallerys;
	}

	public void setMerProGallerys(Set<MerchantProductGallery> merProGallerys) {
		this.merProGallerys = merProGallerys;
	}

	public Gallery getGallery() {
		return gallery;
	}

	public void setGallery(Gallery gallery) {
		this.gallery = gallery;
	}

	public Set<Route> getRoutes() {
		return routes;
	}

	public void setRoutes(Set<Route> routes) {
		this.routes = routes;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public Product getParent() {
		return parent;
	}

	public void setParent(Product parent) {
		this.parent = parent;
	}

	public List<Product> getChildren() {
		return children;
	}

	public void setChildren(List<Product> children) {
		this.children = children;
	}

	public Set<Gallery> getGallerys() {
		return gallerys;
	}

	public void setGallerys(Set<Gallery> gallerys) {
		this.gallerys = gallerys;
	}

	public Long getParentId() {
		return parentId;
	}

	public void setParentId(Long parentId) {
		this.parentId = parentId;
		if (parentId != null)
			this.parent = new Product(parentId);
	}

	public Long getGalleryId() {
		return galleryId;
	}

	public void setGalleryId(Long galleryId) {
		this.galleryId = galleryId;
		if (galleryId != null)
			this.gallery = new Gallery(galleryId);
	}

	public Long[] getGalleryIds() {
		return galleryIds;
	}

	public void setGalleryIds(Long[] galleryIds) {
		this.galleryIds = galleryIds;
		if (galleryIds != null && galleryIds.length > 0) {
			for (Long galleryId : galleryIds) {
				this.gallerys.add(new Gallery(galleryId));
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
		return "Product [id=" + id + ", productMark=" + productMark + ", productName=" + productName + ", type=" + type
				+ ", state=" + state + ", sort=" + sort + ", desc=" + desc + "]";
	}

}
