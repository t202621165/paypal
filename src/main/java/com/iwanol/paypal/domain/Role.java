package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;

/**
 * 角色实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "roleWithUser",
		attributeNodes = {@NamedAttributeNode(value = "users")}
	),
	@NamedEntityGraph(
		name = "roleWithUserAndResource",
		attributeNodes = {
			@NamedAttributeNode(value = "users"),
			@NamedAttributeNode(value = "resources")
		}
	)
})
public class Role implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;  //主键id
	
	@Column(unique = true,nullable = false)
	private String roleMark; //角色标识
	
	private String roleName; //角色名称
	
	private String roleDiscription; //角色描述
	
	@JsonIgnore
	@ManyToMany
	private Set<Resource> resources = new HashSet<Resource>();
	
	@ManyToMany
	private Set<User> users = new HashSet<User>();
	
	@Transient
	private Long[] userId;
	
	@Transient
	private Long[] resourceId;
	
	/**
	 * 角色添加/编辑参数验证
	 * @return
	 */
	public ReturnMsgEnum validate() {
		if (roleMark == null || "".equals(roleMark))
			return ReturnMsgEnum.error.setMsg("角色标识不能为空！");
		
		if (roleName == null || "".equals(roleName))
			return ReturnMsgEnum.error.setMsg("角色名称不能为空！");
		
		if (roleDiscription == null || "".equals(roleDiscription))
			return ReturnMsgEnum.error.setMsg("角色描述不能为空！");
		if (resourceId != null) {
			for (int i = 0; i < resourceId.length; i++) {
				resources.add(new Resource(resourceId[i]));
			}
		}
		if (userId != null) {
			for (int i = 0; i < userId.length; i++) {
				users.add(new User(userId[i]));
			}
		}
		return ReturnMsgEnum.success.setMsg("校验通过");
	}
	
	/**
	 * 添加用户
	 * @param user
	 */
	public void addUser(User user) {
		if (this.users == null)
			this.users = new HashSet<User>();
		
		this.users.add(user);
	}
	
	/**
	 * 获取系统角色
	 * @param mark
	 * @return
	 */
	public static Role getSystemRole(String mark) {
		Role role = new Role();
		role.setRoleMark(mark);
		role.setRoleName("超级管理员");
		role.setRoleDiscription("系统默认角色，拥有最高权限！");
		return role;
	}
	
	public Role() {
		super();
	}
	
	public Role(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleMark() {
		return roleMark;
	}

	public void setRoleMark(String roleMark) {
		this.roleMark = roleMark;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getRoleDiscription() {
		return roleDiscription;
	}

	public void setRoleDiscription(String roleDiscription) {
		this.roleDiscription = roleDiscription;
	}

	public Set<Resource> getResources() {
		return resources;
	}

	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}

	public Set<User> getUsers() {
		return users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	public Long[] getUserId() {
		return userId;
	}

	public void setUserId(Long[] userId) {
		this.userId = userId;
	}

	public Long[] getResourceId() {
		return resourceId;
	}

	public void setResourceId(Long[] resourceId) {
		this.resourceId = resourceId;
	}

	@Override
	public String toString() {
		return "Role [id=" + id + ", roleMark=" + roleMark + ", roleName=" + roleName + ", roleDiscription="
				+ roleDiscription + "]";
	}
	
}
