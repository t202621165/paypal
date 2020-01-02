package com.iwanol.paypal.controller;

import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.SystemSet;
import com.iwanol.paypal.service.SystemSetService;

@RestController
@RequestMapping(value="system", method=RequestMethod.POST)
@PreAuthorize("hasAuthority('system.html')")
public class SystemController {

	@Autowired
	private SystemSetService systemSetService;
	
	@ModelAttribute
	public void getEntity(Map<String, Object> map, HttpServletRequest request) {
		if ("/system/edit".equals(request.getRequestURI())) {
			SystemSet systemSet = systemSetService.findByMark();
			map.put("systemSet", systemSet);
		}
	}
	
	/**
	 * 日志监控开关
	 * @return
	 */
	@PostMapping("/logState")
	@PreAuthorize("hasAuthority('system/edit')")
	public JSONObject logState(@RequestParam(value="checked") boolean checked) {
		return systemSetService.updateLogState(checked);
	}
	
	/**
	 * 通道轮循开关
	 * @return
	 */
	@PostMapping("/routeState")
	@PreAuthorize("hasAuthority('system/edit')")
	public JSONObject routeState(@RequestParam(value="checked") boolean checked) {
		return systemSetService.updateRouteState(checked);
	}
	
	/**
	 * 风控范围设置
	 * @param scope
	 * @return
	 */
	@PostMapping("/scope")
	@PreAuthorize("hasAuthority('system/edit')")
	public JSONObject tailScope(@RequestParam(value="scope") BigDecimal scope){
		return systemSetService.updateTailAmountScope(scope);
	}
	
	/**
	 * 编辑系统设置
	 * @param systemSet
	 * @return
	 */
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('system/edit')")
	public JSONObject systemEdit(@ModelAttribute("systemSet") SystemSet systemSet) {
		return systemSetService.updateEntity(systemSet, systemSet.getId() == null);
	}
}
