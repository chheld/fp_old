package com.support.android.designlibdemo.ui;

import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.support.android.designlibdemo.AppController;
import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.provider.OrderSuggestionProvider;
import com.support.android.designlibdemo.ui.fragments.AboutFragment;
import com.support.android.designlibdemo.ui.fragments.ContactListFragment;
import com.support.android.designlibdemo.ui.fragments.HintFragment;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLConnection;

public class ContactListActivity extends AppCompatActivity {

    private MenuItem searchItem;
    private SearchRecentSuggestions suggestions;
    private SearchView searchView;
    private TextView tvHinweis;
    private AppController mAppController = AppController.getInstance();
    private Context mContext = this;

    private final String VOLLEY_PATTERNS_CONTACT_LIST = "VOLLEY_PATTERNS_CONTACT_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_contacts_toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            //getSupportActionBar().setSubtitle(null);
        }

        suggestions = new SearchRecentSuggestions(this, OrderSuggestionProvider.AUTHORITY, OrderSuggestionProvider.MODE);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = new SearchView(getSupportActionBar().getThemedContext());
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setImeOptions(EditorInfo.IME_ACTION_SEARCH); // Lupe anzeigen in der Tastatur
        //searchView.setSubmitButtonEnabled(true); //OK-Button anzeigen
        SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        // Collapse the search menu when the user hits the back key
        searchAutoComplete.setOnFocusChangeListener(new View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) showSearch(false);
            }
        });

        try {
            // This sets the cursor resource ID to 0 or @null which will make it visible on white background
            Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
            mCursorDrawableRes.setAccessible(true);
            mCursorDrawableRes.set(searchAutoComplete, 0);

        } catch (Exception e) {
        }

        checkServerConnection checkServerConnection = new checkServerConnection();
        checkServerConnection.execute("qw1");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_orders, menu);

        // Search Icon referenzieren
        searchItem = menu.findItem(R.id.itm_action_search);

        MenuItemCompat.setActionView(searchItem, searchView);
        MenuItemCompat.setShowAsAction(searchItem, MenuItemCompat.SHOW_AS_ACTION_ALWAYS | MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         int id = item.getItemId();

        switch (id) {

            case R.id.action_settings: // TODO
                return true;

            case R.id.itm_action_search: // TODO
                showSearch(true);
                return true;

            case R.id.action_clear_history: // ok
                suggestions.clearHistory();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        if (intent.getAction()== "android.intent.action.SEARCH") {

            showSearch(false); //Suchfeld schliessen

            //Suchbegriff speichern in der Vorschlagsliste
            Bundle extras = intent.getExtras();
            String userQuery = String.valueOf(extras.get(SearchManager.USER_QUERY));
            String query = String.valueOf(extras.get(SearchManager.QUERY));
            suggestions.saveRecentQuery(query, "in Kontakten");

            //Toast.makeText(this, "query: " + query + " user_query: " + userQuery, Toast.LENGTH_SHORT).show(); // TEST Meldung

            Bundle args = new Bundle(); // Uebergabe-Parameter für Fragment erstellen
            args.putString("search", query);
            getSupportActionBar().setSubtitle("Suche  '" + query + "'");

            showFragment("list", args); // Fragment OrdersList anzeigen
        }
    }

    private void showSearch(boolean visible) {

        if (visible)
            MenuItemCompat.expandActionView(searchItem);
        else
            MenuItemCompat.collapseActionView(searchItem);
    }

    /**
     * Called when the hardware BACK button is pressed
     */
    @Override
    public void onBackPressed() {
        //showSearch(false);
        super.onBackPressed();
    }

    /**
     * Called when the hardware SEARCH button is pressed
     */
    @Override
    public boolean onSearchRequested() {
        super.onSearchRequested();
        showSearch(true);
        // dont show the built-in search dialog
        return false;
    }

    private void showFragment(String key, @Nullable Bundle args) {

        tvHinweis.setVisibility(View.GONE);

        Fragment fragment = null;
        switch (key) {

            case "hint": // test
                fragment = new HintFragment();
                Bundle bundle = new Bundle();
                bundle.putString("hint","Hinweis");
                fragment.setArguments(bundle);
                break;

            case "about": // test
                fragment = new AboutFragment();
                break;

            case "list": // ok
                fragment = new ContactListFragment();
                fragment.setArguments(args);
                break;

            default:
                break;
        }
        if (fragment != null) {
            FragmentManager frgManager = getSupportFragmentManager();
            frgManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // This will tell to Volley to cancel all the pending requests
        mAppController.cancelPendingRequests(VOLLEY_PATTERNS_CONTACT_LIST);
    }

    public class checkServerConnection extends AsyncTask<String, Boolean, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String s = params[0];
            Boolean ret = false;
            //isURLReachable(); //ok
            ret  = isConnectedToServer("http://222.222.222.60", 2000);
            Log.d("isConnectedToServer", ret.toString());
            publishProgress(ret);
            return ret;
        }
        @Override
        protected void onProgressUpdate(Boolean... values) {
            Log.d("isConnectedToServer", "onProgressUpdate(): " + values[0]);
        }

        @Override
        protected void onPostExecute(Boolean res) {

            super.onPostExecute(res);
            Log.d("isConnectedToServer", "onPostExecute(): " + res);
            if (res==false) {
                Toast.makeText(mContext, "Keine Serververbindung", Toast.LENGTH_SHORT).show();
                showVPN();
            }

            //showEditDialog();
        }

        private void showVPN() {

            try {
                PackageManager manager = mContext.getPackageManager();
                Intent intent = manager.getLaunchIntentForPackage("app.openconnect");
                //intent.putExtra("Fp", "upb ssl"); // zum direkten Öffenen der FP-Einstellungen, sonst weglassen
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //TODO: wirklich notwendig ?
                mContext.startActivity(intent);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                Toast.makeText(mContext, "VPN App ist nicht installiert", Toast.LENGTH_SHORT).show();
            }
        }

        private Boolean isConnectedToServer(String url, int timeout) {
            try{
                URL myUrl = new URL(url);
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(timeout);
                connection.connect();
                return true;
            } catch (Exception e) {
                // Handle your exceptions
            }
            return false;
        }
    }
}
