package to.bs.bruningseriesmeterial.listener;

import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.adapter.EpisodesAdapter;
import to.bs.bruningseriesmeterial.fragments.EpisodsFragment;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsOnItemSelectedListener implements AdapterView.OnItemSelectedListener {
    private EpisodsFragment episodsFragment;
    public EpisodsOnItemSelectedListener(EpisodsFragment episodsFragment) {
        this.episodsFragment = episodsFragment;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        EpisodesAdapter episodesAdapter;
        if(position == 0){
            List<Episode> episodes = new ArrayList<>();
            episodesAdapter = new EpisodesAdapter(episodes, episodsFragment);
        }else if(position == 1){
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(position-1), episodsFragment);
        }else{
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(position-1), episodsFragment);
        }
        episodsFragment.getRecyclerView().setAdapter(episodesAdapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        EpisodesAdapter episodesAdapter;
        if(episodsFragment.isSpecial()) {
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(1), episodsFragment);
        }else {
            episodesAdapter = new EpisodesAdapter(episodsFragment.getEpisods().get(0), episodsFragment);
        }
        episodsFragment.getRecyclerView().setAdapter(episodesAdapter);
    }
}
