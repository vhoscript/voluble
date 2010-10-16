package com.loedolff.cn;

import java.io.IOException;
import java.net.URL;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class SourceDocument {
	
	private URL url;
	private HtmlPage page;
	
	public SourceDocument(URL url) throws IOException, SAXException {
		final WebClient webClient = new WebClient();
		webClient.setThrowExceptionOnScriptError(false); 
		webClient.setThrowExceptionOnFailingStatusCode(false);
		page = (HtmlPage) webClient.getPage(url);
		this.url = page.getUrl();	
	}
	
	void appendSource(DomNode e) {
		if (e.getNodeName().equals("#text")) {
			source.append(escapeCharCodes(e.getNodeValue()));
			return;
		}
		if (e.getNodeName().equals("#comment")) {
			source.append("<!--");
			source.append(e.getTextContent());
			source.append("-->");
			source.append("\n");
			return;
		}
		source.append("<").append(e.getNodeName());
		NamedNodeMap nnm = e.getAttributes();
		for (int i=0; i<nnm.getLength(); i++) {
			Node node = nnm.item(i);
			source.append(" ").append(node.getNodeName()).append("=").append("\"").append(node.getNodeValue()).append("\"");
		}
		source.append(">");
		if (e.getNodeName().equals("script")) {
			source.append(e.getTextContent());
		}
		for (DomNode h : e.getChildNodes()) {
			appendSource(h);
		}
		source.append("</"+e.getNodeName()+">");
	}
	
	private String escapeCharCodes(String nodeValue) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<nodeValue.length(); i++) {
			sb.append(escapeCharCode(nodeValue.charAt(i)));
		}
		return sb.toString();
	}

	private Object escapeCharCode(char c) {
		if (c < 32) {
			return "";
		} else if (c < 128 || c > 10000) {
			return c;
		} else {
			return "&#" + (int) c + ";";
		}
	}

	private StringBuffer source = new StringBuffer();
	public StringBuffer asStringBuffer() {
		source.append("<html>");
		for (DomNode h : page.getDocumentElement().getChildNodes()) {
			appendSource(h);
			source.append("");
		}
		source.append("</html>");
		return source;
	}

	public String getUrl() {
		return url.toExternalForm();
	}

	public void replaceSource(StringBuffer sb) {
		this.source = sb;
	}

	interface NodeVisitor {
		void nodeStart(DomNode e);
		void nodeEnd(DomNode e);
	}

	public void visitAllNodes(NodeVisitor nodeVisitor) {
		for (DomNode h : page.getDocumentElement().getChildNodes()) {
			visitChildren(nodeVisitor, h);
		}
	}
	
	private void visitChildren(NodeVisitor nodeVisitor, DomNode e) {
		nodeVisitor.nodeStart(e);
		for (DomNode h : e.getChildNodes()) {
			visitChildren(nodeVisitor, h);
		}
		nodeVisitor.nodeEnd(e);
	}

}
