package com.lxtx.dao;

import com.lxtx.model.CloudClearDetail;

public interface CloudClearDetailMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-11-07 12:14:22
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-07 12:14:22
     */
    int insert(CloudClearDetail record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-07 12:14:22
     */
    int insertSelective(CloudClearDetail record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-11-07 12:14:22
     */
    CloudClearDetail selectByPrimaryKey(Integer id);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-07 12:14:22
     */
    int updateByPrimaryKeySelective(CloudClearDetail record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-07 12:14:22
     */
    int updateByPrimaryKey(CloudClearDetail record);
}