package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudChnBackCommission;

public interface CloudChnBackCommissionMapper {
	/**
	 * 根据主键删除 参数:主键 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-13 21:35:09
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * 插入，空属性也会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-13 21:35:09
	 */
	int insert(CloudChnBackCommission record);

	/**
	 * 插入，空属性不会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-13 21:35:09
	 */
	int insertSelective(CloudChnBackCommission record);

	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 * 
	 * @ibatorgenerated 2016-11-13 21:35:09
	 */
	CloudChnBackCommission selectByPrimaryKey(Integer id);

	/**
	 * 根据主键修改，空值条件不会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-13 21:35:09
	 */
	int updateByPrimaryKeySelective(CloudChnBackCommission record);

	/**
	 * 根据主键修改，空值条件会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-13 21:35:09
	 */
	int updateByPrimaryKey(CloudChnBackCommission record);

	List<CloudChnBackCommission> getCommissionByDate(@Param("day") int date);
}