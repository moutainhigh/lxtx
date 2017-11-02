package org.takeback.chat.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gold_fund_history")
public class GoldFundHistory {
	private Integer id;
	private Integer uid;
	private Integer serverid;
	private BigDecimal amount;
	private Integer otype;
	private Integer card_num;
	private Date time;

	public GoldFundHistory() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Basic
	@Column(name = "uid")
	public Integer getUid() {
		return this.uid;
	}

	public void setUid(final Integer uid) {
		this.uid = uid;
	}

	@Basic
	@Column(name = "serverid")
	public Integer getServerid() {
		return serverid;
	}

	public void setServerid(Integer serverid) {
		this.serverid = serverid;
	}

	@Basic
	@Column(name = "amount")
	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	@Basic
	@Column(name = "otype")
	public Integer getOtype() {
		return otype;
	}

	public void setOtype(Integer otype) {
		this.otype = otype;
	}

	@Basic
	@Column(name = "card_num")
	public Integer getCard_num() {
		return card_num;
	}

	public void setCard_num(Integer card_num) {
		this.card_num = card_num;
	}

	@Basic
	@Column(name = "time")
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
