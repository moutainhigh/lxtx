package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudCoupon;

public interface CloudCouponMapper {
	/**
	 * 根据主键删除 参数:主键 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:34
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * 插入，空属性也会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:34
	 */
	int insert(CloudCoupon record);

	/**
	 * 插入，空属性不会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:34
	 */
	int insertSelective(CloudCoupon record);

	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:34
	 */
	CloudCoupon selectByPrimaryKey(Integer id);

	/**
	 * 根据主键修改，空值条件不会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:34
	 */
	int updateByPrimaryKeySelective(CloudCoupon record);

	/**
	 * 根据主键修改，空值条件会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-24 17:26:34
	 */
	int updateByPrimaryKey(CloudCoupon record);

	List<CloudCoupon> queryCloudCouponByStatus(@Param("uid") Integer uid, @Param("status") int status);

	CloudCoupon queryCloudCouponByUid(@Param("uid") Integer uid, @Param("date") String date);

	int queryCountCloudCouponByStatus(@Param("uid") Integer uid, @Param("status") int status);

	void updateStatusById(@Param("id") Integer id, @Param("status") int couponStatUseed,
			@Param("orderId") Integer integer);

	int queryCloudCouponCount(@Param("uid") Integer uid);

	CloudCoupon queryCloudCouponByUidAndDate(@Param("uid") Integer uid, @Param("date") String date);

	CloudCoupon queryRateCouponByUidAndDate(@Param("uid") Integer uid, @Param("date") String date);
}