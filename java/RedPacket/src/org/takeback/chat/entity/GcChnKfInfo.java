// 
// Decompiled by Procyon v0.5.30
// 

package org.takeback.chat.entity;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GenerationType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Entity;

@Entity
@Table(name = "gc_chn_kf_info", schema = "gamechat", catalog = "")
public class GcChnKfInfo {
	private Integer id;
	private String chnno;
	private String kfNm;
	private String kfImgUrl;

	public GcChnKfInfo() {
	}

	public GcChnKfInfo(Integer id, String chnno, String kfNm, String kfImgUrl) {
		super();
		this.id = id;
		this.chnno = chnno;
		this.kfNm = kfNm;
		this.kfImgUrl = kfImgUrl;
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
	@Column(name = "chnno", nullable = false)
	public String getChnno() {
		return chnno;
	}

	public void setChnno(String chnno) {
		this.chnno = chnno;
	}

	@Basic
	@Column(name = "kf_nm", nullable = false)
	public String getKfNm() {
		return kfNm;
	}

	public void setKfNm(String kfNm) {
		this.kfNm = kfNm;
	}

	@Basic
	@Column(name = "kf_img_url", nullable = false)
	public String getKfImgUrl() {
		return kfImgUrl;
	}

	public void setKfImgUrl(String kfImgUrl) {
		this.kfImgUrl = kfImgUrl;
	}

}
