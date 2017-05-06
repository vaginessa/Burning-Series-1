package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TabHost;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.EpisodesAdapter;

public class Episods extends Fragment {
    private boolean special;
    private int seasons;

    private Season season;
    private ProgressDialog dialog;
    private Map<Integer,List<Episode>> eps = new HashMap<>();
    private RecyclerView recyclerView;
    private EpisodesAdapter[] episodesAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private TabHost tabhost;
    private Spinner spinner;


    public Episods() {
    }

    public static Episods newInstance(Season s) {
        Episods fragment = new Episods();
        fragment.season = s;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        episodesAdapter = new EpisodesAdapter[1];
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        new DownloadEpisods().execute();
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        dialog.setMessage(getString(R.string.Episods_wait));
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);
        dialog.show();

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_episods, container, false);
        MainActivity.getInstance().setTitle(season.getName());
        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) v.findViewById(R.id.frgament_ep_rv);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);


        recyclerView.setAdapter(episodesAdapter[0]);
        spinner = (Spinner) v.findViewById(R.id.frgament_ep_spinner);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    episodesAdapter[0] = new EpisodesAdapter(eps.get(position),getActivity());
                }else if(position == 1){
                    episodesAdapter[0] = new EpisodesAdapter(eps.get(position),getActivity());
                }else{
                    episodesAdapter[0] = new EpisodesAdapter(eps.get(position),getActivity());
                }
                recyclerView.setAdapter(episodesAdapter[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                if(special) {
                    episodesAdapter[0] = new EpisodesAdapter(eps.get(1),getActivity());
                }else {
                    episodesAdapter[0] = new EpisodesAdapter(eps.get(0),getActivity());
                }
                recyclerView.setAdapter(episodesAdapter[0]);
            }
        });
        return v;

    }
    private class DownloadEpisods extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... params) {
            Document doc = null;
            try {
                doc = Jsoup.connect(season.getUrl()).get();
                Element body = doc.body();
                for (Element seasonsElement : body.getElementsByClass("clearfix").get(0).getElementsByTag("li")) {
                    if(seasonsElement.hasClass("special")){
                        special = true;
                    }
                    seasons++;
                }
                for (int i = 0; i < seasons; i++) {
                    final int finalI = i;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            dialog.setMessage(getString(R.string.Episods_wait_season) + " " + finalI);
                        }
                    });

                    if(eps.get(i) == null){
                        eps.put(i,new ArrayList<Episode>());
                    }
                    if(special){
                        doc = Jsoup.connect(season.getUrl()+"/"+i).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                    }else{
                        doc = Jsoup.connect(season.getUrl()+"/"+(i+1)).userAgent(RandomUserAgent.getRandomUserAgent()).get();
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


                        Episode episode = new Episode(epName,EngEpName,link,season);
                        episode.setWatched(MainActivity.getInstance().getDbHelper().isInsertEpisode(season.getName(),epName));
                        eps.get(i).add(episode);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            cancel(true);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

        @Override
        protected void onCancelled(Void aVoid) {


            if(special){
                arrayAdapter.add(getActivity().getString(R.string.ep_spinner_special));
                for (int i = 0; i < (seasons-1); i++) {
                    arrayAdapter.add(getActivity().getString(R.string.ep_spinner) + " " +  (i+1));
                }
            }else{
                for (int i = 0; i < seasons; i++) {
                    arrayAdapter.add(getActivity().getString(R.string.ep_spinner) + " " + (i+1));
                }
            }

            if(special){
                episodesAdapter[0] = new EpisodesAdapter(eps.get(1),getActivity());
            }else{
                episodesAdapter[0] = new EpisodesAdapter(eps.get(0),getActivity());
            }
            spinner.setAdapter(arrayAdapter);
            recyclerView.setAdapter(episodesAdapter[0]);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(position == 0){
                        episodesAdapter[0] = new EpisodesAdapter(eps.get(position),getActivity());
                    }else if(position == 1){
                        episodesAdapter[0] = new EpisodesAdapter(eps.get(position),getActivity());
                    }else{
                        episodesAdapter[0] = new EpisodesAdapter(eps.get(position),getActivity());
                    }
                    recyclerView.setAdapter(episodesAdapter[0]);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                    if(special) {
                        episodesAdapter[0] = new EpisodesAdapter(eps.get(1),getActivity());
                    }else {
                        episodesAdapter[0] = new EpisodesAdapter(eps.get(0),getActivity());
                    }
                    recyclerView.setAdapter(episodesAdapter[0]);
                }
            });
            if(special) {
                episodesAdapter[0] = new EpisodesAdapter(eps.get(1),getActivity());
                spinner.setSelection(1);
            }else {
                episodesAdapter[0] = new EpisodesAdapter(eps.get(0),getActivity());
                spinner.setSelection(0);
            }

            recyclerView.setAdapter(episodesAdapter[0]);
            dialog.dismiss();
        }


    }


}
