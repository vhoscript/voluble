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
	private URL url;
	
	public SourceDocument(URL url) throws IOException, SAXException {
		HttpUnitOptions.setScriptingEnabled(true);
		HttpUnitOptions.setExceptionsThrownOnScriptError(false);
		HttpUnitOptions.setDefaultCharacterSet("GB2312");
		WebConversation wc = new WebConversation();

		WebRequest req = new GetMethodWebRequest(url.toExternalForm());
		WebResponse resp = wc.getResponse(req);
		this.url = resp.getURL();

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
				System.out.println("Replacing: ["+m.group()+"] with "+"["+r+"]");
				m.appendReplacement(output, r);
			} else {
				String r = attribute + "=\""+url.toExternalForm()+m.group(1)+"\"" + m.group(2);
				System.out.println("Replacing: ["+m.group()+"] with "+"["+r+"]");
				m.appendReplacement(output, r);
			}
		}
		return output;
	}

}
