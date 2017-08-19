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

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.util.GregorianCalendar;
import java.util.List;

/*
 * Created by koale on 12/08/17.
 */


public class TabFragment extends Fragment {


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
        JodaTimeAndroid.init(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {




        View rootView = inflater.inflate(R.layout.fragment_tab_money_item_list, container, false);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.my_recycler_view);
        rv.setHasFixedSize(true);

        dbHelper = new DBHelper(getContext());
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();
        input = new ArrayList<>();
        int tab = getArguments().getInt("numtab");

        LocalDate dt = new LocalDate(LocalDate.now());

        switch (tab){
            case 1:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.minusDays(1).toDate(), dt.plusDays(1).toDate())).list();
                Log.w("koko","1size " + input.size());
            case 2:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfWeek().withMinimumValue().toDate(),dt.dayOfWeek().withMaximumValue().toDate())).list();
                Log.w("koko","2size " + input.size());
            case 3:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfMonth().withMinimumValue().toDate(),dt.dayOfMonth().withMaximumValue().toDate())).list();
                Log.w("koko","3size " + input.size());
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