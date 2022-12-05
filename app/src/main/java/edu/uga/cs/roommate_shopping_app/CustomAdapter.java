package edu.uga.cs.roommate_shopping_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;
import edu.uga.cs.roommate_shopping_app.db.DAO;

public class CustomAdapter extends BaseAdapter {

    Context context;
    ArrayList<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
    LayoutInflater inflater;
    String houseKey;

    public CustomAdapter(Context applicationContext, ArrayList<ShoppingList> shoppingLists, String houseKey){
        this.context = applicationContext;
        this.shoppingLists = shoppingLists;
        this.houseKey = houseKey;
        inflater = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return shoppingLists.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.pending_shopping_list, null);
        //TextView listPrice = view.findViewById(R.id.listPrice);
        TextView listName = view.findViewById(R.id.purchasedList);
        //listPrice.setText(shoppingLists.get(i).getPrice().toString());
        final String listKey = shoppingLists.get(i).getId();
        listName.setText(shoppingLists.get(i).getName());
        final Button checkButton = view.findViewById(R.id.completeButton);

        final DAO dao = DAO.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ownerRef = database.getReference("/houses/" + houseKey + "/lists/" + listKey + "/owner");
        ownerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ownerName = dataSnapshot.getValue(String.class);
                if ((ownerName != null) && ownerName.equals(dao.getUserLookupEmail())){
                    checkButton.setVisibility(View.VISIBLE);
                    checkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dao.setListStatus(houseKey, listKey, "purchased");
                        }
                    });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
        return view;
    }

}
