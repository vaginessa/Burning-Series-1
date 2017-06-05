package to.bs.bruningseriesmeterial.asynctasks;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import to.bs.bruningseriesmeterial.fragments.EpisodsFragment;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsUpdateList extends AsyncTask<Void,Void,Void> {
    private EpisodsFragment episodsFragment;
    private int seasons;
    private int seasons1;
    private int ep;

    public EpisodsUpdateList(EpisodsFragment episodsFragment) {
        this.episodsFragment = episodsFragment;
    }

    @Override
    protected Void doInBackground(Void... params) {
        Document doc = null;
        try {
            doc = Jsoup.connect(episodsFragment.getSeason().getUrl()).get();
            Element body = doc.body();
            boolean special = false;
            seasons = 0;
            for (Element seasonsElement : body.getElementsByClass("clearfix").get(0).getElementsByTag("li")) {
                if(seasonsElement.hasClass("special")){
                    special = true;
                }
                seasons++;
            }
            if(special){
                seasons1 = 2;
            }else{
                seasons1 = 1;
            }
            for (int i = 0; i < seasons; i++) {
                final int finalI = i;
                episodsFragment.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        episodsFragment.getDialog().setMessage(episodsFragment.getString(R.string.Episods_wait_season) + " " + finalI);
                    }
                });

                if(episodsFragment.getEpisods().get(i) == null){
                    episodsFragment.getEpisods().put(i,new ArrayList<Episode>());
                }
                if(special){
                    doc = Jsoup.connect(episodsFragment.getSeason().getUrl()+"/"+i).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                }else{
                    doc = Jsoup.connect(episodsFragment.getSeason().getUrl()+"/"+(i+1)).userAgent(RandomUserAgent.getRandomUserAgent()).get();
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


                    Episode episode = new Episode(epName,EngEpName,link, episodsFragment.getSeason());
                    if (MainActivity.getInstance().getDbHelper().isInsertEpisode(episodsFragment.getSeason().getName(),epName)){
                        episode.setWatched(true);
                        if(special){
                            ep++;

                            seasons1 = i+1;

                        }else{
                            ep++;
                            seasons1 = i+1;
                        }
                    }
                    episodsFragment.getEpisods().get(i).add(episode);
                }

                if(ep == 0){
                    ep = 0;
                }
            }
            episodsFragment.setSpecial(special);
        } catch (IOException e) {
            e.printStackTrace();
        }

        cancel(true);
        return null;
    }
    @Override
    protected void onCancelled(Void aVoid) {
        episodsFragment.getArrayAdapter().add(episodsFragment.getActivity().getString(R.string.ep_spinner_search));

        if(episodsFragment.isSpecial()){
            episodsFragment.getArrayAdapter().add(episodsFragment.getActivity().getString(R.string.ep_spinner_special));
            for (int i = 0; i < (seasons-1); i++) {
                episodsFragment.getArrayAdapter().add(episodsFragment.getActivity().getString(R.string.ep_spinner) + " " +  (i+1));
            }
        }else{
            for (int i = 0; i < seasons; i++) {
                episodsFragment.getArrayAdapter().add(episodsFragment.getActivity().getString(R.string.ep_spinner) + " " + (i+1));
            }
        }

        EpisodesAdapter episodesAdapter;
        if(episodsFragment.isSpecial()){
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(1), episodsFragment);
        }else{
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(0), episodsFragment);
        }
        episodsFragment.getSpinner().setAdapter(episodsFragment.getArrayAdapter());
        episodsFragment.getRecyclerView().setAdapter(episodesAdapter);
        if(episodsFragment.isSpecial()) {
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(1), episodsFragment);
            episodsFragment.getSpinner().setSelection(2);
        }else {
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(0), episodsFragment);
            episodsFragment.getSpinner().setSelection(1);
        }
        episodsFragment.getRecyclerView().setAdapter(episodesAdapter);
        episodsFragment.getDialog().dismiss();
        this.episodsFragment.getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        episodsFragment.getSpinner().setSelection(seasons1);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                episodsFragment.getLlm().smoothScrollToPosition(episodsFragment.getRecyclerView(),new RecyclerView.State(),ep);
            }
        }, 250);

    }
}
