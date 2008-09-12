package com.notehive.maven.repo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.notehive.maven.util.FileUtil;

/**
 * Check if artifacts from the different folders are used in a pom
 * 
 * @author hansloedolff
 * 
 */
public class CheckUsage {
	
	private List<String> pomFileLines;

	/**
	 * 
	 * @param suspects
	 * @param pomOutputFile output from running maven with the "-X" option 
	 * @throws IOException 
	 */
	public void checkUsage(Compare compare, String pomOutputFile) throws IOException {

		StringBuffer sb = FileUtil.readFile(pomOutputFile);
		
		for (FolderInfo folderInfo : compare.getDifferentFolders()) {
			
			// build a suspected artifact name from the folder info, like so:
			// folder "./javax/servlet/servlet-api/2.4:" becomes
			// artifact "javax.servlet:servlet-api:jar:2.4"
			
			// only process folders that end in a version number (or -SNAPSHOT)
			if (folderInfo.hasVersion()) {
				String[] suspects = folderInfo.getQualifyingNames();
				
				for (String suspect : suspects) {
					// if suspect is found
					int i = sb.indexOf(suspect); 
					if (i >= 0) {
						
						// check that suspect name is not actually part of a longer valid 
						// (non "different") folder.  for example, we may have matched 
						// "org.apache.maven.surefire:surefire-api:jar:2.4", but 
						// in reality the pom references "org.apache.maven.surefire:surefire-api:jar:2.4.4"
						
						char c1 = sb.charAt(i+suspect.length()); 
						char c2 = sb.charAt(i+suspect.length()+1);
						// first character after suspect is not a dot
						// AND 2nd character is not a number
						if (c1 != '.' && (c2 < 48 || c2 > 58)) {
							System.out.println("Suspect: " + suspect);
							System.out.println("Folder: " + folderInfo.getName());
							
							checkFolderContent(compare, folderInfo);
						}
					}
				}
			}
			
		}
	}

	/** 
	 * if this is a -SNAPSHOT version, check if the -SNAPSHOT.jars have the
	 * same size.  could also check if the largest -number.jar timestamp
	 * snapshots have the same versions 
	 */
	private void checkFolderContent(Compare compare, FolderInfo folderInfo) {
		FolderInfo folder1 = compare.findMatchingFolder(compare.getRepo1Folders(), folderInfo);
		FolderInfo folder2 = compare.findMatchingFolder(compare.getRepo2Folders(), folderInfo);
		if (folder1 != null && folder2 != null) {
			FileInfo snapshotJar1 = folder1.getSnapshotJar();
			FileInfo snapshotJar2 = folder2.getSnapshotJar();
			if (snapshotJar1 != null && snapshotJar2 != null) {
				System.out.println("Snapshot jar from repo1, size: " + snapshotJar1.getSize());
				System.out.println("Snapshot jar from repo2, size: " + snapshotJar2.getSize());
				if (snapshotJar1.getSize() != snapshotJar2.getSize()) {
					System.out.println("----- Files in repo 1");
					printAllFiles(folder1);
					System.out.println("----- Files in repo 2");
					printAllFiles(folder2);
				}
			}
		} else if (folder1 == null && folder2 != null) {
			System.out.println("----- Artifact only in repo1");
		} else if (folder2 == null && folder1 != null) {
			System.out.println("----- Artifact only in repo2");
		}
	}

	private void printAllFiles(FolderInfo folder) {
		for (FileInfo fileInfo : folder.getFileInfoList()) {
			System.out.println("  " + fileInfo.getName() + " : " + fileInfo.getSize());
		}
	}


}
