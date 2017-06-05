package to.bs.bruningseriesmeterial.listener;

import android.widget.SearchView;

import to.bs.bruningseriesmeterial.fragments.EpisodsFragment;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsOnCloseListener implements SearchView.OnCloseListener {
    private EpisodsFragment episodsFragment;
    public EpisodsOnCloseListener(EpisodsFragment episodsFragment) {
        this.episodsFragment = episodsFragment;
    }

    @Override
    public boolean onClose() {
        episodsFragment.getSpinner().setSelection(episodsFragment.getEpisodsOnQueryTextListener().getId());
        return true;
    }
}
