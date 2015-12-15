package com.example.models;

import android.R.integer;

public class User {

	private int id;
	private String name;
	private String password;
	
	public User() {
		super();
	}

	public User(int id2, String name, String password) {
		this.id = id2;
		this.name = name;
		this.password = password;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
