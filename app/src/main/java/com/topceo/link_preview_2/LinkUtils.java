package com.topceo.link_preview_2;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by MrPhuong on 1/31/2018.
 */

public class LinkUtils {

    //Pull all links from the body for easy retrieval
    public static List<String> extractUrls(String text) {
        List<String> containedUrls = new ArrayList<String>();
        String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        Matcher urlMatcher = pattern.matcher(text);

        while (urlMatcher.find()) {
            containedUrls.add(text.substring(urlMatcher.start(0),
                    urlMatcher.end(0)));
        }

        return containedUrls;
    }

    public static String getHostName(String urlInput) {
        urlInput = urlInput.toLowerCase();
        String hostName = urlInput;
        if (!urlInput.equals("")) {
            if (urlInput.startsWith("http") || urlInput.startsWith("https")) {
                try {
                    URL netUrl = new URL(urlInput);
                    String host = netUrl.getHost();
                    if (host.startsWith("www")) {
                        hostName = host.substring("www".length() + 1);
                    } else {
                        hostName = host;
                    }
                } catch (MalformedURLException e) {
                    hostName = urlInput;
                }
            } else if (urlInput.startsWith("www")) {
                hostName = urlInput.substring("www".length() + 1);
            }
            return hostName;
        } else {
            return "";
        }
    }

    public static void extractURL() {
        URL aURL = null;
        try {
            aURL = new URL("http://example.com:80/docs/books/tutorial"
                    + "/index.html?name=networking#DOWNLOADING");

            System.out.println("protocol = " + aURL.getProtocol()); //http
            System.out.println("authority = " + aURL.getAuthority()); //example.com:80
            System.out.println("host = " + aURL.getHost()); //example.com
            System.out.println("port = " + aURL.getPort()); //80
            System.out.println("path = " + aURL.getPath()); //  /docs/books/tutorial/index.html
            System.out.println("query = " + aURL.getQuery()); //name=networking
            System.out.println("filename = " + aURL.getFile()); ///docs/books/tutorial/index.html?name=networking
            System.out.println("ref = " + aURL.getRef()); //DOWNLOADING
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


    }
}
