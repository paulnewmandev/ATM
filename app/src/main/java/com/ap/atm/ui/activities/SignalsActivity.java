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
import com.ap.atm.models.SignalModel;
import com.ap.atm.ui.adapters.SignalsAdapter;
import com.ap.atm.utils.ApiUtils;
import com.ap.atm.utils.DialogUtils;
import com.ap.atm.utils.SessionUtils;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.BaseJsonHttpResponseHandler;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import me.alexrs.prefs.lib.Prefs;

public class SignalsActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private SignalsAdapter mAdapter;
    private List<SignalModel> mListData = new ArrayList<>();
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signals);
        getSupportActionBar().setTitle("SEÑALES VERTICALES");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mContext = this;
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
                startActivityForResult(new Intent(mContext, AddPortraitSignalActivity.class), SessionUtils.CREATE_NEW_ELEMENT);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        getSignals();
    }

    private void initViews(){
        mRecycler = findViewById(R.id.mRecyclerSignals);
        initActivity();
    }

    private void initActivity(){
        if(!Prefs.with(mContext).getString(SessionUtils.prefs.signals.name(), "").isEmpty()){
            showData();
        }
        getSignals();
    }

    private void getSignals(){
        final MaterialDialog mDialog = DialogUtils.showProgress(mContext, getString(R.string.title_get_data), getString(R.string.content_get_data));
        if(!mListData.isEmpty()){
            mDialog.dismiss();
        }
        String mUrl = ApiUtils.API_URL+ApiUtils.GET_SIGNALS_ALL;
        new AsyncHttpClient().get(mUrl, new BaseJsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, String rawJsonResponse, Object response) {
                mDialog.dismiss();
                if(!Prefs.with(mContext).getString(SessionUtils.prefs.signals.name(), "").contentEquals(rawJsonResponse)){
                    Prefs.with(mContext).save(SessionUtils.prefs.signals.name(), rawJsonResponse);
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
        mListData = SessionUtils.getSignals(mContext);
        if(mListData != null){
            mAdapter = new SignalsAdapter(mContext, mListData);
            LinearLayoutManager llm = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            mRecycler.setLayoutManager(llm);
            mRecycler.setAdapter(mAdapter);
        }
    }
}
