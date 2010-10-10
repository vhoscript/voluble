package com.loedolff.cn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SourceDocument {
	
	private StringBuffer source = new StringBuffer();
	private URL url;
	
	public SourceDocument(URL url) throws IOException, SAXException {
		final WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnScriptError(true); 
		final HtmlPage page = (HtmlPage) webClient.getPage(url);

//		HttpUnitOptions.setDefaultCharacterSet("GB2312");
		this.url = page.getUrl();
		
		// get URL - may be different from parameter if we were redirected
		// source.append(page.getWebResponse().getContentAsString());
		source.append("<html>\n");
		for (HtmlElement h : page.getDocumentElement().getChildElements()) {
			appendSource(h);
			source.append("\n");
		}
		source.append("</html>\n");
	}
	
	void appendSource(HtmlElement e) {
		source.append("<").append(e.getNodeName()).append(" ");
		for (DomAttr a : e.getAttributesMap().values()) {
			source.append(a.getName()).append("=").append("\"").append(a.getValue()).append("\"").append(" ");
		}
		source.append(">");
		source.append("\n");
		source.append(e.getTextContent());
		for (HtmlElement h : e.getChildElements()) {
			appendSource(h);
			source.append("\n");
		}
		source.append("</"+e.getNodeName()+">");
		source.append("\n");
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
