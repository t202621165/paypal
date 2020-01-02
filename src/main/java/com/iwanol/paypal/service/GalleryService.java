package com.iwanol.paypal.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.base.vo.echarts.EchartsPie;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.GalleryProductCost;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.Route;
import com.iwanol.paypal.repository.GalleryProductCostRepository;
import com.iwanol.paypal.repository.GalleryRepository;
import com.iwanol.paypal.repository.MerchantProductGalleryRepository;
import com.iwanol.paypal.repository.OrderRepository;
import com.iwanol.paypal.repository.ProductRepository;
import com.iwanol.paypal.repository.RouteRepository;
import com.iwanol.paypal.vo.OrderVo;
import com.iwanol.paypal.vo.RateArray;

@Service
public class GalleryService extends BaseServiceImpl<Gallery,Gallery> {
	
	@Autowired
	private EntityManager entityManager;
	@Autowired
	private OrderService orderService;
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private RouteRepository routeRepository;
	@Autowired
	private GalleryRepository galleryRepository;
	@Autowired
	private ProductRepository productRepository;
	@Autowired
	private GalleryProductCostRepository galleryProductCostRepository;
	@Autowired
	private MerchantProductGalleryRepository merchantProductGalleryRepository;

	@Override
	public BaseRepository<Gallery, Long> getRepository() {
		return galleryRepository;
	}

	@SuppressWarnings("serial")
	@Override
	public Specification<Gallery> getSpecification(Gallery v) {
		return new Specification<Gallery>() {
			@Override
			public Predicate toPredicate(Root<Gallery> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				Predicate predicate = null;
				Boolean state = v.getState();
				if (state != null) {
					predicate = cb.equal(root.get("state").as(Boolean.class), state);
					return cb.and(predicate);
				}
				return null;
			}
		};
	}
	
