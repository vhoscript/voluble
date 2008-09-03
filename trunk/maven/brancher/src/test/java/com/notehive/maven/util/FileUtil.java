package com.notehive.maven.util;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

	public static StringBuffer readFile(String filename)
			throws java.io.IOException {
		return readInputStream(new FileInputStream(filename));
	}

	public static StringBuffer readResource(String resource)
			throws java.io.IOException {
		return readInputStream(FileUtil.class
				.getClassLoader().getResourceAsStream(resource));
	}

	private static StringBuffer readInputStream(InputStream in)
			throws FileNotFoundException, IOException {
		if (in == null) {
			throw new RuntimeException("Resource not found");
		}
		StringBuffer sb = new StringBuffer();
		BufferedInputStream inputStream = new BufferedInputStream(in);
		int i;
		while ((i = inputStream.read()) != -1) {
			sb.append((char) i);
		}
		in.close();
		return sb;
	}

}
