package com.support.android.designlibdemo.model.Kontakt;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Kontaktliste {

    private ArrayList<Kontakt> mListe;
    private KontaktlisteGeaendertListener mListeGeaendertListener = null;

    public Kontaktliste(ArrayList<Kontakt> lst) {
        super();
        mListe = lst;
    }


    public Kontaktliste() {
        super();
        mListe = new ArrayList<Kontakt>();
    }


    public void add(Kontakt a) {
        mListe.add(a);
        OnKontaktAdded(a.getPERSONNR());
    }


    public void add(JSONArray orders) {
        Gson gson = new Gson();
        Kontakt kontakt;
        try {
            for (int i = 0; i<=orders.length(); i++) {
                kontakt = gson.fromJson(orders.getJSONObject(i).toString(), Kontakt.class);
                //kontakt.setObjectString(orders.getJSONObject(i).toString());
                this.add(kontakt);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void add(JSONObject json){
        try {
            this.add(json.getJSONArray("orders"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public ArrayList<Kontakt> getList() {

        return mListe;
    }


    private void OnKontaktAdded(String s) {
        if (mListeGeaendertListener!=null) {
            mListeGeaendertListener.onKontaktlisteGeaendert(s);
            }
    }


    public void setKontaktlisteGeaendertListener(KontaktlisteGeaendertListener lsn) {
        mListeGeaendertListener = lsn;
    }


    public void Clear() {
        mListe.clear();
        OnKontaktAdded("");
    }
}
