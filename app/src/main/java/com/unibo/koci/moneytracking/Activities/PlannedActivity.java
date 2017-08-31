package com.unibo.koci.moneytracking.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.unibo.koci.moneytracking.Adapters.MoneyItemAdapter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.MoneyItemDao;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlannedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    DBHelper dbHelper;
    MoneyItemDao moneyItemDao;
    MoneyItemAdapter adapter;
    List<MoneyItem> input;
    private Toolbar toolbar_planned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned);
        init_toolbar();

        dbHelper = new DBHelper(this);
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();
        input = new ArrayList<>();
        LocalDate dt = new LocalDate(LocalDate.now());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_planned);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(new Date(0), dt.toDate())).list();

        adapter = new MoneyItemAdapter(input);
        recyclerView.setAdapter(adapter);
    }

    private void init_toolbar() {
        toolbar_planned = (Toolbar) findViewById(R.id.toolbar_planned);
        setSupportActionBar(toolbar_planned);
        getSupportActionBar().setTitle("Planned");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
}
