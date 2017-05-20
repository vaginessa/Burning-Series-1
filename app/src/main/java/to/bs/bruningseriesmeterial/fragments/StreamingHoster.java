package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import to.bs.bruningseriesmeterial.asynctasks.StreamingHosterUpdateHosterList;
import to.bs.bruningseriesmeterial.hosters.HostFetcher;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.adapter.HostAdapter;

public class StreamingHoster extends Fragment {
    private static Episode episode;
    private ProgressDialog dialog;
    private RecyclerView recyclerView;

    private StreamingHosterUpdateHosterList updateHosterList;

    public StreamingHoster() {
    }

    public static StreamingHoster newInstance(Episode ep) {
        StreamingHoster fragment = new StreamingHoster();
        episode = ep;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        if (episode.getHosters().size() == 0) {
            updateHosterList = new StreamingHosterUpdateHosterList(this);
            updateHosterList.execute();
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.fragment_hoster_wait));
            dialog.setCancelable(false);
            dialog.show();

        }

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity.getInstance().setTitle(episode.getGerName());
        View v = inflater.inflate(R.layout.fragment_hoster, null, false);
        recyclerView = (RecyclerView) v.findViewById(R.id.fragment_hoster_recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(llm);
        recyclerView.setAdapter(new HostAdapter(episode.getHosters()));

        return v;
    }

    public Episode getEpisode() {
        return episode;
    }

    public RecyclerView getRecyclerView() {
        return recyclerView;
    }

    public ProgressDialog getDialog() {
        return dialog;
    }
}
