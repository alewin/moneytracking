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
    private EditText repeatPlanned;
    private Button buttonAdd;
    private EditText dateInputText;
    private Spinner categorySpinner;
    private Spinner occurrenceSpinner;
    private Toolbar toolbar;
    private LinearLayout li_planned;
    private Place place;
    private long catid, locid;

    MoneyItem money_item;
    PlannedItem planned_item;

    String occurrence_type = "";
    DBHelper dbHelper;
    Boolean isPlanned = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_item);

        isPlanned = (Boolean) getIntent().getExtras().getSerializable("planned");
        li_planned = (LinearLayout) findViewById(R.id.planned_layout);
        dbHelper = new DBHelper(this);

        if (isPlanned) {
            li_planned.setVisibility(View.VISIBLE);
            planned_item = (PlannedItem) (getIntent().getSerializableExtra("planned_item"));

        } else {
            li_planned.setVisibility(View.GONE);
            money_item = (MoneyItem) (getIntent().getSerializableExtra("money_item"));

        }

        init_editText();

        init_toolbar();
        init_dateinput();
        init_addbutton();
        init_selectategory();
        init_placeAPI();

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
                // your code here
            }
        });
    }

    private void init_editText() {
        addLocation = (AutoCompleteTextView) findViewById(R.id.addLocation);
        nameAdd = (EditText) findViewById(R.id.add_name);
        descriptionAdd = (EditText) findViewById(R.id.add_description);
        amountAdd = (EditText) findViewById(R.id.add_amount);
        buttonAdd = (Button) findViewById(R.id.add_button);
        dateInputText = (EditText) findViewById(R.id.check_date);
        categorySpinner = (Spinner) findViewById(R.id.add_category);
        toolbar = (Toolbar) findViewById(R.id.toolbar2);
        buttonAdd.setText("Edit");

        if (isPlanned) {
            planned_item.__setDaoSession(dbHelper.getDaoSession());
            occurrenceSpinner = (Spinner) findViewById(R.id.add_occurrence);
            init_planned_occurrence();
            repeatPlanned = (EditText) findViewById(R.id.add_repeat);
            nameAdd.setText(planned_item.getName());
            String amount = (String.format("%.0f", planned_item.getAmount()));
            amountAdd.setText(amount);
            Date d = planned_item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateInputText.setText(String.valueOf(sdf.format(d.getTime())));
            descriptionAdd.setText(String.valueOf(planned_item.getDescription()));
            addLocation.setText(String.valueOf(planned_item.getLocation().getName()));
            repeatPlanned.setText(String.valueOf(planned_item.getRepeat()));
            catid = planned_item.getCategoryID();
            locid = planned_item.getLocationID();
        } else {
            money_item.__setDaoSession(dbHelper.getDaoSession());

            nameAdd.setText(money_item.getName());
            String amount = (String.format("%.0f", money_item.getAmount()));
            amountAdd.setText(amount);
            Date d = money_item.getDate();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateInputText.setText(String.valueOf(sdf.format(d.getTime())));
            descriptionAdd.setText(String.valueOf(money_item.getDescription()));
            addLocation.setText(String.valueOf(money_item.getLocation().getName()));
            catid = money_item.getCategoryID();
            locid = money_item.getLocationID();
        }
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
                        Intent intent = new Intent(EditActivity.this, CategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

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
                // your code here
            }
        });
        i = 0;
        if (isPlanned) {
            while (i <= listItems.size() && !planned_item.getCategory().getName().equals(listItems.get(i))) {
                i++;
            }
        } else {
            while (i <= listItems.size() && !money_item.getCategory().getName().equals(listItems.get(i))) {
                i++;
            }
        }


        categorySpinner.setSelection(i);


    }


    private void init_addbutton() {

        try {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name, description;
                    double amount = 0;
                    Date date;
                    Integer repeat = 0;
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

                    if (categorySpinner.getSelectedItem().toString().isEmpty()) {
                        //  categorySpinner.setError("Select category");
                        ok = false;
                    }


                    if ((addLocation.getText().toString().isEmpty())) {
                        addLocation.setError("Insert Location");
                        ok = false;
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
                        Location tmp = isPlanned ? planned_item.getLocation() : money_item.getLocation();
                        String item_loc_name = tmp.getName();

                        if (!item_loc_name.equals(addLocation.getText().toString())) {
                            if (place != null) {
                                if (addLocation.getText().toString().equals(place.getAddress().toString())) {
                                    //nuovo place aggiunto
                                    loc = new Location(null, place.getAddress().toString(), place.getLatLng().latitude, place.getLatLng().longitude);
                                } else {
                                    // place modificcato diverso dal testo del editbox che è diverso da quello originale di item, quindi aggiungo loc nullo
                                    loc = new Location(null, addLocation.getText().toString(), 0, 0);
                                }
                            }
                            // cancello vecchia locazione e aggiungo la nuova
                            dbHelper.getDaoSession().delete(tmp);
                            locid = dbHelper.getDaoSession().insert(loc);
                        }

                        if (isPlanned) {
                            Date planned_date = createPlannedDate(occurrence_type, date);

                            planned_item.setAmount(amount);
                            planned_item.setName(name);
                            planned_item.setDescription(description);
                            planned_item.setDate(date);
                            planned_item.setCategoryID(catid);
                            planned_item.setLocationID(locid);

                            //planned
                            planned_item.setRepeat(repeat);
                            planned_item.setOccurrence(occurrence_type);
                            planned_item.setPlannedDate(planned_date);

                            dbHelper.getDaoSession().update(planned_item);


                        } else {
                            money_item.setAmount(amount);
                            money_item.setName(name);
                            money_item.setDescription(description);
                            money_item.setDate(date);
                            money_item.setCategoryID(catid);
                            money_item.setLocationID(locid);
                            dbHelper.getDaoSession().update(money_item);
                        }


                        Toast.makeText(EditActivity.this, "Edited", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditActivity.this, "Please fill all input", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (NullPointerException x) {
            Toast.makeText(EditActivity.this, "Please fill all input!", Toast.LENGTH_LONG).show();

        }
    }

    private Date createPlannedDate(String type, Date d) {

        LocalDate lo = LocalDate.fromDateFields(d);
        // LocalDate llw = LocalDate.parse("01/09/2017", DateTimeFormat.forPattern("dd/MM/yyyy"));
        //  long llro = llw.toDate().getTime();
        //  Date fssds = llw.toDate();

        switch (type) {
            case "Daily":
                lo = lo.plusDays(1);
                break;
            case "Weekly":
                lo = lo.plusWeeks(1);
                break;
            case "Monthly":
                lo = lo.plusMonths(1);
                break;
            case "Yearly":
                lo = lo.plusYears(1);
                break;
        }
        return lo.toDate();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(EditActivity.this, DetailActivity.class);
        if (isPlanned) {
            intent.putExtra("planned_item", planned_item);
        } else {
            intent.putExtra("money_item", money_item);
        }
        intent.putExtra("planned", isPlanned);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }

    public static Date getDate(String datestring) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();

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
        mPlaceArrayAdapter = new PlaceAdapter(this, android.R.layout.simple_list_item_1, BOUNDS_MOUNTAIN_VIEW, null);
        addLocation.setAdapter(mPlaceArrayAdapter);
    }

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            final PlaceAdapter.PlaceAutocomplete item = mPlaceArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            Log.i(LOG_TAG, "Selected: " + item.description);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);

        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            place = places.get(0);
            addLocation.setText(place.getAddress().toString());
            return;


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