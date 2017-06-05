package to.bs.bruningseriesmeterial.listener;

import android.provider.SearchRecentSuggestions;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.ToWatchAdapter;
import to.bs.bruningseriesmeterial.fragments.ToWatch;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class ToWatchOnQueryTextListener implements SearchView.OnQueryTextListener {
    private ToWatch toWatch;

    public ToWatchOnQueryTextListener(ToWatch toWatch) {
        this.toWatch = toWatch;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ArrayList<Season> seasons1 = filter(toWatch.getTowatch(), query);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        ToWatchAdapter seasonAdapter = new ToWatchAdapter(seasons1, toWatch.getActivity());
        toWatch.getRecyclerView().setAdapter(seasonAdapter);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ArrayList<Season> seasons1 = filter(toWatch.getTowatch(), newText);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        ToWatchAdapter seasonAdapter = new ToWatchAdapter(seasons1, toWatch.getActivity());
        toWatch.getRecyclerView().setAdapter(seasonAdapter);
        return true;
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
}
