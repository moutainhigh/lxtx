package com.lxtx.service.user;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lxtx.dao.CloudNoticeMapper;
import com.lxtx.model.CloudNotice;
import com.lxtx.util.StringUtil;

@Service
public class CloudNoticeService {

	@Autowired
	private CloudNoticeMapper cloudNoticeMapper;

	public List<CloudNotice> queryNotice() {
		String now = StringUtil.formatDate(new Date(), "yyyy-MM-dd");
		return cloudNoticeMapper.queryNoticeByDay(now);
	}
}
