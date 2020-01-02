package com.iwanol.paypal.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.repository.GalleryRepository;
import com.iwanol.paypal.repository.MerchantProductGalleryRepository;
import com.iwanol.paypal.repository.OrderRepository;
import com.iwanol.paypal.repository.ProductRateRepository;
import com.iwanol.paypal.repository.ProductRepository;
import com.iwanol.paypal.repository.RouteRepository;
import com.iwanol.paypal.vo.OrderVo;

@Service
public class ProductService extends BaseServiceImpl<Product,Product> {

	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private RouteRepository routeRepository;
	@Autowired
	private GalleryRepository galleryRepository;
	@Autowired
	private ProductRateRepository productRateRepository;
	@Autowired
	private MerchantProductGalleryRepository merchantProductGalleryRepository;
	
	@Override
	public BaseRepository<Product, Long> getRepository() {
		return productRepository;
	}

	@SuppressWarnings("serial")
	@Override
	public Specification<Product> getSpecification(Product v) {
		return new Specification<Product>() {
			@Override
			public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = null;
				Integer type = v.getType();
				if (type != null && type != -1) {
					predicate = cb.equal(root.get("type").as(Integer.class), type);
					return cb.and(predicate);
				}
				return null;
			}
		};
	}
	
	@Transactional
	@Override
	public JSONObject saveEntity(Product t) {
		if (!addValidate(t)) {
			Integer sort = productRepository.findMaxSort();
			if (sort != null)
				t.setSort(sort++);
			productRepository.save(t);
			ReturnMsgEnum.success.setMsg("产品“"+t.getProductName()+"”，添加成功！");
		}else{
			ReturnMsgEnum.error.setMsg("标识“"+t.getProductMark()+"”不可用！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	public List<Product> findByType(Integer type) {
		return productRepository.findByTypeOrderBySort(type);
	}
	
	@Override
	@Transactional
	public JSONObject deleteEntity(Long id) {
		Optional<Product> optional = productRepository.findById(id);
		if (optional.isPresent()) {
			Product product = optional.get();
			Long count = orderService.findOrderCountBySpe(new OrderVo(null, null, id));
			if (count > 0) {
				ReturnMsgEnum.error.setMsg("当前产品近三个月内存在交易记录，不可删除！");
			}else{
				orderRepository.deleteByProduct(product);
				productRateRepository.deleteByProduct(product);
				routeRepository.deleteByProduct(product);
				merchantProductGalleryRepository.deleteByProduct(product);
				productRepository.deleteGalleryProducts(id);
				productRepository.delete(product);
				ReturnMsgEnum.success.setMsg("产品“"+product.getProductName()+"”已删除！");
			}
		}else{
			ReturnMsgEnum.error.setMsg("删除失败，当前产品不存在或已被删除！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 根据产品类型查询产品
	 * @param type
	 * @return
	 */
	public List<Product> findAllByType(Integer type) {
		return productRepository.findAllByType(type);
	}
	
	public Product findProductAndGallerysById(Long id) {
		Optional<Product> optional = productRepository.findProductById(id);
		if (optional.isPresent())
			return optional.get();
		return null;
	}
	
	public List<Product> findByParentOrderBySort(Product parent) {
		return productRepository.findByParentOrderBySort(parent);
	}
	
	/**
	 * 启用/禁用产品
	 * @param product
	 * @return
	 */
	public JSONObject updateState(Product product) {
		productRepository.updateState(product.getId());
		ReturnMsgEnum.success.setMsg(product.isChecked()?"产品已启用！":"产品已禁用！");
		return ReturnMsgEnum.returnMsg();
	}

	/**
	 * 产品添加校验
	 * 根据产品标识校验当前产品是否已存在
	 */
	@Override
	public boolean addValidate(Product t) {
		Optional<Product> optional = productRepository.findByProductMark(t.getProductMark());
		if (optional.isPresent()) {
			if (optional.get().getId().equals(t.getId()))
				return false;
			return true;
		}
		return false;
	}

	/**
	 * 更新产品默认通道
	 * @param product
	 * @return
	 */
	public JSONObject updateDefaultGallery(Product product) {
		Optional<Gallery> optional = galleryRepository.findById(product.getGalleryId());
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("设置失败，当前通道不存在或已被删除！").toJson();
		productRepository.updateDefaultGallery(product, optional.get());
		return ReturnMsgEnum.success.setMsg("设置成功！").toJson();
	}

}
