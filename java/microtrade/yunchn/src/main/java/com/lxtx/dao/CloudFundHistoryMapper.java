package com.lxtx.dao;

import java.math.BigDecimal;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudFundHistory;

public interface CloudFundHistoryMapper {
	/**
	 * 根据主键删除 参数:主键 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-12-06 18:57:00
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * 插入，空属性也会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-12-06 18:57:00
	 */
	int insert(CloudFundHistory record);

	/**
	 * 插入，空属性不会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-12-06 18:57:00
	 */
	int insertSelective(CloudFundHistory record);

	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 * 
	 * @ibatorgenerated 2016-12-06 18:57:00
	 */
	CloudFundHistory selectByPrimaryKey(Integer id);

	/**
	 * 根据主键修改，空值条件不会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-12-06 18:57:00
	 */
	int updateByPrimaryKeySelective(CloudFundHistory record);

	/**
	 * 根据主键修改，空值条件会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-12-06 18:57:00
	 */
	int updateByPrimaryKey(CloudFundHistory record);

	List<CloudFundHistory> queryForPageChnFillList(@Param("uid") Integer id, @Param("chnno") String chnno,
			@Param("begin") String begin, @Param("end") String end);

	int queryFillUserCount(@Param("uid") Integer id, @Param("chnno") String chnno, @Param("begin") String begin,
			@Param("end") String end);

	BigDecimal queryFillAmount(@Param("uid") Integer id, @Param("chnno") String chnno, @Param("begin") String begin,
			@Param("end") String end);

}