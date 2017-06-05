package to.bs.bruningseriesmeterial.adapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.activity.Episodes;
import to.bs.bruningseriesmeterial.fragments.EpisodsFragment;

/**
 * Created by Phillipp on 10.04.2017.
 */

public class ToWatchAdapter extends RecyclerView.Adapter<ToWatchAdapter.ViewHolder> implements SectionIndexer{
    private List<Season> allseasons;
    private FragmentActivity fragmentActivity;
    private ArrayList<Integer> mSectionPositions;

    public ToWatchAdapter(List<Season> seasons, FragmentActivity activity) {
        allseasons = seasons;
        fragmentActivity = activity;
    }

    @Override
    public ToWatchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.towatch, parent, false);
        ToWatchAdapter.ViewHolder viewHolder = new ToWatchAdapter.ViewHolder(v);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final ToWatchAdapter.ViewHolder holder, final int position) {
        final Season s = allseasons.get(position);
        holder.title.setText(s.getName());
        holder.watched.setChecked(s.isWatched());
        holder.progressBar.setMax(s.getEps());
        holder.progressBar.setProgress(s.getEpsw());
        holder.watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s.isWatched()){
                    s.setWatched(false);
                    s.runInfo();
                    MainActivity.getInstance().getDbHelper().removeSeason(s.getName());
                    MainActivity.getInstance().getDbHelper().addSeasonToWatch(s.getName(),s.getGerne(),s.getDescription(),s.getEps());
                }else{
                    MainActivity.getInstance().getDbHelper().removeSeasonToWatch(s.getName());
                    MainActivity.getInstance().getDbHelper().addSeason(s.getName());
                    s.setWatched(true);
                }
            }
        });
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                s.run();
                holder.clickListenerCard.recyclerViewListClicked(v,position);
            }
        });

    }


    @Override
    public int getItemCount() {
        return allseasons.size();
    }

    @Override
    public Object[] getSections() {
        List<String> sections = new ArrayList<>(26);
        mSectionPositions = new ArrayList<>(26);
        for (int i = 0, size = allseasons.size(); i < size; i++) {
            String section = String.valueOf(allseasons.get(i).getName().charAt(0)).toUpperCase();
            if (!sections.contains(section)) {
                sections.add(section);
                mSectionPositions.add(i);
            }
        }
        return sections.toArray(new String[0]);
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        if(mSectionPositions.size() == 0) return 0;
        return mSectionPositions.get(sectionIndex);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView title;
        CheckBox watched;
        ClickListenerCard clickListenerCard;
        ProgressBar progressBar;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.towatch_card);
            title = (TextView)itemView.findViewById(R.id.towatch_title);
            watched = (CheckBox) itemView.findViewById(R.id.towatch_watched);
            progressBar = (ProgressBar) itemView.findViewById(R.id.towatch_seasoncomplete);
            clickListenerCard = new ClickListenerCard();

        }
    }
    public class ClickListenerCard implements ClickListenerOnPos{

        @Override
        public void recyclerViewListClicked(View v, int position) {
            ProgressDialog dialog = new ProgressDialog(MainActivity.getInstance());
            dialog.setCancelable(false);
            dialog.setProgressStyle(R.style.Widget_AppCompat_ProgressBar);
            dialog.setMessage(MainActivity.getInstance().getString(R.string.Episods_wait));
            dialog.show();
            Intent myIntent = new Intent(MainActivity.getInstance(),Episodes.class);
            Season s = allseasons.get(position);
            s.runNow(dialog,myIntent);
            myIntent.putExtra("season",s);
        }
    }

    public interface ClickListenerOnPos  {
        public void recyclerViewListClicked(View v, int position);
    }
}
