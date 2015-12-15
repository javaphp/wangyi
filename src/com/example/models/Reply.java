package com.example.models;

import android.R.integer;

public class Reply {
	private int id;
	private User user;
	private int articleId;
	
	private String replyContent;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public int getArticleId() {
		return articleId;
	}
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	public String getReplyContent() {
		return replyContent;
	}
	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}
	
	public Reply() {
		super();
	}
	public Reply(int id, User user, int articleId, String replyContent) {
		this.id = id;
		this.user = user;
		this.articleId = articleId;
		this.replyContent = replyContent;
	}
	
}
