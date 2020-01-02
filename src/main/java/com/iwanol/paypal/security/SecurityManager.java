package com.iwanol.paypal.security;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.iwanol.paypal.domain.Resource;
import com.iwanol.paypal.domain.User;

/**
 * Security管理类
 * @author Administrator
 * 2018年6月1日 下午4:47:57
 */
public class SecurityManager {
	
	/**
	 * 判断是否是匿名用户
	 * @return
	 */
	public static boolean isAnonymous() {
		return  getAuthentication() instanceof AnonymousAuthenticationToken;
	}
	
	/**
	 * 获取用户认证上下文
	 * @return
	 */
	public static SecurityContext getSecurityContext() {
		return SecurityContextHolder.getContext();
	}
	
	/**
	 * 获取用户认证信息
	 * @return
	 */
	public static Authentication getAuthentication() {
		return getSecurityContext().getAuthentication();
	}
	
	public static Object getPrincipal(){
		 return getAuthentication().getPrincipal();
	}
	
	/**
	 * 获取当前登录用户
	 * @return
	 */
	public static User getUser() {
		return (User) getPrincipal();
	}
	
	/**
	 * 判断当前登录用户是否拥有某个角色
	 * @param roleId
	 * @return
	 */
	public static boolean hasRole(Long roleId) {
		return getUser().hasRole(roleId);
	}
	
	/**
	 * 判断当前登录用户是否拥有某个角色
	 * @param roleMark
	 * @return
	 */
	public static boolean hasRole(String roleMark) {
		return getUser().hasRole(roleMark);
	}
	
	/**
	 * 获取当前登录用户名
	 * @return
	 */
	public static String getUserName() {
		if (isAnonymous())
			return "匿名用户";
		return getUser().getUserName();
	}
	
	public static boolean verifyPwd(String password) {
		BCryptPasswordEncoder pwdEncoder = new BCryptPasswordEncoder();
		String pwd = getRequest().getParameter("passWord");
		if (!StringUtils.isEmpty(pwd))
			return pwdEncoder.matches(pwd, password);
		
		return true;
	}
	
	/**
	 * 获取当前用的的所有权限
	 * @return
	 */
	public static List<Resource> getUserResource() {
		return getUser().getResources();
	}
	
	/**
	 * 获取当前Request
	 * @return
	 */
	public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
    }
	
	/**
	 * 获取当前登录用户的session
	 * @return
	 */
	public static HttpSession getSession() {
		return getRequest().getSession();
	}
	
	/**
	 * 获取当前登录用户session的ID
	 * @return
	 */
	public static String getSessionId() {
		return getSession().getId();
	}
	
	/**
	 * 将数据保存至session
	 * @param key
	 * @param value
	 */
	public static void setValue2Session(String key, Object value) {
		getSession().setAttribute(key, value);
	}
	
	/**
	 * 从session中获取数据
	 * @param key
	 * @return
	 */
	public static Object getValueFromSession(String key) {
		return getSession().getAttribute(key);
	}

	/**
	 * 删除session中指定数据
	 * @param name
	 */
	public static void removeSession(String name) {
		getSession().removeAttribute(name);
	}
}
