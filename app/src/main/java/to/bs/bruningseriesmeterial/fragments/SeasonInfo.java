package to.bs.bruningseriesmeterial.fragments;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;

import static android.R.attr.bitmap;


public class SeasonInfo extends Fragment {
    private static Season myseason;
    private Bitmap map;

    public SeasonInfo() {
        // Required empty public constructor
    }

    public static SeasonInfo newInstance(Season season) {
        SeasonInfo fragment = new SeasonInfo();
        myseason = season;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_season_info, container, false);
        final ImageView imageButton = (ImageView) v.findViewById(R.id.season_image);
        Picasso.with(getContext()).load(myseason.getImage()).into(imageButton);
        TextView Genres = (TextView) v.findViewById(R.id.Genres);
        Genres.setText("Genres: "+myseason.getGerne());
        TextView Produktionsjahre = (TextView) v.findViewById(R.id.Produktionsjahre);
        Produktionsjahre.setText("Produktionsjahre: "+myseason.getProductionyear());
        TextView Hauptdarsteller = (TextView) v.findViewById(R.id.Hauptdarsteller);
        Hauptdarsteller.setText("Hauptdarsteller: "+myseason.getMainactor());
        TextView Produzenten = (TextView) v.findViewById(R.id.Produzenten);
        Produzenten.setText("Produzenten: "+myseason.getProducers());
        TextView Regisseure = (TextView) v.findViewById(R.id.Regisseure);
        Regisseure.setText("Regisseure: "+myseason.getDirectors());
        TextView Autoren = (TextView) v.findViewById(R.id.Autoren);
        Autoren.setText("Autoren: "+myseason.getAuhtor());
        TextView Desc = (TextView) v.findViewById(R.id.stroy);
        Desc.setText("Beschreibung: "+myseason.getDescription());
        return v;
    }


}
