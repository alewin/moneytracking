package com.unibo.koci.moneytracking.Adapters;

/*
 * Created by koale on 12/08/17.
 */

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.unibo.koci.moneytracking.Activities.DetailActivity;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.R;

import org.joda.time.DateTime;

import java.util.List;


public class MoneyItemAdapter extends RecyclerView.Adapter<MoneyItemAdapter.ViewHolder> {

    private List<MoneyItem> moneyItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtDate;
        public ImageView iconitem;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.itemlist_title);
            txtDescription = (TextView) v.findViewById(R.id.itemlist_description);
            txtDate = (TextView) v.findViewById(R.id.itemlist_date);
            iconitem = (ImageView) v.findViewById(R.id.itemlist_icon);
        }
    }

    public void add(int position, MoneyItem item) {
        moneyItems.add(position, item);
        notifyItemInserted(position);
        notifyDataSetChanged();

    }

    public void remove(int position) {
        moneyItems.remove(position);
        notifyItemRemoved(position);
    }


    // Provide a suitable constructor (depends on the kind of dataset)
    public MoneyItemAdapter(List<MoneyItem> myDataset) {
        moneyItems = myDataset;
    }


    @Override
    public MoneyItemAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.money_item_list, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final OnClickListener titleListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("item", moneyItems.get(position));
                v.getContext().startActivity(intent);
                notifyDataSetChanged();

            }
        };
        holder.layout.setOnClickListener(titleListener);


        String name = moneyItems.get(holder.getAdapterPosition()).getName().toString();
        String description = String.valueOf(moneyItems.get(holder.getAdapterPosition()).getAmount());
        DateTime date = new DateTime(moneyItems.get(holder.getAdapterPosition()).getDate());

        holder.txtTitle.setText(name);
        holder.txtDescription.setText(description + "€");
        holder.txtDate.setText(date.toString("dd/MM/yy"));


        if (moneyItems.get(holder.getAdapterPosition()).getAmount() > 0) {
            holder.iconitem.setImageResource(R.drawable.thumb_up);
        } else {
            holder.iconitem.setImageResource(R.drawable.thumb_down);
        }


    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return moneyItems.size();
    }

}