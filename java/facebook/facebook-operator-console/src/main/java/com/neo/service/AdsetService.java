package com.neo.service;

import java.math.BigInteger;

import com.neo.entity.Adset;

public interface AdsetService {

	Adset getAdsetById(BigInteger id);
	
}
