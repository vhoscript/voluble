package com.notehive.maven.brancher;

import org.junit.Test;


public class TestMakeSnapshot {

	@Test
	public void testMakeSnapshot() {
		MakeSnapshot makeSnapshot = new MakeSnapshot();
		makeSnapshot.setRootFolder("/home/loedolff/dev/core-3.3.1");
		makeSnapshot.setFromVersion("3.3.0");
		makeSnapshot.setToVersion("3.3.1-SNAPSHOT");
		makeSnapshot.switchVersion();
	}
	
}
