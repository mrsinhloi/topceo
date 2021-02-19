package com.topceo.checkUpdate;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by MrPhuong on 2017-01-19.
 */

public class MarketVersionChecker {
    public static String getMarketVersion(String packageName) {
        /*try {
            Document doc = Jsoup.connect(
                    "https://play.google.com/store/apps/details?id="
                            + packageName).get();
            Elements Version = doc.select(".content");

            for (Element mElement : Version) {
                if (mElement.attr("itemprop").equals("softwareVersion")) {
                    return mElement.text().trim();
                }
            }

        } catch (Exception ex) {

        }*/

        String newVersion = null;
        try {

            Document doc  = Jsoup.connect("https://play.google.com/store/apps/details?id=" + packageName)/*.timeout(15000)*/
//                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
//                    .referrer("http://www.google.com")
                    .get();
            Elements items = doc.select("span.htlgb");
            Element item = null;//items.get(3);//vi tri the span thứ 3, <span class="htlgb">1.0</span>
            //Tim item o vi tri co dang version 2.0.64 => split(.) array[3 phan tu]
            if(items!=null && items.size()>0){
                for (int i = 0; i < items.size(); i++) {
                    Element e = items.get(i);
                    String text = e.ownText();
                    String[] arr = text.split("\\.");
                    if(arr!=null && arr.length==3){//2.0.64 => [2,0,64]
                        item = e;
                        break;
                    }
                }
            }

            if(item!=null){
                newVersion = item.ownText();
            }

            return newVersion;

        } catch (Exception e) {
            return newVersion;
        }

    }

    public static String getMarketVersionFast(String packageName) {
        String mData = "", mVer = null;

        try {
            URL mUrl = new URL("https://play.google.com/store/apps/details?id="
                    + packageName);
            HttpURLConnection mConnection = (HttpURLConnection) mUrl
                    .openConnection();

            if (mConnection == null)
                return null;

            mConnection.setConnectTimeout(5000);
            mConnection.setUseCaches(false);
            mConnection.setDoOutput(true);

            if (mConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader mReader = new BufferedReader(
                        new InputStreamReader(mConnection.getInputStream()));

                while (true) {
                    String line = mReader.readLine();
                    if (line == null)
                        break;
                    mData += line;
                }

                mReader.close();
            }

            mConnection.disconnect();

        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        String startToken = "softwareVersion\">";
        String endToken = "<";
        int index = mData.indexOf(startToken);

        if (index == -1) {
            mVer = null;

        } else {
            mVer = mData.substring(index + startToken.length(), index
                    + startToken.length() + 100);
            mVer = mVer.substring(0, mVer.indexOf(endToken)).trim();
        }

        return mVer;
    }
}
