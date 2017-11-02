package com.lxtx.dao;

import com.lxtx.model.CloudUser;

public interface CloudUserMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-12-12 15:36:57
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-12-12 15:36:57
     */
    int insert(CloudUser record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-12-12 15:36:57
     */
    int insertSelective(CloudUser record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-12-12 15:36:57
     */
    CloudUser selectByPrimaryKey(Integer id);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-12-12 15:36:57
     */
    int updateByPrimaryKeySelective(CloudUser record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-12-12 15:36:57
     */
    int updateByPrimaryKey(CloudUser record);
}