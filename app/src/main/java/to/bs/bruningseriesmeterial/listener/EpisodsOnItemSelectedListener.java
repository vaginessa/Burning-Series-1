package to.bs.bruningseriesmeterial.listener;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.adapter.EpisodesAdapter;
import to.bs.bruningseriesmeterial.fragments.Episods;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private Episods episods;
    public EpisodsOnItemSelectedListener(Episods episods) {
        this.episods = episods;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EpisodesAdapter episodesAdapter;
        if(position == 0){
            List<Episode> episodes = new ArrayList<>();
            episodesAdapter = new EpisodesAdapter(episodes,episods.getActivity());
        }else if(position == 1){
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(position-1),episods.getActivity());
        }else{
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(position-1),episods.getActivity());
        }
        episods.getRecyclerView().setAdapter(episodesAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        EpisodesAdapter episodesAdapter;
        if(episods.isSpecial()) {
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(1),episods.getActivity());
        }else {
            episodesAdapter = new EpisodesAdapter(episods.getEpisods().get(0),episods.getActivity());
        }
        episods.getRecyclerView().setAdapter(episodesAdapter);
    }
}
