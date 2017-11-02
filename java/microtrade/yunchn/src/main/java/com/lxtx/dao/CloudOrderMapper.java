package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudOrder;
import com.lxtx.model.CloudPerHalfHourProfit;
import com.lxtx.model.vo.CloudChnSumProfit;

public interface CloudOrderMapper {
	/**
	 * 根据主键删除 参数:主键 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-27 12:19:02
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * 插入，空属性也会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-27 12:19:02
	 */
	int insert(CloudOrder record);

	/**
	 * 插入，空属性不会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-11-27 12:19:02
	 */
	int insertSelective(CloudOrder record);

	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 * 
	 * @ibatorgenerated 2016-11-27 12:19:02
	 */
	CloudOrder selectByPrimaryKey(Integer id);

	/**
	 * 根据主键修改，空值条件不会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-27 12:19:02
	 */
	int updateByPrimaryKeySelective(CloudOrder record);

	/**
	 * 根据主键修改，空值条件会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-11-27 12:19:02
	 */
	int updateByPrimaryKey(CloudOrder record);

	List<CloudPerHalfHourProfit> queryForPageChnSum(@Param("uid") Integer id, @Param("begin") String begin,
			@Param("end") String end, @Param("end1") String end1);

	CloudPerHalfHourProfit queryChnSumByChnno(@Param("begin") String begin, @Param("end") String end,
			@Param("chnno") String chnno, @Param("end1") String end1);

	List<CloudOrder> queryForPageChnOrderList(@Param("chnno") String chnno, @Param("begin") String begin,
			@Param("end") String end);

	List<CloudOrder> queryForPageUserOrderList(@Param("uid") Integer uid);

	CloudChnSumProfit querySumChns(@Param("uid") Integer id, @Param("begin") String begin, @Param("end") String end);

	CloudChnSumProfit querySumChns2(@Param("uid") Integer id, @Param("begin") String begin, @Param("end") String end);

}