package com.iwanol.paypal.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Role;

@Repository
public interface RoleRepository extends BaseRepository<Role,Long> {
	
	@EntityGraph(value = "roleWithUser", type = EntityGraphType.FETCH)
	Optional<Role> findByRoleMark(String roleMark);
	
	@EntityGraph(value = "roleWithUser", type = EntityGraphType.FETCH)
	List<Role> findAll();

	@EntityGraph(value = "roleWithUserAndResource", type = EntityGraphType.FETCH)
	Page<Role> findAll(Pageable pageable);
	
	@EntityGraph(value = "roleWithUserAndResource", type = EntityGraphType.FETCH)
	Optional<Role> findOneById(Long id);
}
