package com.support.android.designlibdemo.ui.fragments;

import android.support.v4.app.Fragment;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.support.android.designlibdemo.R;

public  class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_about, container, false);

        // evtl. übergebene Parameter ermitteln
        //Integer pos = getArguments().getInt("position");

        //Menu anzeigen

        // Versions-Information aus dem Package auslesen
        String versionName = "xxx";
        String packageName = "xxx";
        try {
            PackageInfo packageInfo = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0);
            ApplicationInfo appInfo = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(), PackageManager.GET_META_DATA);
            versionName = packageInfo.versionName;
            packageName = appInfo.packageName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        TextView txtName = (TextView) view.findViewById(R.id.txtLabel);
        txtName.setText(txtName.getText() + "\n\n" + " Name:\n" + packageName + "\n\n" + " Version:\n" + versionName);

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(false);


    }
}