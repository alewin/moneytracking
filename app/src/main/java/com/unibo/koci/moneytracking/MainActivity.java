package com.unibo.koci.moneytracking;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.unibo.koci.moneytracking.Activities.ArchiveActivity;
import com.unibo.koci.moneytracking.Activities.CategoriesActivity;
import com.unibo.koci.moneytracking.Activities.ChartTypeActivity;
import com.unibo.koci.moneytracking.Activities.DetailActivity;
import com.unibo.koci.moneytracking.Activities.NewItemActivity;
import com.unibo.koci.moneytracking.Activities.PlannedActivity;
import com.unibo.koci.moneytracking.Activities.ReportActivity;
import com.unibo.koci.moneytracking.Activities.SettingsActivity;
import com.unibo.koci.moneytracking.Adapters.ViewPagerAdapter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.Category;
import com.unibo.koci.moneytracking.Fragments.TabFragment;

import org.joda.time.LocalDate;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager viewPager;
    private ViewPagerAdapter vpage_adapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;
    private TextView total_amount;

    DBHelper dbHelper;

    /*TODO

    Functionality2: Manage periodic/planned expenses
    Ø Add information about periodic expenses (e.g. loan)
    Ø Add information about planned expenses (e.g. bill)
    Ø Budget must be updated at the payment date
    Ø Periodic reminders should be shown 1 and 2 days
    before (e.g. through notifications or alert dialogs)

    Display locations on the Google Maps

    export graph image

    cLEAR CODE
    *
    * */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (!prefs.getBoolean("firstTime", false)) {
            init_firstTimeStart();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", true);
            editor.commit();
        }

        init_tabview();
        init_toolbar();
        init_fab();
        init_navview();


    }

    private void init_firstTimeStart() {
        String categories[] = getResources().getStringArray(R.array.categories_array);
        for (String item : categories) {
            Category c = new Category(null, item);
            dbHelper.getDaoSession().getCategoryDao().insert(c);
        }
    }

    private void init_navview() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void init_fab() {
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newItem = new Intent(MainActivity.this, NewItemActivity.class);
                startActivity(newItem);
            }
        });
    }

    private void init_toolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        total_amount = (TextView) findViewById(R.id.available_amount);

        String amount = (String.format("%.0f", dbHelper.getTotal(new LocalDate(0), LocalDate.now())));


        total_amount.setText(String.valueOf(amount + "€"));
        setSupportActionBar(toolbar);
    }


    private void init_tabview() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpage_adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to vpage_adapter one by one

        TabFragment one = new TabFragment().newInstance(1), two = new TabFragment().newInstance(2), three = new TabFragment().newInstance(3);
        vpage_adapter.addFragment(one, getResources().getString(R.string.tab_day));
        vpage_adapter.addFragment(two, getResources().getString(R.string.tab_week));
        vpage_adapter.addFragment(three, getResources().getString(R.string.tab_month));
        viewPager.setAdapter(vpage_adapter);


        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
                Log.w("ale", tab.getText().toString() + tab.getPosition());
                viewpager.setVisibility(View.GONE);
                viewpager.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
                Log.w("ale", tab.getText().toString() + tab.getPosition());
                viewpager.setVisibility(View.GONE);
                viewpager.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
                Log.w("ale", tab.getText().toString() + tab.getPosition());
                viewpager.setVisibility(View.GONE);
                viewpager.setVisibility(View.VISIBLE);
            }
        });
        tabLayout.setupWithViewPager(viewPager);

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(MainActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }else if (id == R.id.nav_planned) {
            startActivity(new Intent(MainActivity.this, PlannedActivity.class));

        }
        else if (id == R.id.nav_category) {
            startActivity(new Intent(MainActivity.this, CategoriesActivity.class));

        } else if (id == R.id.nav_graph) {
            startActivity(new Intent(MainActivity.this, ChartTypeActivity.class));

        } else if (id == R.id.nav_archive) {
            startActivity(new Intent(MainActivity.this, ArchiveActivity.class));

        } else if (id == R.id.nav_report) {
            startActivity(new Intent(MainActivity.this, ReportActivity.class));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

}