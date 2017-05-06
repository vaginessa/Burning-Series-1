package to.bs.bruningseriesmeterial.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.IOException;

import to.bs.bruningseriesmeterial.Hoster.hosts.VideoHost;
import to.bs.bruningseriesmeterial.Hoster.hosts.Vivo;
import to.bs.bruningseriesmeterial.Hoster.hosts.openload;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.fragments.Hoster.OpenLoad;

import static to.bs.bruningseriesmeterial.R.layout.episode;

public class CapatchCheck extends Fragment {
    private static final String Captcha = "URL";

    private String url;
    private static Episode episode;

    public static CapatchCheck newInstance(String newURL,Episode ep) {
        CapatchCheck fragment = new CapatchCheck();
        Bundle args = new Bundle();
        args.putString(Captcha, newURL);
        fragment.setArguments(args);
        episode = ep;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            url = getArguments().getString(Captcha);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_capatch_check, container, false);
        WebView view = (WebView) v.findViewById(R.id.captcha_site);
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        view.loadUrl(url);
        view.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if(url.contains("openload.co")){
                    OpenLoad fragment = OpenLoad.newInstance(url, episode);
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent, fragment,"OpenLoad").addToBackStack("S").commit();
                }else{
                    String Newurl = url;
                    VideoHost host = getHost(Newurl);
                    if(host == null){
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.getInstance(), R.string.host_bad_request,Toast.LENGTH_LONG).show();
                            }
                        });
                        Uri uri = Uri.parse(Newurl);
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                        browserIntent.setData(uri);
                        MainActivity.getInstance().startActivity(browserIntent);
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragmentManager.findFragmentByTag("EP")).addToBackStack("S").commit();
                        return true;
                    }
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(host.getStream(Newurl)));
                        intent.setDataAndType(Uri.parse(host.getStream(Newurl)), "video/mp4");
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        MainActivity.getInstance().startActivity(intent);
                        MainActivity.getInstance().getDbHelper().addEpisode(episode.getSeason().getName(),episode.getGerName());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.flContent, fragmentManager.findFragmentByTag("EP")).addToBackStack("S").commit();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                return super.shouldOverrideUrlLoading(view, url);

            }
        });

        return v;
    }
    private VideoHost getHost(String link){
        VideoHost host = null;
        if(link.contains("vivo.sx")){
            host = new Vivo();
        }
        return host;
    }

}
