package com.iwanol.paypal.repository;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.Notice;
import com.iwanol.paypal.domain.User;

@Repository
public interface NoticeRepository extends BaseRepository<Notice, Long>{

	@EntityGraph(value = "notcieWithUserAndMerchant", type = EntityGraphType.FETCH)
	Page<Notice> findAll(Specification<Notice> spec, Pageable pageable);
	
	@Query("SELECT new com.iwanol.paypal.domain.Merchant(m.id,m.account,m.email) "
			+ "FROM Notice n LEFT JOIN n.merchant m WHERE n.merchant IS NOT NULL GROUP BY m")
	List<Merchant> findAllMerchant();

	/**
	 * 开启/关闭消息
	 * @param id
	 * @return
	 */
	@Transactional
	@Query(value = "UPDATE notice SET state = CASE WHEN state=0 then 1 when state=1 then 0 else 0 end WHERE id=?1", nativeQuery = true)
	@Modifying
	int updateState(Long id);

	@Transactional
	@Query(value = "DELETE FROM Notice n WHERE n.user = ?1")
	@Modifying
	int deleteByUser(User user);
}
