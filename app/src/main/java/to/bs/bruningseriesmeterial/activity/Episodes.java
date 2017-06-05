package to.bs.bruningseriesmeterial.activity;

import android.app.ProgressDialog;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import to.bs.bruningseriesmeterial.R;
import to.bs.bruningseriesmeterial.Utils.Season;
import to.bs.bruningseriesmeterial.fragments.EpisodsFragment;
import to.bs.bruningseriesmeterial.fragments.SeasonInfo;

public class Episodes extends AppCompatActivity {

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_episodes);
        toolbar = (Toolbar) findViewById(R.id.ep_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        final Season season = (Season) getIntent().getSerializableExtra("season");
        setTitle(season.getName());

        final TabLayout tabLayout = (TabLayout) findViewById(R.id.ep_tab_layout);
        //tabLayout.addTab(tabLayout.newTab().setText(R.string.menu_info));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.menu_info));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.menu_episodes));

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.ep_pager);
        viewPager.setCurrentItem(1);
        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return SeasonInfo.newInstance(season);
                    case 1:
                        return EpisodsFragment.newInstance(season,position);
                }
                return null;
            }

            @Override
            public int getCount() {
                return tabLayout.getTabCount();
            }
        };
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
