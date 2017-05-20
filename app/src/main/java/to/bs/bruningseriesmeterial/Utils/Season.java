package to.bs.bruningseriesmeterial.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import to.bs.bruningseriesmeterial.MainActivity;

/**
 * Created by Phillipp on 10.04.2017.
 */

public class Season {
    private String name;
    private String gerne;
    private String url;
    private ArrayList<Episode> episodes;
    private boolean watched = false;
    private boolean toWatch = false;
    private Bitmap image;
    private String Description;
    private int eps;
    private int epsw;

    public Season(String name, String gerne, String url, ArrayList<Episode> episodes) {
        this.name = name;
        this.gerne = gerne;
        this.url = url;
        this.episodes = episodes;
    }

    public Season(String name, String url) {
        this.name = name;
        this.url = url;

    }

    public Season(String name, String gerne, String url, boolean watched, boolean toWatch) {
        this.name = name;
        this.gerne = gerne;
        this.url = url;
        this.watched = watched;
        this.toWatch = toWatch;

    }
    public Season() {

    }

    public boolean isWatched() {
        return watched;
    }

    public boolean isToWatch() {
        return toWatch;
    }

    public void setToWatch(boolean toWatch) {
        this.toWatch = toWatch;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public String getName() {
        return name;
    }

    public String getGerne() {

        return gerne;
    }


    public String getUrl() {
        return url;
    }

    public Bitmap getImage() {

        return image;
    }

    public String getDescription() {

        return Description;
    }
    public void runInfo() {
        new DownloadIonformations().execute();
        new DownloadEpisods().execute();
    }
    public void run(){
        setWatched(MainActivity.getInstance().getDbHelper().isInsertSeason(getName()));
        new DownloadIonformations().execute();

    }


    public ArrayList<Episode> getEpisodes() {
        return episodes;
    }

    public int getEps() {
        return MainActivity.getInstance().getDbHelper().getEpisodes(getName());
    }

    public int getEpsw() {
        return MainActivity.getInstance().getDbHelper().getWatchedEpisodes(getName());
    }


    public class DownloadIonformations extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(getUrl()).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                Element body = doc.body();
                String imageLink = body.getElementById("sp_right").getElementsByTag("img").first().attr("src");
                URL url = new URL("https://bs.to"+imageLink);
                image = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                String desc = body.getElementById("sp_left").getElementsByTag("p").first().text();
                Description = desc;
                gerne = body.getElementById("sp_left").getElementsByTag("p").get(1).text();
                cancel(true);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
    private class DownloadEpisods extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(getUrl()).get();
                Element body = doc.body();

                int seasons = 0;
                boolean special = false;
                for (Element seasonsElement : body.getElementsByClass("clearfix").get(0).getElementsByTag("li")) {
                    if (seasonsElement.hasClass("special")) {
                        special = true;
                    }
                    seasons++;
                }
                for (int i = 0; i < seasons; i++) {
                    final int finalI = i;

                    if (!special) {
                        doc = Jsoup.connect(getUrl() + "/" + (i + 1)).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                    }else{
                        doc = Jsoup.connect(getUrl() + "/" + (i + 1)).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                    }
                    if (!special) {
                        body = doc.body();
                        for (Element episodes : body.getElementsByTag("tr")) {
                            eps++;
                        }
                    }else{
                        body = doc.body();
                        for (Element episodes : body.getElementsByTag("tr")) {
                            eps++;
                        }
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            cancel(true);
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            MainActivity.getInstance().getDbHelper().updateCount(getName(),eps);
        }
    }
}
