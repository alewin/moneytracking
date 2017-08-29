package com.unibo.koci.moneytracking.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.unibo.koci.moneytracking.Adapters.CategoriesAdapter;
import com.unibo.koci.moneytracking.Database.DBHelper;
import com.unibo.koci.moneytracking.Entities.Category;
import com.unibo.koci.moneytracking.Entities.DaoSession;
import com.unibo.koci.moneytracking.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by koale on 17/08/17.
 */

public class CategoriesActivity extends AppCompatActivity {

    private DBHelper dbHelper;
    private DaoSession daoSession;
    private ListView catListView;
    private List<Category> categories_list;
    private CategoriesAdapter catadapter;
    private Toolbar toolbar_categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        dbHelper = new DBHelper(this);
        daoSession = dbHelper.getDaoSession();

        init_toolbar();
        init_listadapter();
        updateUI();
    }

    private void init_toolbar() {
        toolbar_categories = (Toolbar) findViewById(R.id.toolbar_categories);
        setSupportActionBar(toolbar_categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private void init_listadapter() {

        categories_list = new ArrayList();
        catadapter = new CategoriesAdapter(this, categories_list);
        catListView = (ListView) findViewById(R.id.listview_cat);
        catListView.setAdapter(catadapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category_add, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_cat:
                final EditText catEditText = new EditText(this);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add a new category")
                        .setMessage("Insert name")
                        .setView(catEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cat_name = String.valueOf(catEditText.getText());

                                Category loc = new Category(null, cat_name);
                                long cat_id = dbHelper.getDaoSession().insert(loc);
                                updateUI();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteCat(View view) {
        View parent = (View) view.getParent();
        TextView catidTextView = (TextView) parent.findViewById(R.id.category_id);
        String cat_id = String.valueOf(catidTextView.getText());
        DaoSession daoSession = dbHelper.getDaoSession();
        Category c = daoSession.getCategoryDao().load(Long.valueOf(cat_id));
        daoSession.getCategoryDao().delete(c);
        updateUI();
    }


    private void updateUI() {
        categories_list = dbHelper.getDaoSession().getCategoryDao().loadAll();
        catadapter.clear();
        catadapter.addAll(categories_list);
        catadapter.notifyDataSetChanged();
    }
}