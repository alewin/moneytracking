package com.unibo.koci.moneytracking.Activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
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

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
    LinearLayout layout_planned;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        dbHelper = new DBHelper(this);
        isPlanned = (Boolean) getIntent().getExtras().getSerializable("planned");
        layout_planned = (LinearLayout) findViewById(R.id.planned_layout_detail);

        if (isPlanned) {
            planned_item = (PlannedItem) (getIntent().getSerializableExtra("planned_item"));
            layout_planned.setVisibility(View.VISIBLE);
        } else {
            money_item = (MoneyItem) (getIntent().getSerializableExtra("money_item"));
            layout_planned.setVisibility(View.GONE);

        }

        init_toolbar();
        init_textviews();
        set_textview();
        init_map();
        init_fab();

    }

    private void set_textview() {
        if (isPlanned) {
            planned_item.__setDaoSession(dbHelper.getDaoSession());
            DecimalFormat df = new DecimalFormat("#.00");
            String amount = df.format(planned_item.getAmount());
            txt_amount.setText(amount + "€");
            txt_category.setText((planned_item.getCategory().getName()));
            txt_date.setVisibility(View.GONE);

            txt_description.setText(String.valueOf(planned_item.getDescription()));
            txt_postion.setText(String.valueOf(planned_item.getLocation().getName()));
            txt_occurence.setText(String.valueOf(planned_item.getOccurrence()));
            txt_repeat.setText(String.valueOf(planned_item.getRepeat()));
            Date d = planned_item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txt_nextdate.setText(String.valueOf(sdf.format(d.getTime())));
        } else {
            money_item.__setDaoSession(dbHelper.getDaoSession());
            DecimalFormat df = new DecimalFormat("#.00");
            String amount = df.format(money_item.getAmount());
            txt_amount.setText(amount + "€");
            txt_category.setText((money_item.getCategory().getName()));
            Date d = money_item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            txt_date.setText(String.valueOf(sdf.format(d.getTime())));
            txt_description.setText(String.valueOf(money_item.getDescription()));
            txt_postion.setText(String.valueOf(money_item.getLocation().getName()));
        }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);

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

    private LatLng getCoordinate(String location_name){
        LatLng latLng = new LatLng(0,0);
        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses;
            addresses = geocoder.getFromLocationName(location_name, 1);
            if (addresses.size() > 0) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                latLng = new LatLng(latitude,longitude);
                return latLng;

            }
        }  catch(IOException ex){
            return latLng;
        }
        return latLng;

    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng item_pos = new LatLng(0,0);
        Location loc_tmp = isPlanned ? planned_item.getLocation() : money_item.getLocation();
        if(loc_tmp.getLatitude() == loc_tmp.getLongitude() && loc_tmp.getLatitude() == 0){

            item_pos = getCoordinate(loc_tmp.getName());
            loc_tmp.setLatitude(item_pos.latitude);
            loc_tmp.setLongitude(item_pos.longitude);
            dbHelper.getDaoSession().update(loc_tmp);
        }
        else {
            item_pos = new LatLng(loc_tmp.getLatitude(), loc_tmp.getLongitude());
        }
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
