<%@page import="java.net.URLDecoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
{"info":"<%=request.getParameter("code_img_url") %>","out_trade_no":"<%=request.getParameter("tradeId") %>"}