package com.iwanol.paypal.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

/**
 * 商户个人认证或企业认证实体(图片资源存于monggodb)
 * @author leo
 *
 */
@Entity
public class Certificate implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(nullable = false, unique = true, length = 18)
	private String idNumber; // 身份证号码
	
	@Column(nullable = false, length = 6)
	private String realName; // 真实姓名
	
	private String frontCardImgUrl; //法人身份证图片正面
	
	private String afterCardImgUrl; //法人身份证图片反面
	
	private String frontLicense; //企业营业执照正面
	
	private String afterLicense; //企业营业执照反面
	
	@OneToOne(optional = false)
	@JoinColumn(unique = true)
	private Merchant merchant;

	public Certificate() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(String idNumber) {
		this.idNumber = idNumber;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getFrontCardImgUrl() {
		return frontCardImgUrl;
	}

	public void setFrontCardImgUrl(String frontCardImgUrl) {
		this.frontCardImgUrl = frontCardImgUrl;
	}

	public String getAfterCardImgUrl() {
		return afterCardImgUrl;
	}

	public void setAfterCardImgUrl(String afterCardImgUrl) {
		this.afterCardImgUrl = afterCardImgUrl;
	}

	public String getFrontLicense() {
		return frontLicense;
	}

	public void setFrontLicense(String frontLicense) {
		this.frontLicense = frontLicense;
	}

	public String getAfterLicense() {
		return afterLicense;
	}

	public void setAfterLicense(String afterLicense) {
		this.afterLicense = afterLicense;
	}
	
	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

}
