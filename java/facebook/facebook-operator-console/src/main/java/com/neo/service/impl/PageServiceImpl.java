package com.neo.service.impl;

import java.math.BigInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.neo.entity.Page;
import com.neo.repository.PageRepository;
import com.neo.service.PageService;

@Service
public class PageServiceImpl implements PageService{

	@Autowired
	private PageRepository pageRepository;
	
	@Override
	public Page getPageById(BigInteger pageId) {
		return pageRepository.findOne(pageId);
	}

}
