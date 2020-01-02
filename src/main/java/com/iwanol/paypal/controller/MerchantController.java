package com.iwanol.paypal.controller;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.MerchantProductGallery;
import com.iwanol.paypal.service.MerchantService;
import com.iwanol.paypal.until.PageData;
import com.iwanol.paypal.vo.RateArray;

@RestController
@RequestMapping(value="merchant",method=RequestMethod.POST)
@PreAuthorize("hasAuthority('merchant.html')")
public class MerchantController {
	
	@Autowired
	private MerchantService merchantService;
	
	@ModelAttribute
	public void getEntity(@RequestParam(value="id", required=false) Long id, 
			Map<String, Object> map, HttpServletRequest request) {
		if (id != null && "/merchant/edit".equals(request.getRequestURI())) {
			Merchant merchant = merchantService.findEntity(id);
			map.put("merchant", merchant);
		}
	}

	/**
	 * 分页查询所有注册商户
	 * @param pageData
	 * @param merchant
	 * @return
	 */
	@PostMapping(value = "/list")
	public Map<String, Object> merchantList(PageData pageData, Merchant merchant){
		return merchantService.findEntityBySpec(pageData, merchant);
	}
	
	/**
	 * 切换商户状态
	 * @param product
	 * @return
	 */
	@PostMapping("/state")
	public JSONObject merchantState(Merchant merchant) {
		return merchantService.updateState(merchant);
	}
	
	/**
	 * 编辑当前商户信息
	 * @param product
	 * @return
	 */
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('profile.html')")
	public JSONObject editMerchant(@ModelAttribute("merchant") Merchant merchant) {
		return merchantService.updateEntity(merchant, merchant.getId() == null);
	}
	
	/**
	 * 修改商户登陆密码
	 * @param request
	 * @param merchant
	 * @return
	 */
	@PostMapping(value = "/pass")
	@PreAuthorize("hasAuthority('profile.html')")
	public JSONObject updatePassword(HttpServletRequest request, Merchant merchant){
		return merchantService.updatePassword(request, merchant);
	}
	/**
	 * 删除商户
	 * @param request
	 * @param merchant
	 * @return
	 */
	@PostMapping(value = "/delete")
	@PreAuthorize("hasAuthority('merchant/delete')")
	public JSONObject deleteMerchant(Merchant merchant){
		return merchantService.deleteEntity(merchant.getId());
	}
	
	/**
	 * 编辑商户费率
	 * @param request
	 * @param merchant
	 * @return
	 */
	@PostMapping(value = "/rate")
	@PreAuthorize("hasAuthority('profile.html')")
	public JSONObject updateMerchantRate(@RequestBody List<RateArray> rates){
		return merchantService.updateMerchantRate(rates);
	}
	
	/**
	 * 修改当前商户默认通道
	 * @param request
	 * @param merchant
	 * @return
	 */
	@PostMapping(value = "/gallery")
	@PreAuthorize("hasAuthority('profile.html')")
	public JSONObject updateMerchantGallery(MerchantProductGallery vo){
		return merchantService.updateMerchantProductGallery(vo);
	}
	
	/**
	 * 修改所有商户默认通道
	 * @param request
	 * @param merchant
	 * @return
	 */
	@PostMapping(value = "/gallerys")
	@PreAuthorize("hasAuthority('merchant/gallerys')")
	public JSONObject updateMerchantGallerys(MerchantProductGallery vo){
		return merchantService.updateMerchantProductGallerys(vo);
	}
	
}
