package to.bs.bruningseriesmeterial.Hoster.hosts;



import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import to.bs.bruningseriesmeterial.Utils.Episode;

/**
 * Created by Phillipp on 30.04.2017.
 */

public class openload extends VideoHost {
    private String link;
    private Document document;
    @Override
    public String getStream(String link) throws Exception {
        document = Jsoup.parse(link);
        String text = document.body().getElementById("streamurl").text();
        link = String.format("https://openload.co/stream/%s?mime=true",text);
        return link;
    }

}
