package com.support.android.designlibdemo.ui.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.support.android.designlibdemo.AppController;
import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.model.Auftrag.Auftrag;
import com.support.android.designlibdemo.model.Auftrag.Auftragsliste;
import com.support.android.designlibdemo.ui.OrderDetailsActivity;
import com.support.android.designlibdemo.ui.adapter.OrderListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressLint("ValidFragment")
public class OrderListFragment extends Fragment {

    private Context mContext;
    private Auftragsliste mAuftragsliste = new Auftragsliste();
    private OrderListAdapter mAdapter = null;
    private int mSearchRequestCounter = 0;      // Zaehler fuer die http-Anfragen
    private int mSearchResults = 0;             // Zaehler Suchergebnis gesamt
    private String mSearchString;
    private ListView listView;
    private ProgressBar progressBar;
    private AppController mAppController;

/*
    public OrderListFragment(Context c) {
        mContext = c;
    }
*/

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            //TODO: Restore the fragment's state here
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //TODO: Save the fragment's state here
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mAppController = AppController.getInstance();
        mContext =  getActivity();
        View view = inflater.inflate(R.layout.fragment_orderlist, container, false);
        listView = (ListView) view.findViewById(R.id.listview);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);

        mSearchString = getArguments().getString("search", null); // evtl. uebergebene SUCH-Parameter ermitteln
        if (mSearchString != null) doSearch(mSearchString);

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(AppController.VOLLEY_PATTERNS);
    }

    private class DialogBox extends Builder {

        private Context context;


        public DialogBox(Context c) {
            super(c);
            context = c;
        }


        public DialogBox(Context context, String title, String message) {
            super(context);
            this.context = context;
            setMessage(message);
            setTitle(title);
            setCancelable(false);
            setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss(); // It's just for info so we don't really care what this does
                }
            });
        }
    }

    private void doSearch(String search) {

        if (search.length() < 4) {
            Toast.makeText(mContext, "Mindestens 5 Zeichen eingeben", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Suche '" + search + "'", Toast.LENGTH_SHORT).show();

            //Adapter erzeugen und Listview zuweisen
            mAdapter = new OrderListAdapter(mContext, mAuftragsliste.getList());
            listView.setAdapter(mAdapter);

            progressBar.setVisibility(View.VISIBLE);  // Fortschrittsanzeige anzeigen

            // Bei Auswahl eines Listeneintrags neue Activity starten
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // This will tell to Volley to cancel all the pending requests
                    mAppController.cancelPendingRequests(AppController.VOLLEY_PATTERNS);
                    progressBar.setVisibility(View.GONE);  // Fortschrittsanzeige ausblenden

                    Auftrag auftrag = (Auftrag) parent.getItemAtPosition(position);
                    //TODO: speichern des Auftrags in letzte Vorgänge

                    Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                    //intent.putExtra("auftrag",auftrag); // Version 1
                    intent.putExtra("anr",auftrag.getANR()); // Version 2
                    //NavUtils.navigateUpFromSameTask(intent);
                    startActivity(intent);
                }
            });
            // start http requests
            mSearchRequestCounter = 0;
            callAPIOrdersByANR("http://222.222.222.60/api/orders/anr?where=" + search);
            //Auftrag auftrag = new Auftrag();
            //auftrag.loadOrderDataByANR(mContext,"http://222.222.222.60/api/orders/anr?where=" + search);
            callAPIOrdersByMNR("http://222.222.222.60/api/orders/mnr/" + search);
            callAPIOrdersByKTXT("http://222.222.222.60/api/orders/ktxt?where=" + search + "&fields=anr,mnr,ktxt,bemerkung,komm,kw,kj");
        }
    }

    private void callAPIOrdersByANR(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        JsonObjectRequest req = new JsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    mAuftragsliste.add(orders);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        Toast.makeText(mContext, orders.length() + " Einträge über ANR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    VolleyLog.e("Error: ", e.getMessage());
                    Toast.makeText(mContext, e.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                //DialogBox dialogBox = new DialogBox(mContext, "Fehler", error.getMessage());
                //dialogBox.show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
            }
        });
        //req.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 2));
        mAppController.addToRequestQueue(req);
    }

    private void callAPIOrdersByMNR(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        JsonObjectRequest req = new JsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    //Log.d("JSON",response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    mAuftragsliste.add(orders);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        Toast.makeText(mContext, orders.length() + " Einträge über MNR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
            }
        });
        //req.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 2));
        mAppController.addToRequestQueue(req);
    }

    private void callAPIOrdersByKTXT(String search) {

        // Increase Counter for Progressbar
        mSearchRequestCounter++;

        JsonObjectRequest req = new JsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    //Log.d("JSON",response.toString(4));
                    JSONArray orders = response.getJSONArray("orders");
                    mAuftragsliste.add(orders);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        Toast.makeText(mContext, orders.length() + " Einträge über KTXT gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Toast.makeText(mContext, error.toString(), Toast.LENGTH_SHORT).show();
                mSearchRequestCounter--;
                if (mSearchRequestCounter < 1) progressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
            }
        });
        //req.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 3));
        req.setRetryPolicy(new DefaultRetryPolicy(3000, 1, 2));
        mAppController.addToRequestQueue(req);
    }
}


