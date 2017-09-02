package com.unibo.koci.moneytracking.Activities;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
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
import com.unibo.koci.moneytracking.Entities.PlannedItem;
import com.unibo.koci.moneytracking.MainActivity;
import com.unibo.koci.moneytracking.R;

import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


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
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(44.4833333, 11.3333333), new LatLng(44.4833333, 11.3333333));

    //object view
    private AutoCompleteTextView addLocation;
    private EditText nameAdd;
    private EditText descriptionAdd;
    private EditText amountAdd;

    private Button buttonAdd;
    private EditText dateInputText;
    private Spinner categorySpinner;
    private Toolbar toolbar;
    private LinearLayout li_planned;
    private EditText repeatPlanned;
    private Spinner occurrenceSpinner;

    private Place place;
    private long catid;
    DBHelper dbHelper;
    Boolean isPlanned = false;

    String occurrence_type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        isPlanned = (Boolean) getIntent().getExtras().getSerializable("planned");
        dbHelper = new DBHelper(this);

        init_editText();
        init_placeAPI();
        init_toolbar();
        init_dateinput();
        init_addbutton();
        init_selectategory();
        if (isPlanned) {
            li_planned.setVisibility(View.VISIBLE);
            init_planned_occurrence();
        } else {
            li_planned.setVisibility(View.GONE);
        }

    }


    private void init_editText() {
        li_planned = (LinearLayout) findViewById(R.id.planned_layout);
        nameAdd = (EditText) findViewById(R.id.add_name);
        descriptionAdd = (EditText) findViewById(R.id.add_description);
        amountAdd = (EditText) findViewById(R.id.add_amount);
        buttonAdd = (Button) findViewById(R.id.add_button);
        dateInputText = (EditText) findViewById(R.id.check_date);
        categorySpinner = (Spinner) findViewById(R.id.add_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);

        if (isPlanned) {
            occurrenceSpinner = (Spinner) findViewById(R.id.add_occurrence);
            repeatPlanned = (EditText) findViewById(R.id.add_repeat);
        }
    }


    private void init_planned_occurrence() {

        final String[] stringArray = getResources().getStringArray(R.array.occurrence);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, R.id.text_spinner, stringArray);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_row);
        occurrenceSpinner.setAdapter(spinnerArrayAdapter);
        occurrenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                occurrence_type = stringArray[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }

    private void init_toolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void init_dateinput() {

        dateInputText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar mcurrentDate = Calendar.getInstance();
                int mYear = mcurrentDate.get(Calendar.YEAR);
                int mMonth = mcurrentDate.get(Calendar.MONTH);
                int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog mDatePicker = new DatePickerDialog(NewItemActivity.this, new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                        //int: the month between 0-11.
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        LocalDate lo = new LocalDate(selectedyear, (selectedmonth + 1), selectedday);

                        String date_string = sdf.format(lo.toDate());
                        dateInputText.setText(date_string);
                    }
                }, mYear, mMonth, mDay);
                mDatePicker.setTitle("Select date");
                if (isPlanned) {
                    LocalDate lo = LocalDate.fromDateFields(new Date());
                    lo = lo.plusDays(1);
                    mDatePicker.getDatePicker().setMinDate(lo.toDate().getTime());
                } else {
                    mDatePicker.getDatePicker().setMaxDate(new Date().getTime());
                }
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
                        startActivity(new Intent(NewItemActivity.this, CategoriesActivity.class));
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


        final String[] categories_string = listItems.toArray(new String[listItems.size()]);
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_row, R.id.text_spinner, categories_string);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_row);
        categorySpinner.setAdapter(spinnerArrayAdapter);


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View selectedItemView, int position, long id) {
                catid = categories_list.get(position).getCategoryID();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });


    }


    private void init_addbutton() {

        try {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name, description;

                    double amount = 0;
                    Date date;
                    long locid;
                    boolean ok = true;
                    Location loc = new Location(null, "", 0, 0);
                    Integer repeat = 0;

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

                    if (categorySpinner.getSelectedItem().toString().isEmpty()) {
                        //  categorySpinner.setError("Select category");
                        ok = false;
                    }


                    if (place == null) {
                        if (addLocation.getText().toString().isEmpty()) {
                            addLocation.setError("Insert Location");
                            ok = false;
                        } else {
                            loc = new Location(null, addLocation.getText().toString(), 0, 0);
                        }
                    } else {
                        loc = new Location(null, place.getAddress().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
                    }


                    date = getDate(dateInputText.getText().toString());
                    if (date.toString().isEmpty()) {
                        dateInputText.setError("Insert Date");
                        ok = false;
                    }

                    if (amountAdd.getText().toString().isEmpty()) {
                        ok = false;
                    } else {

                        amount = Double.valueOf((amountAdd.getText().toString().replace(',', '.')));

                    }

                    if (isPlanned) {

                        if (repeatPlanned.getText().toString().isEmpty()) {
                            repeatPlanned.setError("Insert Repeat time values");
                            ok = false;
                        } else {
                            repeat = Integer.valueOf(repeatPlanned.getText().toString());
                        }

                        if (occurrence_type.isEmpty()) {
                            ok = false;
                        }

                    }
                    if (ok) {
                        locid = dbHelper.getDaoSession().insert(loc);
                        if (isPlanned) {

                            PlannedItem pi = new PlannedItem(null, name, description, date, amount, catid, locid, occurrence_type, repeat);
                            dbHelper.getDaoSession().insert(pi);
                        } else {
                            MoneyItem mi = new MoneyItem(null, name, description, date, amount, catid, locid);
                            dbHelper.getDaoSession().insert(mi);
                        }
                        Toast.makeText(NewItemActivity.this, "Added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(NewItemActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);


                    } else {
                        Toast.makeText(NewItemActivity.this, "Please fill all input", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (NullPointerException x) {
            Toast.makeText(NewItemActivity.this, "Please fill all input!", Toast.LENGTH_LONG).show();

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
        addLocation = (AutoCompleteTextView) findViewById(R.id
                .addLocation);
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
        Toast.makeText(this, "Google Places API connection failed with error code:" + connectionResult.getErrorCode(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}