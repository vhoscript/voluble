package com.notehive.maven.brancher;

public class Main {
	
	public static void main(String[] args) {
		MakeSnapshot makeSnapshot = new MakeSnapshot();
		
		makeSnapshot.setRootFolder("/home/loedolff/dev/core-3.3.1/common/util");
		makeSnapshot.setFromVersion("3.3.0");
		makeSnapshot.setToVersion("3.3.1-SNAPSHOT");
		makeSnapshot.setScmFrom("tags/releases/3.3.0");
		makeSnapshot.setScmTo("branches/3.3.1");
		makeSnapshot.switchVersion();
	}

}
