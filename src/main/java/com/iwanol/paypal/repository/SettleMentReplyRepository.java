package com.iwanol.paypal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.SettleMentReply;
import com.iwanol.paypal.domain.User;

@Repository
public interface SettleMentReplyRepository extends BaseRepository<SettleMentReply, Long>{

	@EntityGraph(value = "replyWithUserAndSettlement", type = EntityGraphType.FETCH)
	Page<SettleMentReply> findAll(Specification<SettleMentReply> spec, Pageable pageable);
	
	List<SettleMentReply> findByUser(User user);
	
	@Transactional
	@Query(value = "UPDATE settle_ment_reply SET state = 1 WHERE id = ?1", nativeQuery = true)
	@Modifying
	int updateState(Long id);
	
	@Transactional
	@Query(value = "DELETE FROM SettleMentReply s WHERE s.user = ?1")
	@Modifying
	int deleteByUser(User user);
}
