package com.iwanol.paypal.repository;

import java.math.BigDecimal;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.SystemSet;

@Repository
public interface SystemSetRepository extends BaseRepository<SystemSet, Long>{
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.SystemSet("
			+ "s.mark,s.webName,s.domainName,s.webCopyRight,"
			+ "s.serviceQQ,s.servicePhone) FROM SystemSet s")
	Optional<SystemSet> find();

	Optional<SystemSet> findByMark(String mark);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE system_set SET log_state = CASE WHEN  log_state = 0 then 1  when log_state = 1 then 0 else 0 end WHERE mark=?1", nativeQuery = true)
	int updateLogState(String mark);
	
	@Transactional
	@Modifying
	@Query(value = "UPDATE system_set SET route_state = CASE WHEN  route_state = 0 then 1  when route_state = 1 then 0 else 0 end WHERE mark=?1", nativeQuery = true)
	int updateRouteState(String mark);
	
	@Transactional
	@Modifying
	@Query(value = "update system_set set tail_amount_scope = ?1 where mark = ?2",nativeQuery = true)
	int updateTailAmountScope(BigDecimal scope,String mark);
	
}
