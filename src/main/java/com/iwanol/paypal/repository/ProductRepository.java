package com.iwanol.paypal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Product;

@Repository
public interface ProductRepository extends BaseRepository<Product,Long>{
	
	@EntityGraph(value = "productWithGalleryAndGallerys" ,type = EntityGraphType.FETCH)
	List<Product> findByTypeOrderBySort(Integer type);
	
	@EntityGraph(value = "productWithGalleryAndGallerys" ,type = EntityGraphType.FETCH)
	List<Product> findByParentOrderBySort(Product parent);
	
	@EntityGraph(value = "productWithGallerys" ,type = EntityGraphType.FETCH)
	Optional<Product> findProductById(Long id);
	
	@EntityGraph(value = "productWithChildren" ,type = EntityGraphType.FETCH)
	Optional<Product> findProductByProductMark(String productMark);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.Product(p.id, p.productName) "
			+ "FROM Product p WHERE p.type = ?1 ORDER BY p.sort")
	List<Product> findAllByType(Integer type);
	
	@EntityGraph(value = "productWithGallery" ,type = EntityGraphType.FETCH)
	Page<Product> findAll(@Nullable Specification<Product> spec, Pageable pageable);
	
	@EntityGraph(value = "productWithGallerys" ,type = EntityGraphType.FETCH)
	List<Product> findAll();
	
	/**
	 * 根据产品标识查询产品信息
	 * @param productMark
	 * @return
	 */
	Optional<Product> findByProductMark(String productMark);
	

	@Query(value = "SELECT new com.iwanol.paypal.domain.Product(p.id, p.type, p.productMark, p.productName, g.id) "
			+ "FROM Product p LEFT JOIN p.gallerys g WITH g = ?1 ORDER BY p.sort")
	List<Product> findProductByGallery(Gallery gallery);
	
	/**
	 * 开启/关闭产品
	 * @param id
	 * @return
	 */
	@Transactional
	@Query(value = "UPDATE product SET state = CASE WHEN  state=0 then 1 "
			+ "when state=1 then 0 else 0 end WHERE id=?1 OR parent_id=?1", nativeQuery = true)
	@Modifying
	int updateState(Long id);
	
	@Transactional
	@Query(value = "DELETE FROM gallery_products WHERE products_id = ?1", nativeQuery = true)
	@Modifying
	int deleteGalleryProducts(Long productId);
	
	@Query(value = "SELECT MAX(sort) FROM product", nativeQuery = true)
	Integer findMaxSort();

	/**
	 * 更新产品默认通道
	 * @param id
	 * @return
	 */
	@Transactional
	@Query(value = "UPDATE Product p SET p.gallery = ?2 WHERE p = ?1 OR p.parent = ?1")
	@Modifying
	int updateDefaultGallery(Product product, Gallery gallery);
	/**
	 * 更新产品默认通道
	 * @param id
	 * @return
	 */
	@Transactional
	@Query(value = "UPDATE Product p SET p.gallery = null WHERE p.gallery = ?1")
	@Modifying
	int updateDefaultGalleryByGallery(Gallery gallery);
}
