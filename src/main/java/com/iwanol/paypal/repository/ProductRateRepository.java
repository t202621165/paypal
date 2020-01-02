package com.iwanol.paypal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.ProductRate;

@Repository
public interface ProductRateRepository extends BaseRepository<ProductRate, Long>{
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.ProductRate(r.id, r.productRate, p.id, "
			+ "p.productMark, p.productName, p.type) FROM ProductRate r RIGHT JOIN r.product p "
			+ "WITH r.merchant = ?1 ORDER BY p.sort")
	List<ProductRate> findByMerchant(Merchant merchant);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.ProductRate(r.id, r.merchant.id, r.product.id, "
			+ "r.productRate) FROM ProductRate r WHERE r.merchant = ?1 AND r.product IN(?2)")
	List<ProductRate> findByMerchantAndProduct(Merchant merchant, List<Product> products);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM ProductRate p WHERE p.product = ?1")
	int deleteByProduct(Product product);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM ProductRate r WHERE r.merchant = ?1")
	int deleteByMerchant(Merchant merchant);
}
