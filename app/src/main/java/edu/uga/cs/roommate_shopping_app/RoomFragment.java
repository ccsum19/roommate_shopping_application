package edu.uga.cs.roommate_shopping_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;

public class RoomFragment extends Fragment {

    private RecyclerView currentRView;
    private ArrayList<ShoppingList> currentLists;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.fragment_room, container, false);

        currentRView = rootView.findViewById(R.id.current_list_recycler_view);
        currentRView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        DAO dao = DAO.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail() + "/houseID");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final String houseID = dataSnapshot.getValue(String.class);
                DatabaseReference listsRef = database.getReference("/houses/" + houseID + "/lists/");
                listsRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        currentLists = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ShoppingList list = snapshot.getValue(ShoppingList.class);
                            list.setId(snapshot.getKey());
                            if (list.getStatus().equals("open")) {
                                RecyclerView.Adapter currentAdapter = new ShoppingListAdapter(getContext(), currentLists, houseID);
                                currentLists.add(list);
                                currentRView.setAdapter(currentAdapter);
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                TextView listNameView = (TextView) viewHolder.itemView.findViewById(R.id.purchasedList);
                final String listName = listNameView.getText().toString();
                final DAO dao = DAO.getInstance();
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail() + "/houseID");
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final String houseID = dataSnapshot.getValue(String.class);
                        for (ShoppingList list : currentLists) {
                            if (list.getName().equals(listName)) {
                                dao.removeListFromHouse(houseID, list.getId());
                            }
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) { }
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(currentRView);
        return rootView;
    }

}
