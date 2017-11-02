package com.lxtx.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.lxtx.model.LotteryCqsscData;

public interface LotteryCqsscDataMapper {

	int deleteByPrimaryKey(Integer id);
	
	LotteryCqsscData selectByDateAndSn(@Param("date") Integer date, @Param("sn") Integer sn);

	LotteryCqsscData selectByPrimaryKey(Integer id);
	
	List<LotteryCqsscData> selectByDate(Integer date);

	LotteryCqsscData getLatestLotteryData(int selfDefineDate);
}