package to.bs.bruningseriesmeterial.fragments.frgamenthoster;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.IOException;

import to.bs.bruningseriesmeterial.hosters.hosts.VideoHost;
import to.bs.bruningseriesmeterial.hosters.hosts.openload;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;

public class OpenLoad extends Fragment {
    private static final String OpenLoad = "URL";

    private String url;
    private static Episode episode;

    public static OpenLoad newInstance(String newURL, Episode ep) {
        OpenLoad fragment = new OpenLoad();
        Bundle args = new Bundle();
        args.putString(OpenLoad, newURL);
        fragment.setArguments(args);
        episode = ep;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            url = getArguments().getString(OpenLoad);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_open_load, container, false);
        WebView view = (WebView) v.findViewById(R.id.openload_VIEW);
        WebSettings webSettings = view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        view.addJavascriptInterface(new OpenLoad.MyJavaScriptInterface(),"HTMLOUT");
        view.loadUrl(url);
        view.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                if(url.contains("openload")){
                    view.loadUrl("javascript:window.HTMLOUT.processHTML('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                }
            }
        });

        return v;
    }
    class MyJavaScriptInterface
    {
        @JavascriptInterface
        public void processHTML(String html)
        {
            String Newurl = html;


            VideoHost host = getHost(url);
            if(host == null){
                Uri uri = Uri.parse(Newurl);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                browserIntent.setData(uri);
                MainActivity.getInstance().startActivity(browserIntent);
                return;
            }
            try {
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.popBackStack();
                MainActivity.getInstance().getDbHelper().addEpisode(episode.getSeason().getName(),episode.getGerName());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(host.getStream(Newurl)));
                intent.setDataAndType(Uri.parse(host.getStream(Newurl)), "video/mp4");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MainActivity.getInstance().startActivity(intent);


            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    private VideoHost getHost(String link){
        VideoHost host = null;
        if(link.contains("openload.co")){
            host = new openload();
        }
        return host;
    }
}
