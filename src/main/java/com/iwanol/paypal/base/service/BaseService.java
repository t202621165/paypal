package com.iwanol.paypal.base.service;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.iwanol.paypal.base.vo.BaseVo;
import com.iwanol.paypal.until.PageData;

/**
 * 公共service
 * @author leo
 *
 * @param <T>
 */
public interface BaseService <T,V> {
	/**
	 * 新增
	 * @param t
	 * @return
	 */
	JSONObject saveEntity(T t);
	
	/**
	 * 更新实体
	 * @param t
	 * @return
	 */
	JSONObject updateEntity(T t, boolean validate);
	
	/**
	 * 通过ID删除实体
	 * @param id
	 */
	JSONObject deleteEntity(Long id);
	
	/**
	 * 通过实体删除
	 * @param t
	 */
	JSONObject deleteEntity(T t);
	
	/**
	 * 批量删除
	 * @param t
	 */
	void deleteEntityInBatch(List<T> t);
	
	/**
	 * 通过id查找实体
	 * @param id
	 * @return
	 */
	T findEntity(Long id);
	
	
	/**
	 * 查找集合
	 * @param ids
	 * @return
	 */
	List<T> findEntitys(List<Long> ids);
	
	/**
	 * 查询所有实体列表
	 * @return
	 */
	List<T> findEntitys();
	
	/**
	 * 分页
	 * @param pageable
	 * @return
	 */
	Map<String, Object> findEntityByPage(PageData pageData);
	
	/**
	 * 复杂条件分页查询
	 * @param spec
	 * @param pageData
	 * @return
	 */
	Map<String, Object> findEntityBySpec(PageData pageData,V v);
	
	/**
	 * 自定义复杂条件分页查询
	 * @param v
	 * @param isSumData
	 * @return
	 */
	Map<String, Object> findPageVo(BaseVo v,Boolean isSumData);
	
}
