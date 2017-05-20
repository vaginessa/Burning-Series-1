package to.bs.bruningseriesmeterial.asynctasks;

import android.os.AsyncTask;
import android.view.WindowManager;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.adapter.EpisodesAdapter;
import to.bs.bruningseriesmeterial.fragments.Episods;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsUpdateList extends AsyncTask<Void,Void,Void> {
    private Episods episods;
    private int seasons;

    public EpisodsUpdateList(Episods episods) {
        this.episods = episods;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Document doc = null;
        try {
            doc = Jsoup.connect(episods.getSeason().getUrl()).get();
            Element body = doc.body();
            boolean special = false;
            seasons = 0;
            for (Element seasonsElement : body.getElementsByClass("clearfix").get(0).getElementsByTag("li")) {
                if(seasonsElement.hasClass("special")){
                    special = true;
                }
                seasons++;
            }
            for (int i = 0; i < seasons; i++) {
                final int finalI = i;
                episods.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        episods.getDialog().setMessage(episods.getString(R.string.Episods_wait_season) + " " + finalI);
                    }
                });

                if(episods.getEpisods().get(i) == null){
                    episods.getEpisods().put(i,new ArrayList<Episode>());
                }
                if(special){
                    doc = Jsoup.connect(episods.getSeason().getUrl()+"/"+i).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                }else{
                    doc = Jsoup.connect(episods.getSeason().getUrl()+"/"+(i+1)).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                }

                body = doc.body();
                for (Element episodes : body.getElementsByTag("tr")) {
                    String epName = "";
                    String link = episodes.getElementsByTag("td").get(1).select("a").attr("href");;
                    if(episodes.getElementsByTag("td").get(1).select("a.strong").size() > 0){
                        epName = episodes.getElementsByTag("td").get(1).getElementsByTag("strong").first().text();
                    }else{
                        epName = episodes.getElementsByTag("td").get(1).select("a").attr("title");
                    }

                    String EngEpName = "";
                    if(episodes.getElementsByTag("td").get(1).select("a.span").size() > 0){
                        EngEpName = episodes.getElementsByTag("td").get(1).getElementsByTag("span").get(0).getElementsByTag("i").first().text();
                    }


                    Episode episode = new Episode(epName,EngEpName,link,episods.getSeason());
                    episode.setWatched(MainActivity.getInstance().getDbHelper().isInsertEpisode(episods.getSeason().getName(),epName));
                    episods.getEpisods().get(i).add(episode);
                }
            }
            episods.setSpecial(special);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cancel(true);
        return null;
    }
    @Override
    protected void onCancelled(Void aVoid) {
        episods.getArrayAdapter().add(episods.getActivity().getString(R.string.ep_spinner_search));

        if(episods.isSpecial()){
            episods.getArrayAdapter().add(episods.getActivity().getString(R.string.ep_spinner_special));
            for (int i = 0; i < (seasons-1); i++) {
                episods.getArrayAdapter().add(episods.getActivity().getString(R.string.ep_spinner) + " " +  (i+1));
            }
        }else{
            for (int i = 0; i < seasons; i++) {
                episods.getArrayAdapter().add(episods.getActivity().getString(R.string.ep_spinner) + " " + (i+1));
            }
        }

        EpisodesAdapter episodesAdapter;
        if(episods.isSpecial()){
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(1),episods.getActivity());
        }else{
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(0),episods.getActivity());
        }
        episods.getSpinner().setAdapter(episods.getArrayAdapter());
        episods.getRecyclerView().setAdapter(episodesAdapter);
        if(episods.isSpecial()) {
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(1),episods.getActivity());
            episods.getSpinner().setSelection(2);
        }else {
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(0),episods.getActivity());
            episods.getSpinner().setSelection(1);
        }
        episods.getRecyclerView().setAdapter(episodesAdapter);
        episods.getDialog().dismiss();
        this.episods.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
}
