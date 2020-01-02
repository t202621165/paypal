package com.iwanol.paypal.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Certificate;
import com.iwanol.paypal.domain.Merchant;

@Repository
public interface CertificateRepository extends BaseRepository<Certificate,Long> {

	Optional<Certificate> findByMerchant(Merchant merchant);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Certificate c WHERE c.merchant = ?1")
	int deleteByMerchant(Merchant merchant);

}
