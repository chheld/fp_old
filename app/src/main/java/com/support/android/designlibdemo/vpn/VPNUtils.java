package com.support.android.designlibdemo.vpn;

import android.content.Context;
import android.widget.Toast;

class VPNUtils {

    public static void makeToast(Context context, String text){
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
    }
}