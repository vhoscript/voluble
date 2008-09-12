package com.notehive.maven.repo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

/**
 * compare the "ls -alR" output from two repositories to see if there are any
 * diffirences
 * 
 * @author hansloedolff
 * 
 */
public class Compare {

	private String lsFile1;
	private String lsFile2;
	
	/** folders that are different between two repositories */
	private List<FolderInfo> differentFolders;
	
	public List<FolderInfo> getDifferentFolders() {
		return differentFolders;
	}

	/** set output from ls -alR from two repositories */
	public void setFiles(String lsFile1, String lsFile2) {
		this.lsFile1 = lsFile1;
		this.lsFile2 = lsFile2;

	}

	public void process() throws Exception {

		List<FolderInfo> list1 = getFolderInfo(lsFile1);
		List<FolderInfo> list2 = getFolderInfo(lsFile2);
		
		differentFolders = new ArrayList<FolderInfo>();
		
		checkContains(">", list1, list2);
		checkContains("<", list2, list1);
		
		checkFileTotals(list1, list2);
	}

	/** check if both lists have the same totals for the files they have in common */
	private void checkFileTotals(List<FolderInfo> list1, List<FolderInfo> list2) {
		for (FolderInfo fi1 : list1) {
			for (FolderInfo fi2 : list2) {
				if (fi1.getName().equals(fi2.getName())) {
					if (fi1.getTotal() != fi2.getTotal()) {
						System.out.println("* " + fi1.getName() + " [" +
								fi1.getTotal() + "] vs. [" + 
								fi2.getTotal() +"]");
						differentFolders.add(fi1);
					}
				}
			}
		}
	}

	/* 
	 * find entries in list2 that are not in list 1.
	 * the "direction" string is purely costmetic
	 */
	private void checkContains(String direction, List<FolderInfo> list1, List<FolderInfo> list2) {
		for (FolderInfo fi1 : list1) {
			boolean foundMatch = false;
FOUND_MATCH:
			for (FolderInfo fi2 : list2) {
				if (fi1.getName().equals(fi2.getName())) {
					foundMatch = true;
					break FOUND_MATCH;
				}
			}
			if (!foundMatch) {
				System.out.println(direction + " " + fi1.getName());
				differentFolders.add(fi1);
			}
		}
	}

	/**
	 * look for directories (lines that start with a dot) the next line contains
	 * the file count, for example: A line containing "./ant/ant" is followed 
	 * by a line containing "total 32"
	 */
	private List<FolderInfo> getFolderInfo(String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		List<FolderInfo> folderInfoList = new ArrayList<FolderInfo>();
		
		int lineNumber = 0;
		String line = null;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			if (line.startsWith(".")) {
				FolderInfo fi = new FolderInfo();
				fi.setName(line);
				
				line = br.readLine();
				if (line == null || !line.startsWith("total ")) {
					throw new RuntimeException("Expected another line after line number ["
							+ lineNumber + "] to contain the string \"total n\" (where 'n' is a number)");
				}
				int total = Integer.parseInt(line.substring(6).trim());
				fi.setTotal(total);
				folderInfoList.add(fi);
			}
		}
		br.close();
		
		return folderInfoList;
	}
}
