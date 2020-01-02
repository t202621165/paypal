package com.iwanol.paypal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.Route;

@Repository
public interface RouteRepository extends BaseRepository<Route, Long>{

	@Query(value = "SELECT new com.iwanol.paypal.domain.Route(r.id, g.id, p.id, r.state, r.url,g.riskState,p.productMark) "
			+ "FROM Route r LEFT JOIN r.gallery g LEFT JOIN r.product p WHERE r.gallery = ?1 AND r.product IN (?2)")
	List<Route> findByGalleryAndProduct(Gallery gallery, List<Product> products);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Route r WHERE r.gallery = ?1 AND r.product NOT IN (?2)")
	int deleteByGallery(Gallery gallery,List<Product> products);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Route r WHERE r.gallery = ?1")
	int deleteByGallery(Gallery gallery);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Route r WHERE r.product = ?1")
	int deleteByProduct(Product product);
}
