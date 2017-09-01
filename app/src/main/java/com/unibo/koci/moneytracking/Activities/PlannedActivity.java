package com.unibo.koci.moneytracking.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.unibo.koci.moneytracking.Adapters.PlannedItemAdapter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.PlannedItem;
import com.unibo.koci.moneytracking.Entities.PlannedItemDao;
import com.unibo.koci.moneytracking.R;

import java.util.ArrayList;
import java.util.List;

public class PlannedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    DBHelper dbHelper;
    PlannedItemDao plannedItemDao;
    PlannedItemAdapter adapter;
    List<PlannedItem> input;
    private Toolbar toolbar_planned;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned);
        init_toolbar();

        dbHelper = new DBHelper(this);
        init_fab();
        init_list();

    }

    private void init_list() {
        plannedItemDao = dbHelper.getDaoSession().getPlannedItemDao();
        input = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_planned);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        input = plannedItemDao.queryBuilder().orderAsc(PlannedItemDao.Properties.PlannedDate).list();

        adapter = new PlannedItemAdapter(input);
        recyclerView.setAdapter(adapter);
    }

    private void init_fab() {
        fab = (FloatingActionButton) findViewById(R.id.fab_add_planned);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PlannedActivity.this, NewItemActivity.class);
                intent.putExtra("planned", true);
                startActivity(intent);
            }
        });
    }

    private void init_toolbar() {
        toolbar_planned = (Toolbar) findViewById(R.id.toolbar_planned);
        setSupportActionBar(toolbar_planned);
        getSupportActionBar().setTitle("Planned");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_planned_delete, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete_planned:
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Remove all planned item?")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (dbHelper.clearReport()) {
                                    Toast.makeText(PlannedActivity.this, "Planned items deleted", Toast.LENGTH_LONG).show();
                                    input.clear();

                                    adapter.notifyDataSetChanged();
                                }
                            }
                        })
                        .setNegativeButton("No", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
