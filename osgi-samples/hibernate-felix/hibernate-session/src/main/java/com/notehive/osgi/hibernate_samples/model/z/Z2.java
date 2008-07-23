package com.notehive.osgi.hibernate_samples.model.z;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "Z2")
public class Z2 {

	@Id
	@Column(name = "id")
	@GeneratedValue
	private long id;

	@Column(name = "string1")
	private String string1;

	@Column(name = "string2")
	private String string2;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getString1() {
		return string1;
	}

	public void setString1(String string1) {
		this.string1 = string1;
	}

	public String getString2() {
		return string2;
	}

	public void setString2(String string2) {
		this.string2 = string2;
	}

}
