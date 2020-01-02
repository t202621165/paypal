package com.iwanol.paypal.domain;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedSubgraph;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iwanol.paypal.base.enums.ResourcesEnum;

/**
 * 系统资源实体
 * @author leo
 *
 */
@Entity
@NamedEntityGraphs({
	@NamedEntityGraph(
			name = "resourceWithchildern",
			attributeNodes = {@NamedAttributeNode(value = "childern")}
		),
	@NamedEntityGraph(
			name = "resourceWithChildrenAndChildren",
			attributeNodes = {@NamedAttributeNode(value = "childern", subgraph = "ChildrenWithChildren")},
			subgraphs = {@NamedSubgraph(name = "ChildrenWithChildren",attributeNodes = {@NamedAttributeNode(value = "childern")})}
		)
})
public class Resource implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id; //主键id
	
	private String resourceMark; //资源标识
	
	private String resourceName; //资源名称
	
	private Integer sort; //排序
	
	@JsonIgnore
	@ManyToOne(fetch = FetchType.LAZY)
	private Resource parent; //父级id
	
	private String resourceIcon; //资源ico图标
	
	@JsonIgnore
	@OneToMany(mappedBy="parent",cascade={CascadeType.REMOVE,CascadeType.PERSIST})
	@OrderBy("sort ASC")
	private Set<Resource> childern = new HashSet<Resource>();
	
	@JsonIgnore
	@ManyToMany(mappedBy="resources")
	private Set<Role> roles = new HashSet<Role>();
	
	/**
	 * 清除实体关系 做级联删除
	 */
	public void clearChildern(){
		this.parent.getChildern().remove(this);
	}
	
	public static Resource getResourceByEnum(ResourcesEnum rEnum) {
		Resource resource = new Resource();
		Long id = rEnum.getId();
		if (id != null)
			resource.setId(id);
		resource.setResourceIcon(rEnum.getIcon());
		resource.setResourceMark(rEnum.getMark());
		resource.setResourceName(rEnum.getName());
		resource.setSort(rEnum.getSort());
		return resource;
	}

	public Resource() {
		super();
	}
	
	public Resource(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getResourceMark() {
		return resourceMark;
	}

	public void setResourceMark(String resourceMark) {
		this.resourceMark = resourceMark;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public Resource getParent() {
		return parent;
	}

	public void setParent(Resource parent) {
		this.parent = parent;
	}

	public Set<Resource> getChildern() {
		return childern;
	}

	public void setChildern(Set<Resource> childern) {
		this.childern = childern;
	}

	public String getResourceIcon() {
		return resourceIcon;
	}

	public void setResourceIcon(String resourceIcon) {
		this.resourceIcon = resourceIcon;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Override
	public String toString() {
		return "Resource [id=" + id + ", resourceMark=" + resourceMark + ", resourceName=" + resourceName + ", sort="
				+ sort + ", resourceIcon=" + resourceIcon + "]";
	}
	
}
