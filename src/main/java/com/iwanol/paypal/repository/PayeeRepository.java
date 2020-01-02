package com.iwanol.paypal.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Payee;

@Repository
public interface PayeeRepository extends BaseRepository<Payee, Long> {

	Optional<Payee> findByMark(String mark);
	
	Optional<Payee> findByIsDefault(Boolean isDefault);

	@Transactional
	@Modifying
	@Query(value = "UPDATE payee SET is_default = CASE WHEN id = ?1 THEN 1 ELSE 0 END", nativeQuery = true)
	int payeeRepository(Long id);
	
}
