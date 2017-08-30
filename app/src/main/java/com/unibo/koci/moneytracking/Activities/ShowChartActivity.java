package com.unibo.koci.moneytracking.Activities;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.github.mikephil.charting.charts.BarChart;
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

import java.util.ArrayList;
import java.util.List;

public class ShowChartActivity extends AppCompatActivity {

    Toolbar show_chart_toolbar;
    PieChart pieChart;
    BarChart barChart;
    DBHelper dbHelper;
    String[] xData = {
            "January", "February", "March",
            "April", "May", "June", "July",
            "August", "September", "October",
            "November", "December"};


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

        createChart(x, colors);

    }

    private float getDataCategory(LocalDate start, LocalDate end) {

        return Math.abs((float) (dbHelper.getTotalExpense(start, end)));
    }

    private void init_chart_category() {
        BarChart barChart = (BarChart) findViewById(R.id.show_chart_category);

        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setDrawGridBackground(false);

        XAxis xl = barChart.getXAxis();
        xl.setGranularity(1f);
        xl.setCenterAxisLabels(true);
        xl.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xl.setAxisMinimum(0f);

        xl.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return "ok" + value;
            }
        });

        YAxis leftAxis = barChart.getAxisLeft();


        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(30f);
        leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true
        barChart.getAxisRight().setEnabled(false);

        //data
        float groupSpace = 0.04f;
        float barSpace = 0.02f; // x2 dataset
        float barWidth = 0.46f; // x2 dataset
        // (0.46 + 0.02) * 2 + 0.04 = 1.00 -> interval per "group"

        int startYear = 1;
        int endYear = 10;


        List<BarEntry> yVals1 = new ArrayList<BarEntry>();
        List<BarEntry> yVals2 = new ArrayList<BarEntry>();


        for (int i = startYear; i < endYear; i++) {
            yVals1.add(new BarEntry(i, 0.4f));

        }

        for (int i = startYear; i < endYear; i++) {
            yVals2.add(new BarEntry(i, 0.7f));
        }


        BarDataSet set1, set2;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            // create 2 datasets with different types
            set1 = new BarDataSet(yVals1, "Expense");
            set1.setStackLabels(new String[]{"Births", "Divorces", "Marriages", "ok", "ppp"});

            set1.setColor(Color.RED);
            set2 = new BarDataSet(yVals2, "Profit");
            set2.setStackLabels(new String[]{"Births", "Divorces", "Marriages", "ok", "ppp"});

            set2.setColor(Color.GREEN);

            ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
            dataSets.add(set1);
            dataSets.add(set2);

            BarData data = new BarData(dataSets);
            barChart.setData(data);
        }

        barChart.setPinchZoom(true);

        barChart.animateY(3000);
        barChart.animateX(3000);

        barChart.getBarData().setBarWidth(barWidth);
        barChart.getXAxis().setAxisMinValue(startYear);
        barChart.groupBars(startYear, groupSpace, barSpace);
        barChart.invalidate();
    }

    private void init_chart_category2() {


        barChart = (BarChart) findViewById(R.id.show_chart_category);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();

        for (int i = 0; i < 10; i++) {
            yVals2.add(new BarEntry(i, 4));
            yVals1.add(new BarEntry(i, 5));
        }

        for (int i = 10; i < 20; i++) {
            yVals2.add(new BarEntry(i, 3));
            yVals1.add(new BarEntry(i, 7));
        }


        // create 2 datasets
        BarDataSet set1 = new BarDataSet(yVals1, "Men");
        set1.setColor(Color.BLUE);
        BarDataSet set2 = new BarDataSet(yVals2, "Women");
        set2.setColor(Color.RED);

        BarData data = new BarData(set1, set2);

        barChart.setPinchZoom(true);

        barChart.animateY(3000);
        barChart.animateX(3000);

        barChart.setDrawGridBackground(false);

        float barWidth = 0.45f; // x2 dataset
        data.setBarWidth(barWidth); // set the width of each bar
        barChart.setData(data);
        barChart.invalidate(); // refresh


    }


    private void createChart(ArrayList<PieEntry> yEntrys, ArrayList<Integer> colors) {

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setColors(colors);

        pieChart.setCenterText("X");
        pieChart.setCenterTextColor(Color.BLACK);

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


