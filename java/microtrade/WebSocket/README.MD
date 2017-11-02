 需要注意的两点，在部署完成之后需要将在tomcat应用目录中的lib目录下的catalina.jar和tomcat-coyote.jar删掉，比如项目的lib目录在D:\workspace\WebSocket\WebRoot\WEB-INF\lib，而部署的应用lib目录是在D:\tools\apache-tomcat-7.0.32\webapps\WebSocket\WEB-INF\lib，删掉部署目录的lib目录中连两个jar就可以了，否则会包Could not initialize class com.ibcio.WebSocketMessageServlet错误，切记。
    如果还是无法建立连接，请下载最新的tomcat，忘了是那个版本的tomcatcreateWebSocketInbound是没有request参数的，现在的这个代码是有这个参数了，7.0.3XX版本都是带这个参数的，切记。

总结
    使用WebSocket开发服务器推送非常方便，这个是个简单的应用，其实还可以结合WebRTC实现视频聊天和语音聊天。在我的Leetop项目中已经实现了浏览器端视频聊天的功能，大家可以去看看 www.ibcio.com，在我的另外一篇文章中有详细的介绍：http://blog.csdn.net/leecho571/article/details/8207102，下图就是Leetop项目的效果图：