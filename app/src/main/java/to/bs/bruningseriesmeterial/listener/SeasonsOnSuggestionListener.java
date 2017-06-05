package to.bs.bruningseriesmeterial.listener;

import android.app.SearchManager;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.widget.CursorAdapter;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.SeasonAdapter;
import to.bs.bruningseriesmeterial.fragments.Seasons;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class SeasonsOnSuggestionListener implements SearchView.OnSuggestionListener {
    private Seasons seasons;

    public SeasonsOnSuggestionListener(Seasons seasons) {
        this.seasons = seasons;
    }

    @Override
    public boolean onSuggestionSelect(int position) {
        CursorAdapter c = seasons.getSearchView().getSuggestionsAdapter();
        Cursor cur = c.getCursor();
        cur.moveToPosition(position);
        String val = cur.getString(cur.getColumnIndex(BaseColumns._ID));
        ArrayList<Season> seasons1 = filter(seasons.getSeasons(), val);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        SeasonAdapter seasonAdapter = new SeasonAdapter(seasons1);
        seasons.getRecyclerView().setAdapter(seasonAdapter);
        seasons.getSearchView().setQuery(val,false);
        return true;
    }

    @Override
    public boolean onSuggestionClick(int position) {
        CursorAdapter c = seasons.getSearchView().getSuggestionsAdapter();
        Cursor cur = c.getCursor();
        cur.moveToPosition(position);
        String val = cur.getString(cur.getColumnIndex(SearchManager.SUGGEST_COLUMN_TEXT_1));
        ArrayList<Season> seasons1 = filter(seasons.getSeasons(), val);
        Collections.sort(seasons1, new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        SeasonAdapter seasonAdapter = new SeasonAdapter(seasons1);
        seasons.getRecyclerView().setAdapter(seasonAdapter);
        seasons.getSearchView().setQuery(val,false);
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
