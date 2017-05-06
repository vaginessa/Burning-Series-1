package to.bs.bruningseriesmeterial.Hoster.hosts;


import android.util.Base64;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;

/**
 * Created by Phillipp on 12.04.2017.
 */

public class Vivo extends VideoHost {

    public String getStream(String link) throws IOException {
        String videoresult = "";
        Document document = Jsoup.connect(link).userAgent(RandomUserAgent.getRandomUserAgent()).get();
        Element body = document.body();
        String text = body.getElementsByClass("vivo-website-wrapper").first().getElementsByTag("script").get(1).data();
        Pattern pattern = Pattern.compile("Core\\.InitializeStream\\s*?\\(\\'([A-Za-z\\d]+)");
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            matcher.reset();
            if (matcher.find()) {
                String result = matcher.group(1);
                String DECODED = new String(Base64.decode(result, Base64.DEFAULT), Charset.forName("UTF-8"));
                DECODED = DECODED.replace("\\","");
                Pattern video = Pattern.compile("(https?://[^<>\"]+/get/[^<>\"]+)");
                Matcher videomatcher = video.matcher(DECODED);
                videomatcher.reset();
                if (videomatcher.find()){
                    videoresult = videomatcher.group(1);
                }
            }
        }
        return videoresult;
    }
}
