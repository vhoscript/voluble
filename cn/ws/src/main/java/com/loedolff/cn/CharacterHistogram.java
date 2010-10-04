package com.loedolff.cn;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.xml.sax.SAXException;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.HttpUnitOptions;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

public class CharacterHistogram {
	
	private Map<Character, Integer> occurance = new HashMap<Character, Integer>();
	private List<Entry<Character, Integer>> orderedByOccurance; 
    
    private int totalCharacters = 0;
	
    public CharacterHistogram() throws IOException, SAXException {
	}
	
	public void addWords(URL url) throws IOException, SAXException {
    	HttpUnitOptions.setScriptingEnabled(false);
    	HttpUnitOptions.setDefaultCharacterSet("GB2312");
    	WebConversation wc = new WebConversation();
    	
    	WebRequest req = new GetMethodWebRequest(url.toExternalForm());
        WebResponse resp = wc.getResponse(req);
        
        Charset cs = Charset.forName("GB2312");
        
    	InputStreamReader fr = new InputStreamReader(resp.getInputStream(), cs);
        
    	OutputStreamWriter out = new OutputStreamWriter(
    			new FileOutputStream("target/out"), Charset.forName("UTF-8"));
        
        int i;
        while ((i = fr.read()) != -1) {
        	Character c = (char) i;
        	if (!isChineseCharacter(c)) {
        		continue;
        	}
        	totalCharacters++;
        	out.write(c.charValue());

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

        out.flush();
        out.close();
        
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

			@Override
			public int compare(Entry<Character, Integer> a,
					Entry<Character, Integer> b) {
				return b.getValue() - a.getValue();
			}
    		
    	});
	}
	
	public void writeHistogram(String outputFilename) throws IOException {
		// how many characters occur one's, twice, etc.
    	Map<Integer, Integer> occuranceHistogram = 
    		new HashMap<Integer, Integer>();
		
    	OutputStreamWriter out2 = new OutputStreamWriter(
    			new FileOutputStream(outputFilename), Charset.forName("UTF-8"));
    	PrintWriter pw = new PrintWriter(out2);
    	List<Entry<Character, Integer>> l = getOrderedByOccurance();
    	
    	int sanityCheck = 0;
    	for (Entry<Character, Integer> entry : l) {
			out2.write(entry.getKey());
			out2.write(" - ");
			out2.write(entry.getValue().toString());
			out2.write("\n");
			sanityCheck += entry.getValue();
			
			Integer count = occuranceHistogram.get(entry.getValue());
			if (null == count) {
				occuranceHistogram.put(entry.getValue(), 1);
			} else {
				occuranceHistogram.put(entry.getValue(), count+1);
			}
    	}

    	List<Entry<Integer, Integer>> l2 = 
    		new ArrayList<Entry<Integer, Integer>>(occuranceHistogram.entrySet());
    	Collections.sort(l2, new Comparator<Entry<Integer, Integer>>() {

			@Override
			public int compare(Entry<Integer, Integer> a,
					Entry<Integer, Integer> b) {
				return b.getKey() - a.getKey();
			}
    		
    	});
    	
    	int totalCharacters = 0;
    	pw.println();
    	pw.println("Occurance Histogram");
    	pw.println("Occurs | Characters that occur that many times | Total so far | % of total text");
    	
    	{
    		Entry<Integer, Integer> e = l2.get(2);
    		int sum = (l2.get(1).getValue() + l2.get(0).getValue() + e.getValue());
    		int product = (l2.get(1).getValue()*l2.get(1).getKey() +
    				l2.get(0).getValue()*l2.get(0).getKey() + 
    				e.getValue() * e.getKey());
    		pw.println("(for example, " + e.getValue() + " characters " +
    				"occur " + e.getKey() + " times and " +
    				sum
    				+ " characters occur " + e.getKey() + " or more times, " +
    				"accounting for " + 100*product/this.totalCharacters + "% of the total text parsed");
    		pw.println();
    	}
    	
    	int product = 0;
    	for (Entry<Integer, Integer> e : l2) {
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
    	
    	System.out.println("Sanity check... these values should match:" + sanityCheck + " = " + this.totalCharacters);
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
