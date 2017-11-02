package com.lxtech.cloud.okcoin.main;

import com.lxtech.cloud.okcoin.WebSocketBase;
import com.lxtech.cloud.okcoin.WebSocketService;

/**
 * 通过继承WebSocketBase创建WebSocket客户端
 * @author okcoin
 *
 */
public class WebSoketClient extends WebSocketBase{
	public WebSoketClient(String url,WebSocketService service){
		super(url,service);
	}
}
