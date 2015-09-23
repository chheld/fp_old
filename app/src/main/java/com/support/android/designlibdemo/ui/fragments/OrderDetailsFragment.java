package com.support.android.designlibdemo.ui.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.support.android.designlibdemo.AppController;
import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.model.Adresse.Adresse;
import com.support.android.designlibdemo.model.Auftrag.Auftrag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public  class OrderDetailsFragment extends Fragment {

    private AppController mAppController = AppController.getInstance();
    private Auftrag mAuftrag;

    private Context mContext;
    private TextView tvVertreterName;
    private ProgressBar pbVertreter;
    private TextView tvLieferadresse;
    private ProgressBar pbLieferadresse;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mContext=getActivity();

        View view = inflater.inflate(R.layout.fragment_orderdetails, container, false);

        // Auftragsdaten anzeigen
        TextView tvANr = (TextView) view.findViewById(R.id.tvANr);
        TextView tvBemerkung = (TextView) view.findViewById(R.id.tvBemerkung);
        TextView tvBestellnummer = (TextView) view.findViewById(R.id.tvBestellnummer);
        TextView tvKommission = (TextView) view.findViewById(R.id.tvKommission);

        pbVertreter = (ProgressBar) view.findViewById(R.id.progressBarVertreter);
        TextView tvVertr1 = (TextView) view.findViewById(R.id.tvVertreter1);
        tvVertreterName = (TextView) view.findViewById(R.id.tvVertreterName);

        // Status anzeigen
        TextView tvStatus2 = (TextView) view.findViewById(R.id.tvStatus2);
        TextView tvzDesc = (TextView) view.findViewById(R.id.tvZDesc);
        TextView tvSpezifizierung = (TextView) view.findViewById(R.id.tvSpezifizierung);

        // Termine anzeigen
        TextView tvKdWunschTermin = (TextView) view.findViewById(R.id.tvKdWunschTermin);
        TextView tvKdBestTermin = (TextView) view.findViewById(R.id.tvKdBestTermin);
        TextView tvProdPlanTermin = (TextView) view.findViewById(R.id.tvProdPlanTermin);
        TextView tvProdDispTermin = (TextView) view.findViewById(R.id.tvProdDispTermin);

        // Kundendaten anzeigen
        TextView tvKdNr = (TextView) view.findViewById(R.id.tvKdNr);
        TextView tvKTxt = (TextView) view.findViewById(R.id.tvKTxt);
        TextView tvKW = (TextView) view.findViewById(R.id.tvKW);
        TextView tvKJ = (TextView) view.findViewById(R.id.tvKJ);

        // Lieferung anzeigen
        TextView tvLieferadresseNr = (TextView) view.findViewById(R.id.tvLieferAdresseNr);
        pbLieferadresse = (ProgressBar) view.findViewById(R.id.progressBarLieferadresse);
        tvLieferadresse = (TextView) view.findViewById(R.id.tvLieferAdresse);

        if (getArguments() != null) mAuftrag = getArguments().getParcelable("auftrag");

        if (mAuftrag!=null) {

            // Auftragsdaten anzeigen
            tvANr.setText(mAuftrag.getANR());
            tvBemerkung.setText(mAuftrag.getBEMERKUNG());
            if(tvBemerkung.getText().toString().trim().length()==0)  tvBemerkung.setVisibility(View.GONE);
            tvBestellnummer.setText(mAuftrag.getBELEGNRBEST());
            if(tvBestellnummer.getText().toString().trim().length()==0) {
                tvBestellnummer.setVisibility(View.GONE);
            } else
            {
                tvBestellnummer.setText("Bestellung: " + mAuftrag.getBELEGNRBEST());
            }
            tvKommission.setText(mAuftrag.getKOMM());
            if(tvKommission.getText().toString().trim().length()==0) {
                tvKommission.setVisibility(View.GONE);
            } else
            {
                tvKommission.setText("Kommission: " + mAuftrag.getKOMM());
            }

            // Vertreter anzeigen
            tvVertr1.setText(mAuftrag.getVERTRETER1());
            tvVertreterName.setText("Lädt ...");
            if(tvVertr1.getText().toString().trim().length()==0) {
                tvVertr1.setVisibility(View.GONE);
            }
            else
            {
                RelativeLayout ly = (RelativeLayout)  view.findViewById(R.id.container_vertreter);

                ly.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callAPIContactByPersonNr("http://222.222.222.60/api/contact/personnr?where=" + mAuftrag.getVERTRETER1());
                    }
                });

                callAPIContactByPersonNr("http://222.222.222.60/api/contact/personnr?where=" + mAuftrag.getVERTRETER1());
            }

            // Status anzeigen
            tvzDesc.setText(mAuftrag.getZDESC());
            if(tvzDesc.getText().toString().trim().length()==0)  tvzDesc.setVisibility(View.GONE);
            tvStatus2.setText(mAuftrag.getSTATUS2());
            if(tvStatus2.getText().toString().trim().length()==0)  tvStatus2.setVisibility(View.GONE);
            tvSpezifizierung.setText(mAuftrag.getSTATUS1());
            if(tvSpezifizierung.getText().toString().trim().length()==0)  tvSpezifizierung.setVisibility(View.GONE);

            // Termine anzeigen
            tvKdWunschTermin.setText(mAuftrag.getUSEINTREFFTERMIN()); //USEintreffTermin nicht in der REST Abfrage
            if(tvKdWunschTermin.getText().toString().trim().length()==0) {
                tvKdWunschTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblKdWunschTermin);
                lbl.setVisibility(View.GONE);
            }
            tvKdBestTermin.setText(mAuftrag.getUSEINTREFFTBEST()); // USEintreffTBest
            if(tvKdBestTermin.getText().toString().trim().length()==0) {
                tvKdBestTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblKdBestTermin);
                lbl.setVisibility(View.GONE);
            }
            tvProdPlanTermin.setText(mAuftrag.getSEGM1TERM()); // Segm1.Term
            if(tvProdPlanTermin.getText().toString().trim().length()==0) {
                tvProdPlanTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblProdPlanTermin);
                lbl.setVisibility(View.GONE);
            }
            tvProdDispTermin.setText(mAuftrag.getSEGM2TERM()); // Segm2.Term
            if(tvProdDispTermin.getText().toString().trim().length()==0) {
                tvProdDispTermin.setVisibility(View.GONE);
                TextView lbl = (TextView) view.findViewById(R.id.lblProdDispTermin);
                lbl.setVisibility(View.GONE);
            }

            // Kundendaten anzeigen
            tvKdNr.setText(mAuftrag.getMNR());
            tvKTxt.setText(mAuftrag.getKTXT());
            if(tvKTxt.getText().toString().trim().length()==0)  tvKTxt.setVisibility(View.GONE);
            tvKW.setText(Integer.toString(mAuftrag.getKW()));
            tvKJ.setText(Integer.toString(mAuftrag.getKJ()));

            // Lieferung anzeigen
            tvLieferadresseNr.setText(mAuftrag.getADRNR2());
            tvLieferadresse.setText("Lädt ...");
            if(tvLieferadresseNr.getText().toString().trim().length()==0) {
                tvLieferadresseNr.setVisibility(View.GONE);
            }
            else
            {
                RelativeLayout ly = (RelativeLayout)  view.findViewById(R.id.container_lieferadresse);

                ly.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callAPIAdresseByAdresseNr("http://222.222.222.60/api/adresse/adressenr?where=" + mAuftrag.getADRNR2());
                    }
                });

                callAPIAdresseByAdresseNr("http://222.222.222.60/api/adresse/adressenr?where=" + mAuftrag.getADRNR2());
            }

        }
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(false);
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(AppController.VOLLEY_PATTERNS);
    }

    private void callAPIContactByPersonNr(String search) {

        pbVertreter.setVisibility(View.VISIBLE);

        JsonObjectRequest req = new JsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contact = response.getJSONArray("contact");
                    JSONObject jsonA = contact.getJSONObject(0);
                        String vname = jsonA.getString("VORNAME");
                        if (vname.equals("null")) vname = "";
                        String nname = jsonA.getString("NAME");
                        if (nname.equals("null")) nname = "";
                        tvVertreterName.setText(vname + " " + nname);
                        pbVertreter.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    VolleyLog.e("Error: ", e.getMessage());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    pbVertreter.setVisibility(View.GONE);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                //if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                pbVertreter.setVisibility(View.GONE);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        AppController.getInstance().addToRequestQueue(req);
    }

    private void callAPIAdresseByAdresseNr(String search) {

        pbLieferadresse.setVisibility(View.VISIBLE);

        JsonObjectRequest req = new JsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray address = response.getJSONArray("address");
                    Gson gson = new Gson();
                    Adresse adresse = gson.fromJson(address.getJSONObject(0).toString(), Adresse.class);
                    String adr1 = adresse.getZUSATZ1() + "\n";
                    String adr2 = adresse.getZUSATZ2() + "\n";
                    String str = adresse.getSTRASSE() + "\n";
                    String plz = adresse.getPLZORT() + " ";
                    String ort = adresse.getORT();
                    tvLieferadresse.setText(adr1 + adr2 + str + plz + ort);

                    pbLieferadresse.setVisibility(View.GONE);

                } catch (JSONException e) {

                    e.printStackTrace();
                    VolleyLog.e("Error: ", e.getMessage());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    pbLieferadresse.setVisibility(View.GONE);

                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                pbLieferadresse.setVisibility(View.GONE);
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        AppController.getInstance().addToRequestQueue(req);
    }
}