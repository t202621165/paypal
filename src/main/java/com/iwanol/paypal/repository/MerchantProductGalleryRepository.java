package com.iwanol.paypal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.MerchantProductGallery;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.vo.MerchantProductGalleryVo;

@Repository
public interface MerchantProductGalleryRepository extends BaseRepository<MerchantProductGallery,Long> {

	@EntityGraph(value = "withProductAndGallery", type = EntityGraphType.FETCH)
	List<MerchantProductGallery> findByMerchant(Merchant merchant);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.MerchantProductGallery(mpg.id, p.id, g.id) "
			+ "FROM MerchantProductGallery mpg LEFT JOIN mpg.product p "
			+ "LEFT JOIN mpg.gallery g WHERE mpg.merchant = ?1")
	List<MerchantProductGallery> findListByMerchant(Merchant merchant);
	
	@Query(value = "SELECT new com.iwanol.paypal.vo.MerchantProductGalleryVo(p.id, mpg.gallery.id, p.productName, "
			+ "p.productMark, p.type, g.id, g.galleryName) FROM MerchantProductGallery mpg RIGHT JOIN mpg.product p "
			+ "WITH mpg.merchant = ?1 LEFT JOIN p.gallerys g")
	List<MerchantProductGalleryVo> findList(Merchant merchant);
	
	Optional<MerchantProductGallery> findByMerchantAndProduct(Merchant merchant, Product product);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE merchant_product_gallery g LEFT JOIN product p ON "
			+ "g.product_id = p.id SET g.gallery_id = ?1 WHERE p.id = ?2 OR p.parent_id = ?2", 
			nativeQuery = true)
	int updateGallery(Long galleryId, Long productId);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM MerchantProductGallery g WHERE g.product = ?1")
	int deleteByProduct(Product product);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM MerchantProductGallery g WHERE g.gallery = ?1")
	int deleteByGallery(Gallery gallery);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM MerchantProductGallery g WHERE g.merchant = ?1")
	int deleteByMerchant(Merchant merchant);

}
