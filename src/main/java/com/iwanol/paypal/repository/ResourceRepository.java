package com.iwanol.paypal.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Resource;


@Repository
public interface ResourceRepository extends BaseRepository<Resource,Long> {

	@EntityGraph(value = "resourceWithChildrenAndChildren", type = EntityGraphType.FETCH)
	List<Resource> findByParentOrderBySort(Resource resource);
	

	@EntityGraph(value = "resourceWithchildern", type = EntityGraphType.FETCH)
	List<Resource> findByRolesRoleMark(String roleMark);
}
