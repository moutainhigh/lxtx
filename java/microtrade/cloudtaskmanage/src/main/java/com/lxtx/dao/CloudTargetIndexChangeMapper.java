package com.lxtx.dao;

import com.lxtx.model.CloudTargetIndexChange;

public interface CloudTargetIndexChangeMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    int insert(CloudTargetIndexChange record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    int insertSelective(CloudTargetIndexChange record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    CloudTargetIndexChange selectByPrimaryKey(Integer id);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    int updateByPrimaryKeySelective(CloudTargetIndexChange record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-10-28 18:18:26
     */
    int updateByPrimaryKey(CloudTargetIndexChange record);
}