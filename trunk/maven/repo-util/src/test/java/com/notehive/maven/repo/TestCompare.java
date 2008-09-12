package com.notehive.maven.repo;

import org.junit.Test;


public class TestCompare {

	@Test public void testCompare() throws Exception {
		
		
		Compare compare = new Compare();
		compare.setFiles("target/test-classes/repo1", "target/test-classes/repo2");
		
		compare.process();
		
		CheckUsage checkUsage = new CheckUsage();
		checkUsage.checkUsage(compare, "target/test-classes/pom-output");
		
	}
}
