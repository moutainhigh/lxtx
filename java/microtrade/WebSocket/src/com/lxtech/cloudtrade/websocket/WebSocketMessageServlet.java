package com.lxtech.cloudtrade.websocket;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.websocket.StreamInbound;

import com.lxtech.cloudtrade.net.KestrelConnector;
import com.lxtech.cloudtrade.net.KestrelMessageListener;

@WebServlet(urlPatterns = { "/message"})
//如果要接收浏览器的ws://协议的请求就必须实现WebSocketServlet这个类
public class WebSocketMessageServlet extends org.apache.catalina.websocket.WebSocketServlet {

	private static final long serialVersionUID = 1L;
	
	//public static int ONLINE_USER_COUNT	= 1;
	
	private static AtomicInteger maxClientId = new AtomicInteger(0);
	
	//跟平常Servlet不同的是，需要实现createWebSocketInbound，在这里初始化自定义的WebSocket连接对象
    @Override
    protected StreamInbound createWebSocketInbound(String subProtocol,HttpServletRequest request) {
        return new WebSocketMessageInbound(KestrelConnector.QUEUE_NAME, maxClientId.incrementAndGet());
    }

	@Override
	public void init() throws ServletException {
		super.init();
		
		//create a thread to read the messages from the kestrel queue
		new Thread(new KestrelMessageListener(KestrelConnector.QUEUE_NAME)).start();
	}
    
}