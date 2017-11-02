// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.support.ord;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.takeback.chat.entity.GcRoom;
import org.takeback.chat.entity.Message;
import org.takeback.chat.service.RoomService;
import org.takeback.chat.store.room.Room;
import org.takeback.chat.store.user.User;
import org.takeback.chat.utils.MessageUtils;

@Component("showRoomDescCmd")
public class ShowRoomDescCmd implements Command {
	@Autowired
	private RoomService roomService;

	@Override
	public void exec(final Map<String, Object> data, final Message message, final WebSocketSession session,
			final Room room, final User user) {
		GcRoom roomD = this.roomService.getRoomById(room.getId());
		if (roomD != null) {
			room.setDetail(roomD.getDetail());
		}
		MessageUtils.sendCMD(session, "showRoomDesc", room);
	}
}
