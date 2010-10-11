package com.loedolff.cn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SourceDocument {
	
	private StringBuffer source = new StringBuffer();
	private URL url;
	
	public SourceDocument(URL url) throws IOException, SAXException {
		final WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnScriptError(false); 
		final HtmlPage page = (HtmlPage) webClient.getPage(url);

//		HttpUnitOptions.setDefaultCharacterSet("GB2312");
		this.url = page.getUrl();
		
		// get URL - may be different from parameter if we were redirected
		// source.append(page.getWebResponse().getContentAsString());
		source.append("<html>");
//		source.append(page.getDocumentElement().asXml());
		
		for (DomNode h : page.getDocumentElement().getChildNodes()) {
			appendSource(h);
			source.append("");
		}
		source.append("</html>");
	}
	
	void appendSource(DomNode e) {
		if (e.getNodeName().equals("#text")) {
			source.append(e.asText());
			return;
		}
		if (e.getNodeName().equals("#comment")) {
			source.append("<!--");
			source.append(e.asText());
			source.append("-->");
			return;
		}
		source.append("<").append(e.getNodeName()).append(" ");
		NamedNodeMap nnm = e.getAttributes();
		for (int i=0; i<nnm.getLength(); i++) {
			Node node = nnm.item(i);
			source.append(node.getNodeName()).append("=").append("\"").append(node.getNodeValue()).append("\"").append(" ");
		}
		source.append(">");
		source.append("");
		if (e.getNodeName().equals("script")) {
			source.append(e.getTextContent());
		}
		for (DomNode h : e.getChildNodes()) {
			appendSource(h);
			source.append("");
		}
		source.append("</"+e.getNodeName()+">");
		source.append("");
	}
	
	private boolean isChineseCharacter(Character c) {
		return c>12600 & c < 65000;
	}
	
	/** write source document to local file 
	 * @throws IOException */
	public void write(String file) throws IOException {
		
		StringBuffer output;
		output = replace("src", source);
		output = replace("href", output);
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),
				Charset.forName("GB2312"));
			   
		out.append(output);
		
		out.close();
	}

	private StringBuffer replace(String attribute, StringBuffer input) {
		StringBuffer output = new StringBuffer();
		
		Pattern p = Pattern.compile(attribute + "=['\"]?(.*?)['\"]?([ >])");
		Matcher m = p.matcher(input);
		while (m.find()) {
			if (m.group(1).toLowerCase().startsWith("http") ||
					m.group(1).toLowerCase().startsWith("javascript")) {
				String r = attribute + "=\""+m.group(1)+"\""+m.group(2);
				m.appendReplacement(output, r);
			} else {
				String r = attribute + "=\""+url.toExternalForm()+m.group(1)+"\"" + m.group(2);
				m.appendReplacement(output, r);
			}
		}
		return output;
	}

	public StringBuffer getSource() {
		return source;
	}

}
