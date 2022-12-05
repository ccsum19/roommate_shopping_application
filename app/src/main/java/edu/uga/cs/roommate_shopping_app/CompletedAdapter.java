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
import java.util.Locale;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;
import edu.uga.cs.roommate_shopping_app.dom.User;

public class CompletedAdapter extends BaseAdapter {

    Context context;
    ArrayList<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
    LayoutInflater inflater;
    String houseKey;

    public CompletedAdapter(Context applicationContext, ArrayList<ShoppingList> shoppingLists, String houseKey){
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
        view = inflater.inflate(R.layout.purchased_shopping_list, null);
        TextView listPrice = view.findViewById(R.id.listPrice);
        TextView listName = view.findViewById(R.id.listName);
        TextView avgPrice = view.findViewById(R.id.price_average);
        String price = "";
        int pricenum;
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final String listKey = shoppingLists.get(i).getId();
        listName.setText(shoppingLists.get(i).getName());

        price = shoppingLists.get(i).getPrice();
        listPrice.setText("total : " + price);
        pricenum = Integer.parseInt(price);
        final DAO dao = DAO.getInstance();

        DatabaseReference usersRef= database.getReference("/users/");
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int numRoommates = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    User user = snapshot.getValue(User.class);
                    if (user.getHouseID() != null && user.getHouseID().equals(houseKey)) {
                        numRoommates++;
                    }
                }
                avgPrice.setText("average : " + pricenum/numRoommates);            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });



        final Button completedButton = view.findViewById(R.id.completedButton);

        completedButton.setVisibility(View.VISIBLE);
        completedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completedButton.setText("COMPLETE");
                dao.setListStatus(houseKey, listKey, "Completed");
            }
        });

        DatabaseReference priceRef = database.getReference("/houses/" + houseKey + "/lists/" + listKey + "/price");

        return view;
    }
}
