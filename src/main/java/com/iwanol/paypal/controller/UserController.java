package com.iwanol.paypal.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.domain.User;
import com.iwanol.paypal.service.UserService;

@RestController
@RequestMapping(value = "user", method = RequestMethod.POST)
@PreAuthorize("hasAuthority('user.html')")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	/**
	 * 添加新用户
	 * @param user
	 * @return
	 */
	@PostMapping("/add")
	@PreAuthorize("hasAuthority('user/add')")
	public JSONObject addUser(User user) {
		return userService.saveEntity(user);
	}
	
	/**
	 * 删除用户
	 * @param user
	 * @return
	 */
	@PostMapping("/delete")
	@PreAuthorize("hasAuthority('user/delete')")
	public Map<String, Object> deleteUser(User user) {
		return userService.deleteEntity(user);
	}
	
	/**
	 * 开启/关闭用户
	 * @param user
	 * @return
	 */
	@PostMapping("/state")
	public Map<String, Object> updateUserState(User user) {
		return userService.updateState(user.getId());
	}
	
	/**
	 * 修改用户密码
	 * @param user
	 * @return
	 */
	@PostMapping("/pass")
	@PreAuthorize("hasAuthority('user/pass')")
	public Map<String, Object> updateUserPassword(User user) {
		return userService.updateUserPassword(user);
	}
	
	/**
	 * 给用户分配角色
	 * @param user
	 * @return
	 */
	@PostMapping("/roles")
	@PreAuthorize("hasAuthority('user/roles')")
	public Map<String, Object> updateUserRoles(User user) {
		return userService.updateUserRoles(user);
	}
	
}
