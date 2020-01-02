package com.iwanol.paypal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.vo.MerchantVo;

@Repository
public interface MerchantRepository extends BaseRepository<Merchant,Long> {
	
	Optional<Merchant> findByAccount(String account);
	
	Optional<Merchant> findByEmail(String email);
	
	@Query(value = "SELECT new com.iwanol.paypal.vo.MerchantVo(COUNT(*),m.state) FROM Merchant m GROUP BY m.state")
	List<MerchantVo> findMerchantCount();
	
	@Query(value = "SELECT m.email FROM Merchant m WHERE m.email != null AND m.email != '' AND m.state = ?1")
	List<String> findEmailByState(Integer state);

	@Transactional
	@Query(value = "UPDATE Merchant m SET m.passWord = ?2 WHERE m.id = ?1")
	@Modifying
	int updatePassWordById(Long id, String password);
	
	@Transactional
	@Query(value = "UPDATE Merchant m SET m.state = ?2 WHERE m.id = ?1")
	@Modifying
	int updateState(Long id, Integer state);
	
	@Transactional
	@Query(value = "UPDATE Merchant m SET m.merchant = null WHERE m = ?1")
	@Modifying
	int updateAngencyMerchant(Merchant merchant);

}
