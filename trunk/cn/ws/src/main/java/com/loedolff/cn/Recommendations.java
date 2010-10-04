package com.loedolff.cn;

import java.io.FileInputStream;
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

	private final static String iKnowFile = "src/main/resources/iknow.txt";
	
	public void recommend(CharacterHistogram ch) throws IOException { 
	    Charset cs = Charset.forName("UTF-8");
	    
	    Set<Character> iKnowSet = new HashSet<Character>();
	    
	    // read characters from the file I aleardy know
	    
		InputStreamReader fr = new InputStreamReader(
					new FileInputStream(iKnowFile), cs);

        int i;
        while ((i = fr.read()) != -1) {
        	Character c = (char) i;
    		iKnowSet.add(c);
        }

		OutputStreamWriter out = new OutputStreamWriter(
				new FileOutputStream("target/recommended.txt"), Charset.forName("UTF-8"));
		
    	PrintWriter pw = new PrintWriter(out);
    	List<Entry<Character, Integer>> l = ch.getOrderedByOccurance();
    	
    	for (Entry<Character, Integer> entry : l) {
    		if (iKnowSet.contains(entry.getKey())) {
    			continue;
    		}
			pw.print(entry.getKey());
			pw.print(" - ");
			pw.print(entry.getValue().toString());
			pw.println();
    	}

    	pw.close();
	}

	
}
