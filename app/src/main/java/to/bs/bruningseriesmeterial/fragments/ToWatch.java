package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private RecyclerView recyclerView;
    private SearchView searchView;
    private ToWatchUpdateSeasonsList updateSeasonsList;
    private SwipeRefreshLayout swipeRefreshLayout;

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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_to_watch_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                towatch = new ArrayList<>();
                dialog.show();
                updateSeasonsList = new ToWatchUpdateSeasonsList(ToWatch.this);
                updateSeasonsList.execute(url);
            }
        });
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_to_watch_recyclerview);
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


        searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());

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

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public void setSeasonAdapter(ToWatchAdapter seasonAdapter) {
        this.seasonAdapter = seasonAdapter;
    }

    public void setTowatch(List<Season> towatch) {
        this.towatch = towatch;
    }

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}
