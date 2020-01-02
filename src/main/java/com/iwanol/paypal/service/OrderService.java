package com.iwanol.paypal.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.base.vo.echarts.EchartsPie;
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.PlatformOrder;
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.domain.SystemSet;
import com.iwanol.paypal.repository.OrderRepository;
import com.iwanol.paypal.until.CommonUtil;
import com.iwanol.paypal.until.DateUtil;
import com.iwanol.paypal.until.HttpSender;
import com.iwanol.paypal.until.MD5Util;
import com.iwanol.paypal.until.ToolUtil;
import com.iwanol.paypal.vo.OrderVo;

@Service
public class OrderService extends BaseServiceImpl<PlatformOrder,OrderVo>{
	
	@Autowired
	private OrderRepository orderRepository;
	@Autowired
	private SystemSetService systemSetService;
	@Autowired
	private EntityManager entityManager;

	@Override
	public BaseRepository<PlatformOrder, Long> getRepository() {
		return orderRepository;
	}
	
	@SuppressWarnings("serial")
	@Override
	public Specification<PlatformOrder> getSpecification(OrderVo vo) {
		return new Specification<PlatformOrder>() {
			@Override
			public Predicate toPredicate(Root<PlatformOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				List<Predicate> list = new ArrayList<Predicate>();
				list.add(cb.greaterThanOrEqualTo(root.get("orderDate").as(Date.class), vo.getStartDate()));
				list.add(cb.lessThanOrEqualTo(root.get("orderDate").as(Date.class), vo.getEndDate()));
				// 根据商户查询订单
				Long merchantId = vo.getMerchantId();
				if (merchantId != null) {
					list.add(cb.equal(root.get("merchant").as(Merchant.class), new Merchant(merchantId)));
				}
				// 根据充值产品查询订单
				Long productId = vo.getProductId();
				if (productId != null) {
					Predicate p1 = cb.equal(root.get("product").as(Product.class), new Product(productId));
					Predicate p2 = cb.equal(root.get("product").get("parent").as(Product.class), new Product(productId));
					list.add(cb.or(p1,p2));
				}
				// 根据产品通道查询订单
				Long galleryId = vo.getGalleryId();
				if (galleryId != null) {
					list.add(cb.equal(root.get("gallery").as(Gallery.class), new Gallery(galleryId)));
				}
				// 根据状态值查询订单
				Integer state = vo.getState();
				if (state != null) {
					list.add(cb.equal(root.get("state").as(Integer.class), state));
				}
				//根据订单金额查询订单
				BigDecimal startAmount = vo.getStartAmount();
				if (startAmount != null) {
					list.add(cb.greaterThanOrEqualTo(root.get("amount").as(BigDecimal.class), startAmount));
				}
				BigDecimal endAmount = vo.getEndAmount();
				if (endAmount != null) {
					list.add(cb.lessThanOrEqualTo(root.get("amount").as(BigDecimal.class), endAmount));
				}
				
				Predicate[] predicates = new Predicate[list.size()];
				predicates = list.toArray(predicates);
				return cb.and(predicates);
			}
			
		};
	}
	
	/**
	 * 根据条件查询订单数量
	 * @param vo
	 * @return
	 */
	public Long findOrderCountBySpe(OrderVo vo) {
		return orderRepository.count(getSpecification(vo));
	}

	@Override
	public boolean addValidate(PlatformOrder t) {
		return false;
	}

	public EchartsPie findOrderDayReport() {
		String[] dates = DateUtil.getDayArray(15);
		OrderVo vo = new OrderVo(dates[0]+" 00:00:00",dates[14]+" 23:59:59",entityManager);
		vo.reSetGroupByBuf(new StringBuffer(" AND temp.state != 0 GROUP BY DATE_FORMAT(temp.orderDate,'%Y-%m-%d')"));
		EchartsPie echarts = new EchartsPie(vo.findOrderSumDate(), dates);
		return echarts;
	}
	
	public EchartsPie findOrderProductDayReport() {
		OrderVo vo = new OrderVo(entityManager);
		vo.reSetGroupByBuf(new StringBuffer(" AND temp.state != 0 GROUP BY temp.product ORDER BY amount DESC"));
		EchartsPie echarts = new EchartsPie(vo.findOrderSumDate(), true);
		return echarts;
	}

