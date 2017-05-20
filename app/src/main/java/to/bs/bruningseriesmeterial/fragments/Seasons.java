package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.SeasonAdapter;
import to.bs.bruningseriesmeterial.asynctasks.SeasonsUpdateSeasonsList;
import to.bs.bruningseriesmeterial.listener.SeasonsOnQueryTextListener;
import to.bs.bruningseriesmeterial.listener.SeasonsOnSuggestionListener;

public class Seasons extends Fragment {
    private static final String URL = "SerienURL";
    private static MenuItem item;
    private String url;
    private ProgressDialog dialog;
    private List<Season> seasons;
    private SeasonAdapter seasonAdapter;
    private IndexFastScrollRecyclerView recyclerView;
    private SearchView searchView;
    private SearchManager searchManager;
    private SeasonsUpdateSeasonsList updateSeasonsList;

    public Seasons() {
        // Required empty public constructor
    }


    public static Seasons newInstance(String param1, MenuItem menuItem) {
        item = menuItem;
        Seasons fragment = new Seasons();
        Bundle args = new Bundle();
        args.putString(URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
        }
        seasons = new ArrayList<>();
        updateSeasonsList = new SeasonsUpdateSeasonsList(this);
        updateSeasonsList.execute(url);

        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        dialog.setMessage(getString(R.string.Seasons_wait));
        dialog.setCancelable(false);

        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        MainActivity.getInstance().setTitle(item.getTitle());
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_seasons, container, false);
        recyclerView = (IndexFastScrollRecyclerView) v.findViewById(R.id.fragment_seasons_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        seasonAdapter = new SeasonAdapter(seasons,getActivity());
        recyclerView.setAdapter(seasonAdapter);
        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.serach, menu);
        MenuItem item = menu.findItem(R.id.grid_default_search);
        searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());

        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);


        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setOnSuggestionListener(new SeasonsOnSuggestionListener(this));
        searchView.setIconified(false);
        searchView.setOnQueryTextListener(new SeasonsOnQueryTextListener(this));
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.clearFocus();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainActivity.hideKeyboard(getActivity());
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public IndexFastScrollRecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setSeasonAdapter(SeasonAdapter seasonAdapter) {
        this.seasonAdapter = seasonAdapter;
    }

    public SeasonAdapter getSeasonAdapter() {
        return seasonAdapter;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public SearchView getSearchView() {
        return searchView;
    }
}
