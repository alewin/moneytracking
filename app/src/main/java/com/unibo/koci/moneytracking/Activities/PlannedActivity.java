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
import com.unibo.koci.moneytracking.Entities.PlannedItem;
import com.unibo.koci.moneytracking.Entities.PlannedItemDao;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlannedActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    DBHelper dbHelper;
    PlannedItemDao plannedItemDao;
    MoneyItemAdapter adapter;
    List<MoneyItem> input;
    private Toolbar toolbar_planned;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned);
        init_toolbar();

        dbHelper = new DBHelper(this);
        plannedItemDao = dbHelper.getDaoSession().getPlannedItemDao();
        input = new ArrayList<>();
        LocalDate dt = new LocalDate(LocalDate.now());

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_planned);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //input = plannedItemDao.queryBuilder().where(PlannedItem.Properties.Date.between(new Date(0), dt.toDate())).list();

        //adapter = new MoneyItemAdapter(input);
       // recyclerView.setAdapter(adapter);
    }

    private void init_toolbar() {
        toolbar_planned = (Toolbar) findViewById(R.id.toolbar_planned);
        setSupportActionBar(toolbar_planned);
        getSupportActionBar().setTitle("Planned");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    /*TODO

    Functionality2: Manage periodic/planned expenses
    Ø Add information about periodic expenses (e.g. loan)
    Ø Add information about planned expenses (e.g. bill)
    Ø Budget must be updated at the payment date
    Ø Periodic reminders should be shown 1 and 2 days
    before (e.g. through notifications or alert dialogs)

0) creare tabella PLANNNED simile a moneyitem

1) ACTIVITY che permette di scegliere ogni quanto ripetere la transizione
    giornaliera = ogni giorno
    settimanale = indicare il giorno in cui si ripeterà ( lun,mart,merc)
    mensile = indicare il giorno del mese in cui si ripetera ( attenzione a febbrario 1-

2) aprire activity NEW ITEM cambiando, se planned = true inserirlo nella tabella planned

3) notifiche:
   esempio

   oggi è il 31 agosto


   affito, ****, ****, ****, ***, mensile,


    *
    *
    *
    * */
}
