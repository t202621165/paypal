package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * 平台商户偏重业务实体
 * @author leo
 *
 */
@Entity
public class MerchantBusiness implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	private String businessName; //业务名称
	
	private String businessDiscription;//业务描述
	
	@OneToMany(mappedBy="merchantBusiness")
	private Set<Merchant> merchants = new HashSet<Merchant>();

	public MerchantBusiness() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getBusinessDiscription() {
		return businessDiscription;
	}

	public void setBusinessDiscription(String businessDiscription) {
		this.businessDiscription = businessDiscription;
	}

	public Set<Merchant> getMerchants() {
		return merchants;
	}

	public void setMerchants(Set<Merchant> merchants) {
		this.merchants = merchants;
	}

}
