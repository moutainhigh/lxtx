// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.store.pcegg;

import org.takeback.core.dictionary.DictionaryController;
import org.takeback.core.dictionary.Dictionary;
import org.takeback.chat.entity.PcGameLog;
import org.slf4j.LoggerFactory;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.function.Function;
import java.util.Collection;
import java.util.Collections;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import org.apache.commons.lang3.time.DateUtils;
import java.util.Iterator;
import org.takeback.chat.utils.MessageUtils;
import org.takeback.chat.entity.Message;
import org.takeback.chat.store.room.Room;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.StringUtils;
import java.io.Serializable;
import org.springframework.transaction.annotation.Transactional;
import org.takeback.util.JSONUtils;
import java.util.Map;
import org.jsoup.Jsoup;
import org.joda.time.LocalTime;
import java.io.IOException;
import java.util.Calendar;
import java.util.TimerTask;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import java.util.Objects;
import org.takeback.chat.entity.PcEggLog;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.List;
import org.takeback.chat.service.PcEggService;
import org.takeback.chat.store.room.RoomStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.takeback.dao.BaseDAO;
import org.slf4j.Logger;

public class PcEggStore
{
    private static final Logger LOGGER;
    @Autowired
    private BaseDAO dao;
    @Autowired
    private RoomStore roomStore;
    @Autowired
    private PcEggService pcEggService;
    private List<PeriodConfig> periodConfigs;
    private int closeSeconds;
    private ThreadPoolTaskExecutor threadPool;
    private ReentrantReadWriteLock lock;
    private ArrayBlockingQueue<PcEggLog> cache;
    private PcEggLog latest;
    private static PcEggStore store;
    private static byte check;
    
    PcEggStore() throws IllegalAccessException {
        this.closeSeconds = 30;
        this.lock = new ReentrantReadWriteLock();
        this.cache = new ArrayBlockingQueue<PcEggLog>(10);
        if (PcEggStore.check != 8) {
            throw new IllegalAccessException("Cannot access this constructor.");
        }
        PcEggStore.store = this;
        ++PcEggStore.check;
    }
    
    public static PcEggStore getStore() {
        return PcEggStore.store; 
    }
    
