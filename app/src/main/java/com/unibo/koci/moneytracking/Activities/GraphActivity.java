package com.unibo.koci.moneytracking.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.unibo.koci.moneytracking.R;

public class GraphActivity extends AppCompatActivity {

    Toolbar graph_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        init_toolbar();


    }

    private void init_toolbar() {
        graph_toolbar = (Toolbar) findViewById(R.id.toolbar_graph);
        setSupportActionBar(graph_toolbar);
        getSupportActionBar().setTitle("Graph");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
