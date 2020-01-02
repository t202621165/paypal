 package com.iwanol.paypal.security;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

public class MyPermissionEvaluator implements PermissionEvaluator {
	@Override
	public boolean hasPermission(Authentication authentication,
			Object targetDomainObject, Object permission) {
		// TODO Auto-generated method stub
		return this.hasPermission(authentication, permission);
	}

	@Override
	public boolean hasPermission(Authentication authentication,
			Serializable targetId, String targetType, Object permission) {
		// TODO Auto-generated method stub
		return false;
	}
	
	   /**
	    * 简单的字符串比较，相同则认为有权限
	    */
	   private boolean hasPermission(Authentication authentication, Object permission) {
		   
	      Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
	      for (GrantedAuthority authority : authorities) {
	         if (authority.getAuthority().equals(permission)) {
	            return true;
	         }
	      }
	      return false;
	   }

}
