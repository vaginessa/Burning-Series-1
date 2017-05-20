package to.bs.bruningseriesmeterial.adapter;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.fragments.Episods;

/**
 * Created by Phillipp on 10.04.2017.
 */

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.ViewHolder> implements SectionIndexer{
    private List<Season> allseasons;
    private FragmentActivity fragmentActivity;
    private ArrayList<Integer> mSectionPositions;

    public SeasonAdapter(List<Season> seasons, FragmentActivity activity) {
        allseasons = seasons;
        fragmentActivity = activity;
    }

    @Override
    public SeasonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.season, parent, false);
        SeasonAdapter.ViewHolder viewHolder = new SeasonAdapter.ViewHolder(v);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(final SeasonAdapter.ViewHolder holder, final int position) {
        final Season s = allseasons.get(position);

        holder.title.setText(s.getName());
        holder.ToWatch.setChecked(s.isToWatch());
        holder.ToWatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s.isToWatch()){
                    s.setToWatch(false);
                    MainActivity.getInstance().getDbHelper().removeSeasonToWatch(s.getName());
                }else{
                    s.runInfo();
                    MainActivity.getInstance().getDbHelper().addSeasonToWatch(s.getName(),s.getGerne(),s.getDescription(),s.getEps());
                    s.setToWatch(true);
                }
            }
        });
        holder.watched.setChecked(s.isWatched());
        holder.watched.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(s.isWatched()){
                    s.setWatched(false);
                    MainActivity.getInstance().getDbHelper().removeSeason(s.getName());
                }else{
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
        CheckBox ToWatch;
        ClickListenerCard clickListenerCard;

        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.season_card);
            title = (TextView)itemView.findViewById(R.id.towatch_title);
            watched = (CheckBox) itemView.findViewById(R.id.season_watched);
            ToWatch = (CheckBox) itemView.findViewById(R.id.season_to_watch);
            clickListenerCard = new ClickListenerCard();

        }
    }
    public class ClickListenerCard implements ClickListenerOnPos{

        @Override
        public void recyclerViewListClicked(View v, int position) {
            Episods fragment = Episods.newInstance(allseasons.get(position));
            FragmentManager fragmentManager = fragmentActivity.getSupportFragmentManager();
            fragmentManager.beginTransaction().setCustomAnimations(R.anim.slide_to_left,R.anim.slide_to_right,R.anim.slide_to_left,R.anim.slide_to_right).replace(R.id.flContent, fragment,"EP").addToBackStack("S").commit();
        }
    }

    public interface ClickListenerOnPos  {
        public void recyclerViewListClicked(View v, int position);
    }
}
