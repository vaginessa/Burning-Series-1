package to.bs.bruningseriesmeterial.listener;


import android.app.SearchManager;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.widget.CursorAdapter;
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

public class ToWatchOnSuggestionListener implements SearchView.OnSuggestionListener {
    private ToWatch toWatch;
    public ToWatchOnSuggestionListener(ToWatch toWatch) {
        this.toWatch = toWatch;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        CursorAdapter c = toWatch.getSearchView().getSuggestionsAdapter();
        Cursor cur = c.getCursor();
        cur.moveToPosition(position);
        String val = cur.getString(cur.getColumnIndex(BaseColumns._ID));
        ArrayList<Season> seasons = filter(toWatch.getTowatch(), val);
        Collections.sort(seasons, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        ToWatchAdapter seasonAdapter = new ToWatchAdapter(seasons, toWatch.getActivity());
        toWatch.getRecyclerView().setAdapter(seasonAdapter);
        toWatch.getSearchView().setQuery(val,false);
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        CursorAdapter c = toWatch.getSearchView().getSuggestionsAdapter();
        Cursor cur = c.getCursor();
        cur.moveToPosition(position);
        String val = cur.getString(cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        ArrayList<Season> seasons1 = filter(toWatch.getTowatch(), val);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        for (Season season : seasons1) {
            season.run();
        }
        ToWatchAdapter seasonAdapter = new ToWatchAdapter(seasons1, toWatch.getActivity());
        toWatch.getRecyclerView().setAdapter(seasonAdapter);
        toWatch.getSearchView().setQuery(val,false);
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
