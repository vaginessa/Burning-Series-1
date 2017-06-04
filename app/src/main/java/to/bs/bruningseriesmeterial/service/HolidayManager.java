package to.bs.bruningseriesmeterial.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.os.Parcel;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.Map;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.database.SeasonDbHelper;
import to.bs.bruningseriesmeterial.hosters.HostFetcher;
import to.bs.bruningseriesmeterial.hosters.HostFetcherBackground;

/**
 * Created by Phillipp on 26.05.2017.
 */

public class HolidayManager extends IntentService {


    public HolidayManager() {
        super("Holiday");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

            if (mWifi.isConnected()) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                boolean holiday = sharedPref.getBoolean("holiday",false);
                if (holiday) {
                    SeasonDbHelper dbHelper = MainActivity.getInstance().getDbHelper();
                    for (String serie : dbHelper.getSeasons()) {
                        for (Map.Entry<String, String> ep : dbHelper.getEpisodsToWatch(serie).entrySet()) {
                            String name = ep.getKey();
                                /*Document doc = Jsoup.connect("https://bs.to/" + ep.getValue()).userAgent(RandomUserAgent.getRandomUserAgent()).get();
                                Element body = doc.body();
                                for (Element hosts : body.getElementsByClass("Hoster-tabs")) {
                                    for (Element host : hosts.getElementsByTag("li")) {
                                        Document documentHost = Jsoup.connect("https://bs.to/" + host.getElementsByTag("a").first().attr("href")).get();
                                        String url = documentHost.body().getElementsByClass("Hoster-player").first().attr("href");
                                        HostFetcherBackground fetcherBackground = new HostFetcherBackground(url,name);
                                        fetcherBackground.connect();

                                    }
                                }*/
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
