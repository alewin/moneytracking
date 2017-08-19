package com.unibo.koci.moneytracking;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.unibo.koci.moneytracking.Activities.CategoriesActivity;
import com.unibo.koci.moneytracking.Activities.NewItemActivity;
import com.unibo.koci.moneytracking.Activities.SettingsActivity;
import com.unibo.koci.moneytracking.Activities.ViewPagerAdapter;
import com.unibo.koci.moneytracking.Fragments.TabFragment;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private ViewPager viewPager;
    private ViewPagerAdapter vpage_adapter;
    private Toolbar toolbar;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init_tabview();
        init_toolbar();
        init_fab();
        init_navview();
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
        setSupportActionBar(toolbar);
    }

    private void init_tabview() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        vpage_adapter = new ViewPagerAdapter(getSupportFragmentManager());

        // Add Fragments to vpage_adapter one by one
        vpage_adapter.addFragment(new TabFragment().newInstance(1), getResources().getString(R.string.tab_day));
        vpage_adapter.addFragment(new TabFragment().newInstance(2), getResources().getString(R.string.tab_week));
        vpage_adapter.addFragment(new TabFragment().newInstance(3), getResources().getString(R.string.tab_month));
        viewPager.setAdapter(vpage_adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(tabListener);
        tabLayout.setupWithViewPager(viewPager);
    }
    TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            viewPager.setCurrentItem(tab.getPosition());
            Log.w("kokokoko",String.valueOf(tab.getPosition()));
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

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
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            // Handle the camera action
        } else if (id == R.id.nav_category) {
            startActivity(new Intent(MainActivity.this, CategoriesActivity.class));

        } else if (id == R.id.nav_graph) {

        } else if (id == R.id.nav_report) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}