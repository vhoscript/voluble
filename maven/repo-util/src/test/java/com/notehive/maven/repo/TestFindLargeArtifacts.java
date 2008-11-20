package com.notehive.maven.repo;

import org.junit.Test;


public class TestFindLargeArtifacts {

	@Test public void testCompare() throws Exception {
		
		FindLargeArtifacts fla = new FindLargeArtifacts();
		fla.setFile("target/test-classes/snapshot-repo");
		fla.process();
		
	}
}
