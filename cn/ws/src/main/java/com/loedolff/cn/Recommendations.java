package com.loedolff.cn;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class Recommendations {

	private CharacterHistogram characterHistogram;
	
	private Known known;
	
	public Recommendations(CharacterHistogram characterHistogram, Known known) {
		this.characterHistogram = characterHistogram;
		this.known = known;
	}
	
	public void recommend() throws IOException { 
		    
		OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream("target/recommended.txt"), Charset.forName("UTF-8"));
		
    	PrintWriter pw = new PrintWriter(out);
    	
    	for (Entry<Character, Integer> entry : characterHistogram.getOrderedByOccurance()) {
    		if (known.contains(entry.getKey())) {
    			continue;
    		}
			pw.print(entry.getKey());
			pw.print(" - ");
			pw.print(entry.getValue().toString());
			pw.println();
    	}

    	pw.close();
	}

	public void writeHtmlRecommendations(String filename) throws FileNotFoundException {
		OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream(filename), Charset.forName("UTF-8"));
		
    	PrintWriter pw = new PrintWriter(out);
    	pw.println("<html>");
    	pw.println("<head>");
    	pw.println("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
    	pw.println("</head>");
    	pw.println("<body>");
    	pw.println("<table>");
    	
    	for (Entry<Character, Integer> entry : characterHistogram.getOrderedByOccurance()) {
    		if (known.contains(entry.getKey())) {
    			continue;
    		}
        	pw.println("<tr><td>");
			pw.print(entry.getKey());
			pw.print(" - ");
			pw.print(entry.getValue().toString());
			pw.println();
			pw.println("</td></tr>");
    	}
    	pw.println("</table>");
    	pw.println("</html>");

    	pw.close();
	}


}
