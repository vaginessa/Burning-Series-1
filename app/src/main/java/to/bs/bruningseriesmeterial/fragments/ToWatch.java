package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.SeasonAdapter;
import to.bs.bruningseriesmeterial.adapter.ToWatchAdapter;
import to.bs.bruningseriesmeterial.history.SeasonSearchHistory;

public class ToWatch extends Fragment {
    private static final String URL = "SerienURL";
    private String url;


    private static List<Season> towatch;
    private ProgressDialog dialog;
    private ToWatchAdapter[] seasonAdapter;
    private IndexFastScrollRecyclerView recyclerView;
    private ArrayList<Season> seasons1;
    private SearchManager searchManager;
    private SearchView searchView;

    public ToWatch() {
    }

    public static ToWatch newInstance(String param1) {
        towatch = new ArrayList<>();
        ToWatch fragment = new ToWatch();
        Bundle args = new Bundle();
        args.putString(URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
        }
        seasonAdapter = new ToWatchAdapter[1];
        towatch = new ArrayList<>();
        new ToWatch.UpdateSeasons().execute();

        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        dialog.setMessage(getString(R.string.Seasons_wait));
        dialog.setCancelable(false);
        dialog.show();
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_to_watch, container, false);
        setHasOptionsMenu(true);
        recyclerView = (IndexFastScrollRecyclerView) v.findViewById(R.id.fragment_to_watch_recyclerview);
        recyclerView.setIndexTextSize(12);
        recyclerView.setIndexbarMargin(4);
        recyclerView.setIndexbarWidth(40);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        seasonAdapter[0] = new ToWatchAdapter(towatch,getActivity());
        recyclerView.setAdapter(seasonAdapter[0]);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.serach, menu);
        MenuItem item = menu.findItem(R.id.grid_default_search);


        searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));


        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                seasons1 = filter(towatch, query);
                Collections.sort(seasons1, new Comparator<Season>() {
                    @Override
                    public int compare(Season o1, Season o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                for (Season season : seasons1) {
                    season.run();
                }
                seasonAdapter[0] = new ToWatchAdapter(filter(seasons1,query),getActivity());
                recyclerView.setAdapter(seasonAdapter[0]);
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(getContext(),
                        SeasonSearchHistory.AUTHORITY, SeasonSearchHistory.MODE);
                suggestions.saveRecentQuery(query, null);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                seasons1 = filter(towatch,newText);
                Collections.sort(seasons1, new Comparator<Season>() {
                    @Override
                    public int compare(Season o1, Season o2) {
                        return o1.getName().compareToIgnoreCase(o2.getName());
                    }
                });
                seasonAdapter[0] = new ToWatchAdapter(seasons1,getActivity());
                recyclerView.setAdapter(seasonAdapter[0]);
                return true;
            }
        });
        /*SearchView searchView = (SearchView)menu.findItem(R.id.grid_default_search).getActionView();
        */
        super.onCreateOptionsMenu(menu, inflater);
    }


    private ArrayList<Season> filter(List<Season> season, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Season> filteredModelList = new ArrayList<>();
        for (Season model : season) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                model.runInfo();
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
    private class UpdateSeasons extends AsyncTask<Void,Void, Void> {
        int z = 0;

        @Override
        protected Void doInBackground(Void... params) {

            Document doc = null;
            try {
                doc = Jsoup.connect(url).userAgent(RandomUserAgent.getRandomUserAgent()).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Element body = doc.body();
            Element seriesContainer = body.getElementById("seriesContainer");
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
                        if (towatch.contains(season)) {
                            continue;
                        }
                        if (MainActivity.getInstance().getDbHelper().isInsertSeasonToWatch(season.getName())) {
                            season.runInfo();
                            towatch.add(season);
                        }

                    }
                    z++;
                }
            }
            cancel(true);
            Collections.sort(towatch, new Comparator<Season>() {
                @Override
                public int compare(Season o1, Season o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }


        @Override
        protected void onCancelled(Void aVoid) {
            dialog.dismiss();
            recyclerView.setHasFixedSize(true);
            seasonAdapter[0] = new ToWatchAdapter(towatch,getActivity());
            recyclerView.setAdapter(seasonAdapter[0]);
            super.onCancelled(aVoid);
            seasonAdapter[0].notifyDataSetChanged();
        }

    }
}
