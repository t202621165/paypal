package com.iwanol.paypal.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.ProductRate;
import com.iwanol.paypal.repository.ProductRateRepository;

public class ProductRateService extends BaseServiceImpl<ProductRate, Object>{

	@Autowired
	private ProductRateRepository productRateRepository;
	
	@Override
	public BaseRepository<ProductRate, Long> getRepository() {
		return productRateRepository;
	}

	@Override
	public boolean addValidate(ProductRate t) {
		return false;
	}

	@Override
	public Specification<ProductRate> getSpecification(Object v) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public List<ProductRate> findByMerchant(Merchant merchant) {
		return productRateRepository.findByMerchant(merchant);
	}

}
