package to.bs.bruningseriesmeterial.asynctasks;

import android.os.AsyncTask;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.ToWatchAdapter;
import to.bs.bruningseriesmeterial.fragments.ToWatch;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class ToWatchUpdateSeasonsList extends AsyncTask<String,Void,List<Season>> {
    private ToWatch toWatch;
    public ToWatchUpdateSeasonsList(ToWatch toWatch) {
        this.toWatch = toWatch;
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
                    if (MainActivity.getInstance().getDbHelper().isInsertSeasonToWatch(season.getName())) {

                        toWatch.getTowatch().add(season);
                    }

                }
                z++;
            }
        }
        cancel(true);
        Collections.sort(toWatch.getTowatch(), new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        return toWatch.getTowatch();
    }

    @Override
    protected void onCancelled(List<Season> seasons) {
        toWatch.getDialog().dismiss();
        ToWatchAdapter seasonAdapter = new ToWatchAdapter(seasons, toWatch.getActivity());
        toWatch.getRecyclerView().setAdapter(seasonAdapter);
        toWatch.setTowatch(seasons);
        this.toWatch.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        super.onCancelled(seasons);
    }
}
