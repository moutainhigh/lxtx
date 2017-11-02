package com.lxtx.dao;

import java.util.List;

import com.lxtx.model.CloudTarget;

public interface CloudTargetMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    int insert(CloudTarget record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    int insertSelective(CloudTarget record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    CloudTarget selectByPrimaryKey(Integer id);

    /**
     * 根据代码返回标的
     * @param name
     * @return
     */
    CloudTarget selectByName(String name);
    
    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    int updateByPrimaryKeySelective(CloudTarget record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-10-28 18:18:33
     */
    int updateByPrimaryKey(CloudTarget record);
    
    List<CloudTarget> selectAll();
}