package com.lxtx.dao;

import com.lxtx.model.CloudUserLimit;

public interface CloudUserLimitMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-11-09 14:37:17
     */
    int deleteByPrimaryKey(Integer uid);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-09 14:37:17
     */
    int insert(CloudUserLimit record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-09 14:37:17
     */
    int insertSelective(CloudUserLimit record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-11-09 14:37:17
     */
    CloudUserLimit selectByPrimaryKey(Integer uid);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-09 14:37:17
     */
    int updateByPrimaryKeySelective(CloudUserLimit record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-09 14:37:17
     */
    int updateByPrimaryKey(CloudUserLimit record);

	void updateTranAmountById(CloudUserLimit upUserLimit);

	void uptRePayByid(CloudUserLimit userLimit);
}