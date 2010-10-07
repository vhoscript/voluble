package com.loedolff.cn;

import java.io.IOException;
import java.net.URL;

import org.testng.annotations.Test;
import org.xml.sax.SAXException;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    @Test
    public void test() throws IOException, SAXException {
    	
    	System.out.println();
    	System.out.println("In wsj1.html");
    	System.out.println("------------");
    	CharacterHistogram wh1 = new CharacterHistogram();
    	wh1.addWords(new URL("http://cn.wsj.com"));
    	
//    	System.out.println();
//    	System.out.println("Combined");
//    	System.out.println("--------");
//    	wh1.addWords(new URL("file:src/test/resources/wsj2.html"));
    	
    	wh1.writeHistogram("target/histogram.txt");
    	wh1.writeHtmlHistogram("target/histogram.txt");
    	
    	Recommendations r = new Recommendations();
    	r.recommend(wh1);
    	
    	//++++ based on what I know... how much of the text should I be
    	// able to read?
    }
}

