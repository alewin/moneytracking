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


public class TabFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {


    DBHelper dbHelper;
    MoneyItemDao moneyItemDao;
    MoneyItemAdapter adapter;
    List<MoneyItem> input;
    SwipeRefreshLayout swipeLayout;

    int current_selected_tab;

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


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.w("ale", "onViewCreated");
        super.onViewCreated(view, savedInstanceState);
    }


    void loadItems(int tab) {

        input = new ArrayList<>();
        Log.w("ale", "oncreate");

        LocalDate dt = new LocalDate(LocalDate.now());
        Log.w("ales", String.valueOf(tab));
        switch (tab) {
            case 1:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.toDate(), dt.toDate())).list();
                Log.w("xaless", "dayly");
                break;
            case 2:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfWeek().withMinimumValue().toDate(), dt.dayOfWeek().withMaximumValue().toDate())).list();
                Log.w("xaless", "week");
                break;
            case 3:
                input = moneyItemDao.queryBuilder().where(MoneyItemDao.Properties.Date.between(dt.dayOfMonth().withMinimumValue().toDate(), dt.dayOfMonth().withMaximumValue().toDate())).list();
                Log.w("xaless", "month");
                break;
        }
        adapter = new MoneyItemAdapter(input);

        adapter.notifyDataSetChanged();

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w("ale", "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_tab_money_item_list, container, false);
        int tab = getArguments().getInt("numtab");
        loadItems(tab);

        RecyclerView rv = (RecyclerView) rootView.findViewById(R.id.recycler_view_money);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);

        adapter.notifyDataSetChanged();
        container.destroyDrawingCache();

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container_money);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.CYAN);

        return rootView;
    }


    @Override
    public void onPause() {
        Log.w("ale", "onPause");

        super.onPause();
        if (swipeLayout != null) {
            swipeLayout.setRefreshing(false);
            swipeLayout.destroyDrawingCache();
            swipeLayout.clearAnimation();
        }
    }

    @Override
    public void onResume() {
        Log.w("ale", "onResume");

        super.onResume();

    }

    public  void ciao() {

    }
    @Override
    public void onRefresh() {
        getActivity().getWindow().getDecorView().findViewById(android.R.id.content).invalidate();
        loadItems(1); loadItems(2);loadItems(3);
        adapter.notifyDataSetChanged();
         getFragmentManager().beginTransaction().detach(this).attach(this).commit();

        Log.w("ale", "onRefresh");
        adapter.notifyDataSetChanged();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                loadItems(1); loadItems(2);loadItems(3);
                adapter.notifyDataSetChanged();
                swipeLayout.setRefreshing(false);
                adapter.notifyDataSetChanged();

            }
        }, 1000);
    }


}