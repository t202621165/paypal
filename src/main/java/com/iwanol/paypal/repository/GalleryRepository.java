package com.iwanol.paypal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.vo.RateArray;

@Repository
public interface GalleryRepository extends BaseRepository<Gallery,Long> {

	/**
	 * 根据通道标识查询通道
	 * @param galleryMark
	 * @return
	 */
	Optional<Gallery> findByGalleryMark(String galleryMark);
	
	@Query(value = "SELECT new com.iwanol.paypal.vo.RateArray(p.id,p.productName,p.productMark,p.type,"
			+ "r.state,r.url,c.payRate) FROM Product p LEFT JOIN p.routes r WITH r.gallery = ?1 "
			+ "LEFT JOIN r.gallery g LEFT JOIN g.galleryProductCosts c WITH c.productId = p.id")
	List<RateArray> findGalleryDetails(Gallery g);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.Gallery(g.id, g.galleryName, p.id) "
			+ "FROM Gallery g Right JOIN g.products p WHERE p = ?1 ORDER BY g.sort")
	List<Gallery> findGalleryByProduct(Product p);
	
	@Query(value = "SELECT MAX(sort) FROM gallery", nativeQuery = true)
	Integer findMaxSort();
	
	/**
	 * 开启/关闭产品
	 * @param id
	 * @return
	 */
	@Transactional
	@Query(value = "UPDATE gallery SET state = CASE WHEN state=0 then 1 when state=1 then 0 else 0 end WHERE id=?1", nativeQuery = true)
	@Modifying
	int updateState(Long id);
}
