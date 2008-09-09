package com.notehive.maven.brancher;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.StringReader;
import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import com.notehive.maven.util.FileUtil;

public class MakeSnapshot {

	private File rootFolder;

	private String fromVersion;

	private String toVersion;

	/** prefix for scm/connection and scm/developerConnection */ 
	private String scmConnection;

	private String scmUrl;

	private StringBuffer rawUpdateVersionXml;

	private TransformerFactory factory;

	public void setRootFolder(String rootFolder) {
		this.rootFolder = new File(rootFolder);
	}

	public void setFromVersion(String fromVersion) {
		this.fromVersion = fromVersion;
	}

	public void setToVersion(String toVersion) {
		this.toVersion = toVersion;
	}

	public void setScmConnection(String scmConnection) {
		this.scmConnection = scmConnection;
	}

	public void setScmUrl(String scmUrl) {
		this.scmUrl = scmUrl;
	}

	public void switchVersion() {
		
		try {
			rawUpdateVersionXml = FileUtil.readResource("update-version.xsl");
			
			System.setProperty("javax.xml.transform.TransformerFactory",
				"net.sf.saxon.TransformerFactoryImpl");
			
			factory = TransformerFactory.newInstance();

			// find all the poms
			findPoms(rootFolder, new PomTransformer() {
				
				public void transform(File pomFile) throws Exception {
					System.out.println("Processing: " + pomFile.getAbsolutePath());
					
					String scmUrlPathToAppend = pomFile.getParentFile().getAbsolutePath().
							substring(rootFolder.getAbsolutePath().length());
					
					File pomBackup = new File(pomFile.getAbsolutePath() + ".backup");
					
					// first make a backup
					FileUtil.copyFile(pomFile.getAbsolutePath(), 
							pomBackup.getAbsolutePath());
					
					// then overwrite the original file
					String updateVersionTransform = MessageFormat.format(rawUpdateVersionXml.toString(),
							new Object[]{fromVersion, toVersion, 
										 scmConnection + scmUrlPathToAppend, 
										 scmUrl + scmUrlPathToAppend});
					
					Transformer transformer = factory.newTransformer(new StreamSource(
							new StringReader(updateVersionTransform.toString())));

					transformer.transform(new StreamSource(pomBackup), new StreamResult(
							new FileOutputStream(pomFile)));
					
// to only print the transform and not do it:					
//					transformer.transform(new StreamSource(pomBackup), new StreamResult(
//							System.out));
				}
					
				});
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	private void findPoms(File folder, PomTransformer pomTransformer) throws Exception {

		File[] files = folder.listFiles();
		for (File file : files) {
			if (file.getName().equals("pom.xml")) {
				pomTransformer.transform(file);
			} else if (file.isDirectory()) {
				findPoms(file, pomTransformer);
			}
		}
	}

	
	public void learn() throws Exception {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(
				MakeSnapshot.class.getClassLoader().getResourceAsStream("learn.xsl")));
		transformer.transform(
				new StreamSource(MakeSnapshot.class.getClassLoader().getResourceAsStream("learn.xml")), 
				new StreamResult(System.out));
	}

	public void searchAndReplace(final String from, final String to) throws Exception {
		// find all the poms
		findPoms(rootFolder, new PomTransformer() {
			
			public void transform(File pomFile) throws Exception {
				
				StringBuffer sb = FileUtil.readFile( 
						pomFile.getAbsolutePath());
				
			    Pattern pattern = Pattern.compile(from);
			    
			    StringBuffer result = new StringBuffer();
			    Matcher matcher = pattern.matcher(sb);
			    while(matcher.find()) {
			    	String replacement = MessageFormat.format(to, matcher.group(1));
			    	matcher.appendReplacement(result, replacement);
				}
			    matcher.appendTail(result );
				
				System.out.println(result.toString());
			}
				
			});
		
	}

}
