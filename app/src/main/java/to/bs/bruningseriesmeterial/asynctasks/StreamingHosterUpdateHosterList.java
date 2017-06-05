package to.bs.bruningseriesmeterial.asynctasks;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.adapter.HostAdapter;
import to.bs.bruningseriesmeterial.fragments.StreamingHosterFragment;
import to.bs.bruningseriesmeterial.hosters.HostFetcher;

/**
 * Created by Phillipp on 20.05.2017.
 */

public class StreamingHosterUpdateHosterList extends AsyncTask {
    private StreamingHosterFragment streamingHosterFragment;

    public StreamingHosterUpdateHosterList(StreamingHosterFragment streamingHosterFragment) {
        this.streamingHosterFragment = streamingHosterFragment;
    }

    @Override
    protected Object doInBackground(Object[] params) {
        try {

            Document doc = Jsoup.connect("https://bs.to/"+ streamingHosterFragment.getEpisode().getLink()).userAgent(RandomUserAgent.getRandomUserAgent()).get();
            Element body = doc.body();
            for (Element hosts : body.getElementsByClass("Hoster-tabs")) {
                for (Element host : hosts.getElementsByTag("li")) {
                    Document documentHost = Jsoup.connect("https://bs.to/"+host.getElementsByTag("a").first().attr("href")).get();
                    String url = documentHost.body().getElementsByClass("Hoster-player").first().attr("href");
                    HostFetcher hosterFetcher = new HostFetcher(url,host.text(), streamingHosterFragment.getEpisode());
                    streamingHosterFragment.getEpisode().getHosters().add(hosterFetcher);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        cancel(true);
        return null;
    }

    @Override
    protected void onCancelled(Object o) {
        streamingHosterFragment.getRecyclerView().setAdapter(new HostAdapter(streamingHosterFragment.getEpisode().getHosters()));
        streamingHosterFragment.getDialog().dismiss();
    }
}
