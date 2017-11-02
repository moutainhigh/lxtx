package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.CloudUser;
import com.lxtx.model.vo.BrokerUnderPerVo;

public interface CloudUserMapper {
	/**
	 * 根据主键删除 参数:主键 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:43
	 */
	int deleteByPrimaryKey(Integer id);

	/**
	 * 插入，空属性也会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:43
	 */
	int insert(CloudUser record);

	/**
	 * 插入，空属性不会插入 参数:pojo对象 返回:删除个数
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:43
	 */
	int insertSelective(CloudUser record);

	/**
	 * 根据主键查询 参数:查询条件,主键值 返回:对象
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:43
	 */
	CloudUser selectByPrimaryKey(Integer id);

	/**
	 * 根据主键修改，空值条件不会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:43
	 */
	int updateByPrimaryKeySelective(CloudUser record);

	/**
	 * 根据主键修改，空值条件会修改成null 参数:1.要修改成的值 返回:成功修改个数
	 * 
	 * @ibatorgenerated 2016-10-28 20:11:43
	 */
	int updateByPrimaryKey(CloudUser record);

	CloudUser selectByWxid(String wxid);

	void updateVisitById(CloudUser user);

	/**
	 * 
	 * Description:添加手机号
	 *
	 * @author hecm
	 * @date 2016年10月28日 下午9:23:51
	 */
	void updateTelById(CloudUser user);

	void updatePwdById(CloudUser user);

	void updateBalanceById(CloudUser user);
	
	void reduceBalanceById(CloudUser user);

	int checkTelExist(String mobile);

	List<BrokerUnderPerVo> selectUnderPerByMbl(@Param("brokerId") Integer brokerId, @Param("mobile") String mobile);

	void updateBalanceAndContractById(CloudUser user);

	void updateContractAmountById(CloudUser user);

}