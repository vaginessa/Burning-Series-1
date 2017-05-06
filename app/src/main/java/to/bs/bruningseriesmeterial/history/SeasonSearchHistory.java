package to.bs.bruningseriesmeterial.history;

import android.content.SearchRecentSuggestionsProvider;


/**
 * Created by Phillipp on 29.04.2017.
 */

public class SeasonSearchHistory extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "to.bs.bruningseriesmeterial";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public SeasonSearchHistory() {
        setupSuggestions(AUTHORITY, MODE);
    }

}
