package com.lxtech.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CloudOrder implements Serializable {
    /**
     * cloud_order.id
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer id;

    /**
     * cloud_order.uid
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer uid;

    /**
     * cloud_order.subject_id (标的物id)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer subjectId;
    
    private String subject;

    public String getSubject() {
      return subject;
    }

    public void setSubject(String subject) {
      this.subject = subject;
    }

    /**
     * cloud_order.direction (1:做多2:做空)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer direction;

    /**
     * cloud_order.money
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private BigDecimal money;

    /**
     * cloud_order.amount
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer amount;

    /**
     * cloud_order.contract_money
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private BigDecimal contractMoney;

    /**
     * cloud_order.limit (止损点)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer limit;

    /**
     * cloud_order.cleared (是否平仓)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer cleared;

    /**
     * cloud_order.order_time
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Date orderTime;

    /**
     * cloud_order.clear_time
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Date clearTime;

    /**
     * cloud_order.order_index
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer orderIndex;

    /**
     * cloud_order.clear_index
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer clearIndex;

    /**
     * cloud_order.commission (手续费)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private BigDecimal commission;

    /**
     * cloud_order.status (结算状态,1:已结算，2.未结算)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer status;

    /**
     * cloud_order.clear_upper_limit (结算的上限值)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer clearUpperLimit;

    /**
     * cloud_order.clear_down_limit (结算的下限值)
     * @ibatorgenerated 2016-10-28 18:18:36
     */
    private Integer clearDownLimit;
    
    //0 未交付 else 1 已平仓 
    private String dealkind;
    //2赚、3亏、 退1
    private String perStatus;
    private String clearTypeNm;
    //赚亏样式
    private String perStyle;
    // 看涨  看跌
    private String orderType;
    //利润
    private BigDecimal profit;
    //标的物名
    private String cname;
    
    private int human;
    
    private String chnno;
    
    private BigDecimal coupon_commission;

    private BigDecimal sys_profit;
    
    private BigDecimal cash;
    
    private BigDecimal sys_loss;
    
    private BigDecimal f_profit;
    
    private BigDecimal coupou_money;
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Integer subjectId) {
        this.subjectId = subjectId;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public void setMoney(BigDecimal money) {
        this.money = money;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public BigDecimal getContractMoney() {
        return contractMoney;
    }

    public void setContractMoney(BigDecimal contractMoney) {
        this.contractMoney = contractMoney;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getCleared() {
        return cleared;
    }

    public void setCleared(Integer cleared) {
        this.cleared = cleared;
    }

    public String getOrderTime() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (orderTime != null) {
			return sdf.format(orderTime);
		}
		return "";
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public String getClearTime1() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (clearTime != null) {
			return sdf.format(clearTime);
		}
		return "";
    }

    public Date getClearTime() {
		return clearTime;
	}

	public void setClearTime(Date clearTime) {
        this.clearTime = clearTime;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Integer getClearIndex() {
        return clearIndex;
    }

    public void setClearIndex(Integer clearIndex) {
        this.clearIndex = clearIndex;
    }

    public BigDecimal getCommission() {
        return commission;
    }

    public void setCommission(BigDecimal commission) {
        this.commission = commission;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getClearUpperLimit() {
        return clearUpperLimit;
    }

    public void setClearUpperLimit(Integer clearUpperLimit) {
        this.clearUpperLimit = clearUpperLimit;
    }

    public Integer getClearDownLimit() {
        return clearDownLimit;
    }

    public void setClearDownLimit(Integer clearDownLimit) {
        this.clearDownLimit = clearDownLimit;
    }

	public String getDealkind() {
		return dealkind;
	}

	public void setDealkind(String dealkind) {
		this.dealkind = dealkind;
	}

	public String getPerStatus() {
		return perStatus;
	}

	public void setPerStatus(String perStatus) {
		this.perStatus = perStatus;
	}

	public String getPerStyle() {
		return perStyle;
	}

	public void setPerStyle(String perStyle) {
		this.perStyle = perStyle;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public BigDecimal getProfit() {
		return profit;
	}

	public void setProfit(BigDecimal profit) {
		this.profit = profit;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getClearTypeNm() {
		return clearTypeNm;
	}

	public void setClearTypeNm(String clearTypeNm) {
		this.clearTypeNm = clearTypeNm;
	}

	public int getHuman() {
		return human;
	}

	public void setHuman(int human) {
		this.human = human;
	}

	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	public BigDecimal getCoupon_commission() {
		return coupon_commission;
	}

	public void setCoupon_commission(BigDecimal coupon_commission) {
		this.coupon_commission = coupon_commission;
	}

	public BigDecimal getSys_profit() {
		return sys_profit;
	}

	public void setSys_profit(BigDecimal sys_profit) {
		this.sys_profit = sys_profit;
	}

	public BigDecimal getCash() {
		return cash;
	}

	public void setCash(BigDecimal cash) {
		this.cash = cash;
	}

	public BigDecimal getSys_loss() {
		return sys_loss;
	}

	public void setSys_loss(BigDecimal sys_loss) {
		this.sys_loss = sys_loss;
	}

	public BigDecimal getF_profit() {
		return f_profit;
	}

	public void setF_profit(BigDecimal f_profit) {
		this.f_profit = f_profit;
	}

	public BigDecimal getCoupou_money() {
		return coupou_money;
	}

	public void setCoupou_money(BigDecimal coupou_money) {
		this.coupou_money = coupou_money;
	}
 


	
	
	
}