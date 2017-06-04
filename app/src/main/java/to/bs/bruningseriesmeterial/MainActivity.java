package to.bs.bruningseriesmeterial;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;


import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import to.bs.bruningseriesmeterial.database.SeasonDbHelper;
import to.bs.bruningseriesmeterial.fragments.ChangeLog;
import to.bs.bruningseriesmeterial.fragments.Seasons;
import to.bs.bruningseriesmeterial.fragments.ToWatch;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private Toolbar toolbar;
    private ArrayList<String> userAgents;
    private SeasonDbHelper dbHelper;

    final String PREFS_NAME = "MyPrefsFile";
    final String PREFS_NAME_FIRST_RUN = "FirstRun";

    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        checkCallingOrSelfPermission("android.permission.RECORD_AUDIO");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            shouldShowRequestPermissionRationale(android.Manifest.permission.RECORD_AUDIO);
            shouldShowRequestPermissionRationale(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            shouldShowRequestPermissionRationale(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new SeasonDbHelper(getInstance());
        userAgents = new ArrayList<>();
        toolbar = (Toolbar) findViewById(R.id.ep_toolbar);
        setSupportActionBar(toolbar);
        openDialogFragment(ChangeLog.newInstance());
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(R.string.menu_season));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.menu_towtach));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return Seasons.newInstance("https://bs.to/serie-alphabet");
                    case 1:
                        return ToWatch.newInstance("https://bs.to/serie-alphabet");
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

    }
    private void openDialogFragment(DialogFragment dialogStandardFragment) {
        if (dialogStandardFragment != null) {
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            Fragment prev = fm.findFragmentByTag("changelogdemo_dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            dialogStandardFragment.show(ft, "changelogdemo_dialog");
        }
    }



    public ArrayList<String> getUserAgents() {
        return userAgents;
    }

    public static MainActivity getInstance() {
        return instance;
    }

    public SeasonDbHelper getDbHelper() {
        return dbHelper;
    }

    public static void setHeader(){

        getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ImageView i = (ImageView)MainActivity.getInstance().findViewById(R.id.naviagtion_image);
                Bitmap bitmap = null;
                try {

                    bitmap = BitmapFactory.decodeStream((InputStream)new URL("https://bs.to/public/img/header.png").getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                i.setImageBitmap(bitmap);
            }
        });


    }



    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }else{
                this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, R.string.app_close, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2500);
            }
        } else {
            getSupportFragmentManager().popBackStack();
        }

    }


}

