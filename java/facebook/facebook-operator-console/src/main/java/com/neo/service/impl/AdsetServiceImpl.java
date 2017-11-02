package com.neo.service.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neo.entity.Adset;
import com.neo.repository.AdsetRepository;
import com.neo.service.AdsetService;

@Service
public class AdsetServiceImpl implements AdsetService{

	@Autowired
	private AdsetRepository adsetRepos;
	
	@Override
	public Adset getAdsetById(BigInteger id) {
		return this.adsetRepos.findOne(id);
	}
}
