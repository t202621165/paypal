package com.iwanol.paypal.repository;

import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.Bank;
import com.iwanol.paypal.domain.Merchant;
import com.iwanol.paypal.domain.SettleMent;
import com.iwanol.paypal.domain.SettleMentReply;

@Repository
public interface SettleMentRepository extends BaseRepository<SettleMent, Long>{

	@Query(value = "SELECT new com.iwanol.paypal.domain.Bank(m.id,"
			+ "(SUM(s.amount)+SUM(s.cost)+0.00)) FROM SettleMent s "
			+ "LEFT JOIN s.merchant m WHERE s IN(?1) AND s.state = ?2 GROUP BY s.merchant")
	List<Bank> findAmountAndCostByIds(List<SettleMent> list,Integer state);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.SettleMent(s.id, s.serialNumber, s.amount, b.bankNumber) FROM SettleMent s "
			+ "RIGHT JOIN s.bank b WITH b.bankMark = 'ALIPAY' WHERE s IN(?1) AND s.state = 2")
	List<SettleMent> findWaitPayeeRecorde(List<SettleMent> list);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.SettleMent(s.id, s.serialNumber, s.amount, b.bankNumber,b.realName,b.bankMark) FROM SettleMent s "
			+ "RIGHT JOIN s.bank b WITH b.bankMark <> 'ALIPAY' WHERE s IN(?1) AND s.state = 2")
	List<SettleMent> findWaitPayeeRecordeByBank(List<SettleMent> list);
	
	@Query(value = "SELECT new com.iwanol.paypal.domain.SettleMent(s.id, s.serialNumber, s.amount,s.bankName, s.bankNumber,s.realName,s.bankMark,m.account,m.merchantKey) FROM SettleMent s "
			+ "INNER JOIN s.merchant m WHERE s IN(?1) AND s.state = -6 and m.id = s.merchant.id")
	List<SettleMent> findWaitAuditPayeeRecorde(List<SettleMent> list);
	
	@Query(value = "UPDATE SettleMent s SET s.state = ?2,s.complateDate = NOW() WHERE s IN(?3) AND s.state = ?1")
	@Modifying
	@Transactional
	int updateByIds(Integer state,Integer newState,List<SettleMent> list);
	
	@Query(value = "UPDATE SettleMent s SET s.state = ?2,s.complateDate = NOW(),s.settleMentReply = ?4 WHERE s IN(?3) AND s.state = ?1")
	@Modifying
	@Transactional
	int updateByIds(Integer state,Integer newState,List<SettleMent> list,SettleMentReply reply);
	
	@Query(value = "UPDATE SettleMent s SET s.replyState = true WHERE s.settleMentReply = ?1 AND s.state = 1")
	@Modifying
	@Transactional
	int updateStateByReply(SettleMentReply reply);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM SettleMent s WHERE s.bank = ?1")
	int deleteByBank(Bank bank);
	
	@Transactional
	@Modifying
	@Query(value = "DELETE FROM SettleMent s WHERE s.merchant = ?1")
	int deleteByMerchant(Merchant merchant);

	@Transactional
	@Modifying
	@Query(value = "DELETE FROM SettleMent s WHERE s.settleMentReply IN ?1")
	int deleteByReplys(List<SettleMentReply> list);

	@Query(value = "UPDATE SettleMent s SET s.state = 1,s.discription = ?2,s.replyState=1,s.complateDate=?3 WHERE s.id IN (?1) AND s.state = 2")
	@Modifying
	@Transactional
	int updateStateAndDesc(List<Long> ids, String description,Date commplate);
	
	@Query(value = "UPDATE SettleMent s SET s.state = 1,s.discription = ?2,s.replyState=1,s.complateDate=?3 WHERE s.serialNumber = ?1 AND s.state = 2")
	@Modifying
	@Transactional
	int updateStateAndDescBySerialNumber(String serialNumber, String description,Date commplate);
}
