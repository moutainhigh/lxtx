package com.lxtech.game.plaza.websocket;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class WebSocketEndpoint extends WebSocketClient {
	private String addr;
	private int port;

	private boolean isClosed = false;
	
	public WebSocketEndpoint(String addr, int port) throws URISyntaxException {
		super(new URI(String.format("ws://%s:%s", addr, port)));
		this.addr = addr;
		this.port = port;
	}

	private final static Logger logger = LoggerFactory.getLogger(WebSocketEndpoint.class);
	private final static String BODY_SPLITTER = "\r\n\r\n";

	public boolean isClosed() {
		return this.isClosed;
	}

	public WebSocketEndpoint(URI uri) {
		super(uri);
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		this.send("{}");
	}

	public void onMessage(ByteBuffer bytes) {
		
	}

	@Override
	public void onMessage(String message) {
		System.out.println(message);
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		System.out.println(code + reason + remote);
		String template = "Socket has been closed, code:%s, reason:%s, remote:%s";
		String closeInfo = String.format(template, code, reason, remote);
		logger.info(closeInfo);
		this.isClosed = true;
	}

	@Override
	public void onError(Exception ex) {
		logger.error(ex.getMessage());
	}

	private byte[] getByteArray(String source) {
		return source.getBytes();
	}

	public static void main(String[] args) throws URISyntaxException {
		WebSocketEndpoint c = new WebSocketEndpoint("139.129.227.199", 8081);
		c.connect();
		System.out.println("after connect");
		// String str = "{\"Market\":17000, \"Code\":\"%s\", \"PushFlag\":0,
		// \"TimeType\":0,\"TimeValue0\":60,\"TimeValue1\":1200,\"Day\":\"%s
		// 0:0:0\"}";
		//
		// System.out.println((String.format(str, "BU",
		// "2016-11-10")).length());

		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-M-d");
		// System.out.println(sdf.format(new Date()));
	}

}
