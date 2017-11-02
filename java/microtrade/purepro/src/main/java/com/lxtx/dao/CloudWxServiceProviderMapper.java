package com.lxtx.dao;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudWxServiceProvider;

public interface CloudWxServiceProviderMapper {
	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 */
	CloudWxServiceProvider selectByPrimaryKey(Integer id);
	
	CloudWxServiceProvider getActiveProvider();
	
	CloudWxServiceProvider selectByOrigin(@Param("origin")String origin);
}