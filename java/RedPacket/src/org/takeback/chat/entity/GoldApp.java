// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gold_app")
public class GoldApp {
	private Integer id;
	private String appname;
	private String server;
	private BigDecimal card_price;

	public GoldApp() {
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
	@Column(name = "appname", nullable = true)
	public String getAppname() {
		return this.appname;
	}

	public void setAppname(final String appname) {
		this.appname = appname;
	}

	@Basic
	@Column(name = "server", nullable = true)
	public String getServer() {
		return this.server;
	}

	public void setServer(final String server) {
		this.server = server;
	}

	@Basic
	@Column(name = "card_price", nullable = true)
	public BigDecimal getCard_price() {
		return this.card_price;
	}

	public void setCard_price(final BigDecimal card_price) {
		this.card_price = card_price;
	}

}
