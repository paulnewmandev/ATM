package com.ap.atm.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.SessionUtils;

import me.alexrs.prefs.lib.Prefs;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    private Context mContext;
    private CardView mCardSignal;
    private CardView mCardSemaforo;
    private CardView mCardConsultas;
    private CardView mCardOrdenes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = MainActivity.this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View v = navigationView.getHeaderView(0);
        TextView mNameUser = v.findViewById(R.id.headerUserName);
        TextView mCharName = v.findViewById(R.id.headerCharName);
        mNameUser.setText(SessionUtils.getUser(mContext).name);
        mCharName.setText(SessionUtils.getUser(mContext).name.substring(0,1));
        initViews();

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
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_exit) {
            dialogExit();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // Handle the camera action
        } else if (id == R.id.nav_signal) {
            startActivity(new Intent(MainActivity.this, SignalsActivity.class));
        } else if (id == R.id.nav_semaforo) {
            startActivity(new Intent(MainActivity.this, SemaphoresActivity.class));
        } else if (id == R.id.nav_orders) {

        } else if (id == R.id.nav_exit) {
            dialogExit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initViews(){
        mCardSignal = findViewById(R.id.mCardSignal);
        mCardSemaforo = findViewById(R.id.mCardSemaforo);
        mCardConsultas = findViewById(R.id.mCardConsultas);
        mCardOrdenes = findViewById(R.id.mCardOrdenes);
        initActivity();
    }

    private void initActivity(){
        mCardSignal.setOnClickListener(this);
        mCardSemaforo.setOnClickListener(this);
        mCardConsultas.setOnClickListener(this);
        mCardOrdenes.setOnClickListener(this);

        ApiUtils.getDatas(mContext);
    }

    private void dialogExit(){
        final MaterialDialog mDialog = DialogUtils.showDialogAcceptCancel(mContext, getString(R.string.confirm_title), getString(R.string.confirm_loguot));
        mDialog.getActionButton(DialogAction.POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog.dismiss();
                logOut();
            }
        });
    }

    private void logOut(){
        Prefs.with(mContext).removeAll();
        startActivity(new Intent(mContext, LoginActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mCardSignal:
                startActivity(new Intent(MainActivity.this, SignalsActivity.class));
                break;
            case R.id.mCardSemaforo:
                startActivity(new Intent(MainActivity.this, SemaphoresActivity.class));
                break;
            case R.id.mCardConsultas:

                break;
            case R.id.mCardOrdenes:

                break;
        }
    }
}
