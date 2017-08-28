package com.unibo.koci.moneytracking.Adapters;

/*
 * Created by koale on 12/08/17.
 */

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.unibo.koci.moneytracking.Activities.DetailActivity;
import com.unibo.koci.moneytracking.Activities.SettingsActivity;
import com.unibo.koci.moneytracking.Entities.Category;
import com.unibo.koci.moneytracking.Entities.MoneyItem;
import com.unibo.koci.moneytracking.MainActivity;
import com.unibo.koci.moneytracking.R;

import org.joda.time.DateTime;

import static android.R.attr.onClick;
import static android.R.id.input;


public class MoneyItemAdapter extends RecyclerView.Adapter<MoneyItemAdapter.ViewHolder> {

    private List<MoneyItem> moneyItems;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle;
        public TextView txtDescription;
        public TextView txtDate;
        public ImageView iconitem;
        public ImageButton settingCard;
        public View layout;

        public ViewHolder(View v) {
            super(v);
            layout = v;
            txtTitle = (TextView) v.findViewById(R.id.itemlist_title);
            txtDescription = (TextView) v.findViewById(R.id.itemlist_description);
            txtDate = (TextView) v.findViewById(R.id.itemlist_date);
            iconitem = (ImageView) v.findViewById(R.id.itemlist_icon);
            settingCard = (ImageButton) v.findViewById(R.id.setting_card);
        }
    }

    public void add(int position, MoneyItem item) {
        moneyItems.add(position, item);
        notifyItemInserted(position);
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
                Toast.makeText(v.getContext(), "clicked=" + position, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("item",moneyItems.get(position));
                v.getContext().startActivity(intent);

                //                remove(holder.getAdapterPosition());

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


        holder.settingCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopupMenu(holder.settingCard, position);
            }
        });

    }

    private void showPopupMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_card_option, popup.getMenu());
        //popup.setOnMenuItemClickListener(new MyMenuItemClickListener(position));
        popup.show();
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return moneyItems.size();
    }

}
/*
class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

    private int position;
    public MyMenuItemClickListener(int positon) {
        this.position=positon;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {

            case R.id.Not_interasted_catugury:
                String RemoveCategory=mDataSet.get(position).getCategory();
                // mDataSet.remove(position);
                //notifyItemRemoved(position);
                // notifyItemRangeChanged(position,mDataSet.size());

                mySharedPreferences.saveStringPrefs(Constants.REMOVE_CTAGURY,RemoveCategory,MainActivity.context);
                Toast.makeText(MainActivity.context, "Add to favourite", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.No_interasted:
                mDataSet.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,mDataSet.size());
                Toast.makeText(MainActivity.context, "Done for now", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.delete:
                mySharedPreferences.deletePrefs(Constants.REMOVE_CTAGURY,MainActivity.context);
            default:
        }
        return false;
    }
}
*/