	/**
	 * 新增通道
	 */
	@Override
	public JSONObject saveEntity(Gallery t) {
		if (!addValidate(t)) {
			Integer sort = galleryRepository.findMaxSort();
			if (sort != null)
				t.setSort(sort++);
			galleryRepository.save(t);
			ReturnMsgEnum.success.setMsg("通道“"+t.getGalleryName()+"”，添加成功！");
		}else{
			ReturnMsgEnum.error.setMsg("标识“"+t.getGalleryMark()+"”不可用！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 删除当前通道
	 */
	@Transactional
	@Override
	public JSONObject deleteEntity(Long id) {
		Optional<Gallery> optional = galleryRepository.findById(id);
		if (optional.isPresent()) {
			Gallery gallery = optional.get();
			Long count = orderService.findOrderCountBySpe(new OrderVo(null, id, null));
			if (count > 0) {
				ReturnMsgEnum.error.setMsg("当前通道近三个月内存在交易记录，不可删除！");
			}else{
				// 清除产品默认通道
				productRepository.updateDefaultGalleryByGallery(gallery);
				// 删除订单
				orderRepository.deleteByGallery(gallery);
				// 删除通道轮循信息
				routeRepository.deleteByGallery(gallery);
				// 删除通道费率
				galleryProductCostRepository.deleteByGallery(gallery);
				// 删除商户产品默认通道
				merchantProductGalleryRepository.deleteByGallery(gallery);
				// 删除当前通道
				galleryRepository.delete(gallery);
				ReturnMsgEnum.success.setMsg("通道“"+gallery.getGalleryName()+"”已删除！");
			}
		}else{
			ReturnMsgEnum.error.setMsg("删除失败，当前通道不存在或已被删除！");
		}
		
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 更新通道信息
	 */
	@Override
	public JSONObject updateEntity(Gallery t, boolean pass) {
		if (pass) {
			ReturnMsgEnum.error.setMsg("修改失败，当前通道不存在或已被删除！");
		}else{
			if (addValidate(t))
				return ReturnMsgEnum.error.setMsg("修改失败，通道标识“"+t.getGalleryMark()+"”不可用！").toJson();
			Optional<Product> optional = productRepository.findProductByProductMark("ebank");
			if (optional.isPresent())
				t.reSetProduct(optional.get());
			galleryRepository.save(t);
			ReturnMsgEnum.success.setMsg("修改成功！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	/**
	 * 通道启用/禁用
	 * @param gallery
	 * @return
	 */
	public JSONObject updateState(Gallery gallery) {
		galleryRepository.updateState(gallery.getId());
		ReturnMsgEnum.success.setMsg(gallery.isChecked()?"通道已启用":"通道已禁用！");
		return ReturnMsgEnum.returnMsg();
	}

	/**
	 * 通道添加校验galleryMark
	 */
	@Override
	public boolean addValidate(Gallery v) {
		Optional<Gallery> optional = galleryRepository.findByGalleryMark(v.getGalleryMark());
		if (optional.isPresent()) {
			if (optional.get().getId().equals(v.getId()))
				return false;
			return true;
		}
		return false;
	}
	
	/**
	 * 查询通道详情
	 * @param gallery
	 * @param model
	 */
	public void findGalleryDetails(Gallery gallery, Model model) {
		Optional<Gallery> optional = galleryRepository.findById(gallery.getId());
		if (optional.isPresent()) {
			List<RateArray> list = galleryRepository.findGalleryDetails(gallery);
			model.addAttribute("gallery", optional.get());
			model.addAttribute("details", list);
			model.addAttribute("products", productRepository.findProductByGallery(gallery));
			OrderVo vo = new OrderVo(gallery.getId(),entityManager);
			vo.reSetGroupByBuf(new StringBuffer(" AND temp.state != 0 GROUP BY temp.product ORDER BY amount DESC"));
			model.addAttribute("data", new EchartsPie(vo.findOrderSumDate(),true));
			vo.reSetGroupByBuf(new StringBuffer(" AND temp.state != 0"));
			model.addAttribute("total", vo.findOrderSumDate());
		}
	}

	/**
	 * 更新通道费率和路由信息
	 * @param list
	 * @return
	 */
	@Transactional
	public JSONObject updateRateAndRoute(List<RateArray> list) {
		List<GalleryProductCost> costs = new ArrayList<GalleryProductCost>();
		List<Route> routes = new ArrayList<Route>();
		Long galleryId = list.get(0).getGalleryId();
		Gallery gallery = galleryRepository.findById(galleryId).get();
		RateArray rateArray = new RateArray();
		rateArray.galleryProductAndIds(list);
		List<Long> productIds = rateArray.getProductIds();
		List<Product> products = rateArray.getProducts();
		List<GalleryProductCost> list1 = 
				galleryProductCostRepository.findByGalleryAndProductId(gallery, productIds);
		
		List<Route> list2 = routeRepository.findByGalleryAndProduct(gallery, products);
		
		list.stream().filter(rates -> rates.validationRateAndRoute()).forEach(rates -> {
			GalleryProductCost cost = null;
			Long gId = rates.getGalleryId();
			Long pId = rates.getProductId();
			for (GalleryProductCost entity : list1) {
				if (entity.getGalleryId().equals(gId) 
						&& entity.getProductId().equals(pId)) {
					cost = entity;
					break;
				}
			}
			costs.add(rates.getGalleryProductCost(cost));
			
			Route route = null;
			for (Route entity : list2) {
				if (entity.getGalleryId().equals(gId) 
						&& entity.getProductId().equals(pId)) {
					route = entity;					
					break;
				}
			}
			routes.add(rates.getRoute(route));
		});
		if (!costs.isEmpty() && !routes.isEmpty() && costs.size() == routes.size()) {
			galleryProductCostRepository.deleteByGallery(gallery, productIds);
			galleryProductCostRepository.saveAll(costs);
			
			routeRepository.deleteByGallery(gallery, products);
			routeRepository.saveAll(routes);
		}
		
		ReturnMsgEnum.success.setMsg("设置成功！");
		return ReturnMsgEnum.returnMsg();
	}
	
}
