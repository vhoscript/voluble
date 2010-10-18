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
    	// App.main(new String[] {"file:///C:/temp/WSJ.com.htm", "src/main/resources/iknow.txt"});
    	App.main(new String[] {"http://cn.wsj.com", "src/main/resources/iknow.txt"});
    }
}

