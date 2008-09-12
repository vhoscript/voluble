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
	public void checkUsage(List<FolderInfo> suspects, String pomOutputFile) throws IOException {

		StringBuffer sb = FileUtil.readFile(pomOutputFile);
		
		for (FolderInfo folderInfo : suspects) {
			
			// build a suspected artifact name from the folder info, like so:
			// folder "./javax/servlet/servlet-api/2.4:" becomes
			// artifact "javax.servlet:servlet-api:jar:2.4"
			
			// only process folders that end in a version number (or -SNAPSHOT)
			if (folderInfo.hasVersion()) {
				String suspect = folderInfo.getQualifyingName();
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
					}
				}
			}
			
		}
	}

}
