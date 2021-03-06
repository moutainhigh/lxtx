// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import org.springframework.web.util.WebUtils;
import org.takeback.util.context.ContextUtils;
import javax.servlet.http.HttpServletRequest;
import com.google.common.collect.ImmutableMap;
import java.io.Serializable;
import org.takeback.chat.entity.GcRoom;
import org.takeback.util.exception.CodedBaseRuntimeException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("roomFeeService")
public class RoomFeeService extends MyListServiceInt
{
    @Autowired
    RoomStore roomStore;
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data = (Map<String, Object>) req.get("data");
        final String roomId = data.get("roomId").toString();
        if (roomId == null) {
            throw new CodedBaseRuntimeException("房间号不能为空");
        }
        final GcRoom rm = this.dao.get(GcRoom.class, roomId);
        if (rm == null) {
            throw new CodedBaseRuntimeException("错误的房间号");
        }
        Double fee = 0.0;
        try {
            fee = Double.valueOf(data.get("val").toString());
        }
        catch (Exception e) {
            throw new CodedBaseRuntimeException("错误的数值");
        }
        final String hql = "update GcRoom set sumfee = COALESCE(sumfee,0) - :val where sumfee>:val and  id=:roomId";
        final int effected = this.dao.executeUpdate(hql, ImmutableMap.of("val", fee, "roomId", roomId));
        if (effected == 0) {
            throw new CodedBaseRuntimeException("超出可用额度：" + rm.getSumFee());
        }
        final HttpServletRequest request = (HttpServletRequest)ContextUtils.get("$httpRequest");
        final String admin = (String)WebUtils.getSessionAttribute(request, "$uid");
        data.put("admin", admin);
        data.put("createDate", new Date());
        data.put("roomName", rm.getName());
        super.save(req);
    }
}
