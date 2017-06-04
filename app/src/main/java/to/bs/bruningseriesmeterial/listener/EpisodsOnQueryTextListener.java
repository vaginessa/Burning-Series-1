package to.bs.bruningseriesmeterial.listener;

import android.provider.SearchRecentSuggestions;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.adapter.EpisodesAdapter;
import to.bs.bruningseriesmeterial.fragments.EpisodsFragment;
import to.bs.bruningseriesmeterial.history.SeasonSearchHistory;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsOnQueryTextListener implements SearchView.OnQueryTextListener {
    private EpisodsFragment episodsFragment;
    private int id;

    public EpisodsOnQueryTextListener(EpisodsFragment episodsFragment) {
        this.episodsFragment = episodsFragment;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ArrayList<Episode> epsSerach = new ArrayList<Episode>();
        for (int i = 0; i < episodsFragment.getEpisods().size(); i++) {
            for (Episode episode : episodsFragment.getEpisods().get(i)) {
                epsSerach.add(episode);
            }
        }
        ArrayList<Episode> epsFiltered = filter(epsSerach, query);
        Collections.sort(epsFiltered, new Comparator<Episode>() {
            @Override
            public int compare(Episode o1, Episode o2) {
                return o1.getGerName().compareToIgnoreCase(o2.getGerName());
            }
        });
        EpisodesAdapter episodesAdapter = new EpisodesAdapter(epsFiltered, episodsFragment.getActivity());
        episodsFragment.getRecyclerView().setAdapter(episodesAdapter);
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(episodsFragment.getContext(), SeasonSearchHistory.AUTHORITY, SeasonSearchHistory.MODE);
        suggestions.saveRecentQuery(query, null);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if(episodsFragment.getSpinner().getSelectedItemPosition() != 0){
            id = episodsFragment.getSpinner().getSelectedItemPosition();
            episodsFragment.getSpinner().setSelection(0);
        }

        ArrayList<Episode> epsSerach = new ArrayList<Episode>();
        for (int i = 0; i < episodsFragment.getEpisods().size(); i++) {
            for (Episode episode : episodsFragment.getEpisods().get(i)) {
                epsSerach.add(episode);
            }
        }
        ArrayList<Episode> epsFiltered = filter(epsSerach, newText);
        Collections.sort(epsFiltered, new Comparator<Episode>() {

            @Override
            public int compare(Episode o1, Episode o2) {
                return o1.getGerName().compareToIgnoreCase(o2.getGerName());
            }
        });
        EpisodesAdapter episodesAdapter = new EpisodesAdapter(epsFiltered, episodsFragment.getActivity());
        episodsFragment.getRecyclerView().setAdapter(episodesAdapter);
        return true;
    }
    private ArrayList<Episode> filter(List<Episode> season, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Episode> filteredModelList = new ArrayList<>();
        for (Episode model : season) {
            final String text = model.getGerName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }

    public int getId() {
        return id;
    }
}
