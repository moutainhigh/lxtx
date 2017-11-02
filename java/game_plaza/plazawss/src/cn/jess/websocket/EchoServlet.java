package cn.jess.websocket;  
  
import java.io.IOException;  
import java.nio.ByteBuffer;  
import java.nio.CharBuffer;  
import java.util.ArrayList;  
import java.util.List;  
  
import javax.servlet.http.HttpServletRequest;  
  
import org.apache.catalina.websocket.MessageInbound;  
import org.apache.catalina.websocket.StreamInbound;  
import org.apache.catalina.websocket.WebSocketServlet;  
import org.apache.catalina.websocket.WsOutbound;  
  
public class EchoServlet extends WebSocketServlet {  
    /** 
     *  
     */  
    private static final long serialVersionUID = -2581942001009336822L;  
    //private static final Logger log = Logger.getLogger("EchoServlet");  
    private static List<MessageInbound> socketList = new ArrayList<MessageInbound>();  
//  privat static final long ser  
      
    private static List<WebSocketMessageInbound> connsList = new ArrayList<WebSocketMessageInbound>();  
      
    protected StreamInbound createWebSocketInbound(String subProtocol,HttpServletRequest request){  
          
        return new WebSocketMessageInbound();  
    }  
      
    public class WebSocketMessageInbound extends MessageInbound{  
        protected void onClose(int status){  
            super.onClose(status);  
            socketList.remove(this);              
        }  
        protected void onOpen(WsOutbound outbound){  
            super.onOpen(outbound);  
            socketList.add(this);  
        }  
        //@Override  
        protected void onBinaryMessage(ByteBuffer message) throws IOException {  
            // TODO Auto-generated method stub  
            System.out.println("onBinarymessage");  
        }  
        @Override  
        protected void onTextMessage(CharBuffer message) throws IOException {  
            // TODO Auto-generated method stub  
            for(MessageInbound messageInbound : socketList){  
                CharBuffer buffer = CharBuffer.wrap(message);  
                WsOutbound outbound = messageInbound.getWsOutbound();  
                outbound.writeTextMessage(buffer);  
                outbound.flush();                 
            }  
        }  
    }  
}  