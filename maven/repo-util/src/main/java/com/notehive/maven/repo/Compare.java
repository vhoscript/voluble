package com.notehive.maven.repo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
	
	private int lineNumber = 0;

	private String lsFile1;
	private String lsFile2;
	
	/** folders that are different between two repositories */
	private List<FolderInfo> differentFolders;

	private List<FolderInfo> repo1Folders;

	private List<FolderInfo> repo2Folders;
	
	public List<FolderInfo> getDifferentFolders() {
		return differentFolders;
	}

	public List<FolderInfo> getRepo1Folders() {
		return repo1Folders;
	}

	public List<FolderInfo> getRepo2Folders() {
		return repo2Folders;
	}

	/** set output from ls -alR from two repositories */
	public void setFiles(String lsFile1, String lsFile2) {
		this.lsFile1 = lsFile1;
		this.lsFile2 = lsFile2;
	}

	public void process() throws Exception {

		repo1Folders = getFolderInfo(lsFile1);
		repo2Folders = getFolderInfo(lsFile2);
		
		differentFolders = new ArrayList<FolderInfo>();

		System.out.print(".");
		checkContains(">", repo1Folders, repo2Folders);
		System.out.print(".");
		checkContains("<", repo2Folders, repo1Folders);
		checkFileTotals(repo1Folders, repo2Folders);
		System.out.print(".");
		
		System.out.println();
	}

	/** check if both lists have the same totals for the files they have in common */
	private void checkFileTotals(List<FolderInfo> list1, List<FolderInfo> list2) {
		for (FolderInfo fi1 : list1) {
			for (FolderInfo fi2 : list2) {
				if (fi1.getName().equals(fi2.getName())) {
					if (fi1.getTotal() != fi2.getTotal()) {
//						System.out.println("* " + fi1.getName() + " [" +
//								fi1.getTotal() + "] vs. [" + 
//								fi2.getTotal() +"]");
						differentFolders.add(fi1);
					}
					break;
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
			if (null == findMatchingFolder(list2, fi1)) {
//				System.out.println(direction + " " + fi1.getName());
				differentFolders.add(fi1);
			}
		}
	}

	public FolderInfo findMatchingFolder(List<FolderInfo> list2, FolderInfo fi1) {
		for (FolderInfo fi2 : list2) {
			if (fi1.getName().equals(fi2.getName())) {
				return fi2;
			}
		}
		return null;
	}

	/**
	 * look for directories (lines that start with a dot) the next line contains
	 * the file count, for example: A line containing "./ant/ant" is followed 
	 * by a line containing "total 32"
	 */
	private List<FolderInfo> getFolderInfo(String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		List<FolderInfo> folderInfoList = new ArrayList<FolderInfo>();
		
		lineNumber = 0;
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

				fi.setFileInfoList(getFileInfoList(br));

				folderInfoList.add(fi);
			}
		}
		br.close();
		
		return folderInfoList;
	}

	private List<FileInfo> getFileInfoList(BufferedReader br) throws IOException {
		// get the list of files in the folder
		
		List<FileInfo> fileList = new ArrayList<FileInfo>();
		
		String line = br.readLine();
		while (line != null && line.trim().length() > 0) {
			lineNumber++;
			line = line.trim();
			int lastSpace = line.lastIndexOf(" ");
			String[] lineWords = line.split("\\s+");
			
			// expect line to look like this:
			// -rw-rw-r--   1 hudson hudson 20655 Jan 30  2008 maven-repository-metadata-2.0.jar
			if (lineWords.length != 9) {
				throw new RuntimeException("Expected 9 \"words\" in line number ["
						+ lineNumber + "].  I see [" + lineWords.length + "]");
			}
			// not interested in "." and ".." directory entries
			if (lineWords[8].equals(".") || lineWords[8].equals("..")) {
				line = br.readLine();
				continue;
			}
			FileInfo fi = new FileInfo();
			fi.setName(lineWords[8]);
			fi.setSize(Integer.parseInt(lineWords[4]));
			
			fileList.add(fi);
			
			line = br.readLine();
		}
		
		return fileList;
	}
}
