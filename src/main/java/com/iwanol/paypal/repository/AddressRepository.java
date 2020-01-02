package com.iwanol.paypal.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Address;
import com.iwanol.paypal.domain.Merchant;

@Repository
public interface AddressRepository extends BaseRepository<Address, Long>{

	@EntityGraph(value = "addressWithMerchantAndMerchantBusiness", type = EntityGraphType.FETCH)
	Optional<Address> findByMerchant(Merchant merchant);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Address a WHERE a.merchant = ?1")
	int deleteByMerchant(Merchant merchant);
}
