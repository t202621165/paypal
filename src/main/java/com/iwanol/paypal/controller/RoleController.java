package com.iwanol.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.Role;
import com.iwanol.paypal.service.RoleService;
import com.iwanol.paypal.until.PageData;

@RestController
@RequestMapping(value = "role", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('role.html')")
public class RoleController {
	
	@Autowired
	private RoleService roleService;
	
	@PostMapping("/list")
	public Map<String, Object> roleList(PageData pageData) {
		return roleService.findEntityByPage(pageData);
	}
	
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('role/add')")
	public JSONObject addRole(Role role) {
		return roleService.saveEntity(role);
	}
	
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('role/delete')")
	public JSONObject deleteRole(Role role) {
		return roleService.deleteEntity(role);
	}
	
	@PostMapping("/edit")
	@PreAuthorize("hasAuthority('role/edit')")
	public JSONObject updateRole(Role role) {
		return roleService.updateEntity(role);
	}

}
