package edu.uga.cs.roommate_shopping_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;

public class ListActivity extends AppCompatActivity {

    private Button addItemButton, markAsPurchasedButton, markAsDeleateButton;
    private TextView addItemName;
    private RecyclerView rView;
    private Toolbar toolbar;

    private ShoppingList list;
    private FirebaseDatabase database;
    private String listID;
    private String listName;
    private String houseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        rView = findViewById(R.id.listItems);
        rView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        database = FirebaseDatabase.getInstance();
        listID = getIntent().getExtras().getString("listID");
        listName = getIntent().getExtras().getString("listName");
        houseID = getIntent().getExtras().getString("houseID");
        toolbar = findViewById(R.id.shopping_list_name_toolbar);
        toolbar.setTitle(listName);
        DatabaseReference listRef = database.getReference( "/houses/" + houseID + "/lists/" + listID);
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list = dataSnapshot.getValue(ShoppingList.class);
                final ShoppingListItemAdapter adapter = new ShoppingListItemAdapter(list, listID, houseID);
                rView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                TextView itemNameView = (TextView) viewHolder.itemView.findViewById(R.id.list_item_name);
                String itemName = itemNameView.getText().toString();
                DAO dao = DAO.getInstance();
                dao.removeItemFromHoustList(itemName, listID, houseID);
            }
        });
        itemTouchHelper.attachToRecyclerView(rView);

        addItemButton = findViewById(R.id.add_item_button);
        addItemName = findViewById(R.id.add_item_name);
        addItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addItemName.getText().equals("")) {
                    String newItemName = addItemName.getText().toString();
                    addItemName.setText("");
                    DAO dao = DAO.getInstance();
                    dao.addItemToHouseList(newItemName, listID, houseID);
                }
            }
        });

        markAsPurchasedButton = findViewById(R.id.purchasebtn);
        markAsPurchasedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DAO dao = DAO.getInstance();

                dao.setListStatus(houseID,listID, "basket");
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                dao.setListOwner(houseID, listID, dao.getUserLookupEmail());
                dao.setListPrice(houseID, listID, "0");
                FirebaseDatabase database = FirebaseDatabase.getInstance();
               // DatabaseReference usersRef= database.getReference("/users/");
    }
        });

    }
}