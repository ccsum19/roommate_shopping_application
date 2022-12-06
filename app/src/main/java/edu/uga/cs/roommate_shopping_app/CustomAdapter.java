package edu.uga.cs.roommate_shopping_app;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;
import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.User;

public class CustomAdapter extends BaseAdapter {

    Context context;
    private ShoppingList list;
    ArrayList<ShoppingList> shoppingLists = new ArrayList<ShoppingList>();
    LayoutInflater inflater;
    String houseKey;
    private RecyclerView rView;

    public CustomAdapter(Context applicationContext, ArrayList<ShoppingList> shoppingLists, String houseKey) {
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

    @SuppressLint("RestrictedApi")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        view = inflater.inflate(R.layout.pending_shopping_list, null);

        //TextView listPrice = view.findViewById(R.id.listPrice);
        TextView listName = view.findViewById(R.id.purchasedList);
        //listPrice.setText(shoppingLists.get(i).getPrice().toString());
        final String listKey = shoppingLists.get(i).getId();
        listName.setText(shoppingLists.get(i).getName());
        final Button checkButton = view.findViewById(R.id.completeButton);
        final Button listButton = view.findViewById(R.id.listcheckButton);

        final DAO dao = DAO.getInstance();
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ownerRef = database.getReference("/houses/" + houseKey + "/lists/" + listKey + "/owner");
        ownerRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String ownerName = dataSnapshot.getValue(String.class);
                if ((ownerName != null) && ownerName.equals(dao.getUserLookupEmail())) {
                    checkButton.setVisibility(View.VISIBLE);

                    listButton.setVisibility(View.VISIBLE);

                    checkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getRootView().getContext());
                            alertDialogBuilder.setTitle("Purchase");
                            alertDialogBuilder.setMessage("Please enter the total bill for this purchase.");
                            View viewInflated = LayoutInflater.from(v.getRootView().getContext()).inflate(R.layout.price_field, viewGroup, false);
                            alertDialogBuilder.setView(viewInflated);
                            final TextView priceInput = viewInflated.findViewById(R.id.editText);
                            alertDialogBuilder.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int which) {
                                            final String price = priceInput.getText().toString();
                                            final DAO dao = DAO.getInstance();
                                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                                            DatabaseReference usersRef = database.getReference("/users/");
                                            usersRef.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        User user = snapshot.getValue(User.class);
                                                        dao.setListPrice(houseKey, listKey, price);
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {}
                                            });
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                            dao.setListStatus(houseKey, listKey, "purchased");
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        final FirebaseDatabase database2 = FirebaseDatabase.getInstance();
        DatabaseReference itemRef = database2.getReference("/houses/" + houseKey + "/lists/" + listKey);
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    listButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            View viewInflated2 = LayoutInflater.from(view.getRootView().getContext()).inflate(R.layout.list_field, viewGroup, false);
                            rView = viewInflated2.findViewById(R.id.itemList);
                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(view.getRootView().getContext());
                            alertDialogBuilder.setTitle("Item lists");
                            alertDialogBuilder.setView(viewInflated2);
                            rView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            final ShoppingListItemAdapter adapter = new ShoppingListItemAdapter(list, listKey, houseKey);
                            rView.setAdapter(adapter);

                            alertDialogBuilder.setNegativeButton("CANCEL",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialogBuilder.setPositiveButton("OK",
                                    new DialogInterface.OnClickListener(){
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            AlertDialog alertDialog = alertDialogBuilder.create();
                            alertDialog.show();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return view;
    }
}