package com.iwanol.paypal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.GalleryProductCost;

@Repository
public interface GalleryProductCostRepository extends BaseRepository<GalleryProductCost,Long> {

	
	@Query(value = "SELECT new com.iwanol.paypal.domain.GalleryProductCost(c.id,g.id,c.productId,c.payRate) FROM GalleryProductCost c LEFT JOIN c.gallery g WHERE c.gallery = ?1 AND c.productId IN (?2)")
	List<GalleryProductCost> findByGalleryAndProductId(Gallery gallery, List<Long> products);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM GalleryProductCost c WHERE c.gallery = ?1 AND c.productId NOT IN (?2)")
	int deleteByGallery(Gallery gallery,List<Long> products);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM GalleryProductCost c WHERE c.gallery = ?1")
	int deleteByGallery(Gallery gallery);
}
