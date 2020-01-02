package com.iwanol.paypal.repository;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.EntityGraph.EntityGraphType;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.iwanol.paypal.base.repository.BaseRepository;
import com.iwanol.paypal.domain.User;

@Repository
public interface UserRepository extends BaseRepository<User,Long> {
	
	@EntityGraph(value = "userWithRole", type = EntityGraphType.FETCH)
	List<User> findAll();
	
	@EntityGraph(value = "userWithRoleAndResource", type = EntityGraphType.FETCH)
	User findByUserName(String username);

	@Transactional
	@Query(value = "UPDATE user SET is_account_non_locked = CASE WHEN  is_account_non_locked=0 then 1  when is_account_non_locked=1 then 0 else 0 end WHERE id=?1", nativeQuery = true)
	@Modifying
	int updateState(Long id);
	
	@Transactional
	@Query(value = "DELETE FROM role_users WHERE  users_id = ?1 AND roles_id = ?2", nativeQuery = true)
	@Modifying
	int deleteUserRole(Long userId, Long roleId);
	
	@Transactional
	@Query(value = "DELETE FROM role_users WHERE  users_id = ?1", nativeQuery = true)
	@Modifying
	int deleteUserRole(Long userId);
	
	@Transactional
	@Query(value = "INSERT role_users(users_id,roles_id) VALUES(?1, ?2)", nativeQuery = true)
	@Modifying
	int addUserRole(Long userId, Long roleId);

	Optional<User> findByUserNameAndPassWord(String username, String password);

	@Transactional
	@Query(value = "UPDATE User u SET u.passWord = ?2 WHERE u.id = ?1")
	@Modifying
	int updatePassWord(Long id, String passWord);
}
