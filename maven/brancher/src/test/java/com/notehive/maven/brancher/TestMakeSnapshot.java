package com.notehive.maven.brancher;

import org.junit.Test;


public class TestMakeSnapshot {

	@Test
	public void testMakeSnapshot() throws Exception {
		MakeSnapshot makeSnapshot = new MakeSnapshot();
		
		// makeSnapshot.setRootFolder("/home/loedolff/dev/core-3.3.1/common/");
		makeSnapshot.setRootFolder("target/test-classes/poms/");
		// makeSnapshot.setRootFolder("/home/loedolff/dev/core-3.3.1/common");
		makeSnapshot.setFromVersion("3.3.0");
		makeSnapshot.setToVersion("3.3.1-SNAPSHOT");
		makeSnapshot.setScmConnection("scm:svn:http://somerepository/releases/3.3.0");
		makeSnapshot.setScmUrl("http://somerepository/viewer/releases/3.3.0");
		makeSnapshot.switchVersion();

//		makeSnapshot.searchAndReplace(
//				//"http://(.*)/(tags)/releases/3.3.0/core/common",
//				"http:(.*)/releases/3.3.0/core/common",
//				"http:{0}/branches/3.3.1/core/common");
		
//		makeSnapshot.learn();
	}
	
}
