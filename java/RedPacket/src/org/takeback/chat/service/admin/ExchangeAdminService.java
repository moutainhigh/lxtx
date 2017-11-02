// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import org.springframework.transaction.annotation.Transactional;
import com.google.common.collect.ImmutableMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("exchangeAdminService")
public class ExchangeAdminService extends MyListServiceInt
{
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        final Map<String, Object> data =  (Map<String, Object>) req.get("data");
        final String status = data.get("status").toString();
        if ("2".equals(status)) {
            final Integer id = Integer.valueOf(data.get("id").toString());
            final String hql = "select count(*) from PubExchangeLog where id=:id and status='2'";
            final Long l = this.dao.count(hql, ImmutableMap.of("id", id));
            if (l == 0L) {
                final Integer uid = Integer.valueOf(data.get("uid").toString());
                final Double money = Double.valueOf(data.get("money").toString());
                final String upd = "update PubUser set money =coalesce(money,0) + :money where id=:uid";
                this.dao.executeUpdate(upd, ImmutableMap.of("money", Double.valueOf(data.get("money").toString()), "uid", uid));
            }
        }
        super.save(req);
    }
}
