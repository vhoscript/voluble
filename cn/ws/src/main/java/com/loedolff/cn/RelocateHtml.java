package com.loedolff.cn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RelocateHtml {
	
	private String url;
	
	/** write source document to local file 
	 * @throws IOException */
	public void write(SourceDocument source, String file) throws IOException {
		
		this.url = source.getUrl();
		
		StringBuffer output;
		output = replace("src", source.asStringBuffer());
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
				String r = attribute + "=\""+ url +m.group(1)+"\"" + m.group(2);
				m.appendReplacement(output, r);
			}
		}
		m.appendTail(output);
		return output;
	}


}
