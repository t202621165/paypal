package com.iwanol.paypal.repository;

import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.MerchantBusiness;

@Repository
public interface BusenessRepository extends BaseRepository<MerchantBusiness, Long>{

}
