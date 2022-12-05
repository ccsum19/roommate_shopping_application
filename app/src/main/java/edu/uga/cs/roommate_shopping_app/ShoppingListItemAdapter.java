package edu.uga.cs.roommate_shopping_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;
import edu.uga.cs.roommate_shopping_app.db.DAO;

public class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemAdapter.ViewHolder> {
    private ArrayList<ShoppingList.ListItem> list;
    private String listID;
    private String houseID;

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBox;
        public TextView itemName;
        public ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.list_item_checkBox);
            itemName = itemView.findViewById(R.id.list_item_name);
        }
    }

    public ShoppingListItemAdapter(ShoppingList list, String listID, String houseID) {
        ArrayList<ShoppingList.ListItem> alist = new ArrayList<>();
        this.listID = listID;
        this.houseID = houseID;
        if (list != null && list.getItems() != null) {
            for (HashMap.Entry<String, Object> item: list.getItems().entrySet()) {
                String name = item.getKey();
                HashMap<String, Object> map = (HashMap<String, Object>) item.getValue();
                boolean isChecked = (boolean) map.get("checked");
                alist.add(new ShoppingList.ListItem(name, isChecked));
            }
        }
        this.list = alist;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.shopping_list_item, parent, false);
        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(contactView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        final ShoppingList.ListItem item = list.get(position);
        CheckBox checkBox = viewHolder.checkBox;
        checkBox.setChecked(item.isChecked());
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                DAO dao = DAO.getInstance();
                dao.checkHouseItem(item.getName(), listID, houseID, isChecked);

            }
        });
        TextView textView = viewHolder.itemName;
        textView.setText(item.getName());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}