    public PcEggLog getLastest() {
        this.lock.readLock().lock();
        try {
            if (this.latest != null && !this.latest.isClosed(0)) {
                return this.latest;
            }
        }
        finally {
            this.lock.readLock().unlock();
        }
        this.lock.writeLock().lock();
        try {
            if (this.latest != null && !this.latest.isClosed(0)) {
                return this.latest;
            }
            while (!Thread.interrupted()) {
                final PcEggLog egg = this.loadLatest();
                if (egg != null && (this.latest == null || !Objects.equals(egg.getId(), this.latest.getId()))) {
                    this.latest = egg;
                    break;
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(3000L);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            final Timer timer = new Timer(true);
            timer.schedule(new TermTimerTask(this.latest, timer), this.latest.getExpireTime());
            final Timer LogTimer = new Timer(true);
            final Calendar c = Calendar.getInstance();
            c.setTime(this.latest.getExpireTime());
            c.add(13, -30);
            LogTimer.schedule(new TermLogTimerTask(LogTimer), c.getTime());
            return this.latest;
        }
        catch (IOException e2) {
            PcEggStore.LOGGER.error("Failed to get data of pc egg.", (Throwable)e2);
            return null;
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }
    
    public boolean isClosed(final PcEggLog pcEggLog) {
        return pcEggLog.isClosed(this.closeSeconds);
    }
    
    public boolean isClosed(final int no) {
        final PcEggLog pcEggLog = this.getLastest();
        return pcEggLog == null || pcEggLog.getId() != no || pcEggLog.isClosed(this.closeSeconds);
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public boolean initData() {
        final PeriodConfig config = this.getPeriodConfig(new LocalTime());
        String html;
        try {
            html = Jsoup.connect(config.getDataSourceUrl()).get().outerHtml();
        }
        catch (IOException e) {
            System.out.println("数据源歇菜!");
            return false;
        }
        final String latest_result = getScriptValue(html, "latest_draw_result = ").trim();
        final String latest_id = getScriptValue(html, "latest_draw_phase = ");
        final String latest_time = getScriptValue(html, "latest_draw_time = ");
        final String phaseData = getScriptValue(html, "phaseData = ");
        this.lock.writeLock().lock();
        try {
            final int id = Integer.valueOf(latest_id);
            if (phaseData.length() < 10) {
                return true;
            }
            final Map datas = JSONUtils.parse(phaseData, Map.class);
            final Map todayData = (Map) datas.get(latest_time.substring(0, latest_time.indexOf(" ")));
            if (todayData != null && todayData.size() > 0) {
                int size = todayData.size();
                if (size > 9) {
                    size = 9;
                }
                for (int i = size; i >= 1; --i) {
                    final String data_id = id - i + "";
                    final Map data = (Map) todayData.get(data_id);
                    final String data_time = data.get("open_time").toString();
                    final Map data_result = (Map) data.get("result");
                    this.savePcEggLog(data_id, data_time, getRusult(data_result));
                }
            }
            this.savePcEggLog(latest_id, latest_time, getRusult(JSONUtils.parse(latest_result, Map.class)));
        }
        finally {
            this.lock.writeLock().unlock();
        }
        return true;
    }
    
    private void savePcEggLog(final String id, final String time, final String result) {
        final PcEggLog pel = parse(id, time, result);
        this.add(pel);
        if (this.dao.get(PcEggLog.class, pel.getId()) == null) {
            this.dao.save(PcEggLog.class, pel);
        }
    }
    
    @Transactional(rollbackFor = { Throwable.class })
    public boolean work(final PcEggLog egg) {
        try {
            final PcEggLog pel = this.load(egg.getId());
            if (pel == null) {
                PcEggStore.LOGGER.error("Cannot load data for term id: {}", (Object)egg.getId());
                return false;
            }
            if (pel.getLucky() != null) {
                this.fillData(egg, pel);
                if (this.dao.get(PcEggLog.class, pel.getId()) == null) {
                    this.dao.save(PcEggLog.class, pel);
                }
                this.pcEggService.open(pel.getId(), pel.getExp(), pel.getLucky());
                this.broadcast();
            }
            return true;
        }
        catch (Exception e) {
            PcEggStore.LOGGER.error("Failed to fetch new data for term id: {}", (Object)egg.getId(), (Object)e);
            return false;
        }
    }
    
    private void broadcast() {
        this.threadPool.execute(() -> {
        	final PcEggLog latest;
        	String simpleWord;
        	final Iterator<PcEggLog> iterator;
        	PcEggLog pel;
        	final String simpleWord2;
        	final long l;
        	final ImmutableMap content;
        	final List<Room> rms;
        	final Iterator<Room> iterator2;
        	Room r;
        	Message msg;
            latest = this.getLastest();
            if (latest != null) {
                simpleWord = "";
                iterator = this.getCache().iterator();
                while (iterator.hasNext()) {
                    pel = iterator.next();
                    simpleWord = simpleWord + " " + (StringUtils.isNotEmpty((CharSequence)pel.getLucky()) ? pel.getLucky() : "?");
                }
                simpleWord2 = ((simpleWord.length() > 1) ? simpleWord.substring(1) : simpleWord);
                l = latest.getExpireTime().getTime() - System.currentTimeMillis();
                content = ImmutableMap.of((Object)"termId", (Object)latest.getId(), (Object)"expireTime", (Object)latest.getExpireTime(), (Object)"remainSeconds", (Object)(int)Math.floor(l / 1000L), (Object)"simpleWord", (Object)simpleWord2, (Object)"logs", (Object)this.getCache());
                rms = this.roomStore.getByType("G03");
                iterator2 = rms.iterator();
                while (iterator2.hasNext()) {
                    r = iterator2.next();
                    msg = new Message("PC_MSG", 0, content);
                    MessageUtils.broadcast(r, msg);
                }
            }
        });
    }
    
    private void fillData(final PcEggLog emptyEgg, final PcEggLog data) {
        emptyEgg.setExp(data.getExp());
        emptyEgg.setDataTime(data.getDataTime());
        emptyEgg.setLucky(data.getLucky());
        emptyEgg.setOpenTime(data.getOpenTime());
        emptyEgg.setSpecial(data.getSpecial());
    }
    
    private PcEggLog getInternal(final int id) {
        for (final PcEggLog pcEgg : this.cache) {
            if (Objects.equals(pcEgg.getId(), id)) {
                return pcEgg;
            }
        }
        return null;
    }
    
    private void add(final PcEggLog pcEgg) {
        if (pcEgg != null) {
            if (this.cache.size() == 10) {
                this.cache.poll();
            }
            this.cache.add(pcEgg);
        }
    }
    
    private PcEggLog load(final int termId) {
        final PeriodConfig config = this.getPeriodConfig(new LocalTime());
        if (config == null) {
            return null;
        }
        String html;
        try {
            html = Jsoup.connect(config.getDataSourceUrl()).get().outerHtml();
        }
        catch (IOException e) {
            System.out.println("数据源歇菜!");
            return null;
        }
        final String latest_id = getScriptValue(html, "latest_draw_phase = ");
        if (Integer.valueOf(latest_id) != termId) {
            return null;
        }
        final String latest_result = getScriptValue(html, "latest_draw_result = ");
        final String latest_time = getScriptValue(html, "latest_draw_time = ");
        return parse(latest_id, latest_time, getRusult(JSONUtils.parse(latest_result, Map.class)));
    }
    
    private PcEggLog loadLatest() throws IOException {
        final PeriodConfig config = this.getPeriodConfig(new LocalTime());
        if (config == null) {
            return null;
        }
        Integer lasterId;
        Date lastTime;
        if (this.latest == null) {
            final String html = Jsoup.connect(config.getDataSourceUrl()).get().outerHtml();
            lasterId = Integer.valueOf(getScriptValue(html, "latest_draw_phase = "));
            final String latest_time = getScriptValue(html, "latest_draw_time = ");
            try {
                lastTime = DateUtils.parseDate(latest_time, new String[] { "yyyy-MM-dd HH:mm:ss" });
            }
            catch (ParseException e) {
                PcEggStore.LOGGER.error("Failed to parse date: {}", (Object)latest_time, (Object)e);
                return null;
            }
        }
        else {
            lasterId = this.latest.getId();
            lastTime = this.latest.getExpireTime();
        }
        final PcEggLog egg = new PcEggLog();
        final Integer newId = lasterId + 1;
        egg.setId(newId);
        egg.setBeginTime(lastTime);
        final Calendar c = Calendar.getInstance();
        c.setTime(egg.getBeginTime());
        c.add(13, config.getPeriodSeconds());
        egg.setExpireTime(c.getTime());
        return egg;
    }
    
    private static PcEggLog parse(final String id, final String time, final String result) {
        final PcEggLog el = new PcEggLog();
        el.setId(Integer.valueOf(id));
        final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            final Date dataTime = sdf.parse(time);
            el.setDataTime(dataTime);
            el.setExpireTime(dataTime);
        }
        catch (ParseException e1) {
            e1.printStackTrace();
        }
        final Integer[] expNumbers = getExp(result);
        el.setExp(expNumbers[0] + "+" + expNumbers[1] + "+" + expNumbers[2]);
        final Integer luckyNum = expNumbers[0] + expNumbers[1] + expNumbers[2];
        final String lucky = (luckyNum < 10) ? ("0" + luckyNum) : luckyNum.toString();
        el.setLucky(lucky);
        el.setOpenTime(new Date());
        String spc = "";
        if (luckyNum > 13) {
            spc += "大,";
        }
        else {
            spc += "小,";
        }
        if (luckyNum % 2 == 0) {
            spc += "双";
        }
        else {
            spc += "单";
        }
        el.setSpecial(spc);
        return el;
    }
    
    private static Integer[] getExp(final String searial) {
        final Integer[] res = new Integer[3];
        final String[] nums = searial.split(",");
        final List<Integer> sn = new ArrayList<Integer>();
        for (int i = 0; i < nums.length; ++i) {
            sn.add(Integer.valueOf(nums[i]));
        }
        Collections.sort(sn);
        Integer temp1 = 0;
        Integer temp2 = 0;
        Integer temp3 = 0;
        for (int j = 0; j <= 5; ++j) {
            temp1 += sn.get(j);
        }
        res[0] = temp1 % 10;
        for (int j = 6; j <= 11; ++j) {
            temp2 += sn.get(j);
        }
        res[1] = temp2 % 10;
        for (int j = 12; j <= 17; ++j) {
            temp3 += sn.get(j);
        }
        res[2] = temp3 % 10;
        return res;
    }
    
    PeriodConfig getPeriodConfig(final LocalTime time) {
        for (final PeriodConfig pc : this.periodConfigs) {
            if (pc.match(time)) {
                return pc;
            }
        }
        return null;
    }
    
    public void setPeriodConfigs(final List<String> periodConfigs) {
        if (periodConfigs != null) {
            (this.periodConfigs = new ArrayList<PeriodConfig>(periodConfigs.size())).addAll(periodConfigs.stream().map(PeriodConfig::new).collect(Collectors.toList()));
        }
    }
    
    public List<PcEggLog> getCache() {
        final List<PcEggLog> eggs = this.cache.stream().collect(Collectors.toList());
        Collections.reverse(eggs);
        return eggs;
    }
    
    public static void main(final String... args) throws Exception {
        try {
            final String html = Jsoup.connect("http://baidu.lecai.com/lottery/draw/view/543/786240?agentId=5563").get().outerHtml();
            final String latest_draw_result = getScriptValue(html, "latest_draw_result = ");
            final String latest_draw_phase = getScriptValue(html, "latest_draw_phase = ");
            final String latest_draw_time = getScriptValue(html, "latest_draw_time = ");
            final String phaseData = getScriptValue(html, "phaseData = ");
            System.out.println(latest_draw_result);
            System.out.println(latest_draw_phase);
            System.out.println(latest_draw_time);
            System.out.println(phaseData);
            System.out.println(phaseData);
        }
        catch (IOException e) {
            System.out.println("数据源歇菜!");
        }
    }
    
    public static String getScriptValue(final String html, final String vName) {
        String value = html.substring(html.indexOf(vName) + vName.length());
        value = value.substring(0, value.indexOf(";")).replaceAll("=", "").replaceAll("'", "");
        return value;
    }
    
    public static String getRusult(final Map result) {
        return result.get("red").toString().replace(" ", "").replace("[", "").replace("]", "");
    }
    
    public void setCloseSeconds(final int closeSeconds) {
        this.closeSeconds = closeSeconds;
    }
    
    public ThreadPoolTaskExecutor getThreadPool() {
        return this.threadPool;
    }
    
    public void setThreadPool(final ThreadPoolTaskExecutor threadPool) {
        this.threadPool = threadPool;
    }
    
    static {
        LOGGER = LoggerFactory.getLogger((Class)PcEggStore.class);
        PcEggStore.check = 8;
    }
    
    private class TermTimerTask extends TimerTask
    {
        private PcEggLog egg;
        private Timer timer;
        
        TermTimerTask(final PcEggLog egg, final Timer timer) {
            this.egg = egg;
            this.timer = timer;
        }
        
        @Override
        public void run() {
            this.timer.cancel();
            PcEggStore.this.add(this.egg);
            PcEggStore.this.broadcast();
        }
    }
    
    private class TermLogTimerTask extends TimerTask
    {
        private Timer timer;
        
        TermLogTimerTask(final Timer timer) {
            this.timer = timer;
        }
        
        @Override
        public void run() {
            PcEggStore.this.threadPool.execute(() -> {
            	this.timer.cancel();
            	final PcEggLog latest;
            	final List<PcGameLog> logs;
            	final StringBuilder content;
            	final Iterator<PcGameLog> iterator;
            	PcGameLog log;
            	String bet;
            	String userId;
            	final List<Room> rms;
            	final Iterator<Room> iterator2;
            	Room r;
            	Message msg;
                latest = PcEggStore.this.getLastest();
                if (latest != null) {
                    logs = PcEggStore.this.pcEggService.getGameLog(latest.getId());
                    if (logs.size() != 0) {
                        content = new StringBuilder();
                        content.append("<p><strong>本期下注：</strong></p>");
                        iterator = logs.iterator();
                        while (iterator.hasNext()) {
                            log = iterator.next();
                            bet = DictionaryController.instance().get("dic.pc.betKey").getText(log.getBet());
                            for (userId = log.getUserId(); userId.length() < 150; userId += "&nbsp;") {}
                            content.append("<p><strong>").append(userId).append("[").append(log.getFreeze()).append(bet).append("]").append("</strong></p>");
                        }
                        rms = PcEggStore.this.roomStore.getByType("G03");
                        iterator2 = rms.iterator();
                        while (iterator2.hasNext()) {
                            r = iterator2.next();
                            msg = new Message("TXT_SYS", 0, content.toString());
                            MessageUtils.broadcast(r, msg);
                        }
                    }
                }
            });
        }
    }
}
