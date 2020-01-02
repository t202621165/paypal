package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 管理员实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
		name = "userWithRole",
		attributeNodes = {@NamedAttributeNode(value = "roles")}
	),
	@NamedEntityGraph(
		name = "userWithRoleAndResource",
		attributeNodes = {@NamedAttributeNode(value = "roles", subgraph = "roleWithResource")},
		subgraphs = {
				@NamedSubgraph(
					name = "roleWithResource", 
					attributeNodes = {
						@NamedAttributeNode(value = "resources")
					}
				)
			}
	)
})
public class User implements UserDetails,Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	@Column(length=11,unique=true,nullable = false)
	private String userName; //用户名
	
	@Column(length=64)
	@NotNull(message="密码不能为空!")
	private String passWord; //密码
	
	@Column(nullable = false)
	private Boolean state; //启用状态
	
	@JsonIgnore
	@ManyToMany(mappedBy="users")
	private Set<Role> roles = new HashSet<Role>();
	
	@JsonIgnore
	@OneToMany(mappedBy="user")
	private Set<SettleMentReply> settleMentReplys = new HashSet<SettleMentReply>();
	
	@Transient
	private List<Resource> resources = new ArrayList<Resource>();
	
	@Transient
	private Long roleId;
	
	@Transient
	private Boolean type;
	
	@Transient
	private String confirmPass;
	
	@Transient
	private String msg;
	
	/**
	 * 获取系统用户
	 * @param mark
	 * @param email
	 * @return
	 */
	public static User getSystemUser(String mark, String email) {
		User user = new User();
		user.setUserName(mark);
		user.setState(Boolean.TRUE);
		user.setPassWord(new BCryptPasswordEncoder().encode(email));
		return user;
	}
	
	public User() {
		super();
	}
	
	public User(Long id) {
		this.id = id;
	}
	
	public User(String userName, String passWord) {
		this.userName = userName;
		this.passWord = passWord;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public Boolean getState() {
		return state;
	}

	public void setState(Boolean state) {
		this.state = state;
	}

	public Set<SettleMentReply> getSettleMentReplys() {
		return settleMentReplys;
	}

	public void setSettleMentReplys(Set<SettleMentReply> settleMentReplys) {
		this.settleMentReplys = settleMentReplys;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Boolean getType() {
		return type;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public String getConfirmPass() {
		return confirmPass;
	}

	public void setConfirmPass(String confirmPass) {
		this.confirmPass = confirmPass;
	}
	
	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	public boolean loginValidate() {
		if (userName == null || "".equals(userName)) {
			this.msg = "用户名不能为空！";
			return false;
		}
		if (passWord == null || "".equals(passWord)) {
			this.msg = "密码不能为空！";
			return false;
		}
		this.passWord = new BCryptPasswordEncoder().encode(passWord);
		return true;
	}

	public boolean passValidate(boolean isAdd) {
		if (isAdd) {
			if (userName == null || "".equals(userName)) {
				this.msg = "用户名不能为空！";
				return false;
			}
			if (userName.length() < 3 || userName.length() > 11) {
				this.msg = "用户名长度有误，3-11位！";
				return false;
			}
		}
		if (passWord == null || "".equals(passWord)) {
			this.msg = "密码不能为空！";
			return false;
		}
		if (passWord.length() < 6 || passWord.length() > 16) {
			this.msg = "密码长度有误，6-16位！";
			return false;
		}
		if (!passWord.equals(confirmPass)) {
			this.msg = "两次密码输入不一致！";
			return false;
		}
		this.state = Boolean.FALSE;
		this.passWord = new BCryptPasswordEncoder().encode(passWord);
		return true;
	}
	
	public boolean hasRole(Long roleId) {
		if (roles != null) {
			for(Role role : roles){
				Long id = role.getId();
				if (roleId != null && roleId.equals(id))
					return true;
			}
		}
			
		return false;
	}
	
	public boolean hasRole(String roleMark) {
		if (roles != null) {
			for(Role role : roles){
				if (roleMark != null && 
						roleMark.equals(role.getRoleMark()))
					return true;
			}
		}
		
		return false;
	}

	@JsonIgnore
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
		for(Role role : roles){
			simpleAuthorities.add(new SimpleGrantedAuthority(role.getRoleMark()));
			for(Resource resource : role.getResources()){
				simpleAuthorities.add(new SimpleGrantedAuthority(resource.getResourceMark()));
			}
		}
		return simpleAuthorities;
	}

	@Override
	public String getPassword() {
		return passWord;
	}

	@Override
	public String getUsername() {
		return userName;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	

	@Override
	public boolean isAccountNonLocked() {
		return state;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", passWord=" + passWord + ", state=" + state + "]";
	}

}
