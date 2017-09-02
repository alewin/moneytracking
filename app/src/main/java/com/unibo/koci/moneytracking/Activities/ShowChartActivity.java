package com.unibo.koci.moneytracking.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowChartActivity extends AppCompatActivity {

    Toolbar show_chart_toolbar;
    PieChart pieChart;
    HorizontalBarChart barChart;
    DBHelper dbHelper;

    LocalDate start, end;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_chart);

        start = (LocalDate) (getIntent().getSerializableExtra("start"));
        end = (LocalDate) (getIntent().getSerializableExtra("end"));

        dbHelper = new DBHelper(this);
        init_toolbar();
        init_chart_profit_expense();
        init_chart_category();

    }

    private void init_toolbar() {
        show_chart_toolbar = (Toolbar) findViewById(R.id.toolbar_show_chart);
        setSupportActionBar(show_chart_toolbar);
        getSupportActionBar().setTitle("Chart");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     * @param type - type=0 expense, type=1 profit
     */
    private float getDataExpenseProfit(LocalDate start, LocalDate end, int type) {
        if (type == 0)
            return Math.abs((float) (dbHelper.getTotalExpense(start, end)));
        else
            return (float) (dbHelper.getTotalProfit(start, end));
    }

    private void init_chart_profit_expense() {
        pieChart = (PieChart) findViewById(R.id.show_chart_profitexpense);
        ArrayList<PieEntry> x = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        x.add(new PieEntry(getDataExpenseProfit(start, end, 0), "Expense"));
        x.add(new PieEntry(getDataExpenseProfit(start, end, 1), "Profit"));

        colors.add(Color.RED);
        colors.add(Color.GREEN);

        createPieChart(x, colors);
    }


    private void init_chart_category() {

        barChart = (HorizontalBarChart) findViewById(R.id.show_chart_category);

        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<BarEntry> yVals2 = new ArrayList<BarEntry>();
        final JSONArray jsonarray = dbHelper.getCategoryProfitExpense(start, end);

        try {
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonobject = jsonarray.getJSONObject(i);
                String name = jsonobject.getString("name");
                String profit = jsonobject.getString("profit");
                String expense = jsonobject.getString("expense");
                yVals1.add(new BarEntry(i, Math.abs(Float.valueOf(expense))));
                yVals2.add(new BarEntry(i, Math.abs(Float.valueOf(profit))));

            }
        } catch (JSONException e) {

        }

        createBarChart(yVals1, yVals2, jsonarray);
    }


    private void createBarChart(List<BarEntry> yVals1, List<BarEntry> yVals2, final JSONArray jsonarray) {

        BarDataSet set1, set2;
        Description d = new Description();
        d.setText("");

        float groupSpace = 0.04f;
        float barSpace = 0.02f;
        float barWidth = 0.46f;

        barChart.setPinchZoom(true);
        barChart.animateY(3000);
        barChart.animateX(3000);
        barChart.setNoDataTextColor(Color.GRAY);

        barChart.setDescription(d);
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setTextSize(2);
        xl.setTextColor(Color.BLACK);
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);
        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                try {
                    int i = (int) value;
                    JSONObject jsonobject = jsonarray.getJSONObject(i);
                    return jsonobject.getString("name");

                } catch (JSONException e) {

                }
                return "";

            }
        });
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextSize(2);
        leftAxis.setTextColor(Color.BLACK);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.valueOf((int) value) + "€";
            }

        });
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f);
        barChart.getAxisRight().setEnabled(false);


        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "Expense");
            set1.setColor(Color.RED);
            set2 = new BarDataSet(yVals2, "Profit");
            set2.setColor(Color.GREEN);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);
            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }


        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(0);
        barChart.groupBars(0, groupSpace, barSpace);

        xl.setAxisMaximum(jsonarray.length());


        barChart.invalidate();

    }

    private void createPieChart(ArrayList<PieEntry> yEntrys, ArrayList<Integer> colors) {

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(colors);

        pieChart.setCenterText(start.toString() + "\n" + end.toString());
        pieChart.setCenterTextColor(Color.BLACK);
        pieChart.setCenterTextSize(20);

        pieChart.animateY(2000);
        pieChart.animateX(2000);
        pieChart.getLegend().setEnabled(true);

        Description description = new Description();
        description.setText("");
        pieChart.setDescription(description);

        //create pie data object
        PieData pieData = new PieData();
        pieData.addDataSet(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }


}


