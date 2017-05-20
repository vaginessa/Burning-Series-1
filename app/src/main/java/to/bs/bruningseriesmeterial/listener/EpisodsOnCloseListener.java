package to.bs.bruningseriesmeterial.listener;

import android.widget.SearchView;

import to.bs.bruningseriesmeterial.fragments.Episods;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class EpisodsOnCloseListener implements SearchView.OnCloseListener {
    private Episods episods;
    public EpisodsOnCloseListener(Episods episods) {
        this.episods = episods;
    }

    @Override
    public boolean onClose() {
        episods.getSpinner().setSelection(episods.getEpisodsOnQueryTextListener().getId());
        return true;
    }
}
