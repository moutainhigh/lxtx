package com.lxtx.dao;

import com.lxtx.model.CloudBroker;

public interface CloudBrokerMapper {
    /**
     * 根据主键删除
     * 参数:主键
     * 返回:删除个数
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * 插入，空属性也会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    int insert(CloudBroker record);

    /**
     * 插入，空属性不会插入
     * 参数:pojo对象
     * 返回:删除个数
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    int insertSelective(CloudBroker record);

    /**
     * 根据主键查询
     * 参数:查询条件,主键值
     * 返回:对象
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    CloudBroker selectByPrimaryKey(Integer id);

    /**
     * 根据主键修改，空值条件不会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    int updateByPrimaryKeySelective(CloudBroker record);

    /**
     * 根据主键修改，空值条件会修改成null,支持大字段类型
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    int updateByPrimaryKeyWithBLOBs(CloudBroker record);

    /**
     * 根据主键修改，空值条件会修改成null
     * 参数:1.要修改成的值
     * 返回:成功修改个数
     * @ibatorgenerated 2016-11-02 12:00:30
     */
    int updateByPrimaryKey(CloudBroker record);

	CloudBroker selectBrokerInfoByUserId(Integer brokerId);
}