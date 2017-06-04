package to.bs.bruningseriesmeterial.asynctasks;

import android.os.AsyncTask;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.SeasonAdapter;
import to.bs.bruningseriesmeterial.fragments.Seasons;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class SeasonsUpdateSeasonsList extends AsyncTask<String,Void,List<Season>> {
    private Seasons seasons;

    public SeasonsUpdateSeasonsList(Seasons seasons) {
        this.seasons = seasons;
    }

    @Override
    protected List<Season> doInBackground(String... params) {
        Document doc = null;
        try {
            doc = Jsoup.connect(params[0]).userAgent(RandomUserAgent.getRandomUserAgent()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Element body = doc.body();
        Element seriesContainer = body.getElementById("seriesContainer");
        List<Season> seasons = new ArrayList<>();
        int z = 0;
        for (Element gerne : seriesContainer.getElementsByTag("div")) {
            Elements seasonsList = gerne.getElementsByTag("ul");

            if(z >= 27){
                break;
            }
            for (Element seasons1 : seasonsList) {
                for (Element s : seasons1.getElementsByTag("a")) {
                    String name = s.text();
                    String link = s.attr("href");

                    Season season = new Season(name, "https://bs.to/" + link);
                    season.setWatched(MainActivity.getInstance().getDbHelper().isInsertSeason(name));
                    season.setToWatch(MainActivity.getInstance().getDbHelper().isInsertSeasonToWatch(name));
                    seasons.add(season);
                }
                z++;
            }
        }
        cancel(true);
        Collections.sort(seasons, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return seasons;
    }

    @Override
    protected void onCancelled(List<Season> seasons) {
        this.seasons.getDialog().dismiss();
        this.seasons.setSeasonAdapter(new SeasonAdapter(seasons));
        this.seasons.getRecyclerView().setAdapter(this.seasons.getSeasonAdapter());
        this.seasons.setSeasons(seasons);
        this.seasons.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }
}
