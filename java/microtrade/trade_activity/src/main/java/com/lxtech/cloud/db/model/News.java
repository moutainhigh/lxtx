package com.lxtech.cloud.db.model;

import java.util.List;

public class News {
	private List<PushArticle> articles;

	public List<PushArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<PushArticle> articles) {
		this.articles = articles;
	}
	
}
