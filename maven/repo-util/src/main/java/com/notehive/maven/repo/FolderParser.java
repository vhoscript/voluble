package com.notehive.maven.repo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * For a file created by running "ls -lRt", parse the file
 * into a list of folder info objects 
 * 
 * @author hansloedolff
 */
public class FolderParser {
	
	private int lineNumber;

	/**
	 * look for directories (lines that start with a dot) the next line contains
	 * the file count, for example: A line containing "./ant/ant" is followed 
	 * by a line containing "total 32"
	 */
	List<FolderInfo> getFolderInfo(String file) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader(file));
		
		List<FolderInfo> folderInfoList = new ArrayList<FolderInfo>();
		
		lineNumber = 0;
		String line = null;
		String rootFolder = null;
		while ((line = br.readLine()) != null) {
			lineNumber++;
			// very first line will tell us what root folder is 
			if (lineNumber == 1) {
				rootFolder = line.substring(0, line.length()-1);	// cut ":" off end
			}
			if (line.startsWith(rootFolder)) {
				FolderInfo fi = new FolderInfo();
				fi.setName(line);
				
				line = br.readLine();
				if (line == null || !line.startsWith("total ")) {
					throw new RuntimeException("Expected another line after line number ["
							+ lineNumber + "] to contain the string \"total n\" (where 'n' is a number)");
				}
				int total = Integer.parseInt(line.substring(6).trim());
				fi.setTotal(total);

				fi.setFileInfoList(getFileInfoList(fi, br));

				folderInfoList.add(fi);
			}
		}
		br.close();
		
		return folderInfoList;
	}

	private List<FileInfo> getFileInfoList(FolderInfo folderInfo, BufferedReader br) throws IOException {
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
			fi.setFolderInfo(folderInfo);
			
			fileList.add(fi);
			
			line = br.readLine();
		}
		
		return fileList;
	}

	
}
