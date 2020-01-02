package com.iwanol.paypal.service;

import java.util.Optional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.enums.ReturnMsgEnum;
import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.base.service.impl.BaseServiceImpl;
import com.iwanol.paypal.domain.Role;
import com.iwanol.paypal.repository.RoleRepository;

@Service
public class RoleService extends BaseServiceImpl<Role,Object> {
	
	@Value("${com.paypal.mark}")
	private String mark;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Override
	public BaseRepository<Role, Long> getRepository() {
		return roleRepository;
	}

	@Override
	public Specification<Role> getSpecification(Object obj) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@SuppressWarnings("serial")
	public Specification<Role> findCountByRoleMark(String roleMark) {
		return new Specification<Role>() {
			@Override
			public Predicate toPredicate(Root<Role> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				return cb.equal(root.get("roleMark").as(String.class), roleMark);
			}
		};
	}
	
	public JSONObject updateEntity(Role t) {
		t.validate();
		if (!ReturnMsgEnum.isSucc)
			return ReturnMsgEnum.returnMsg();
		Long roleId = t.getId();
		Optional<Role> optional = roleRepository.findById(roleId);
		if (!optional.isPresent())
			return ReturnMsgEnum.error.setMsg("要修改角色不存在或已被删除！").toJson();
		
		Role role = optional.get();
		String roleMark = t.getRoleMark();
		if (!role.getRoleMark().equals(roleMark)) {
			if (addValidate(t))
				return ReturnMsgEnum.error.setMsg("当前角色已存在，标识“"+roleMark+"”不可用！").toJson();
		}
		roleRepository.save(t);
		return ReturnMsgEnum.success.setMsg("修改成功！").toJson();
	}
	
	@Override
	public JSONObject saveEntity(Role t) {
		t.validate();
		if (!ReturnMsgEnum.isSucc)
			return ReturnMsgEnum.returnMsg();
		return super.saveEntity(t);
	}
	
	@Override
	public JSONObject deleteEntity(Long id) {
		Optional<Role> optional = roleRepository.findById(id);
		if (optional.isPresent()) {
			Role role = optional.get();
			if (role.getRoleMark().equals(mark))
				return ReturnMsgEnum.error.setMsg("不可删除系统默认角色！").toJson();
		}
		return super.deleteEntity(id);
	}

	public Role findOneById(Long id) {
		Optional<Role> optional = roleRepository.findOneById(id);
		if (optional.isPresent())
			return optional.get();
		return null;
	}

	@Override
	public boolean addValidate(Role t) {
		Long counts = roleRepository.count(findCountByRoleMark(t.getRoleMark()));
		return counts > 0;
	}

}
