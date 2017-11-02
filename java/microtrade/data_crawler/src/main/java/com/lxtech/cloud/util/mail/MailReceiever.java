package com.lxtech.cloud.util.mail;

public class MailReceiever {
private int id;
private String job;
private String receiever;
private String description;
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public void setJob(String job) {
	this.job = job;
}
public void setReceiever(String receiever) {
	this.receiever = receiever;
}
public void setDescription(String description) {
	this.description = description;
}
public String getJob() {
	return job;
}
public String getReceiever() {
	return receiever;
}
public String getDescription() {
	return description;
}
}
