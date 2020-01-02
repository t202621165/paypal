package com.iwanol.paypal.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToOne;

/**
 * 平台商户地址实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "addressWithMerchantAndMerchantBusiness",
		attributeNodes = {@NamedAttributeNode(
								value = "merchant", subgraph = "merchantWithMerchantBusiness")},
		subgraphs = {
			@NamedSubgraph(
				name = "merchantWithMerchantBusiness",
				attributeNodes = {
					@NamedAttributeNode(value = "merchantBusiness")
				}
			)
		}
	)
})
public class Address implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	private String province; //省份
	
	private String city; //城市
	
	private String street; //街道
	
	@OneToOne(optional = false)
	@JoinColumn(unique = true)
	private Merchant merchant;

	public Address() {
		super();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public Merchant getMerchant() {
		return merchant;
	}

	public void setMerchant(Merchant merchant) {
		this.merchant = merchant;
	}

	@Override
	public String toString() {
		return "Address [id=" + id + ", province=" + province + ", city=" + city + ", street=" + street + "]";
	}
	
}
