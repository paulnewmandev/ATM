package com.ap.atm.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ap.atm.R;
import com.ap.atm.utils.SessionUtils;

import me.alexrs.prefs.lib.Prefs;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initActivity();
    }

    private void initActivity(){
        int SPLASH_TIEMPO = 2500;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(Prefs.with(getApplicationContext()).getString(SessionUtils.prefs.user_data.name(), "").isEmpty()){
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                }
            }
        }, SPLASH_TIEMPO);
    }
}
