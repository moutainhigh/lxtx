package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudOrder;

public interface CloudOrderMapper {
	/**
	 * 根据主键删除 参数:主键 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:48
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * 插入，空属性也会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:48
	 */
	int insert(CloudOrder record);

	/**
	 * 插入，空属性不会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:48
	 */
	int insertSelective(CloudOrder record);

	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:48
	 */
	CloudOrder selectByPrimaryKey(Integer id);

	/**
	 * 根据主键修改，空值条件不会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:48
	 */
	int updateByPrimaryKeySelective(CloudOrder record);

	/**
	 * 根据主键修改，空值条件会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-26 18:02:48
	 */
	int updateByPrimaryKey(CloudOrder record);

	List<CloudOrder> selectByLimit(CloudOrder cloudOrder);

	List<CloudOrder> selectByUpperLimit(int indexValue);

	List<CloudOrder> selectByDownLimit(int indexValue);

	List<CloudOrder> selectAllOrderByUId(@Param("uid") Integer uid, @Param("id") Integer id);

	List<CloudOrder> selectAllOrderByUIdAndStatus(@Param("uid") Integer uid, @Param("subject") String subject,
			@Param("status") Integer status);

	int countNewOrder(@Param("uid") Integer id, @Param("subject") String subject);

	int markOrderInProcess(@Param("subject") String subject, @Param("index") Integer index);

	int selectCountByUidAndSubAndStatus(@Param("uid") Integer integer, @Param("subject") String subject,
			@Param("status") int orderStatUntreat);

	int selectCountByUid(Integer uid);

}