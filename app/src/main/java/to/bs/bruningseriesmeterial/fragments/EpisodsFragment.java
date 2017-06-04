package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.activity.Episodes;
import to.bs.bruningseriesmeterial.adapter.EpisodesAdapter;
import to.bs.bruningseriesmeterial.asynctasks.EpisodsUpdateList;
import to.bs.bruningseriesmeterial.listener.EpisodsOnCloseListener;
import to.bs.bruningseriesmeterial.listener.EpisodsOnItemSelectedListener;
import to.bs.bruningseriesmeterial.listener.EpisodsOnQueryTextListener;

public class EpisodsFragment extends Fragment {
    private static int pos;
    private boolean special;

    private Season season;
    private ProgressDialog dialog;
    private Map<Integer,List<Episode>> eps = new HashMap<>();
    private RecyclerView recyclerView;
    private EpisodesAdapter[] episodesAdapter;
    private ArrayAdapter<String> arrayAdapter;
    private Spinner spinner;
    private SearchManager searchManager;
    private SearchView searchView;
    private EpisodsOnQueryTextListener episodsOnQueryTextListener;
    private EpisodsUpdateList updateList;


    public EpisodsFragment() {
    }

    public static EpisodsFragment newInstance(Season s, int position) {
        pos = position;
        EpisodsFragment fragment = new EpisodsFragment();
        fragment.season = s;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        updateList = new EpisodsUpdateList(this);
        updateList.execute();
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        dialog.setMessage(getString(R.string.Episods_wait));
        dialog.show();
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1);

    }

    public static int getPos() {
        return pos;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.getInstance().setTitle(season.getName());
        setHasOptionsMenu(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());

        final View v = inflater.inflate(R.layout.fragment_episods, container, false);
        spinner = (Spinner) v.findViewById(R.id.frgament_ep_spinner);
        recyclerView = (RecyclerView) v.findViewById(R.id.frgament_ep_rv);

        episodesAdapter = new EpisodesAdapter[1];

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(episodesAdapter[0]);


        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new EpisodsOnItemSelectedListener(this));
        return v;

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.serach, menu);

        MenuItem item = menu.findItem(R.id.grid_default_search);
        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        searchView = new SearchView(((Episodes) getActivity()).getSupportActionBar().getThemedContext());
        searchManager = (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        MenuItemCompat.setActionView(item, searchView);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconified(false);

        episodsOnQueryTextListener = new EpisodsOnQueryTextListener(this);
        searchView.setOnQueryTextListener(episodsOnQueryTextListener);
        searchView.setOnCloseListener(new EpisodsOnCloseListener(this));
    }

    public void setEpisodesAdapter(EpisodesAdapter[] episodesAdapter) {
        this.episodesAdapter = episodesAdapter;
    }

    public EpisodesAdapter[] getEpisodesAdapter() {
        return episodesAdapter;
    }

    public Map<Integer, List<Episode>> getEpisods() {
        return eps;
    }


    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public boolean isSpecial() {
        return special;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }

    public Spinner getSpinner() {
        return spinner;
    }

    public EpisodsOnQueryTextListener getEpisodsOnQueryTextListener() {
        return episodsOnQueryTextListener;
    }

    public ArrayAdapter<String> getArrayAdapter() {
        return arrayAdapter;
    }

    public void setArrayAdapter(ArrayAdapter<String> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    public Season getSeason() {
        return season;
    }

    public void setSpecial(boolean special) {
        this.special = special;
    }
}
