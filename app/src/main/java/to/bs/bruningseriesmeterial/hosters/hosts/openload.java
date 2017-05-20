package to.bs.bruningseriesmeterial.hosters.hosts;



import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

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
