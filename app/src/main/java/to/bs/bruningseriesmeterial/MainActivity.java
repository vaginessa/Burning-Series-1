package to.bs.bruningseriesmeterial;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.SearchEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;


import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

import to.bs.bruningseriesmeterial.database.SeasonDbHelper;
import to.bs.bruningseriesmeterial.fragments.Seasons;
import to.bs.bruningseriesmeterial.fragments.ToWatch;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        checkCallingOrSelfPermission("android.permission.RECORD_AUDIO");
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

    }
    private void runTour(){
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean(PREFS_NAME_FIRST_RUN, true)) {

            //settings.edit().putBoolean(PREFS_NAME_FIRST_RUN, false).commit();
        }
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
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



    public void selectDrawerItem(MenuItem menuItem) {
        for (int i = 0; i < nvDrawer.getMenu().size(); i++) {
            MenuItem item = (MenuItem) nvDrawer.getMenu().getItem(i);
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


    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
    public static void hideKeyboard(Activity activity)
    {
        try
        {
            InputMethodManager inputManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        catch (Exception e)
        {
            // Ignore exceptions if any
            Log.e("KeyBoardUtil", e.toString(), e);
        }
    }
}

