package com.notehive.maven.brancher;

public class Main {
	
	public static void main(String[] args) {
		
		if (args.length != 5) {
			System.out.println("Brancher utility.  Recursively changes maven pom versions and updates scm references");
			System.out.println("Usage:");
			System.out.println("java -jar brancher.jar <root folder> <from version> <to version> <scm connection> <scm url>");
			System.out.println("For example:");
			System.out.println("java -jar brancher.jar ../core/ 3.3.0 3.3.1-SNAPSHOT scm:svn:http://somerepository/branches/3.3.0 http://somerepository/releases/3.3.0");
			return;
		}
		
		MakeSnapshot makeSnapshot = new MakeSnapshot();
		
		String rootFolder = args[0];
		String fromVersion = args[1];
		String toVersion = args[2];
		String scmConnection = args[3];
		String scmUrl = args[4];
		
		System.out.println("Running from root folder: " + rootFolder);
		System.out.println("Changing pom versions from: " + fromVersion);
		System.out.println("Changing pom versions to: " + toVersion);
		System.out.println("Using scm connection and developerConnection: " + scmConnection);
		System.out.println("Using scm url: " + scmUrl);
		System.out.println();
		
		makeSnapshot.setRootFolder(rootFolder);
		makeSnapshot.setFromVersion(fromVersion);
		makeSnapshot.setToVersion(toVersion);
		makeSnapshot.setScmConnection(scmConnection);
		makeSnapshot.setScmUrl(scmUrl);
		makeSnapshot.switchVersion();
	}

}
