package com.loedolff.cn;

import java.io.IOException;
import java.net.URL;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

public class SourceDocumentTest {
	@Test public void testRead() throws IOException, SAXException {
		SourceDocument sd = new SourceDocument(new URL("http://cn.wsj.com"));
		RelocateHtml rh = new RelocateHtml();
		rh.write(sd, "target/wsj.html");
	}
}