	public JSONObject findOrderSendCount(PlatformOrder order) {
		try {
			if (order != null) {
				Optional<PlatformOrder> optional = orderRepository.findById(order.getId());
				if (optional.isPresent()){
					SystemSet system = systemSetService.findByMark();
					if (system != null){
						PlatformOrder entity = optional.get();
						Map<String,Object> map = new HashMap<String, Object>();
						Merchant merchant = entity.getMerchant();
						String domain = system.getPayDomainName();
						if(domain.contains("http")){
							domain = domain +"/send";
						}else{
							domain = "http://"+domain+"/send";
						}
						String merchantOrder = entity.getMerchantOrderNumber();
						String merchantKey = merchant.getMerchantKey();
						String signStr = CommonUtil.getBuffer(merchantOrder,entity.getAmount()).toString();
						String sign = MD5Util.sign(signStr, merchantKey, "UTF-8");
						map.put("merchantOrder", merchantOrder);
						map.put("sign", sign);
						String json = HttpSender.getHttpSender().connection(domain, "GET", map);
						JSONObject jsonObj = JSON.parseObject(json);
						if ("10035".equals(jsonObj.get("status"))) {
							ReturnMsgEnum.success.setMsg("补发成功！");
						}else{
							ReturnMsgEnum.error.setMsg(URLDecoder.decode(jsonObj.get("message").toString(),"utf-8"));
						}
					}
				}else{
					ReturnMsgEnum.error.setMsg("补发失败，订单不存在！");
				}
			}else{
				ReturnMsgEnum.error.setMsg("暂无补发记录");
			}
		} catch (Exception e) {
			e.printStackTrace();
			ReturnMsgEnum.error.setMsg("补发失败，请检查系统设置！");
		}
		return ReturnMsgEnum.returnMsg();
	}
	
	public Map<String,Object> orderRefund(PlatformOrder order) throws UnsupportedEncodingException{
		SystemSet systemSet = systemSetService.findByMark();
		Map<String, Object> map = new HashMap<String, Object>();
		if (systemSet != null && !StringUtils.isEmpty(systemSet.getPayDomainName())) {
			PlatformOrder platformOrder = orderRepository.findBySysOrderNumber(order.getSysOrderNumber());
			map.put("party_order_number", order.getPartyOrderNumber());
			map.put("sys_order_number", order.getSysOrderNumber());
			map.put("discription", order.getOrderDiscription());
			if(platformOrder != null){
				String sign = MD5Util.sign(ToolUtil.getToolUtil().formatUrlMap(map, null, true, false, false),
						platformOrder.getGallery().getGalleryMD5Key(), "utf-8");
				map.put("sign", sign);
				String domain = systemSet.getPayDomainName()+"/api/refund";
				if(!domain.contains("http")){
					domain = "http://"+domain+"/api/refund";
				}
				map.put("discription",URLEncoder.encode(order.getOrderDiscription(),"utf-8"));
				String result = HttpSender.getHttpSender().connection(domain,"GET",map);				
				if(!StringUtils.isEmpty(result)){
					JSONObject ob = JSONObject.parseObject(result);
					if(ob.getString("status").equals("10010")){
						map.put("code","10010");
						map.put("msg",URLDecoder.decode(ob.getString("message"),"utf-8"));
					}else if(ob.getString("status").equals("10026")){
						map.put("code","10026");
						map.put("msg",URLDecoder.decode(ob.getString("message"),"utf-8"));
					}else if(ob.getString("status").equals("10040")){
						map.put("code","10040");
						map.put("msg",URLDecoder.decode(ob.getString("message"),"utf-8"));
					}else if(ob.getString("status").equals("10041")){
						map.put("code","10041");
						map.put("url",ob.getString("url"));
					}
				}else{
					map.put("code", "405");
					map.put("msg", "退款请求失败");
				}
			}else{
				map.put("code","10040");
				map.put("msg","该笔订单不存在,拒绝退款");
			}
		} else {
			map.put("code", "405");
			map.put("msg", "请求退款网关失败,请检查网关域名是否正确");
		}
		return map;
	}
	
}
