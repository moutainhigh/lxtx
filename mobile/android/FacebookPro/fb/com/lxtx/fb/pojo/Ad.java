package com.lxtx.fb.pojo;

public class Ad {

	private long id;
	
	private long adSetId;
	
	private int sortIdx;
	
	private String imgPath;
	
	private String headLine;
	
	private String description;
	
	private String linkUrl;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getImgPath() {
		return imgPath;
	}

	public void setImgPath(String imgPath) {
		this.imgPath = imgPath;
	}

	public String getHeadLine() {
		return headLine;
	}

	public void setHeadLine(String headLine) {
		this.headLine = headLine;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}

	public long getAdSetId() {
		return adSetId;
	}

	public void setAdSetId(long adSetId) {
		this.adSetId = adSetId;
	}

	public int getSortIdx() {
		return sortIdx;
	}

	public void setSortIdx(int sortIdx) {
		this.sortIdx = sortIdx;
	}
	
	
}
