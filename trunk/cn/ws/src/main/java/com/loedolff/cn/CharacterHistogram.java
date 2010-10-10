package com.loedolff.cn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.SAXException;

public class CharacterHistogram {
	
	// how many times does each character repeat
	private Map<Character, Integer> occurance = new HashMap<Character, Integer>();
	// list of characters, ordered by the number of times they repeat
	private List<Entry<Character, Integer>> orderedByOccurance; 
	// how many characters repeat 1 times, 2 times, etc.
	private Map<Integer, Integer> occuranceHistogram;
	// histogram ordered by number of times a character occurs
	private List<Entry<Integer, Integer>> orederedOccuranceHistogram;
    
    private int totalCharacters = 0;
	
    public CharacterHistogram() throws IOException, SAXException {
	}
	
	public void addWords(SourceDocument sourceDocument) throws IOException, SAXException {

		StringReader fr = new StringReader(sourceDocument.getSource().toString());
		
        int i;
        while ((i = fr.read()) != -1) {
        	Character c = (char) i;
        	if (!isChineseCharacter(c)) {
        		continue;
        	}
        	totalCharacters++;

        	if (i % 1000 == 0) {
        		System.out.print(".");
        	}
        	
        	Integer count = occurance.get(c);
        	if (count == null) {
        		occurance.put(c, 1);
        	} else {
        		occurance.put(c, count+1);
        	}
        }

        System.out.println();
        System.out.println("Total Chinese characters: " + totalCharacters);
        System.out.println("Total unique Chinese characters: " + occurance.size());
        

    	OutputStreamWriter out2 = new OutputStreamWriter(
    			new FileOutputStream("target/out2"), Charset.forName("UTF-8"));
    	List<Character> l = new ArrayList<Character>(occurance.keySet());
    	Collections.sort(l);
    	for (Character c : l) {
			out2.write(c);
    	}
    	out2.close();
    	
		orderedByOccurance = 
    		new ArrayList<Entry<Character, Integer>>(occurance.entrySet());
    	Collections.sort(orderedByOccurance, new Comparator<Entry<Character, Integer>>() {
			public int compare(Entry<Character, Integer> o1,
					Entry<Character, Integer> o2) {
				return o2.getValue() - o1.getValue();
			}
    	});

    	occuranceHistogram = new HashMap<Integer, Integer>();
    	
    	int sanityCheck = 0;
    	for (Entry<Character, Integer> entry : occurance.entrySet()) {
			sanityCheck += entry.getValue();
			
			Integer count = occuranceHistogram.get(entry.getValue());
			if (null == count) {
				occuranceHistogram.put(entry.getValue(), 1);
			} else {
				occuranceHistogram.put(entry.getValue(), count+1);
			}
    	}
    
    	orederedOccuranceHistogram = 
    		new ArrayList<Entry<Integer, Integer>>(occuranceHistogram.entrySet());
    	Collections.sort(orederedOccuranceHistogram, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> a,
					Entry<Integer, Integer> b) {
				return b.getKey() - a.getKey();
			}
    	});
        
    	System.out.println("Sanity check... these values should match:" + sanityCheck + " = " + this.totalCharacters);
	}
	
	public void writeHistogram(String outputFilename) throws IOException {
		
    	OutputStreamWriter out2 = new OutputStreamWriter(
    			new FileOutputStream(outputFilename), Charset.forName("UTF-8"));
    	PrintWriter pw = new PrintWriter(out2);
    	
    	for (Entry<Character, Integer> entry : getOrderedByOccurance()) {
			out2.write(entry.getKey());
			out2.write(" - ");
			out2.write(entry.getValue().toString());
			out2.write("\n");
    	}

    	int totalCharacters = 0;
    	pw.println();
    	pw.println("Occurance Histogram");
    	pw.println("Occurs | Characters that occur that many times | Total so far | % of total text");
    	
		pw.println(getExplanationString());
		pw.println();
    	
    	int product = 0;
    	for (Entry<Integer, Integer> e : getOrderedOccuranceHistogram()) {
    		totalCharacters += e.getValue();
    		product += e.getValue() * e.getKey();
    		pw.printf("%3d", e.getKey());
    		pw.print(" | ");
    		pw.printf("%3d", e.getValue());
    		pw.print(" | ");
    		pw.printf("%4d", totalCharacters);
    		pw.print(" | ");
    		pw.printf("%3d", 100*product/this.totalCharacters);
    		pw.println();
    	}
    	
    	out2.close();
	}

	public void writeHtmlHistogram(String outputFilename) throws IOException {
		
    	OutputStreamWriter out2 = new OutputStreamWriter(
    			new FileOutputStream(outputFilename), Charset.forName("UTF-8"));
    	PrintWriter pw = new PrintWriter(out2);
    	
    	pw.println("<html>");
    	pw.println("<head>");
    	pw.println("<meta http-equiv='Content-Type' content='text/html; charset=GB2312'>");
    	pw.println("</head>");
    	pw.println("<body>");
    	
    	for (Entry<Character, Integer> entry : getOrderedByOccurance()) {
			out2.write("&#" + (int) entry.getKey());
			out2.write(";");
			out2.write(" - ");
			out2.write(entry.getValue().toString());
			out2.write("<br/>\n");
    	}

    	int totalCharacters = 0;
    	pw.println("<br/>");
    	pw.println("<b>Occurance Histogram<br/></br>");
    	pw.println(getExplanationString());
		pw.println("<br/><br/>");
    	pw.println("<table>");
    	pw.print("<tr><th>Occurs</th>");
    	pw.print("<th>Characters that occur that many times</th>" +
    			"<th>Total so far</th><th>% of total text</th></tr><br/>");
    	int product = 0;
    	for (Entry<Integer, Integer> e : getOrderedOccuranceHistogram()) {
    		totalCharacters += e.getValue();
    		product += e.getValue() * e.getKey();
    		pw.print("<tr>");
    		pw.print("<td>");
    		pw.printf("%3d", e.getKey());
    		pw.print("</td>");
    		pw.print("<td>");
    		pw.printf("%3d", e.getValue());
    		pw.print("</td>");
    		pw.print("<td>");
    		pw.printf("%4d", totalCharacters);
    		pw.print("</td>");
    		pw.print("<td>");
    		pw.printf("%3d", 100*product/this.totalCharacters);
    		pw.print("</td>");
    		pw.println("</tr>");
    	}
    	pw.println("</table>");
    	pw.println("</body>");
    	out2.close();
	}

	private String getExplanationString() {
		
		int sum = (getOrderedOccuranceHistogram().get(1).getValue() + 
				getOrderedOccuranceHistogram().get(0).getValue() + 
				getOrderedOccuranceHistogram().get(2).getValue());
		int product = (getOrderedOccuranceHistogram().get(1).getValue() *
				getOrderedOccuranceHistogram().get(1).getKey() +
				getOrderedOccuranceHistogram().get(0).getValue() * 
				getOrderedOccuranceHistogram().get(0).getKey() + 
				getOrderedOccuranceHistogram().get(2).getValue() * 
				getOrderedOccuranceHistogram().get(2).getKey());

		return "(for example, " + getOrderedOccuranceHistogram().get(2).getValue() + " characters " +
				"occur " + getOrderedOccuranceHistogram().get(2).getKey() + " times and " +
				sum
				+ " characters occur " + getOrderedOccuranceHistogram().get(2).getKey() + " or more times, " +
				"accounting for " + 100*product/this.totalCharacters + "% of the total text parsed";
	}

	private List<Entry<Integer, Integer>> getOrderedOccuranceHistogram() {
		return orederedOccuranceHistogram;
	}

	public List<Entry<Character, Integer>> getOrderedByOccurance() {
		return orderedByOccurance;
	}

	private boolean isChineseCharacter(Character c) {
		return c>12600 & c < 65000;
	}
	
	public void writeReport() {
		
	}
	
}
