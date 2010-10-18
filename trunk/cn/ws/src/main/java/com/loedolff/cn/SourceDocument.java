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
		System.out.println("Url:" + this.url);
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


	public StringBuffer asStringBuffer() {
		final StringBuffer source = new StringBuffer();

		SourceDocument.NodeVisitor myNodeVisitor = new SourceDocument.NodeVisitor() {
			
			private boolean inScript = false;
			
			@Override
			public void nodeStart(DomNode e) {
				if (e.getNodeName().equals("#text")) {
					if (!inScript) {
						source.append(escapeCharCodes(e.getNodeValue()));
					}
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
					inScript = true;
				}
			}
			
			public void nodeEnd(DomNode e) {
				if (e.getNodeName().equals("#text") || e.getNodeName().equals("#comment")) {
					return;
				}
				source.append("</"+e.getNodeName()+">");
				if (e.getNodeName().equals("script")) {
					inScript = false;
				}
			}
		};
		
		source.append("<html>");
		visitAllNodes(myNodeVisitor);
		source.append("</html>");
		return source;
	}

	public String getUrl() {
		return url.toExternalForm();
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
