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
import com.unibo.koci.moneytracking.MainActivity;
import com.unibo.koci.moneytracking.R;


/**
 * Created by koale on 15/08/17.
 */

public class NewItemActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks {

    // google api
    private static String LOG_TAG = "maps";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceAdapter mPlaceArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(
            new LatLng(44.4833333, 11.3333333), new LatLng(44.4833333, 11.3333333));

    //object view
    private AutoCompleteTextView mAutocompleteTextView;
    private EditText nameAdd;
    private EditText descriptionAdd;
    private EditText amountAdd;
    private Button buttonAdd;
    private EditText dateInputText;
    private EditText categoryInputText;
    private Toolbar toolbar;

    private int amount_type; // 0 expense 1 gain
    private Place place;
    private long catid;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        nameAdd = (EditText) findViewById(R.id.add_name);
        descriptionAdd = (EditText) findViewById(R.id.add_description);
        amountAdd = (EditText) findViewById(R.id.add_amount);

        buttonAdd = (Button) findViewById(R.id.add_button);
        dateInputText = (EditText) findViewById(R.id.check_date);
        categoryInputText = (EditText) findViewById(R.id.add_category);

        toolbar = (Toolbar) findViewById(R.id.toolbar2);

        dbHelper = new DBHelper(this);

        init_typeAmount();
        init_placeAPI();
        init_toolbar();
        init_dateinput();
        init_addbutton();
        init_selectategory();
    }
    private void init_typeAmount(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("What do you want to add?")
                .setCancelable(false)
                .setPositiveButton("Expense", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        amount_type = 0;
                        dialog.cancel();
                      //  NewItemActivity.this.finish();
                    }
                })
                .setNegativeButton("Gain", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        amount_type = 1;
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void init_toolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init_dateinput() {
        dateInputText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //To show current date in the datepicker
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(NewItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        dateInputText.setText(selectedday + "/" + selectedmonth + "/" + selectedyear);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });
    }
    private void force_create_new_category(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("There aren't categories, please add new category")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        startActivity(new Intent(NewItemActivity.this, CategoriesActivity.class));

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    private void init_selectategory() {

        List<String> listItems = new ArrayList<String>();
        final List<Category> categories_list = dbHelper.getDaoSession().getCategoryDao().loadAll();

        if(categories_list.size() == 0){
            force_create_new_category();
        }
        int i=0;
        while(listItems.size() != categories_list.size()){
            listItems.add(categories_list.get(i++).getName().toString());
        }

        final CharSequence[] categories_string = listItems.toArray(new CharSequence[listItems.size()]);

        categoryInputText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(v.getContext())
                        .setSingleChoiceItems(categories_string, 0, null)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                                int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                                categoryInputText.setText(categories_list.get(selectedPosition).getName().toString());
                                catid = categories_list.get(selectedPosition).getCategoryID();
                            }
                        })
                        .show();
            }
        });
    }

    private void init_addbutton() {
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location loc = new Location(null, place.getName().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
                long locid = dbHelper.getDaoSession().insert(loc);
                double amount;
                String name = nameAdd.getText().toString();
                String description = descriptionAdd.getText().toString();
                Date date = getDate(dateInputText.getText().toString());
                if(amount_type == 0) {
                    amount = Double.valueOf("-" + amountAdd.getText().toString());
                }else{
                    amount = Double.valueOf(amountAdd.getText().toString());

                }

                Log.w("Aggiungimi", name + " " + description + " " + date +" " + amount + " " + locid + " " + catid + " " );
                MoneyItem mi = new MoneyItem(null,name,description,date,amount,catid,locid);
                long moneyid = dbHelper.getDaoSession().insert(mi);


                Toast.makeText(NewItemActivity.this, "aggiunto", Toast.LENGTH_LONG).show();

            }
        });
    }


    public static Date getDate(String datestring) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
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
        mAutocompleteTextView = (AutoCompleteTextView) findViewById(R.id
                .autoCompleteTextView);
        mAutocompleteTextView.setThreshold(3);

        mAutocompleteTextView.setOnItemClickListener(mAutocompleteClickListener);
        mPlaceArrayAdapter = new PlaceAdapter(this, android.R.layout.simple_list_item_1,
                BOUNDS_MOUNTAIN_VIEW, null);
        mAutocompleteTextView.setAdapter(mPlaceArrayAdapter);
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
            CharSequence attributions = places.getAttributions();
/*
            mNameTextView.setText(Html.fromHtml(place.getName() + ""));
            mAddressTextView.setText(Html.fromHtml(place.getAddress() + ""));
            mIdTextView.setText(Html.fromHtml(place.getId() + ""));
            mPhoneTextView.setText(Html.fromHtml(place.getPhoneNumber() + ""));

            mWebTextView.setText(place.getWebsiteUri() + "");
            if (attributions != null) {
                mAttTextView.setText(Html.fromHtml(attributions.toString()));
            }
            */
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