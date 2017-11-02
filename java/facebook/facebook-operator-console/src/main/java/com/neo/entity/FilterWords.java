package com.neo.entity;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "filterwords")
public class FilterWords {
	@javax.persistence.Id
	@GeneratedValue
	private BigInteger Id;
	@Column
	private String Country;
	@Column
	private String Words;

	public BigInteger getId() {
		return Id;
	}

	public void setId(BigInteger id) {
		Id = id;
	}

	public String getCountry() {
		return Country;
	}

	public void setCountry(String country) {
		Country = country;
	}

	public String getWords() {
		if (Words.indexOf(",") > 0) {
			String[] wordArr = Words.split(",");
			Words = String.join("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;", wordArr);	
		}
		return Words;
	}

	public void setWords(String words) {
		Words = words;
	}

}
