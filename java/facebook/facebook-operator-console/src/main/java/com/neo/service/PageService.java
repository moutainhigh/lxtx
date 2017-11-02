package com.neo.service;

import java.math.BigInteger;

import com.neo.entity.Page;

public interface PageService {

	Page getPageById(BigInteger pageId);
	
}
