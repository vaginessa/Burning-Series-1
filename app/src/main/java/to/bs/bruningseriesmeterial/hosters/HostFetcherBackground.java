package to.bs.bruningseriesmeterial.hosters;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.Utils.RandomUserAgent;
import to.bs.bruningseriesmeterial.fragments.CapatchCheck;
import to.bs.bruningseriesmeterial.fragments.frgamenthoster.OpenLoad;
import to.bs.bruningseriesmeterial.hosters.hosts.VideoHost;
import to.bs.bruningseriesmeterial.hosters.hosts.Vivo;
import to.bs.bruningseriesmeterial.hosters.hosts.openload;

/**
 * Created by Phillipp on 11.04.2017.
 */

public class HostFetcherBackground {
    private String Link;
    private String Name;

    public HostFetcherBackground(String link) {
        Link = link;
    }

    public HostFetcherBackground(String link, String name) {
        Link = link;
        Name = name;
    }

    public void connect() {
        if(getName().contains("Vivo")){
            new VivoLoadAsync().execute();
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

    private String urlFtcher(String link) throws Exception {
        URL url = new URL(link);
        final HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
        ucon.addRequestProperty("User-Agent", RandomUserAgent.getRandomUserAgent());
        ucon.setInstanceFollowRedirects(true);
        final String Newurl;
        int responseType = ucon.getResponseCode();
        if (responseType != 301) {
            return null;
        }
        URL secondURL = new URL(ucon.getHeaderField("Location"));
        Newurl = secondURL.toString();
        return Newurl;
    }
    private VideoHost getHost(String link){
        VideoHost host = null;
        if(link.contains("vivo.sx")){
            host = new Vivo();
        }
        return host;
    }

    private class VivoLoadAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            try {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.getInstance().getApplicationContext());
                String Newurl = urlFtcher(getLink());
                VideoHost host = getHost(Newurl);
                if(host == null){
                    cancel(true);
                    return null;
                }
                String folder = sharedPref.getString("downloadFolder", "NA");
                URL fileurl = new URL(host.getStream(Newurl));
                URLConnection urlConnection = fileurl.openConnection();
                urlConnection.connect();

                InputStream inputStream = new BufferedInputStream(urlConnection.getInputStream(),8192);

                File downloadordner = new File(folder, "bs.to");
                if(!downloadordner.exists()){
                    downloadordner.mkdirs();
                }

                File downloadedFile = new File(downloadordner, getName()+".mp4");
                if (downloadedFile.exists()) {
                    return null;
                }
                OutputStream outputStream = new FileOutputStream(downloadedFile);

                byte[] buffer = new byte[1024];
                int read;
                while ((read = inputStream.read(buffer)) != -1){
                    outputStream.write(buffer, 0, read);
                }
                outputStream.flush();
                outputStream.close();
                inputStream.close();
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
}
