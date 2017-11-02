// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.schedule;

import java.util.Calendar;
import org.takeback.chat.utils.DateUtil;
import org.takeback.chat.entity.PcRateConfig;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import org.takeback.chat.entity.PcConfig;
import java.util.HashMap;
import org.springframework.transaction.annotation.Transactional;
import java.util.Iterator;
import java.util.Date;
import java.util.List;
import org.takeback.chat.utils.NumberUtil;
import org.takeback.chat.entity.ProxyVote;
import java.io.Serializable;
import java.util.Map;
import com.google.common.collect.ImmutableMap;
import org.takeback.chat.entity.PubUser;
import org.takeback.chat.entity.PubConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;
import org.springframework.stereotype.Service;

@Service
public class ProxySchedule
{
    @Autowired
    BaseDAO dao;
    
    @Transactional
    public void work() {
        final PubConfig pc = this.dao.getUnique(PubConfig.class, "param", "water");
        final Double rate = Double.valueOf(pc.getVal());
        if (rate <= 0.0 || rate >= 1.0) {
            return;
        }
        final Map<String, List<Double>> waterConfigs = this.getWaterConfig();
        final Date startDate = this.getStartDate();
        final Date endDate = this.getEndDate();
        final String hql = "from PubUser where parent is not null and userType<>'9' ";
        final List<PubUser> proxys = this.dao.findByHql(hql);
        for (final PubUser u : proxys) {
            try {
                final String countHql = "select coalesce(sum(inoutNum),0) from GcLotteryDetail where uid=:uid and createDate>:startDate and createDate<:endDate";
                final List<Object> recCounts = this.dao.findByHql(countHql, ImmutableMap.of("uid", u.getId(), "startDate", startDate, "endDate", endDate));
                final String pcCountHql = "select coalesce(sum(userInout),0) from PcGameLog where uid=:uid and openTime>:startDate and openTime<:endDate";
                final List<Object> pcCounts = this.dao.findByHql(pcCountHql, ImmutableMap.of("uid", u.getId(), "startDate", startDate, "endDate", endDate));
                final Double num = Double.valueOf(recCounts.get(0).toString()) + Double.valueOf(pcCounts.get(0).toString());
                if (num >= 0.0) {
                    continue;
                }
                final PubUser p = this.dao.get(PubUser.class, u.getParent());
                final ProxyVote v = new ProxyVote();
                v.setCacuDate(endDate);
                v.setLose(NumberUtil.round(Math.abs(num)));
                v.setNickName(u.getUserId());
                v.setUid(u.getId());
                v.setVote(NumberUtil.round(Math.abs(num) * rate));
                v.setParentId(p.getId());
                v.setParentNickName(p.getUserId());
                final String addHql = "update PubUser set money=coalesce(money,0) + :vote where id =:uid";
                final int effected = this.dao.executeUpdate(addHql, ImmutableMap.of("vote", NumberUtil.round(Math.abs(num) * rate), "uid", p.getId()));
                if (effected == 1) {
                    this.dao.save(ProxyVote.class, v);
                }
                final Double pcWater = Double.valueOf(pcCounts.get(0).toString());
                if (pcWater >= 0.0) {
                    continue;
                }
                final Long count = this.dao.count("select count(*) from PcGameLog where uid =:uid and status <>0", ImmutableMap.of("uid", u.getId()));
                final Double waterMin = waterConfigs.get("w_min").get(0);
                if (count < 1L) {
                    continue;
                }
                final Double water = this.getWater(waterConfigs, pcWater);
                if (water <= 0.0) {
                    continue;
                }
                this.dao.executeUpdate("update PubUser set money = money +:water where id =:uid", ImmutableMap.of("water", water, "uid", u.getId()));
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    private Double getWater(final Map<String, List<Double>> confs, Double money) {
        if (money >= 0.0) {
            return 0.0;
        }
        money = Math.abs(money);
        Double water = 0.0;
        for (int i = 1; i <= confs.size(); ++i) {
            final String key = "w_" + i;
            final List<Double> l = confs.get(key);
            if (l == null) {
                return water;
            }
            if (money < l.get(0)) {
                break;
            }
            water = money * l.get(1) / 100.0;
        }
        return water;
    }
    
    @Transactional
    public Map<String, List<Double>> getWaterConfig() {
        final Map<String, List<Double>> config = new HashMap<String, List<Double>>();
        final List<PcConfig> list = this.dao.findByHql("from PcConfig where param like 'w_%' order by id");
        for (final PcConfig c : list) {
            config.put(c.getParam(), this.getValues(c.getVal()));
        }
        return config;
    }
    
    private List<Double> getValues(final String text) {
        final List<Double> res = new ArrayList<Double>();
        final String pattern = "【[0-9]+】";
        final Pattern p = Pattern.compile(pattern);
        final Matcher m = p.matcher(text);
        while (m.find()) {
            res.add(Double.valueOf(m.group().replaceAll("[【】]", "")));
        }
        return res;
    }
    
    @Transactional(readOnly = true)
    public Map<String, List<PcRateConfig>> getPcRateConfigs() {
        final Map<String, List<PcRateConfig>> rates = new HashMap<String, List<PcRateConfig>>();
        final List<PcRateConfig> list = this.dao.findByHql("from PcRateConfig order by id, catalog ");
        for (final PcRateConfig config : list) {
            List<PcRateConfig> prc = rates.get(config.getCatalog());
            if (prc == null) {
                prc = new ArrayList<PcRateConfig>();
                rates.put(config.getCatalog(), prc);
            }
            prc.add(config);
        }
        return rates;
    }
    
    private Date getStartDate() {
        final Date d = DateUtil.getStartOfToday();
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(11, -19);
        return c.getTime();
    }
    
    private Date getEndDate() {
        final Date d = DateUtil.getStartOfToday();
        final Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.add(11, 29);
        return c.getTime();
    }
}
