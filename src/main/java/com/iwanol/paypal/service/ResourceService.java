package com.iwanol.paypal.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ResourcesEnum;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Resource;
import com.iwanol.paypal.repository.ResourceRepository;

@Service
public class ResourceService extends BaseServiceImpl<Resource,Object> {
	
	@Autowired
	private ResourceRepository resourceRepository;	

	@Override
	public BaseRepository<Resource, Long> getRepository() {
		return resourceRepository;
	}
	
	@CacheEvict(allEntries = true,cacheNames = "permission")
	public JSONObject saveALL(List<Resource> resources) {
		resourceRepository.saveAll(resources);
		return ReturnMsgEnum.success.setMsg("添加成功！").toJson();
	}

	@Override
	public Specification<Resource> getSpecification(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Cacheable(key="'permission.children'",cacheNames = "permission")
	public List<Resource> findResourcesByParent() {
		return resourceRepository.findByParentOrderBySort(null);
	}

	@Override
	public boolean addValidate(Resource t) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * 查询系统权限个数
	 * @return
	 */
	@Cacheable(key="'permission.count'",cacheNames = "permission")
	public Long count() {
		return resourceRepository.count();
	}

	/**
	 * 恢复系统权限
	 * @return
	 */
	@Transactional
	@CacheEvict(allEntries = true,cacheNames = "permission")
	public JSONObject refreshResource() {
		List<Resource> resources = resourceRepository.findAll();
		List<Resource> list = ResourcesEnum.getResource(resources);
		resourceRepository.saveAll(list);
		return ReturnMsgEnum.success.setMsg("系统权限恢复成功！").toJson();
	}

}