package to.bs.bruningseriesmeterial;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permissions;
import java.security.acl.Permission;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.jar.Manifest;

import to.bs.bruningseriesmeterial.database.SeasonDbHelper;
import to.bs.bruningseriesmeterial.fragments.ChangeLog;
import to.bs.bruningseriesmeterial.fragments.Seasons;
import to.bs.bruningseriesmeterial.fragments.ToWatch;
import to.bs.bruningseriesmeterial.service.HolidayManager;

public class MainActivity extends AppCompatActivity {
    private static MainActivity instance;
    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<String> userAgents;
    private SeasonDbHelper dbHelper;

    final String PREFS_NAME = "MyPrefsFile";
    final String PREFS_NAME_FIRST_RUN = "FirstRun";

    boolean doubleBackToExitPressedOnce = false;
    private void runTour(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean(PREFS_NAME_FIRST_RUN, true)) {

            //settings.edit().putBoolean(PREFS_NAME_FIRST_RUN, false).commit();
        }
    }

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
        //dbHelper.onUpgrade(dbHelper.getWritableDatabase(),1,1);

        userAgents = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();

        mDrawer.addDrawerListener(drawerToggle);
        nvDrawer = (NavigationView) findViewById(R.id.nvView);

        setupDrawerContent(nvDrawer);
        runTour();
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent startServiceIntent = new Intent(this, HolidayManager.class);
        PendingIntent startServicePendingIntent = PendingIntent.getService(this,0,startServiceIntent,0);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() + 1000*60);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), 1000*60, startServicePendingIntent);
        openDialogFragment(ChangeLog.newInstance());


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
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawer.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        MenuItem item = null;
        for (int i = 0; i < nvDrawer.getMenu().getItem(0).getSubMenu().size(); i++) {
            item = (MenuItem) nvDrawer.getMenu().getItem(0).getSubMenu().getItem(i);
            item.setChecked(false);
            if (item.getItemId() == R.id.menu_nav_season) {
                Fragment fragment = Seasons.newInstance("https://bs.to/serie-alphabet", item);
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.flContent, fragment, "S").commit();
                item.setChecked(true);
                setTitle(item.getTitle());
            }
        }


        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
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

    public void selectDrawerItem(MenuItem menuItem) {
        for (int i = 0; i < nvDrawer.getMenu().size(); i++) {
            MenuItem item = (MenuItem) nvDrawer.getMenu().getItem(i);
            if (item.hasSubMenu()) {
                for (int j = 0; j < nvDrawer.getMenu().getItem(i).getSubMenu().size(); j++) {
                    item = (MenuItem) nvDrawer.getMenu().getItem(i).getSubMenu().getItem(j);
                    item.setChecked(false);
                }
            }

            item.setChecked(false);
        }
        Fragment fragment = null;
        switch(menuItem.getItemId()) {
            case R.id.menu_nav_season:
                fragment = Seasons.newInstance("https://bs.to/serie-alphabet",menuItem);
                break;
            case R.id.menu_nav_towtach:
                fragment = ToWatch.newInstance("https://bs.to/serie-alphabet",menuItem);
                break;
            case R.id.menu_nav_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return;
            default:
                fragment = Seasons.newInstance("https://bs.to/serie-alphabet", menuItem);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment,"S").commit();
        menuItem.setChecked(true);

        setTitle(menuItem.getTitle());
        mDrawer.closeDrawers();
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


    public NavigationView getNvDrawer() {
        return nvDrawer;
    }
}

