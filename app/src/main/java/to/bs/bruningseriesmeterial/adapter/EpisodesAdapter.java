package to.bs.bruningseriesmeterial.adapter;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Episode;
import to.bs.bruningseriesmeterial.fragments.StreamingHoster;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * Created by Phillipp on 11.04.2017.
 */

public class EpisodesAdapter extends RecyclerView.Adapter<EpisodesAdapter.ViewHolder>{


    private List<Episode> episodes;
    private FragmentActivity activity;
    public EpisodesAdapter(List<Episode> episodeList, FragmentActivity fragmentActivity) {
        episodes = episodeList;
        activity = fragmentActivity;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.episode, parent, false);
        EpisodesAdapter.ViewHolder viewHolder = new EpisodesAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            final Episode episode = episodes.get(position);

            holder.watched.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(episode.isWatched()){
                        episode.setWatched(false);
                        MainActivity.getInstance().getDbHelper().removeEpisode(episode.getSeason().getName(),episode.getGerName());
                    }else{
                        MainActivity.getInstance().getDbHelper().addEpisode(episode.getSeason().getName(),episode.getGerName());
                        episode.setWatched(true);
                    }
                }
            });
            holder.watched.setChecked(episode.isWatched());
            holder.gerText.setText(episode.getGerName());
            holder.gerText.setTypeface(null, Typeface.BOLD);

        holder.cv.setSelected(false);

        holder.cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.clickListenerLayout.recyclerViewListClicked(v,position);
                }
            });

    }

    @Override
    public int getItemCount() {
        return episodes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        CheckBox watched;
        TextView gerText;
        ClickListenerLayout clickListenerLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.episode_cv);
            watched = (CheckBox) itemView.findViewById(R.id.episode_checkbox);
            gerText = (TextView) itemView.findViewById(R.id.episode_ger_text);
            clickListenerLayout = new ClickListenerLayout();
        }
    }
    public class ClickListenerLayout implements SeasonAdapter.ClickListenerOnPos {

        @Override
        public void recyclerViewListClicked(View v, int position) {
            InputMethodManager imm = (InputMethodManager)MainActivity.getInstance().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            StreamingHoster host = StreamingHoster.newInstance(episodes.get(position));
            FragmentManager fragmentManager = activity.getSupportFragmentManager();

            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_to_left,R.anim.slide_to_right,R.anim.slide_to_left,R.anim.slide_to_right).replace(R.id.flContent, host,"host").addToBackStack("EP").commit();
            //Episods fragment = Episods.newInstance(episodes.get(position));
            //FragmentManager fragmentManager = activity.getSupportFragmentManager();
            //fragmentManager.beginTransaction().replace(R.id.flContent, fragment,"EP").addToBackStack("S").commit();
        }
    }


    public interface ClickListenerOnPos  {
        public void recyclerViewListClicked(View v, int position);
    }

}
