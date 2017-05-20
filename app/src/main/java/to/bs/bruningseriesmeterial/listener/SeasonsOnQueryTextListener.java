package to.bs.bruningseriesmeterial.listener;

import android.provider.SearchRecentSuggestions;
import android.support.v4.app.Fragment;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.SeasonAdapter;
import to.bs.bruningseriesmeterial.fragments.Seasons;
import to.bs.bruningseriesmeterial.history.SeasonSearchHistory;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class SeasonsOnQueryTextListener implements SearchView.OnQueryTextListener {
    private Seasons seasons;

    public SeasonsOnQueryTextListener(Seasons seasons) {
        this.seasons = seasons;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ArrayList<Season> seasons1 = filter(seasons.getSeasons(), query);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        SeasonAdapter seasonAdapter = new SeasonAdapter(seasons1, seasons.getActivity());
        seasons.getRecyclerView().setAdapter(seasonAdapter);
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(seasons.getContext(), SeasonSearchHistory.AUTHORITY, SeasonSearchHistory.MODE);
        suggestions.saveRecentQuery(query, null);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Season> seasons1 = filter(seasons.getSeasons(), newText);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        SeasonAdapter seasonAdapter = new SeasonAdapter(seasons1, seasons.getActivity());
        seasons.getRecyclerView().setAdapter(seasonAdapter);
        return true;
    }

    private ArrayList<Season> filter(List<Season> season, String query) {
        final String lowerCaseQuery = query.toLowerCase();

        final ArrayList<Season> filteredModelList = new ArrayList<>();
        for (Season model : season) {
            final String text = model.getName().toLowerCase();
            if (text.contains(lowerCaseQuery)) {
                filteredModelList.add(model);
            }
        }
        return filteredModelList;
    }
}
