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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class MoneyItemAdapter extends RecyclerView.Adapter<MoneyItemAdapter.ViewHolder> {

    private List<MoneyItem> moneyItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtAmount;
        public TextView txtDate;
        public ImageView iconitem;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.itemlist_title);
            txtAmount = (TextView) v.findViewById(R.id.itemlist_amount);
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
                intent.putExtra("money_item", moneyItems.get(position));
                intent.putExtra("planned", false);
                v.getContext().startActivity(intent);
                notifyDataSetChanged();

            }
        };
        holder.layout.setOnClickListener(titleListener);


        String name = moneyItems.get(holder.getAdapterPosition()).getName().toString();

        double d_amount = moneyItems.get(holder.getAdapterPosition()).getAmount();
        DecimalFormat df = new DecimalFormat("#.00");
        String amount = df.format(d_amount);
        //String amount = (String.format("%.0f",d_amount ));


        Date d = (moneyItems.get(holder.getAdapterPosition()).getDate());
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        holder.txtTitle.setText(name);
        holder.txtAmount.setText(amount + "€");
        holder.txtDate.setText(sdf.format(d.getTime()));


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