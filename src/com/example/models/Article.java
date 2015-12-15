package com.example.models;

import android.R.integer;

public class Article {
	private int id;
	private String title;
	private String brief;
	private String content;
	private String imgUrl;
	
	public Article() {
		super();
	}

	public Article(int id, String title, String brief, String content,
			String imgUrl) {
		super();
		this.id = id;
		this.title = title;
		this.brief = brief;
		this.content = content;
		this.imgUrl = imgUrl;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBrief() {
		return brief;
	}

	public void setBrief(String brief) {
		this.brief = brief;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
}
