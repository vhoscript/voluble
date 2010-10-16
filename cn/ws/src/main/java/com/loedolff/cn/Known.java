package com.loedolff.cn;


import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class Known {

    private Set<Character> iKnowSet = new HashSet<Character>();

	public Known(String iKnowFile) throws IOException {

		InputStreamReader fr = new InputStreamReader(
				new FileInputStream(iKnowFile), Charset.forName("UTF-8"));

	    int i;
	    while ((i = fr.read()) != -1) {
	    	Character c = (char) i;
	    	if (c > 32) {
	    		iKnowSet.add(c);
	    	}
	    }
	}

	public boolean contains(Character key) {
		return iKnowSet.contains(key);
	}


}
