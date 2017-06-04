package to.bs.bruningseriesmeterial.database;

import android.provider.BaseColumns;

/**
 * Created by Phillipp on 12.04.2017.
 */

public class SeasonContract {
    private SeasonContract() {}

    /* Inner class that defines the table contents */
    public static class SeasonEntry implements BaseColumns {
        public static final String TABLE_NAME = "Seasons";
        public static final String SEASON_NAME = "Serie";

    }
    public static class ToWatchEntry implements BaseColumns {
        public static final String TABLE_NAME = "ToWatch";
        public static final String SEASON_NAME = "Serie";
        public static final String GERNE_NAME = "Gerne";
        public static final String DESCRIPTION_NAME = "DESCRIPTION";
        public static final String EPS_COUNT = "Eps";
        public static final String EPS_WATCHED = "Watched";
    }
    public static class ToWatchEpisodeEntry implements BaseColumns {
        public static final String EP_NAME = "Name";
        public static final String EP_URL = "URL";
    }
    public static class EpisodesEntry implements BaseColumns {
        public static final String TABLE_NAME = "Episodes";
        public static final String SEASON_NAME = "Serie";
        public static final String Episode_NAME = "Episode";
    }
}
