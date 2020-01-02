package com.iwanol.paypal.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.service.ResourceService;

@RestController
@RequestMapping(value = "/resource", method = RequestMethod.POST)
public class ResourceController {
	
	@Autowired
	private ResourceService resourceService;

	@PostMapping("/refresh")
	public JSONObject refresh() {
		return resourceService.refreshResource();
	}
}
