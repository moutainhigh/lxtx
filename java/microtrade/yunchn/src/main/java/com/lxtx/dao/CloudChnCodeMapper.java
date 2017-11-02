package com.lxtx.dao;

import java.util.List;

import com.lxtx.model.CloudChnCode;

public interface CloudChnCodeMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-11-26 20:48:32
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-26 20:48:32
     */
    int insert(CloudChnCode record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-26 20:48:32
     */
    int insertSelective(CloudChnCode record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-11-26 20:48:32
     */
    CloudChnCode selectByPrimaryKey(Integer id);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-26 20:48:32
     */
    int updateByPrimaryKeySelective(CloudChnCode record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-26 20:48:32
     */
    int updateByPrimaryKey(CloudChnCode record);

}