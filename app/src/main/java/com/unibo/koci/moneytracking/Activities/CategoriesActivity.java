package com.unibo.koci.moneytracking.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.unibo.koci.moneytracking.Entities.Category;
import com.unibo.koci.moneytracking.Entities.DaoSession;
import com.unibo.koci.moneytracking.R;

import java.util.ArrayList;

/**
 * Created by koale on 17/08/17.
 */

public class CategoriesActivity  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        Toolbar toolbar_categories = (Toolbar) findViewById(R.id.toolbar_categories);
        setSupportActionBar(toolbar_categories);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
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
                        .setMessage("What do you want to do next?")
                        .setView(catEditText)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String cat_name = String.valueOf(catEditText.getText());

                                Category loc = new Category(null,cat_name);
                                long cat_id = getDaoSession().insert(cat_name);

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
        TextView catTextView = (TextView) parent.findViewById(R.id.category_title);
        TextView catidTextView = (TextView) parent.findViewById(R.id.category_id);

        String cat_name = String.valueOf(catTextView.getText());
        String cat_id = String.valueOf(catidTextView.getText());


        Category c = daoSession.getCategoryDao().load(Long.valueOf( cat_id));
        daoSession.getCategoryDao().delete(c);



        updateUI();
    }

    private void updateUI() {
        ArrayList<String> catList = new ArrayList<>();

/*

        SQLiteDatabase db = mHelper.getReadableDatabase();
        Cursor cursor = db.query(TaskContract.TaskEntry.TABLE,
                new String[]{TaskContract.TaskEntry._ID, TaskContract.TaskEntry.COL_TASK_TITLE},
                null, null, null, null, null);
        while (cursor.moveToNext()) {
            int idx = cursor.getColumnIndex(TaskContract.TaskEntry.COL_TASK_TITLE);
            taskList.add(cursor.getString(idx));
        }

        if (mAdapter == null) {
            mAdapter = new ArrayAdapter<>(this,
                    R.layout.item_todo,
                    R.id.task_title,
                    taskList);
            mTaskListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(taskList);
            mAdapter.notifyDataSetChanged();
        }

        cursor.close();
        db.close();
*/

    }
}