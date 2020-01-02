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
import com.iwanol.paypal.domain.Gallery;
import com.iwanol.paypal.service.GalleryService;
import com.iwanol.paypal.until.PageData;
import com.iwanol.paypal.vo.RateArray;

@RestController
@RequestMapping(value = "gallery", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('gallery.html')")
public class GalleryController{
	
	@Autowired
	private GalleryService galleryService;
	
	@ModelAttribute
	public void getEntity(@RequestParam(value="id", required=false) Long id, 
			Map<String, Object> map, HttpServletRequest request) {
		if (id != null && "/gallery/edit".equals(request.getRequestURI())) {
			Gallery gallery = galleryService.findEntity(id);
			if (gallery != null) {
				gallery.setProducts(null);
			}
			map.put("gallery", gallery);
		}
	}

	/**
	 * 查询所有通道
	 * @param pageData
	 * @param Gallery
	 * @return
	 */
	@PostMapping("/list")
	public Map<String, Object> galleryList(PageData pageData, Gallery Gallery) {
		return galleryService.findEntityBySpec(pageData, Gallery);
	}
	
	/**
	 * 添加新通道
	 * @param gallery
	 * @return
	 */
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('gallery/add')")
	public JSONObject addGallery(Gallery gallery) {
		gallery.init();
		return galleryService.saveEntity(gallery);
	}

	/**
	 * 开启/关闭当前通道
	 * @param product
	 * @return
	 */
	@PostMapping("/state")
	@PreAuthorize("hasAuthority('gallery/edit')")
	public JSONObject onOrOffGallery(Gallery gallery) {
		return galleryService.updateState(gallery);
	}
	
	/**
	 * 删除当前通道
	 * @param product
	 * @return
	 */
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('gallery/delete')")
	public JSONObject deleteGallery(Gallery gallery) {
		return galleryService.deleteEntity(gallery.getId());
	}
	
	/**
	 * 编辑当前通道
	 * @param product
	 * @return
	 */
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('gallery/edit')")
	public JSONObject editGallery(@ModelAttribute("gallery") Gallery gallery) {
		return galleryService.updateEntity(gallery, gallery.getId() == null);
	}
	
	/**
	 * 编辑通道费率和路由信息
	 * @param product
	 * @return
	 */
	@PostMapping("/details")
	@PreAuthorize("hasAuthority('gallery/edit')")
	public JSONObject updateGalleryRateAndRoute(@RequestBody List<RateArray> rates) {
		return galleryService.updateRateAndRoute(rates);
	}
}
