package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudFundHistory;

public interface CloudFundHistoryMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-11-01 16:03:02
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-01 16:03:02
     */
    int insert(CloudFundHistory record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-01 16:03:02
     */
    int insertSelective(CloudFundHistory record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-11-01 16:03:02
     */
    CloudFundHistory selectByPrimaryKey(Integer id);
    
    CloudFundHistory selectByOrderId(String orderId);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-01 16:03:02
     */
    int updateByPrimaryKeySelective(CloudFundHistory record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-01 16:03:02
     */
    int updateByPrimaryKey(CloudFundHistory record);

	List<CloudFundHistory> selectAllFundByUid(@Param("uid")Integer uid, @Param("id")Integer id);

	int updateByOrderId(@Param("orderId")String orderId);
}