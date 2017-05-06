package to.bs.bruningseriesmeterial.Utils;

import java.util.ArrayList;

import to.bs.bruningseriesmeterial.Hoster.HostFetcher;

/**
 * Created by Phillipp on 10.04.2017.
 */

public class Episode {

    private String GerName,EngName,link;
    private ArrayList<HostFetcher> hosters;
    private boolean watched;
    private Season season;

    public Episode(String gerName, String engName,String url,Season s) {
        GerName = gerName;
        EngName = engName;
        hosters = new ArrayList<>();
        season = s;
        link = url;
    }

    public Episode(String gerName, String engName, boolean watched,String url,Season s) {
        GerName = gerName;
        EngName = engName;
        this.watched = watched;
        hosters = new ArrayList<>();
        season = s;
        link = url;
    }

    public Episode() {
        hosters = new ArrayList<>();
    }

    public Season getSeason() {
        return season;
    }

    public String getLink() {
        return link;
    }

    public boolean isWatched() {
        return watched;
    }

    public void setWatched(boolean watched) {
        this.watched = watched;
    }

    public String getGerName() {
        return GerName;
    }

    public String getEngName() {
        return EngName;
    }

    public ArrayList<HostFetcher> getHosters() {
        return hosters;
    }
}
