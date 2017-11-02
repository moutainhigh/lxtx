package org.takeback.chat.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "gc_lottery_detail", schema = "gamechat", catalog = "")
public class InmineInfo {
	@Id
	private int id;
	private Integer inCount = 0;
	private Double inBack = 0.0;
	@Column(name = "inCount", nullable = false, precision = 0)
	public Integer getInCount() {
		return inCount;
	}

	public void setInCount(Integer inCount) {
		this.inCount = inCount;
	}

	@Column(name = "inBack", nullable = false, precision = 0)
	public Double getInBack() {
		return inBack;
	}

	public void setInBack(Double inBack) {
		this.inBack = inBack;
	}

}
