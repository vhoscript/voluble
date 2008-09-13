package com.notehive.maven.repo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileInfo {
	
	private String LATEST_VERSION_REGEX = ".*-(\\d*)\\.";

	private String name;
	private int size;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}

	public boolean getHasVersion(String suffix) {
		return name.matches(LATEST_VERSION_REGEX + suffix);
	}

	public int getLatestVersion(String suffix) {
		// timestamp versions look like this:
		// tva-common-plugin-3.4.0-20080716.202607-6.pom
		
		Pattern p = Pattern.compile(LATEST_VERSION_REGEX + suffix);
		Matcher m = p.matcher(name);
		if (m.matches()) {
			String latestVersion = m.group(1); 
			return Integer.parseInt(latestVersion);
		} else {
			throw new IllegalStateException("Could not get latest version in this name: " + name);
		}

	}

	
}
