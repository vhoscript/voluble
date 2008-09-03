package com.notehive.maven.brancher;

import org.junit.Test;


public class TestMakeSnapshot {

	@Test
	public void testMakeSnapshot() throws Exception {
		MakeSnapshot makeSnapshot = new MakeSnapshot();
		

		
		// makeSnapshot.setRootFolder("/home/loedolff/dev/core-3.3.1");
		makeSnapshot.setRootFolder("/home/loedolff/dev/core-3.3.1/common/util");
		makeSnapshot.setFromVersion("3.3.0");
		makeSnapshot.setToVersion("3.3.1-SNAPSHOT");
		makeSnapshot.switchVersion();
		
//		makeSnapshot.learn();
	}
	
}
