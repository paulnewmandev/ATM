package com.ap.atm.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ap.atm.R;
import com.ap.atm.models.SemaphoreModel;
import com.ap.atm.ui.adapters.SemaphoresAdapter;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.SessionUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

public class SemaphoresActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private SemaphoresAdapter mAdapter;
    private Context mContext;
    private List<SemaphoreModel> mList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_semaphores);
        getSupportActionBar().setTitle("SEMAFOROS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initViews();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_element, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_add_element:
                startActivityForResult(new Intent(mContext, AddSemaphoreActivity.class), SessionUtils.CREATE_NEW_ELEMENT);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        getSemaphores();
    }

    private void initViews(){
        mRecycler = findViewById(R.id.mRecyclerSemaphores);
        mContext = SemaphoresActivity.this;
        initActivity();
    }

    private void initActivity(){
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.semaphores.name(), "").isEmpty()){
            showData();
        }
        getSemaphores();
    }

    private void getSemaphores(){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data), getString(R.string.content_get_data));
        if(!mList.isEmpty()){
            mDialog.dismiss();
        }
        String mUrl = ApiUtils.API_URL+ApiUtils.GET_SEMAFOROS_ALL;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                if(!Prefs.with(mContext).getString(SessionUtils.prefs.semaphores.name(), "").contentEquals(rawJsonResponse)){
                    Prefs.with(mContext).save(SessionUtils.prefs.semaphores.name(), rawJsonResponse);
                    showData();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, String rawJsonData, Object errorResponse) {
                mDialog.dismiss();
                Toast.makeText(mContext, getString(R.string.error_server), Toast.LENGTH_LONG).show();
            }

            @Override
            protected Object parseResponse(String rawJsonData, boolean isFailure) throws Throwable {
                return null;
            }
        });
    }

    private void showData(){
        mList = SessionUtils.getSemaphores(mContext);
        if(mList != null){
            mAdapter = new SemaphoresAdapter(mContext, mList);
            LinearLayoutManager llm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecycler.setLayoutManager(llm);
            mRecycler.setAdapter(mAdapter);
        }
    }
}
