/*
 * (c) Copyright 2011 freiheit.com technologies GmbH
 *
 * Created on 14.05.2011 by Christian Hennig (christian.hennig@freiheit.com)
 *
 * This file contains unpublished, proprietary trade secret information of
 * freiheit.com technologies GmbH. Use, transcription, duplication and
 * modification are strictly prohibited without prior written consent of
 * freiheit.com technologies GmbH.
 */
package testHTMLUnit;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.NodeList;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.javascript.host.css.ComputedCSSStyleDeclaration;
import com.gargoylesoftware.htmlunit.javascript.host.html.HTMLElement;

/**
 * 
 * @author Christian Hennig (christian.hennig@freiheit.com)
 * 
 */
public class Test {

    public static void main( String[] args ) throws Exception {
        // TODO Auto-generated method stub
        homePage();
    }

    /*
     * Versucht einem tchibo.de zu erreichen.
     */
    public static void homePage() throws Exception {
        getSizeOfPage( "http://www.tchibo.de" );
        getSizeOfPage( "http://www.tchibo.de/m" );
    }

    /**
     * @param page
     * @throws IOException
     */
    private static void getSizeOfPage( final String url ) throws IOException {
        final WebClient webClient = new WebClient( BrowserVersion.FIREFOX_24 );
        HtmlPage page = webClient.getPage( url );
        List<DomElement> elems = page.getElementsByTagName( "img" );
        long size = 0;
        for ( DomElement elem : elems ) {
            size += getSizeOfRessource( elem.getAttribute( "src" ) );
        }
        List<DomElement> links = page.getElementsByTagName( "link" );
        for ( DomElement link : links ) {
            size += getSizeOfRessource( link.getAttribute( "href" ) );
        }
        List<DomElement> scripts = page.getElementsByTagName( "script" );
        for ( DomElement script : scripts ) {
            size += getSizeOfRessource( script.getAttribute( "src" ) );
        }
        size  += getSizeOfBackGroundImages(page);
        size += compress( page.getWebResponse().getContentAsString() );
        System.out.println( "HTML " + compress( page.getWebResponse().getContentAsString() ) );
        System.out.println( "All: " + size );
        try {
            webClient.closeAllWindows();
        } catch ( Exception e ) {
        }
    }

    private static long getSizeOfBackGroundImages( HtmlPage page ) {
        long result = 0;
        result += forNodeList( page , "div");
        result += forNodeList( page , "a");
        result += forNodeList( page , "p");
        return result;
    }

    /**
     * @param page
     * @return
     */
    private static long forNodeList( HtmlPage page,String tag ) {
        NodeList list = page.getElementsByTagName(tag);
      long size =0;
    for ( int i =0 ; i < list.getLength(); i++ ) {
        list.item( i );
            HtmlElement element = (HtmlElement)list.item(i);
            ComputedCSSStyleDeclaration style = ((HTMLElement)(element).getScriptObject()).getCurrentStyle();
            
            String backgroundImage = style.getBackgroundImage();
            if(backgroundImage!="none") {
                System.out.println("*"+backgroundImage);
            }
      }
      return size;
    }

    public static int compress( String str ) throws IOException {
        if ( str == null || str.length() == 0 ) {
            return 0;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream( out );
        gzip.write( str.getBytes() );
        gzip.close();
        String outStr = out.toString( "UTF-8" );
        return outStr.length();
    }

    static long getSizeOfRessource( String url ) {
        System.out.print( "." );
        DefaultHttpClient client = new DefaultHttpClient();
        try {
            HttpUriRequest request = new HttpGet( url );
            CloseableHttpResponse result = client.execute( request );
            return result.getEntity().getContentLength();
        } catch ( ClientProtocolException e ) {
            // TODO Auto-generated catch block
        } catch ( Exception e ) {
            // TODO Auto-generated catch block
        }
        return 0;
    }

}
