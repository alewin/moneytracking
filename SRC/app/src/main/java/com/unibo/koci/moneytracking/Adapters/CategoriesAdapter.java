package com.unibo.koci.moneytracking.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.unibo.koci.moneytracking.Activities.CategoriesActivity;
import com.unibo.koci.moneytracking.Entities.Category;
import com.unibo.koci.moneytracking.R;

import java.util.List;

/**
 * Created by koale on 17/08/17.
 */

public class CategoriesAdapter extends ArrayAdapter<Category> {

    private Context mContext;


    public CategoriesAdapter(Context context, List<Category> categories) {

        super(context, 0, categories);
        this.mContext = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Category cat = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.category_item, parent, false);
        }

        TextView catName = (TextView) convertView.findViewById(R.id.category_title);
        TextView catID = (TextView) convertView.findViewById(R.id.category_id);
        ImageView btn_delete = (ImageView) convertView.findViewById(R.id.cat_delete);

        btn_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ((CategoriesActivity) mContext).deleteCat(v);
            }
        });

        catName.setText(cat.getName());
        catID.setText(cat.getCategoryID().toString());
        return convertView;
    }

}