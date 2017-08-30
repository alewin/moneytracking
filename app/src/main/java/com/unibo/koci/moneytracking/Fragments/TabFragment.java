package com.unibo.koci.moneytracking.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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

import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by koale on 12/08/17.
 */


public class TabFragment extends Fragment {


    DBHelper dbHelper;
    MoneyItemDao moneyItemDao;
    MoneyItemAdapter adapter;


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
        setRetainInstance(true);
        JodaTimeAndroid.init(getContext());

        dbHelper = new DBHelper(getContext());
        moneyItemDao = dbHelper.getDaoSession().getMoneyItemDao();


    }



    private List<MoneyItem> getItems() {
        List<MoneyItem> input = new ArrayList<>();
        int tab = getArguments().getInt("numtab");
        LocalDate dt = new LocalDate(LocalDate.now());

        switch (tab) {
            case 1:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.toDate(), dt.toDate())).list();
                break;
            case 2:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfWeek().withMinimumValue().toDate(), dt.dayOfWeek().withMaximumValue().toDate())).list();
                break;
            case 3:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfMonth().withMinimumValue().toDate(), dt.dayOfMonth().withMaximumValue().toDate())).list();
                break;
        }
        return input;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w("ale", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_tab_money_item_list, container, false);
        List<MoneyItem> data = getItems();

        adapter = new MoneyItemAdapter(data);
        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view_money);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        return rootView;
    }


}