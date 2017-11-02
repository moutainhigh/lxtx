package com.lxtx.fb.pojo;

public class Active {

	private long id;
	
	private String imgPath;
	
	private String text;

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

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public boolean valid(){
		return imgPath != null && imgPath.length() > 0 && text != null && text.length() > 0;
	}
}
