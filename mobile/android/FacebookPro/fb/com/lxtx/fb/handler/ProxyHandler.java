package com.lxtx.fb.handler;

import java.util.List;

import com.lxtx.fb.pojo.Proxy;
import com.qlzf.commons.handler.SimpleIbatisEntityHandler;

public class ProxyHandler extends SimpleIbatisEntityHandler<Proxy>{

	public List<Proxy> listUnCheck() {
		return queryForList("listUnCheck");
	}

	public void updateStatus(Proxy proxy) {
		update("updateStatus", proxy);
	}


}
