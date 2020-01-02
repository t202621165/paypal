package com.iwanol.paypal.repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.vo.FundsVo;

@Repository
public interface BankRepository extends BaseRepository<Bank,Long> {
	Optional<Bank> findByMerchantIdAndBankType(Long id,int type);
	
	List<Bank> findByMerchantOrderByBankTypeDesc(Merchant merchant);
	
	@EntityGraph(value = "bankWithMerchantAndOrder", type =EntityGraphType.FETCH)
	Page<Bank> findByBankType(Boolean type, Pageable pageable);
	
	@Query(value = "SELECT new com.iwanol.paypal.vo.FundsVo(m.id, m.account, b.bankName, b.realName, b.bankNumber,  b.overMoney, "
			+ "IFNULL(SUM(o.amount),0.0)+0.0, IFNULL(SUM(o.merchantProfits),0.0)+0.0, IFNULL(SUM(o.platformProfits),0.0)+0.0, "
			+ "p.id, p.productMark, p.type) FROM Bank b LEFT JOIN b.merchant m LEFT JOIN m.platformOrders o WITH (o.state != 0 "
			+ "AND o.orderDate >= ?1 AND o.orderDate <= ?2) LEFT JOIN o.product p WHERE b.bankType = 1 AND b.overMoney > 0 "
			+ "AND b.bankNumber IS NOT NULL GROUP BY m.id,p.id ORDER BY b.overMoney DESC")
	List<FundsVo> find(Date startDate,Date endDate);
	
	@Query(value = "SELECT COUNT(*) FROM bank where merchant_id = ? and over_money > 0", nativeQuery = true)
	Long findCountByMerchant(Long merchantId);
	
	@Transactional
	@Query(value = "UPDATE bank SET bank_type = CASE WHEN id=?1 "
			+ "THEN 1 ELSE 0 END WHERE merchant_id = ?2", nativeQuery = true)
	@Modifying
	int updateBankType(Long id, Long merchantId);

	List<Bank> findByMerchantAndPayeeState(Merchant merchant, Boolean payeeState);

	Optional<Bank> findByIdAndMerchantAndPayeeState(Long bankId, Merchant merchant, boolean b);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM Bank b WHERE b.merchant = ?1")
	int deleteByMerchant(Merchant merchant);

	@Transactional
	@Query(value = "UPDATE bank SET payee_state = CASE WHEN payee_state = 0 THEN 1 "
			+ "WHEN payee_state = 1 THEN 0 ELSE 0 END WHERE id=?1", nativeQuery = true)
	@Modifying
	int updateBankPayeeState(Long id);
	
}