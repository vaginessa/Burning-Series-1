package to.bs.bruningseriesmeterial.fragments.tab;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;

import to.bs.bruningseriesmeterial.R;


public class NewEpisodes extends Fragment {
    private static final String URL = "url";

    private String url;
    private ArrayAdapter<String> newst;
    private ProgressDialog dialog;

    public NewEpisodes() {
    }


    public static NewEpisodes newInstance(String param1) {
        NewEpisodes fragment = new NewEpisodes();
        Bundle args = new Bundle();
        args.putString(URL, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            url = getArguments().getString(URL);
        }
        newst = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1);
        dialog = new ProgressDialog(getContext());
        dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
        dialog.setMessage(getString(R.string.Episods_wait));
        dialog.setCancelable(false);
        dialog.show();
        new DownloadNewest().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.fragment_new_episodes, container, false);
        ListView view = (ListView) v.findViewById(R.id.fragment_new_eps_list);
        view.setAdapter(newst);
        return v;
    }
    public class DownloadNewest extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Document doc = Jsoup.connect(url).get();
                for (Element newe : doc.body().getElementById("newest_episodes").getElementsByTag("ul").first().getElementsByTag("li")) {
                    String name = newe.getElementsByTag("div").get(0).text();
                    String epname = newe.getElementsByTag("div").get(1).text();
                    String string = name + " - " +epname;
                    newst.add(string);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            cancel(true);
            return null;
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }
    }

}
