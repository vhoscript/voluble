package com.notehive.maven.brancher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.notehive.maven.util.FileUtil;

public class MakeSnapshot {

	private File rootFolder;

	private String fromVersion;

	private String toVersion;

	private Transformer transformer;

	public void setRootFolder(String rootFolder) {
		this.rootFolder = new File(rootFolder);
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}

	public void switchVersion() {
		
		try {
			StringBuffer updateVersionTransform = FileUtil.readResource("update-version.xsl");

			TransformerFactory tFactory = TransformerFactory.newInstance();
			
			transformer = tFactory.newTransformer(new StreamSource(
					new StringReader(updateVersionTransform.toString())));

			// find all the poms
			findPoms(rootFolder);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void findPoms(File folder) throws Exception {

		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.getName().equals("pom.xml")) {
				applyTransform(file);
			} else if (file.isDirectory()) {
				findPoms(file);
			}
		}
	}

	private void applyTransform(File pomFile) throws Exception {

		System.out.println("Processing: " + pomFile.getAbsolutePath());

		File outputFile = new File(pomFile.getAbsolutePath() + ".out");
		transformer.transform(new StreamSource(pomFile), new StreamResult(
				new FileOutputStream(outputFile)));

		System.out.println("-------------------------------------------------");
	}

}
