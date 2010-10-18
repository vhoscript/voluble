package com.loedolff.cn;

import com.gargoylesoftware.htmlunit.html.DomNode;

public class AugmentSource {
	
	private Known known;
	
	
	/**
	 * Place yellow background behind known characters
	 * 
	 * @param source
	 * @param known
	 */
	public void highlightKnown(SourceDocument source, Known known) {
		
		this.known = known;
		
		SourceDocument.NodeVisitor myNodeVisitor = new SourceDocument.NodeVisitor() {
	
			boolean inScript = false;
			boolean inBody = false;
			boolean inAnchor = false;
			
			@Override
			public void nodeStart(DomNode e) {
				if (e.getNodeName().equals("script")) {
					inScript = true;
					return;
				}
				if (!inBody && e.getNodeName().equals("body")) {
					inBody = true;
				}
				if (e.getNodeName().equals("a")) {
					inAnchor = true;
				}
				if (!e.getNodeName().equals("#text") || inScript || !inBody || inAnchor) {
					return;
				}
				String input = e.getNodeValue();
				StringBuffer output = new StringBuffer();
				for(int i = 0; i<input.length(); i++) {
					char c = input.charAt(i);
					if (AugmentSource.this.known.contains(c)) {
						output.append("<span style='background:#00F0F0;'>").append(c).append("</span>");
					} else {
						output.append(c);
					}
				}
				e.setNodeValue(output.toString());
			}
			
			public void nodeEnd(DomNode e) {
				if (e.getNodeName().equals("script")) {
					inScript = false;
				}
				if (e.getNodeName().equals("a")) {
					inAnchor = false;
				}
			}
		};
		
		source.visitAllNodes(myNodeVisitor);
	}

}
