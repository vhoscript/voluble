package com.notehive.maven.repo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.event.ListSelectionEvent;

public class FindLargeArtifacts {

	

	private List<FolderInfo> repoFolders;

	public void setFile(String lsFile) throws Exception {
		
		FolderParser folderInfoParser = new FolderParser();
		repoFolders = folderInfoParser.getFolderInfo(lsFile);
		
	}

	@SuppressWarnings("unchecked")
	public void process() {
		
		// make a list of all files
		List<FileInfo> files = new ArrayList<FileInfo>();
		
		// only get the files that are larger than 100MB
		for (FolderInfo folderInfo : repoFolders) {
			for (FileInfo fileInfo : folderInfo.getFileInfoList()) {
				if (fileInfo.getSize() > 100000000) {
					files.add(fileInfo);
				}
			}
		}
		
		// sort files by size
//		Comparator c = new Comparator<FileInfo>(){
//		public int compare(FileInfo arg0, FileInfo arg1){
//			return new Integer(arg1.getSize()).compareTo(arg0.getSize());
//		}
//	};
		// sort by name
		Comparator c = new Comparator<FileInfo>(){
		public int compare(FileInfo arg0, FileInfo arg1){
				return arg0.getName().compareTo(arg1.getName());
			}
		};
		Collections.sort(files, c);
		
		for (int i = 0; i<files.size(); i++) {
			FileInfo fileInfo = files.get(i);

			// print folder URL
			String folderName = fileInfo.getFolderInfo().getName();
			String rootFolder = repoFolders.get(0).getName();
			int x = rootFolder.lastIndexOf("/", rootFolder.length()-3);
			String folderUrl = folderName.substring(x);
			System.out.println("http://maven.tva.tvworks.com/" + folderUrl);
			
			// print file name and size
			System.out.println(fileInfo.getName() + " - " + fileInfo.getSize());
			System.out.println("------------------------------------------------");
		}

		
		
		// print biggest 100 files and the folders they are in
		for (int i = 0; i<100 && i<files.size(); i++) {
			FileInfo fileInfo = files.get(i);
			
			// print folder URL
			String folderName = fileInfo.getFolderInfo().getName();
			String rootFolder = repoFolders.get(0).getName();
			int x = rootFolder.lastIndexOf("/", rootFolder.length()-3);
			String folderUrl = folderName.substring(x);
			System.out.println("http://maven.tva.tvworks.com/" + folderUrl);
			
			// print file name and size
			System.out.println(fileInfo.getName() + " - " + fileInfo.getSize());
			System.out.println("------------------------------------------------");
		}
		
		System.out.println("total files: " + files.size());
		
	}
	

}
