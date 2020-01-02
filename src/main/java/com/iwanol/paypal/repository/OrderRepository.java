package com.iwanol.paypal.repository;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.PlatformOrder;
import com.iwanol.paypal.domain.Product;

@Repository
public interface OrderRepository extends BaseRepository<PlatformOrder, Long> {

	@EntityGraph(value = "orderWithMerchantAndGalleryAndProduct", 
	type = EntityGraphType.FETCH)
	Page<PlatformOrder> findAll(Specification<PlatformOrder> spec, Pageable pageable);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM PlatformOrder o WHERE o.gallery = ?1")
	int deleteByGallery(Gallery gallery);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM PlatformOrder o WHERE o.product = ?1")
	int deleteByProduct(Product product);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM PlatformOrder o WHERE o.merchant = ?1")
	int deleteByMerchant(Merchant merchant);
	
	PlatformOrder findBySysOrderNumber(String sysOrderNumber);
}
