package com.notehive.maven.brancher;

import java.io.File;

public interface PomTransformer {
	
	void transform(File pomFile) throws Exception;

}
