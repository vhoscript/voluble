package com.loedolff.cn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.nio.charset.Charset;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SourceDocument {
	
	private StringBuffer source = new StringBuffer();
	
	public SourceDocument(URL url) throws IOException, SAXException {
		HttpUnitOptions.setScriptingEnabled(false);
		HttpUnitOptions.setDefaultCharacterSet("GB2312");
		WebConversation wc = new WebConversation();

		WebRequest req = new GetMethodWebRequest(url.toExternalForm());
		WebResponse resp = wc.getResponse(req);

		Charset cs = Charset.forName("GB2312");
		InputStreamReader fr = new InputStreamReader(resp.getInputStream(), cs);

		int i;
		while ((i = fr.read()) != -1) {
			source.append((char) i);
		}
		
		fr.close();
	}
	
	private boolean isChineseCharacter(Character c) {
		return c>12600 & c < 65000;
	}
	
	/** write source document to local file 
	 * @throws IOException */
	public void write(String file) throws IOException {
		
		StringBuffer output = new StringBuffer();
		
		Pattern p = Pattern.compile("src=\"(.*)\"");
		Matcher m = p.matcher(source);
		while (m.find()) {
			if (m.group(1).toLowerCase().startsWith("http")) {
				m.appendReplacement(output, "src=\""+m.group(1)+"\"");
				++can replace 2nd parte above with m.group()???
			} else {
				+++download the resource and store it locally, update reference?????+++
				m.appendReplacement(output, "src=\"http://chinese.wsj.com/gb/"+m.group(1)+"\"");
			}
		}
		p = Pattern.compile("href=\"(.*)\"");
		m = p.matcher(output);
		
		StringBuffer output2 = new StringBuffer();

		while (m.find()) {
			if (m.group(1).toLowerCase().startsWith("http")) {
				m.appendReplacement(output2, "src=\""+m.group(1)+"\"");
			} else {
				+++download the resource and store it locally, update reference?????+++
				m.appendReplacement(output2, "href=\"http://chinese.wsj.com/gb/"+m.group(1)+"\"");
			}
		}
		
		OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(file),
				Charset.forName("GB2312"));
			   
		out.append(output2);
		
		out.close();
	}

}
