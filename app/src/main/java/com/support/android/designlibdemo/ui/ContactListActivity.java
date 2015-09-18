package com.support.android.designlibdemo.ui;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.support.android.designlibdemo.AppController;
import com.support.android.designlibdemo.R;
import com.support.android.designlibdemo.provider.OrderSuggestionProvider;
import com.support.android.designlibdemo.ui.fragments.AboutFragment;
import com.support.android.designlibdemo.ui.fragments.HintFragment;

import java.lang.reflect.Field;

public class ContactListActivity extends AppCompatActivity {

    private MenuItem searchItem;
    private SearchRecentSuggestions suggestions;
    private SearchView searchView;
    private TextView tvHinweis;
    private AppController mAppController = AppController.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

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
/*
        // test-Vorschläge erzeugen für Such-Historie
        suggestions.clearHistory();
        String query;
        query = OrderSuggestionProvider.generateRandomSuggestion();
        suggestions.saveRecentQuery(query, "generierter Test-Eintrag");
        query = OrderSuggestionProvider.generateRandomSuggestion();
        suggestions.saveRecentQuery(query, "generierter Test-Eintrag");
*/
        showFragment("hint", null);  // Hinweis bei leerer Liste anzeigen
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
            suggestions.saveRecentQuery(query, query);

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

        Fragment fragment = null;
        switch (key) {

            case "hint":
                fragment = new HintFragment();
                Bundle bundle = new Bundle();
                bundle.putString("hint","Um Kontakte anzuzeigen, benutzen Sie die Suchfunktion am oberen Bildschirmrand.");
                fragment.setArguments(bundle);
                break;

            case "about":
                fragment = new AboutFragment();
                break;

            case "list":
                //fragment = new OrderListFragment(this);
                //fragment.setArguments(args);
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
        mAppController.cancelPendingRequests(AppController.VOLLEY_PATTERNS);
    }
}
