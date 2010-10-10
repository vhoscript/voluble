package com.loedolff.cn;

import java.io.IOException;
import java.net.URL;
import java.util.Date;

import org.xml.sax.SAXException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, SAXException
    {
        System.out.println( "Hello World!" );
    	System.out.println( new Date().toString() );

    	CharacterHistogram wh1 = new CharacterHistogram();
    	wh1.addWords(new SourceDocument(new URL("http://cn.wsj.com")));
    	
//    	System.out.println();
//    	System.out.println("Combined");
//    	System.out.println("--------");
//    	wh1.addWords(new URL("file:src/test/resources/wsj2.html"));
    	
    	wh1.writeHistogram("target/histogram.txt");
    	wh1.writeHtmlHistogram("target/histogram.html");
    	
    	Recommendations r = new Recommendations();
    	r.recommend(wh1);
    }
}
