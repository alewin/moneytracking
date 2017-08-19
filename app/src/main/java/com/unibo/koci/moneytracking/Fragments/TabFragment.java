package com.unibo.koci.moneytracking.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unibo.koci.moneytracking.Adapters.MoneyItemAdapter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.MoneyItemDao;
import com.unibo.koci.moneytracking.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/*
 * Created by koale on 12/08/17.
 */


public class TabFragment extends Fragment {

    private int DAY = 0;
    private int WEEK = 1;
    private int MONTH = 2;

    DBHelper dbHelper;
    MoneyItemDao moneyItemDao;
    MoneyItemAdapter adapter;
    List<MoneyItem> input;

    public static TabFragment newInstance(int numtab) {
        TabFragment myFragment = new TabFragment();
        Bundle args = new Bundle();
        args.putInt("numtab", numtab);
        myFragment.setArguments(args);
        return myFragment;
    }


    public TabFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.v("ciao alessio", Integer.toString(getArguments().getInt("numtab", 0)));

        View rootView = inflater.inflate(R.layout.fragment_tab_money_item_list, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);

        dbHelper = new DBHelper(getContext());
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();
        input = new ArrayList<>();
        int tab = getArguments().getInt("numtab", 0);
        Calendar c = Calendar.getInstance();

        Date time1,time2, currentTime = c.getTime();


/*
        long currentTime = System.currentTimeMillis();
        Date aDayAgo = new Date(currentTime - DateUtils.DAY_IN_MILLIS);
        Date inADay = new Date(currentTime + DateUtils.DAY_IN_MILLIS);
        List<Note> results = noteDao.queryBuilder()
                .where(NoteDao.Properties.Date.gt(aDayAgo), NoteDao.Properties.Date.lt(inADay))
                .build()
                .list();

                LocalDate monthBegin = new LocalDate().withDayOfMonth(1);
LocalDate monthEnd = new LocalDate().plusMonths(1).withDayOfMonth(1).minusDays(1);

        */

        switch (tab){
            case 1:
                time1 = Calendar.getInstance().getTime();
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(time1,time1)).list();
            case 2:
                c.set(Calendar.WEEK_OF_YEAR,1); time1 = c.getTime();
                c.set(Calendar.WEEK_OF_YEAR, 7); time2 = c.getTime();
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(time1,time2)).list();
            case 3:
                c.set(Calendar.DAY_OF_MONTH, 1); time1 = c.getTime();
                c.set(Calendar.DAY_OF_MONTH, 30); time2 = c.getTime();
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(time1,time2)).list();
        }

        adapter = new MoneyItemAdapter(input);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();



              /* List<MoneyItem> input_day = dbHelper.getDaoSession().queryBuilder()
                .where(MoneyItemDao.Properties.Date.between(currentTime,currentTime))
                .list();
*/

/*
        for (int i = 0; i < 10; i++) {
            input.add("Test" + Integer.toString(getArguments().getInt("numtab", 0)) + " - " + i);
        }// define an adapter

*/
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(llm);
        return rootView;

    }


}