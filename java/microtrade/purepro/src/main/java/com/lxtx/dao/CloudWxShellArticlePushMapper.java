package com.lxtx.dao;

import com.lxtx.model.CloudWxShellArticlePush;

public interface CloudWxShellArticlePushMapper {

	public CloudWxShellArticlePush selectByWxid(String wxid);
	
	public void updateByWxid(CloudWxShellArticlePush articlePush);
	
	public void insert(CloudWxShellArticlePush articlePush);
}
