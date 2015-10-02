package com.support.android.designlibdemo.rest;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;

import com.support.android.designlibdemo.AppController;

public class VolleyActivity extends AppCompatActivity {

    private AppController mAppController;
    private Context mContext;

    private final String VOLLEY_TAG = "VOLLEY_TAG";

    public VolleyActivity() {
        super();
        mAppController = AppController.getInstance();
        mContext=this;
    }

    @Override
    public void onStop() {
        super.onStop();
        mAppController.cancelPendingRequests(VOLLEY_TAG);
    }
}
