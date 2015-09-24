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

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.support.android.designlibdemo.AppController;
import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.model.Kontakt.Kontakt;
import com.support.android.designlibdemo.model.Kontakt.Kontaktliste;
import com.support.android.designlibdemo.ui.ContactListActivity;
import com.support.android.designlibdemo.ui.adapter.ContactListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


@SuppressLint("ValidFragment")
public class ContactListFragment extends Fragment {

    private Context mContext;
    private Kontaktliste mListe;
    private ContactListAdapter mAdapter;
    private int mSearchRequestCounter;      // Zaehler fuer die http-Anfragen
    private String mSearchString;
    private ListView mListView;
    private ProgressBar mProgressBar;
    private AppController mAppController;

    public ContactListFragment(Context c) {
        mContext = c;
        mListe = new Kontaktliste();
        mAdapter = null;
        mSearchRequestCounter = 0;
        mAppController = AppController.getInstance();
    }

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

        View view = inflater.inflate(R.layout.fragment_contactlist, container, false);
        mListView = (ListView) view.findViewById(R.id.listview);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);

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

        if (search.length() < 1) {
            Toast.makeText(mContext, "Mindestens 2 Zeichen eingeben", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Suche '" + search + "'", Toast.LENGTH_SHORT).show();

            //Adapter erzeugen und Listview zuweisen
            mAdapter = new ContactListAdapter(mContext, mListe.getList());
            mListView.setAdapter(mAdapter);

            mProgressBar.setVisibility(View.VISIBLE);  // Fortschritts-Anzeige sichtbar

            // Bei Auswahl eines Listeneintrags neue Activity starten
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    // This will tell to Volley to cancel all the pending requests
                    mAppController.cancelPendingRequests(AppController.VOLLEY_PATTERNS);
                    mProgressBar.setVisibility(View.GONE);  // Fortschrittsanzeige ausblenden

                    Kontakt kontakt = (Kontakt) parent.getItemAtPosition(position);
                    //Toast.makeText(mContext, auftrag.getANR(), Toast.LENGTH_SHORT).show();
                    //TODO: speichern des Auftrags in letzte Vorgänge

                    Intent intent = new Intent(mContext, ContactListActivity.class);
                    intent.putExtra("kontakt", kontakt);
                    //NavUtils.navigateUpFromSameTask(intent);
                    startActivity(intent);
                }
            });
            // start http requests
            mSearchRequestCounter = 0;

            callAPIContactsByPersonNr("http://222.222.222.60/api/contacts/personnr?where=" + search);
            //Auftrag auftrag = new Auftrag();
            //auftrag.loadOrderDataByANR(mContext,"http://222.222.222.60/api/orders/anr?where=" + search);
            //callAPIOrdersByMNR("http://222.222.222.60/api/orders/mnr/" + search);
            //callAPIOrdersByKTXT("http://222.222.222.60/api/orders/ktxt?where=" + search + "&fields=anr,mnr,ktxt,bemerkung,komm,kw,kj");
        }
    }

    private void callAPIContactsByPersonNr(String search) {

        // Increase counter for pending search requests
        mSearchRequestCounter++;

        JsonObjectRequest req = new JsonObjectRequest(search, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:%n %s", response.toString(4));
                    JSONArray contacts = response.getJSONArray("contacts");
                    mListe.add(contacts);
                    mAdapter.notifyDataSetChanged();
                    mSearchRequestCounter--;
                    if (mSearchRequestCounter < 1) {
                        mProgressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
                        Toast.makeText(mContext, contacts.length() + " Einträge über PERSONNR gefunden", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
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
                if (mSearchRequestCounter < 1) mProgressBar.setVisibility(View.GONE);  // Fortschritt ausblenden
            }
        });
        //req.setRetryPolicy(new DefaultRetryPolicy(3000, 2, 2));
        AppController.getInstance().addToRequestQueue(req);
    }
}


