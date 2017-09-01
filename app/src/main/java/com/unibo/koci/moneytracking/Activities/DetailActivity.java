package com.unibo.koci.moneytracking.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.Location;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.Entities.PlannedItem;
import com.unibo.koci.moneytracking.MainActivity;
import com.unibo.koci.moneytracking.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    Toolbar toolbar_detail;
    SupportMapFragment mapFragment;
    TextView txt_description, txt_amount, txt_category, txt_date, txt_postion, txt_repeat, txt_occurence, txt_nextdate;
    MoneyItem money_item;
    PlannedItem planned_item;
    DBHelper dbHelper;
    final Context context = this;
    Boolean isPlanned = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dbHelper = new DBHelper(this);
        isPlanned = (Boolean) getIntent().getExtras().getSerializable("planned");

        if (isPlanned) {
            planned_item = (PlannedItem) (getIntent().getSerializableExtra("planned_item"));
        } else {
            money_item = (MoneyItem) (getIntent().getSerializableExtra("money_item"));

        }

        init_toolbar();
        init_textviews();
        init_map();
        init_fab();

    }

    private void init_textviews() {
        txt_amount = (TextView) findViewById(R.id.detail_amount);
        txt_category = (TextView) findViewById(R.id.detail_category);
        txt_date = (TextView) findViewById(R.id.detail_date);
        txt_description = (TextView) findViewById(R.id.detail_description);
        txt_postion = (TextView) findViewById(R.id.detail_position);
        txt_repeat = (TextView) findViewById(R.id.detail_planned_repeat);
        txt_occurence = (TextView) findViewById(R.id.detail_planned_occurence);
        txt_nextdate = (TextView) findViewById(R.id.detail_planned_nextDate);


        if (isPlanned) {
            planned_item.__setDaoSession(dbHelper.getDaoSession());
            String amount = (String.format("%.0f", planned_item.getAmount()));
            txt_amount.setText(amount + "€");
            txt_category.setText((planned_item.getCategory().getName()));
            Date d = planned_item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txt_date.setText(String.valueOf(sdf.format(d.getTime())));
            txt_description.setText(String.valueOf(planned_item.getDescription()));
            txt_postion.setText(String.valueOf(planned_item.getLocation().getName()));
            txt_occurence.setText(String.valueOf(planned_item.getOccurrence()));
            txt_repeat.setText(String.valueOf(planned_item.getRepeat()));
            d = planned_item.getPlannedDate();
            txt_nextdate.setText(String.valueOf(sdf.format(d.getTime())));
        } else {
            money_item.__setDaoSession(dbHelper.getDaoSession());
            String amount = (String.format("%.0f", money_item.getAmount()));
            txt_amount.setText(amount + "€");
            txt_category.setText((money_item.getCategory().getName()));
            Date d = money_item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txt_date.setText(String.valueOf(sdf.format(d.getTime())));
            txt_description.setText(String.valueOf(money_item.getDescription()));
            txt_postion.setText(String.valueOf(money_item.getLocation().getName()));
        }


    }

    private void init_fab() {
        FloatingActionButton fabedit = (FloatingActionButton) findViewById(R.id.fab_edit);
        fabedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailActivity.this, EditActivity.class);
                if (isPlanned) {
                    intent.putExtra("planned_item", planned_item);
                } else {
                    intent.putExtra("money_item", money_item);
                }
                intent.putExtra("planned", isPlanned);
                startActivity(intent);
            }
        });

        FloatingActionButton fabdelete = (FloatingActionButton) findViewById(R.id.fab_delete);
        fabdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle("Delete item")
                        .setMessage("Are you sure?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if (isPlanned) {
                                    delete_PlannedItem(planned_item);
                                } else {
                                    delete_Moneyitem(money_item);
                                }
                                Toast.makeText(DetailActivity.this, "Item deleted", Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);

                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void delete_PlannedItem(PlannedItem i) {
        Location l = i.getLocation();
        dbHelper.getDaoSession().delete(i);
        dbHelper.getDaoSession().delete(l);
    }

    private void delete_Moneyitem(MoneyItem i) {
        Location l = i.getLocation();
        dbHelper.getDaoSession().delete(i);
        dbHelper.getDaoSession().delete(l);
    }

    private void init_map() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Location tmp = isPlanned ? planned_item.getLocation() : money_item.getLocation();

        LatLng item_pos = new LatLng(tmp.getLatitude(), tmp.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomOut());

        String tmpname = isPlanned ? planned_item.getName() : money_item.getName();

        mMap.addMarker(new MarkerOptions().position(item_pos).title(tmpname));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(item_pos).zoom(15).build();
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
        mMap.getUiSettings().setAllGesturesEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private void init_toolbar() {
        toolbar_detail = (Toolbar) findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar_detail);
        String tmpname = isPlanned ? planned_item.getName() : money_item.getName();


        getSupportActionBar().setTitle(tmpname);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


}
