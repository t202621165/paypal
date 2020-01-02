package com.iwanol.paypal.controller;

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
import com.iwanol.paypal.domain.Product;
import com.iwanol.paypal.service.ProductService;
import com.iwanol.paypal.until.PageData;

@RestController
@RequestMapping(value = "product", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('product.html')")
public class ProductController{
	
	@Autowired
	private ProductService productService;
	
	@ModelAttribute
	public void getEntity(@RequestParam(value="id", required=false) Long id, 
			Map<String, Object> map, HttpServletRequest request) {
		if (id != null && "/product/edit".equals(request.getRequestURI())) {
			map.put("product", productService.findEntity(id));
		}
	}

	/**
	 * 分组查询所有产品
	 * @param pageData
	 * @param product
	 * @return
	 */
	@PostMapping("/list")
	public Map<String, Object> productList(PageData pageData, Product product) {
		return productService.findEntityBySpec(pageData, product);
	}
	/**
	 * 添加新产品
	 * @param product
	 * @return
	 */
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('product/add')")
	public JSONObject addProduct(Product product) {
		product.init();
		return productService.saveEntity(product);
	}
	/**
	 * 编辑产品
	 * @param product
	 * @return
	 */
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('product/edit')")
	public JSONObject editProduct(@ModelAttribute("product") Product product) {
		return productService.updateEntity(product, product.getId() == null);
	}
	/**
	 * 设置产品默认通道
	 * @param product
	 * @return
	 */
	@PostMapping("/default")
	@PreAuthorize("hasAuthority('product/edit')")
	public JSONObject defaultGallery(Product product) {
		return productService.updateDefaultGallery(product);
	}
	/**
	 * 开启/关闭当前产品
	 * @param product
	 * @return
	 */
	@PostMapping("/state")
	@PreAuthorize("hasAuthority('product/edit')")
	public JSONObject onOrOffProduct(Product product) {
		return productService.updateState(product);
	}
	
	/**
	 * 删除当前产品
	 * @param product
	 * @return
	 */
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('product/delete')")
	public JSONObject deleteProduct(Product product) {
		return productService.deleteEntity(product.getId());
	}
	
}
