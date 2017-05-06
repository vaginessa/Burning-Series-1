package to.bs.bruningseriesmeterial.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TabHost;

import to.bs.bruningseriesmeterial.MainActivity;
import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.fragments.tab.NewEpisodes;

public class AppDashboard extends Fragment {

    private TabHost tab;

    public AppDashboard() {
    }

    public static AppDashboard newInstance() {
        AppDashboard fragment = new AppDashboard();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final FrameLayout layout = (FrameLayout) v.findViewById(R.id.realtabcontent);
        final Fragment[] fragment = {null};
        tab = (TabHost )v.findViewById(R.id.dash_host);
        tab.setup();
        fragment[0] = NewEpisodes.newInstance("https://bs.to/serie-alphabet");
        Intent updatesIntent = new Intent(getContext(),NewEpisodes.class);
        //tab.addTab(tab.newTabSpec("NPS").setIndicator(MainActivity.getInstance().getText(R.string.new_ep)).setContent(updatesIntent));
        //tab.setCurrentTabByTag("NPS");
        tab.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                if(tabId.equalsIgnoreCase("NPS")){
                    fragment[0] = NewEpisodes.newInstance("https://bs.to/serie-alphabet");
                    layout.removeAllViews();
                    layout.addView(fragment[0].getView());
                }
            }
        });
        return v;
    }

}
