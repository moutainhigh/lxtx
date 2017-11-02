package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.LotteryOrder;

public interface LotteryOrderMapper {

	int deleteByPrimaryKey(Integer id);

	int insert(LotteryOrder record);
	
	List<LotteryOrder> selectByUserIdAndDate(@Param("userId") Integer userId, @Param("date") Integer date);
	
	Integer selectSumOrderMoney(@Param("date") Integer date, @Param("sn") Integer sn);

	LotteryOrder selectByPrimaryKey(Integer id);
	
	List<LotteryOrder> selectOrderByUid(@Param("uid") Integer uid, @Param("id") Integer id);
	
	List<LotteryOrder> selectWinOrdersByDateAndSn(@Param("date") Integer date, @Param("sn") Integer sn);
}