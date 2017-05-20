package to.bs.bruningseriesmeterial.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

import to.bs.bruningseriesmeterial.hosters.HostFetcher;
import to.bs.bruningseriesmeterial.R;

/**
 * Created by Phillipp on 11.04.2017.
 */

public class HostAdapter extends RecyclerView.Adapter<HostAdapter.ViewHolder>{
    private ArrayList<HostFetcher> fetchers;
    public HostAdapter(ArrayList<HostFetcher> ep) {
        fetchers = ep;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.host, parent, false);
        HostAdapter.ViewHolder viewHolder = new HostAdapter.ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        if(fetchers.get(position).getName().contains("Vivo")){
           holder.supported.setChecked(true);
        }
        else if(fetchers.get(position).getName().contains("OpenLoad")){
            holder.supported.setChecked(true);
        }
        holder.hostName.setText(fetchers.get(position).getName());
        holder.host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.clickListenerLayout.recyclerViewListClicked(v,position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fetchers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView host;
        TextView hostName;
        CheckBox supported;
        ClickListenercard clickListenerLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            host = (CardView) itemView.findViewById(R.id.host_cv);
            hostName = (TextView) itemView.findViewById(R.id.host_name);
            supported = (CheckBox) itemView.findViewById(R.id.host_supported);
            clickListenerLayout = new ClickListenercard();
        }
    }
    public class ClickListenercard implements SeasonAdapter.ClickListenerOnPos {

        @Override
        public void recyclerViewListClicked(View v, int position) {
            fetchers.get(position).connect();
        }
    }

    public interface ClickListenerOnPos  {
        public void recyclerViewListClicked(View v, int position);
    }
}
