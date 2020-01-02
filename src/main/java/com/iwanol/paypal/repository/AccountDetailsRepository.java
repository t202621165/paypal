package com.iwanol.paypal.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.AccountDetails;
import com.iwanol.paypal.domain.Merchant;

@Repository
public interface AccountDetailsRepository extends BaseRepository<AccountDetails, Long> {
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM AccountDetails a WHERE a.merchant = ?1")
	int deleteByMerchant(Merchant merchant);

}
