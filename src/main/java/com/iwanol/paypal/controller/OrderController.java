package com.iwanol.paypal.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.PlatformOrder;
import com.iwanol.paypal.service.OrderService;
import com.iwanol.paypal.vo.OrderVo;

@RestController
@RequestMapping(value = "order", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('order.html')")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	
	/**
	 * 订单列表
	 * @param vo
	 * @param request
	 * @return
	 */
	@PostMapping("/list")
	public Map<String, Object> orderList(OrderVo vo,HttpServletRequest request) {
		return orderService.findPageVo(vo,Boolean.TRUE);
	}
	
	/**
	 * 订单补发
	 * @param vo
	 * @param request
	 * @return
	 */
	@PostMapping("/send")
	@PreAuthorize("hasAuthority('order/send')")
	public JSONObject orderSend(PlatformOrder order) {
		return orderService.findOrderSendCount(order);
	}
	
	/**
	 * 订单退款
	 * @param order
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	@PostMapping("/refund")
	@PreAuthorize("hasAuthority('order/refund')")
	public Map<String,Object> orderRefund(PlatformOrder order) throws UnsupportedEncodingException{
		return orderService.orderRefund(order);
	}
	
}
