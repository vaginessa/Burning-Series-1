package to.bs.bruningseriesmeterial.hosters;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

import to.bs.bruningseriesmeterial.hosters.hosts.VideoHost;
import to.bs.bruningseriesmeterial.hosters.hosts.Vivo;
import to.bs.bruningseriesmeterial.hosters.hosts.openload;
import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.fragments.CapatchCheck;
import to.bs.bruningseriesmeterial.fragments.frgamenthoster.OpenLoad;

/**
 * Created by Phillipp on 11.04.2017.
 */

public class HostFetcher {
    private String Link;
    private String Name;
    private Episode episode;

    public HostFetcher(String link,Episode ep) {
        Link = link;
        episode = ep;
    }

    public HostFetcher(String link, String name,Episode ep) {
        Link = link;
        Name = name;
        episode = ep;
    }

    public void connect() {
        if(getName().contains("Vivo")){
            new VivoLoadAsync().execute();
        }else if(getName().contains("OpenLoad")){
            new VivoLoadAsync().execute();
        }else{
            Uri uri = Uri.parse(getLink());
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            browserIntent.setData(uri);
            MainActivity.getInstance().startActivity(browserIntent);
        }

    }

    public String randomUserAgent() {
        Random r = new Random();
        int i = MainActivity.getInstance().getUserAgents().size();
        return MainActivity.getInstance().getUserAgents().get(r.nextInt(i));
    }

    public String getLink() {
        return Link;
    }

    public String getName() {
        return Name;
    }

    public Episode getEpisode() {
        return episode;
    }
    private String urlFtcher(String link) throws Exception {
        URL url = new URL(link);
        final HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
        ucon.addRequestProperty("User-Agent", RandomUserAgent.getRandomUserAgent());
        ucon.setInstanceFollowRedirects(true);
        final String Newurl;
        int responseType = ucon.getResponseCode();
        if (responseType != 301) {
            if(responseType == 400 || responseType == 403){
                MainActivity.getInstance().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.getInstance(), R.string.host_bad_request,Toast.LENGTH_LONG).show();
                    }
                });

            }else if(responseType == 200){
               HostChecker(ucon.getURL().toString());
            }
            return null;
        }
        URL secondURL = new URL(ucon.getHeaderField("Location"));
        Newurl = secondURL.toString();
        return Newurl;
    }
    private void HostChecker(final String u){
        if(u.contains("bs.to")){
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    CapatchCheck fragment = CapatchCheck.newInstance(u,episode);
                    FragmentManager fragmentManager = MainActivity.getInstance().getSupportFragmentManager();
                    //fragmentManager.beginTransaction().replace(R.id.flContent, fragment,"Captcha").addToBackStack("S").commit();
                }
            });
        }else if(u.contains("openload")){
            MainActivity.getInstance().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    OpenLoad fragment = OpenLoad.newInstance(u,episode);
                    FragmentManager fragmentManager = MainActivity.getInstance().getSupportFragmentManager();
                    //fragmentManager.beginTransaction().replace(R.id.flContent, fragment,"OpenLoad").addToBackStack("S").commit();
                }
            });
        }else{
            Uri uri = Uri.parse(u);
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
            browserIntent.setData(uri);
            MainActivity.getInstance().startActivity(browserIntent);
        }

    }
    private VideoHost getHost(String link){
        VideoHost host = null;
        if(link.contains("vivo.sx")){
            host = new to.bs.bruningseriesmeterial.hosters.hosts.Vivo();
        }
        if(link.contains("openload.co")){
            host = new openload();
        }
        return host;
    }

    private class VivoLoadAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                String Newurl = urlFtcher(getLink());
                VideoHost host = getHost(Newurl);
                if(host == null){
                    Uri uri = Uri.parse(Newurl);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    browserIntent.setData(uri);
                    MainActivity.getInstance().startActivity(browserIntent);
                    cancel(true);
                    return null;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(host.getStream(Newurl)));
                intent.setDataAndType(Uri.parse(host.getStream(Newurl)), "video/mp4");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MainActivity.getInstance().startActivity(intent);
                MainActivity.getInstance().getDbHelper().addEpisode(episode.getSeason().getName(),episode.getGerName());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cancel(true);
            return null;
        }

    }
    private class OpenLoadAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                URL url = new URL(getLink());
                final HttpURLConnection ucon = (HttpURLConnection) url.openConnection();

                ucon.addRequestProperty("User-Agent", RandomUserAgent.getRandomUserAgent());
                ucon.setInstanceFollowRedirects(true);
                final String Newurl;
                int responseType = ucon.getResponseCode();
                if (responseType > 301) {
                    if(responseType == 400 || responseType == 403){
                        MainActivity.getInstance().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.getInstance(), R.string.host_bad_request,Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    cancel(true);
                    return null;
                }
                URL secondURL = new URL(ucon.getHeaderField("Location"));
                Newurl = secondURL.toString();
                VideoHost host = getHost(Newurl);
                if(host == null){
                    Uri uri = Uri.parse(Newurl);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, uri);
                    browserIntent.setData(uri);
                    MainActivity.getInstance().startActivity(browserIntent);
                    cancel(true);
                    return null;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(host.getStream(Newurl)));
                intent.setDataAndType(Uri.parse(host.getStream(Newurl)), "video/mp4");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                MainActivity.getInstance().startActivity(intent);
                MainActivity.getInstance().getDbHelper().addEpisode(episode.getSeason().getName(),episode.getGerName());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            cancel(true);
            return null;
        }
        private VideoHost getHost(String link){
            VideoHost host = null;
            if(link.contains("vivo.sx")){
                host = new Vivo();
            }
            if(link.contains("openload.co")){
                host = new openload();
            }
            return host;
        }
    }
}
