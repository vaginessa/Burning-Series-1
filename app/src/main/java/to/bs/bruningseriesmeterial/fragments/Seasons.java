package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
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

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.adapter.SeasonAdapter;
import to.bs.bruningseriesmeterial.asynctasks.SeasonsUpdateSeasonsList;
import to.bs.bruningseriesmeterial.listener.SeasonsOnQueryTextListener;
import to.bs.bruningseriesmeterial.listener.SeasonsOnSuggestionListener;

public class Seasons extends Fragment {
    private static final String URL = "SerienURL";
    private String url;
    private ProgressDialog dialog;
    private List<Season> seasons;
    private SeasonAdapter seasonAdapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private SeasonsUpdateSeasonsList updateSeasonsList;
    private SwipeRefreshLayout swipeRefreshLayout;

    public Seasons() {
        // Required empty public constructor
    }


    public static Seasons newInstance(String param1) {
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
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_seasons, container, false);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.fragment_seasons_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                seasons = new ArrayList<>();
                dialog.show();
                updateSeasonsList = new SeasonsUpdateSeasonsList(Seasons.this);
                updateSeasonsList.execute(url);
            }
        });
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_seasons_recyclerview);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        seasonAdapter = new SeasonAdapter(seasons);
        recyclerView.setAdapter(seasonAdapter);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.serach, menu);
        MenuItem item = menu.findItem(R.id.grid_default_search);
        searchView = new SearchView(((MainActivity) getActivity()).getSupportActionBar().getThemedContext());

        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        MenuItemCompat.setActionView(item, searchView);


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
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public RecyclerView getRecyclerView() {
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

    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return swipeRefreshLayout;
    }
}
