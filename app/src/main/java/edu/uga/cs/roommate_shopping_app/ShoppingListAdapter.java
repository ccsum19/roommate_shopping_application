package edu.uga.cs.roommate_shopping_app;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {
    private ArrayList<ShoppingList> lists;
    private Context context;
    private String houseID;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            itemName = itemView.findViewById(R.id.purchasedList);
        }
    }

    public ShoppingListAdapter(Context context, ArrayList<ShoppingList> lists, String houseID) {
        if (lists == null) {
            this.lists = new ArrayList<>();
        } else {
            this.lists = lists;
        }
        this.context = context;
        this.houseID = houseID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.shopping_list, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ShoppingList list = lists.get(position);
        TextView textView = viewHolder.itemName;
        textView.setText(list.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("houseID", houseID);
                intent.putExtra("listName", list.getName());
                intent.putExtra("listID", list.getId());
                context.getApplicationContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lists.size();
    }


}
