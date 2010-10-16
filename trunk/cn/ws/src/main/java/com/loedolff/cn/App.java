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
    	if (args.length != 2) {
    		System.out.println("Pass URL to source doc as first parameter");
    		System.out.println("Pass utf-8 iKnow filename as second parameter");
    		System.exit(1);
    	}
    	
        System.out.println( "Hello World!" );
    	System.out.println( new Date().toString() );
    	
    	SourceDocument sourceDoc = new SourceDocument(new URL(args[0]));

    	CharacterHistogram wh1 = new CharacterHistogram();
    	wh1.addWords(sourceDoc);
    	
    	wh1.writeHistogram("target/histogram.txt");
    	wh1.writeHtmlHistogram("target/histogram.html");
    	
    	Known known = new Known(args[1]);
    	
    	Recommendations r = new Recommendations(wh1, known);
    	r.writeHtmlRecommendations("target/recommendations.html");
    	
    	AugmentSource augmentSource = new AugmentSource();
    	augmentSource.highlightKnown(sourceDoc, known);
    	
    	RelocateHtml relocateHtml = new RelocateHtml();
    	relocateHtml.write(sourceDoc, "target/wjs-highligted.html");
    }
}
