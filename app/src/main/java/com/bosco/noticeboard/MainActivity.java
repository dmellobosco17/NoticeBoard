package com.bosco.noticeboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static final String TAG = "MainActivity";
    NoticeAdapter NA;
    RecyclerView RV;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver newNoticeBroadcastReceiver;
    DBHelper db;
    List<Notice> notices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DBHelper(this);
        NoticeBoardPreferences.initResources(this);

        notices = new ArrayList<Notice>();
        NetworkHandler.context = getApplicationContext();

        //TODO add menu items
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //coordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(NoticeBoardPreferences.SENT_TOKEN_TO_SERVER, false);
                if (sentToken) {
                    Bundle b = intent.getBundleExtra("data");
                    String result = b.getString("result");
                    //Snackbar.make(coordinatorLayout,result,Snackbar.LENGTH_LONG).show();
                } else {

                }
            }
        };

        final LinearLayoutManager llm = new LinearLayoutManager(this.getBaseContext());
        newNoticeBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                notices = db.getAllNotices();
                NA = new NoticeAdapter(notices);
                RV = (RecyclerView) findViewById(R.id.notice_list);
                RV.setLayoutManager(llm);

                RV.setAdapter(NA);
                RV.setVisibility(View.VISIBLE);
                Log.d(TAG,"New Notice Arrived!!!");
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
            Log.d(TAG, "Register");
        }

        notices = db.getAllNotices();

        NA = new NoticeAdapter(notices);
        RV = (RecyclerView) findViewById(R.id.notice_list);
        RV.setLayoutManager(llm);

        RV.setAdapter(NA);
        RV.setVisibility(View.VISIBLE);

        //If it's the first time app is starting then we must refresh the db
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean("firstRun",true) || db.numberOfChannels() <= 1){
            SyncDatabase sync = new SyncDatabase(this,true);
            sync.execute();
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("firstRun",false).apply();
        }else {
            SyncChannels syncChannels = new SyncChannels(this,true);
            syncChannels.execute();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //TODO handle action buttons
        switch (id) {
            case R.id.action_refresh:
                SyncDatabase sync = new SyncDatabase(this,false);
                sync.execute();
                break;
            case R.id.action_settings:
                Intent i = new Intent(this,SettingsActivity.class);
                startActivityForResult(i, 1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_camara) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(NoticeBoardPreferences.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(newNoticeBroadcastReceiver,
                new IntentFilter(NoticeBoardPreferences.NEW_NOTICE_ARRIVED));
        Log.d(TAG, "Resume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(newNoticeBroadcastReceiver);
        Log.d(TAG, "Pause");
        db.close();
    }

    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    //TODO enable transition effect
    public void onNoticeSelect(View view) {
        //Retrieve notice object
        Notice note = (Notice) view.getTag();

        Log.d(TAG, "Selected notice : " + note.toString());
        Intent i = new Intent(this, NoticeActivity.class);
        i.putExtra("Notice", note);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 1 && resultCode == 2){
            SyncDatabase sync = new SyncDatabase(this,false);
            sync.execute();
        }
        Log.d(TAG,"Request : "+requestCode+" Result : "+resultCode);
    }
}
