package org.takeback.chat.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.takeback.util.BeanUtils;

@Entity
@Table(name = "gc_lottery_detail_human", schema = "gamechat", catalog = "")
public class GcHumanLotteryDetail{
	
    private Integer id;
    private String lotteryid;
    private Integer uid;
    private BigDecimal coin;
    private Date createDate;
    private String roomId;
    private String gameType;
    private double deposit;
    private double addback;
    private double inoutNum;
    private String desc1;
    private int masterId;
    private int gameUid;
    private int status;
    
    public GcHumanLotteryDetail() {
        this.uid = 0;
        this.roomId = "";
        this.gameType = "";
        this.deposit = 0.0;
        this.addback = 0.0;
        this.inoutNum = 0.0;
        this.desc1 = "";
        this.masterId = 0;
        this.gameUid = 0;
        this.status = 0;
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return this.id;
    }
    
    public void setId(final Integer id) {
        this.id = id;
    }
    
    @Basic
    @Column(name = "lotteryid", nullable = false, length = 50)
    public String getLotteryid() {
        return this.lotteryid;
    }
    
    public void setLotteryid(final String lotteryid) {
        this.lotteryid = lotteryid;
    }
    
    @Basic
    @Column(name = "uid", nullable = false)
    public Integer getUid() {
        return this.uid;
    }
    
    public void setUid(final Integer uid) {
        this.uid = uid;
    }
    
    @Basic
    @Column(name = "coin", nullable = false, precision = 0)
    public BigDecimal getCoin() {
        return this.coin;
    }
    
    public void setCoin(final BigDecimal coin) {
        this.coin = coin;
    }
    
    @Basic
    @Column(name = "createdate", nullable = false)
    public Date getCreateDate() {
        return this.createDate;
    }
    
    public void setCreateDate(final Date createdate) {
        this.createDate = createdate;
    }
    
    @Basic
    @Column(name = "addback", nullable = false)
    public double getAddback() {
        return this.addback;
    }
    
    public void setAddback(final double addback) {
        this.addback = addback;
    }
    
    @Basic
    @Column(name = "deposit", nullable = false)
    public double getDeposit() {
        return this.deposit;
    }
    
    public void setDeposit(final double deposit) {
        this.deposit = deposit;
    }
    
    @Basic
    @Column(name = "gameType", nullable = false)
    public String getGameType() {
        return this.gameType;
    }
    
    public void setGameType(final String gameType) {
        this.gameType = gameType;
    }
    
    @Basic
    @Column(name = "inoutNum", nullable = false)
    public double getInoutNum() {
        return this.inoutNum;
    }
    
    public void setInoutNum(final double inout) {
        this.inoutNum = inout;
    }
    
    @Basic
    @Column(name = "masterId", nullable = false)
    public int getMasterId() {
        return this.masterId;
    }
    
    public void setMasterId(final int masterId) {
        this.masterId = masterId;
    }
    
    @Basic
    @Column(name = "roomId", nullable = false)
    public String getRoomId() {
        return this.roomId;
    }
    
    public void setRoomId(final String roomId) {
        this.roomId = roomId;
    }
    
    @Basic
    @Column(name = "desc1", nullable = false)
    public String getDesc1() {
        return this.desc1;
    }
    
    public void setDesc1(final String desc1) {
        this.desc1 = desc1;
    }
    
    public int getGameUid() {
		return gameUid;
	}

	public void setGameUid(int gameUid) {
		this.gameUid = gameUid;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}	
	
	//发包
	public static final int OPER_ISSUE_PACKET = 1;
	//拆包
	public static final int OPER_OPEN_PACKET = 2;

	private Integer chnno;

	private int type;

	@Basic
	@Column(name = "chnno", nullable = true, precision = 0)
	public Integer getChnno() {
		return chnno;
	}

	public void setChnno(Integer chnno) {
		this.chnno = chnno;
	}

	@Basic
	@Column(name = "type", nullable = true, precision = 0)
	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public static void main(String[] args) {
		GcLotteryDetail lotteryDetail = new GcLotteryDetail();
		lotteryDetail.setUid(1844);
		lotteryDetail.setCoin(BigDecimal.ZERO);
		lotteryDetail.setCreateDate(new Date());
		lotteryDetail.setDeposit(5.0d);
		lotteryDetail.setDesc1("desc");
		lotteryDetail.setGameType("G04");
		lotteryDetail.setGameUid(23883);
		lotteryDetail.setAddback(10.0d);
		lotteryDetail.setInoutNum(5.0d);
		lotteryDetail.setMasterId(1848);
		lotteryDetail.setLotteryid("LT201701241917194530005");
		lotteryDetail.setRoomId("S006");
		lotteryDetail.setStatus(0);
		
		final GcHumanLotteryDetail humanLotteryDetail = BeanUtils.map(lotteryDetail, GcHumanLotteryDetail.class);
		humanLotteryDetail.setChnno(3001);
		humanLotteryDetail.setType(1);
		
		System.out.println(humanLotteryDetail.getAddback());
		System.out.println(humanLotteryDetail.getCreateDate());
	}
	
}
