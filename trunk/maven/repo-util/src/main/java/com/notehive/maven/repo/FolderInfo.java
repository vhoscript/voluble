package com.notehive.maven.repo;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FolderInfo {
	
	private String name;
	
	/** don't really know what 'total' is, but its reported by 'ls' for each folder */
	private int total;

	private List<FileInfo> fileInfoList;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public boolean hasVersion() {
		/** regular expression to extract version from the folder name */
		String VERSION_REGEX = ".*/(\\d(?:\\.\\d)+(?:-SNAPSHOT)*):";
		
		return name.matches(VERSION_REGEX); 
	}

	private String getQualifyingName(String packagingType) {
		String REGEX = "./(.*)/(.*)/(\\d(?:\\.\\d)+(?:-SNAPSHOT)*):";

		// build an artifact id from the folder info, like so:
		// folder "./javax/servlet/servlet-api/2.4:" becomes
		// artifact "javax.servlet:servlet-api:jar:2.4"

		Pattern p = Pattern.compile(REGEX);
		Matcher m = p.matcher(name);
		if (m.matches()) {
			String groupId = m.group(1).replace("/", "."); 
			return groupId + ":" + m.group(2) + ":" + packagingType +":" + m.group(3);
		} else {
			throw new IllegalStateException("Could not parse in this name: " + name);
		}
	}
	
	public String[] getQualifyingNames() {
		return new String[] {getQualifyingName("jar"), 
				getQualifyingName("nar")};
	}
	
	public void setFileInfoList(List<FileInfo> fileInfoList) {
		this.fileInfoList = fileInfoList;
	}
	
	public List<FileInfo> getFileInfoList() {
		return fileInfoList;
	}
	
	public FileInfo getSnapshotJar() {
		for (FileInfo fileInfo : fileInfoList) {
			if (fileInfo.getName().endsWith("-SNAPSHOT.jar")) {
				return fileInfo;
			}
		}
		return null;
	}
	public FileInfo getLatestTimestampJar() {
		for (FileInfo fileInfo : fileInfoList) {
			if (fileInfo.getName().endsWith("-SNAPSHOT.jar")) {
				return fileInfo;
			}
		}
		return null;
	}

}
