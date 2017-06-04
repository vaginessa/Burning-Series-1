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
import android.widget.SearchView;

import java.util.ArrayList;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.ToWatchAdapter;
import to.bs.bruningseriesmeterial.asynctasks.ToWatchUpdateSeasonsList;
import to.bs.bruningseriesmeterial.listener.ToWatchOnQueryTextListener;
import to.bs.bruningseriesmeterial.listener.ToWatchOnSuggestionListener;

public class ToWatch extends Fragment {
    private static final String URL = "SerienURL";
    private String url;
    private List<Season> towatch;
    private ProgressDialog dialog;
    private ToWatchAdapter seasonAdapter;
    private IndexFastScrollRecyclerView recyclerView;
    private SearchManager searchManager;
    private SearchView searchView;
    private ToWatchUpdateSeasonsList updateSeasonsList;

    public ToWatch() {
    }

    public static ToWatch newInstance(String param1) {
        ToWatch fragment = new ToWatch();
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
        towatch = new ArrayList<>();
        updateSeasonsList = new ToWatchUpdateSeasonsList(this);
        updateSeasonsList.execute(url);
        dialog = new ProgressDialog(getActivity());
        dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        dialog.setMessage(getString(R.string.Seasons_wait));
        dialog.setCancelable(false);
        dialog.show();

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
        seasonAdapter = new ToWatchAdapter(towatch,getActivity());
        recyclerView.setAdapter(seasonAdapter);


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

        searchView.setIconifiedByDefault(false);
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryRefinementEnabled(true);
        searchView.setOnSuggestionListener(new ToWatchOnSuggestionListener(this));
        searchView.setOnQueryTextListener(new ToWatchOnQueryTextListener(this));
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.clearFocus();

    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    public SearchView getSearchView() {
        return searchView;
    }

    public List<Season> getTowatch() {
        return towatch;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public IndexFastScrollRecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setSeasonAdapter(ToWatchAdapter seasonAdapter) {
        this.seasonAdapter = seasonAdapter;
    }

    public void setTowatch(List<Season> towatch) {
        this.towatch = towatch;
    }
}
