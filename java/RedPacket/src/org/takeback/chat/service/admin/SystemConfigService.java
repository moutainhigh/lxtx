// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.service.admin;

import java.util.Iterator;
import org.springframework.transaction.annotation.Transactional;
import java.util.Map;
import org.takeback.chat.entity.PubConfig;
import java.util.List;
import org.springframework.stereotype.Service;
import org.takeback.core.service.MyListServiceInt;

@Service("configService")
public class SystemConfigService extends MyListServiceInt
{
    public static final String CTRL_FLAG = "control_flag";
    public static final String CTRL_DEFAULT_RATE = "control_default_rate";
    public static final String CTRL_KILL = "control_kill";
    public static final String CTRL_SAVE = "control_save";
    public static final String CTRL_INIT_MONEY = "conf_init_money";
    public static final String CTRL_TALK = "conf_talk";
    public static final String CTRL_TRANSFER = "conf_transfer";
    public static final String CTRL_PROXY_RECHARGE = "conf_proxyRecharge";
    public static final String CTRL_PROXY_WITHDRAW = "conf_proxyWithdraw";
    List<PubConfig> cache;
    static SystemConfigService instance;
    
    public SystemConfigService() {
        SystemConfigService.instance = this;
    }
    
    @Transactional
    @Override
    public void save(final Map<String, Object> req) {
        super.save(req);
        SystemConfigService.instance.reload();
    }
    
    @Transactional
    public static String getValue(final String key) {
        if (SystemConfigService.instance.cache == null) {
            SystemConfigService.instance.reload();
        }
        for (final PubConfig c : SystemConfigService.instance.cache) {
            if (c.getParam().equals(key)) {
                return c.getVal();
            }
        }
        return null;
    }
    
    @Transactional
    private void reload() {
        this.cache = this.dao.findByHql("from PubConfig");
    }
}
