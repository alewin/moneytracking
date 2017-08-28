package com.unibo.koci.moneytracking.Activities;


import android.app.DatePickerDialog;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ListIterator;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.unibo.koci.moneytracking.Adapters.PlaceAdapter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.Category;
import com.unibo.koci.moneytracking.Entities.Location;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.R;



/**
 * Created by koale on 15/08/17.
 */

public class EditActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    // google api
    private static String LOG_TAG = "maps";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(44.4833333, 11.3333333), new LatLng(44.4833333, 11.3333333));

    //object view
    private AutoCompleteTextView addLocation;
    private EditText nameAdd;
    private EditText descriptionAdd;
    private EditText amountAdd;
    private Button buttonAdd;
    private EditText dateInputText;
    private EditText categoryInputText;
    private Toolbar toolbar;

    private Place place;
    private long catid;
    DBHelper dbHelper;
    MoneyItem item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        item = (MoneyItem)(getIntent().getSerializableExtra("item"));
        dbHelper = new DBHelper(this);

        init_editText();

        init_toolbar();
        init_dateinput();
        init_addbutton();
        init_selectategory();
        init_placeAPI();
    }


    private void init_editText(){
        item.__setDaoSession(dbHelper.getDaoSession());

        addLocation = (AutoCompleteTextView) findViewById(R.id.addLocation);
        nameAdd = (EditText) findViewById(R.id.add_name);
        descriptionAdd = (EditText) findViewById(R.id.add_description);
        amountAdd = (EditText) findViewById(R.id.add_amount);
        buttonAdd = (Button) findViewById(R.id.add_button);
        dateInputText = (EditText) findViewById(R.id.check_date);
        categoryInputText = (EditText) findViewById(R.id.add_category);
        buttonAdd.setText("Edit");

        nameAdd.setText(item.getName());
        amountAdd.setText(String.valueOf(item.getAmount()));
        categoryInputText.setText((item.getCategory().getName()));
        dateInputText.setText(String.valueOf(item.getDate()));
        descriptionAdd.setText(String.valueOf(item.getDescription()));
        addLocation.setText(String.valueOf(item.getLocation().getName()));

    }
    private void init_toolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init_dateinput() {

        dateInputText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(EditActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        dateInputText.setText(selectedday + "/" + (selectedmonth + 1) + "/" + selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                mDatePicker.show();
            }
        });
    }

    private void force_create_new_category() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There aren't categories, please add new category")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(EditActivity.this, CategoriesActivity.class));
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void init_selectategory() {

        List<String> listItems = new ArrayList<String>();
        final List<Category> categories_list = dbHelper.getDaoSession().getCategoryDao().loadAll();

        if (categories_list.size() == 0) {
            force_create_new_category();
        }
        int i = 0;
        while (listItems.size() != categories_list.size()) {
            listItems.add(categories_list.get(i++).getName().toString());
        }

        final CharSequence[] categories_string = listItems.toArray(new CharSequence[listItems.size()]);

        categoryInputText
                .setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (hasFocus) {
                            new AlertDialog.Builder(v.getContext(), R.style.DialogStyle)
                                    .setSingleChoiceItems(categories_string, 0, null)
                                    .setPositiveButton("Select", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            dialog.dismiss();
                                            int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                            categoryInputText.setText(categories_list.get(selectedPosition).getName().toString());
                                            catid = categories_list.get(selectedPosition).getCategoryID();
                                        }
                                    })
                                    .show();


                        }
                    }
                });


    }


    private void init_addbutton() {

        try {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("debugkoci", "ok1");
                    String name, description;

                    double amount = 0;
                    Date date;
                    long moneyid, locid;
                    boolean ok = true;
                    Location loc = new Location(null, "", 0, 0);

                    name = nameAdd.getText().toString();
                    if (name.isEmpty()) {
                        nameAdd.setError("Insert name");
                        ok = false;
                    }

                    description = descriptionAdd.getText().toString();
                    if (description.isEmpty()) {
                        descriptionAdd.setError("Insert Description");
                        ok = false;
                    }

                    if (categoryInputText.getText().toString().isEmpty()) {
                        categoryInputText.setError("Select category");
                        ok = false;
                    }


                    if (place == null) {
                        //todo query location
                        if (addLocation.getText().toString().isEmpty()) {
                            addLocation.setError("Insert Location");
                            ok = false;
                        } else {
                            loc = new Location(null, addLocation.getText().toString(), 0, 0);
                        }
                    } else {
                        loc = new Location(null, place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
                    }


                    date = getDate(dateInputText.getText().toString());
                    if (date.toString().isEmpty()) {
                        dateInputText.setError("Insert Date");
                        ok = false;
                    }

                    if (amountAdd.getText().toString().isEmpty()) {
                        ok = false;
                    } else {
                        amount = Double.valueOf(amountAdd.getText().toString());
                    }

                    if (ok) {
                       // locid = dbHelper.getDaoSession().insert(loc);
                        item.setAmount(amount);
                        item.setName(name);
                        item.setDescription(description);
                        item.setDate(date);
                        item.setCategoryID((long)0);
                        item.setLocationID((long)0);
                        dbHelper.getDaoSession().update(item);

                        Toast.makeText(EditActivity.this, "Edited", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(EditActivity.this, "Please fill all input", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (NullPointerException x) {
            Toast.makeText(EditActivity.this, "Please fill all input!", Toast.LENGTH_LONG).show();

        }
    }


    public static Date getDate(String datestring) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date;

        date = new Date();

        try {
            date = format.parse(datestring);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    private void init_placeAPI() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();

        addLocation.setThreshold(3);

        addLocation.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        addLocation.setAdapter(mPlaceArrayAdapter);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " +
                        places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            place = places.get(0);

        }
    };

    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        Log.i(LOG_TAG, "Google Places API connected.");

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(LOG_TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
        Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}