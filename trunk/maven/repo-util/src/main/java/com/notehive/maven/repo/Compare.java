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
	private List<Difference> differences;

	private List<FolderInfo> repo1Folders;

	private List<FolderInfo> repo2Folders;
	
	public List<Difference> getDifferences() {
		return differences;
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
		
		differences = new ArrayList<Difference>();

		System.out.print(".");
		checkContains();
		System.out.print(".");
		checkFolderContents();
		System.out.print(".");
		
		System.out.println();
	}

	/** check if both lists have the same totals for the files they have in common */
	private void checkFolderContents() {
		for (FolderInfo fi1 : repo1Folders) {
			FolderInfo fi2 = findMatchingFolder(repo2Folders, fi1);
			if (fi2 != null) {
				String s = suspiciousDifference(fi1, fi2);
				if (s.length() > 0) {
					Difference difference =  new Difference();
					difference.setRepoFolder1(fi1);
					difference.setRepoFolder2(fi2);
					difference.setDiscrepancy(s);
					differences.add(difference);
				}
			}
		}
	}

	/* 
	 * find entries in list2 that are not in list 1.
	 * the "direction" string is purely costmetic
	 */
	private void checkContains() {
		for (FolderInfo fi : repo1Folders) {
			if (null == findMatchingFolder(repo2Folders, fi)) {
				Difference difference =  new Difference();
				difference.setRepoFolder1(fi);
				difference.setRepoFolder2(null);
				difference.setDiscrepancy("----- Artifact only in repo 1");
				differences.add(difference);
			}
		}
		for (FolderInfo fi : repo2Folders) {
			if (null == findMatchingFolder(repo1Folders, fi)) {
				Difference difference =  new Difference();
				difference.setRepoFolder1(null);
				difference.setRepoFolder2(fi);
				difference.setDiscrepancy("----- Artifact only in repo 2");
				differences.add(difference);
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
	
	/** 
	 * if this is a -SNAPSHOT version, check if the -SNAPSHOT.jars have the
	 * same size.  could also check if the largest -number.jar timestamp
	 * snapshots have the same versions 
	 */
	private String suspiciousDifference(FolderInfo folder1, FolderInfo folder2) {
		StringBuffer sb = new StringBuffer();
		
		compareLatestTimestampFileSizes(folder1, folder2, "jar", sb);
		compareSnapshotFileSizes(folder1, folder2, "jar", sb);
		compareLatestTimestampFileSizes(folder1, folder2, "pom", sb);
		compareSnapshotFileSizes(folder1, folder2, "pom", sb);
		
		if (sb.length() > 0) {
			sb.append("----- Files in repo 1").append("\n");;
			printAllFiles(sb, folder1);
			sb.append("----- Files in repo 2").append("\n");;
			printAllFiles(sb, folder2);
		}
		
		return sb.toString();
	}

	private void compareSnapshotFileSizes(FolderInfo folder1,
			FolderInfo folder2, String suffix, StringBuffer sb) {
		FileInfo snapshot1 = folder1.getSnapshot(suffix);
		FileInfo snapshot2 = folder2.getSnapshot(suffix);
		if (snapshot1 != null && snapshot2 != null) {
			if (snapshot1.getSize() != snapshot2.getSize()) {
				sb.append("----- Snapshot [" + suffix + "] sizes differ:").append("\n");
				sb.append(" Snapshot [" + suffix + "] from repo1, size: " + snapshot1.getSize()).append("\n");;
				sb.append(" Snapshot [" + suffix + "] from repo2, size: " + snapshot2.getSize()).append("\n");;
			}
		} else if (snapshot1 == null && snapshot2 != null) {
			sb.append("----- Could not find snapshot [" + suffix + "] in repo1 (but it is in repo 2)").append("\n");
		} else if (snapshot1 != null && snapshot2 == null) {
			sb.append("----- Could not find snapshot [" + suffix + "] in repo2 (but it is in repo 1)").append("\n");
		}
	}

	private void compareLatestTimestampFileSizes(FolderInfo folder1,
			FolderInfo folder2, String suffix, StringBuffer sb) {
		FileInfo latestTimestamp1 = folder1.getLatestTimestamp(suffix);
		FileInfo latestTimestamp2 = folder2.getLatestTimestamp(suffix);
		if (latestTimestamp1 != null && latestTimestamp2 != null) { 
			if (latestTimestamp1.getName().equals(latestTimestamp2.getName()) 
					&& latestTimestamp1.getSize()==latestTimestamp2.getSize()) {
//					System.out.println("Latest timestamp jar in repos match: " + latestTimestampJar1.getName()
//							+", size: " + latestTimestampJar1.getSize());
			} else {
				sb.append("----- Latest [" + suffix + "] timestamp versions in repos do not match!").append("\n");
				sb.append("From repo 1: " + latestTimestamp1.getName()
						+", size: " + latestTimestamp1.getSize()).append("\n");
				sb.append("From repo 2: " + latestTimestamp2.getName()
						+", size: " + latestTimestamp2.getSize()).append("\n");
			}
		} else if (latestTimestamp1 == null && latestTimestamp2 != null) {
			sb.append("----- Could not find latest timestamp [" + suffix + "] in repo1 (but it is in repo 2)").append("\n");
		} else if (latestTimestamp2 == null && latestTimestamp1 != null) {
			sb.append("----- Could not find latest timestamp [" + suffix + "] in repo2 (but it is in repo 1)").append("\n");
		}
	}

	private void printAllFiles(StringBuffer sb, FolderInfo folder) {
		for (FileInfo fileInfo : folder.getFileInfoList()) {
			sb.append("  " + fileInfo.getName() + " : " + fileInfo.getSize()).append("\n");
		}
	}

	
}
