package com.harshal.roomiesexpenses;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.harshal.roomiesexpenses.fragments.AccountsFragment;
import com.harshal.roomiesexpenses.fragments.AddMonthFragment;
import com.harshal.roomiesexpenses.fragments.AddRoommateFragment;
import com.harshal.roomiesexpenses.fragments.BackupsFragment;
import com.harshal.roomiesexpenses.fragments.ExpenseChartsFragment;
import com.harshal.roomiesexpenses.fragments.GeneratedBillsFragment;
import com.harshal.roomiesexpenses.fragments.MonthlyExpensesFragment;
import com.harshal.roomiesexpenses.fragments.REBaseFragment;
import com.harshal.roomiesexpenses.listener.FragmentChangedListener;
import com.harshal.roomiesexpenses.utils.CustomTypefaceSpan;
import com.harshal.roomiesexpenses.utils.Page;
import com.harshal.roomiesexpenses.utils.Util;

public class RoomiesExpensesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentChangedListener {

    private CharSequence mTitle;
    private String[] navMenuTitles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_roomies_expenses);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setVisibility(View.GONE);
        progressBar.setIndeterminate(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(progressBar);
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mTitle = getTitle();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Grab the NavigationView Menu
        final Menu navMenu = navigationView.getMenu();
        for (int i = 0; i < navMenu.size(); i++){
            MenuItem menuItem = navMenu.getItem(i);
            if (menuItem != null){
                SpannableString spannableString = new SpannableString(menuItem.getTitle());
//                Typeface fontTextGearedSlab = Typeface.createFromAsset(getAssets(), "fonts/GearedSlab.ttf");
                Typeface fontOswaldRegular = Typeface.createFromAsset(getAssets(), "fonts/Oswald_Regular.ttf");

                spannableString.setSpan(new CustomTypefaceSpan("", fontOswaldRegular), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                menuItem.setTitle(spannableString);
                // Here'd you loop over any SubMenu items using the same technique.
            }
        }
        /*// Install an OnGlobalLayoutListener and wait for the NavigationMenu to fully initialize
        navigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Remember to remove the installed OnGlobalLayoutListener
                navigationView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // Loop through and find each MenuItem View
                for (int i = 0, length = navMenuTitles.length; i < length; i++) {
                    final String id = "menuItem" + (i + 1);
                    final MenuItem item = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                    navigationView.findViewsWithText(mMenuItems, item.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                }
                // Loop through each MenuItem View and apply your custom Typeface
                for (final View menuItem : mMenuItems) {
                    ((TextView) menuItem).setTypeface(yourTypeface, Typeface.BOLD);
                }
            }
        });*/

       setFragmentOfPos(0);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            REBaseFragment fragment = (MonthlyExpensesFragment)getSupportFragmentManager().findFragmentByTag("0");
            if(fragment!=null)
                showAlertToExitApplication();
            else
                setFragmentOfPos(0);
        }
    }
/*

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
//            getMenuInflater().inflate(R.menu.roomies_expenses, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int position = Page.MONTHLY_EXPENSES;
        switch (item.getItemId()){
            case R.id.nav_monthly_expenses:
                position = Page.MONTHLY_EXPENSES;
                break;
            case R.id.nav_expense_charts:
                position = Page.EXPENSE_CHARTS;
                break;
            case R.id.nav_generated_bills:
                position = Page.GENERATED_BILLS;
                break;
            case R.id.nav_backups:
                position = Page.BACKUPS;
                break;
            case R.id.nav_add_month:
                position = Page.ADD_MONTHS;
                break;
            case R.id.nav_add_roommate:
                position = Page.ADD_ROOMMATES;
                break;
            case R.id.nav_accounts:
                position = Page.ACCOUNTS;
                break;
            case R.id.nav_share:
                position = Page.SHARE;
                break;
            case R.id.nav_like:
                position = Page.LIKE;
                break;
            case R.id.nav_exit:
                position = Page.EXIT;
                break;
        }
        setFragmentOfPos(position);
        return true;
    }

    private void showAlertToExitApplication() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Do you really want to exit application?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        RoomiesExpensesActivity.this.finish();
                    }
                })
                .setNegativeButton(R.string.no, null).show();
    }

    @Override
    public void onFragmentChanged(int pos) {
        mTitle = navMenuTitles[pos];
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = LayoutInflater.from(this);
        View v = inflator.inflate(R.layout.custom_title_text_view, null);
        ((TextView)v.findViewById(R.id.title)).setText(mTitle);
        actionBar.setCustomView(v);
    }

    @Override
    public void setFragmentOfPos(int pos) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        REBaseFragment fragment = null;
        switch (pos){
            case Page.MONTHLY_EXPENSES:
                fragment = new MonthlyExpensesFragment();
                break;
            case Page.EXPENSE_CHARTS:
                fragment = new ExpenseChartsFragment();
                break;
            case Page.GENERATED_BILLS:
                fragment = new GeneratedBillsFragment();
                break;
            case Page.BACKUPS:
                fragment = new BackupsFragment();
                break;
            case Page.ADD_MONTHS:
                fragment = new AddMonthFragment();
                break;
            case Page.ADD_ROOMMATES:
                fragment = new AddRoommateFragment();
                break;
            case Page.ACCOUNTS:
                fragment = new AccountsFragment();
                break;
            case Page.SHARE:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, getResources().getString(R.string.app_url));
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.share_using)));
                break;
            case Page.LIKE:
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            case Page.EXIT:
                showAlertToExitApplication();
                break;
        }
        if(fragment != null){
            Bundle args = new Bundle();
            args.putInt(Util.ARG_SECTION_NUMBER, pos);
            fragment.setArguments(args);
            fragment.setListener(this);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment, ""+pos)
                    .commit();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }
}
