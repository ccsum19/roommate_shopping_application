package edu.uga.cs.roommate_shopping_app.db;


import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import edu.uga.cs.roommate_shopping_app.dom.House;
import edu.uga.cs.roommate_shopping_app.dom.InboxMessage;
import edu.uga.cs.roommate_shopping_app.dom.ShoppingList;
import edu.uga.cs.roommate_shopping_app.dom.User;

public class DAO {
    private String TAG = "DAO";
    private static DAO dao;
    private FirebaseDatabase database;
    private DatabaseReference ref;

    private ValueEventListener listListener;
    private DAO() {
        this.database = FirebaseDatabase.getInstance();
        this.ref = this.database.getReference("");

    }//constructor
    public static DAO getInstance() {
        if (dao == null) {
            dao = new DAO();
        }
        return dao;
    }//getInstance()

    public String addHouse(String name) {
        Map<String, Object> childUpdates = new HashMap<>();
        DatabaseReference housesRef = this.database.getReference("/houses/");
        String key = housesRef.push().getKey();
        childUpdates.put(key + "/name", name);
        housesRef.updateChildren(childUpdates);
        return key;
    }

    public void addHouseList(final String name) {
        DatabaseReference userRef = this.database.getReference("/users/" + getUserLookupEmail() + "/houseID");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String houseID = dataSnapshot.getValue(String.class);
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference listRef = database.getReference("/houses/" + houseID + "/lists/");
                    ShoppingList list = new ShoppingList(new HashMap<String, Object>(), new HashMap<String, Object>(), new HashMap<String, Object>(),"open", name, "");
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(listRef.push().getKey(), list.toMap());
                    listRef.updateChildren(childUpdates);
                }
                else{
                    Log.w(TAG, "loadPost:onCancelled");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    public void addItemToHouseList(String itemName, String listID, String houseID) {
        checkHouseItem(itemName, listID, houseID, false);
    }

    public void removeItemFromHoustList(String itemName, String listID, String houseID) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/items/" + itemName);
        listRef.removeValue();
    }

    public void checkHouseItem(String itemName, String listID, String houseID, boolean isChecked) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/items/" + itemName);
        DatabaseReference basketRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/basket/" + itemName);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("checked", isChecked);
        listRef.updateChildren(childUpdates);
        if(isChecked){
            Map<String, Object> childUpdates2 = new HashMap<>();
            childUpdates2.put("checked", true);
            basketRef.updateChildren(childUpdates2);
            removeItemFromHoustList(itemName, listID, houseID);
        }
        else{
            removeItemFromBasketList(itemName, listID, houseID);
        }
    }

    public void checkBasketItem(String itemName, String listID, String houseID, boolean isChecked) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/basket/" + itemName);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("checked", isChecked);
        listRef.updateChildren(childUpdates);
    }

    public void putInBasket(String itemName, String listID, String houseID){
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/basket/" + itemName);

    }

    public void removeItemFromBasketList(String itemName, String listID, String houseID) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/basket/" + itemName);
        listRef.removeValue();
    }


    //stored in RTDB; not supported by Firebase Authentication; key = email
    public void updateUserInfo(String email, String firstName, String lastName, String username) {
        //RTDB doesnt like 'dots'
        email = email.replaceAll("\\.", ",");
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + email + "/firstname", firstName);
        childUpdates.put("/users/" + email + "/lastname", lastName);
        childUpdates.put("/users/" + email + "/username", username);
        ref.updateChildren(childUpdates);
    }

    public void addUserToHouse(House house) {
        DatabaseReference userRef = this.database.getReference("/users/" + getUserLookupEmail());
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("houseID", house.getId());
        childUpdates.put("houseName", house.getName());
        userRef.updateChildren(childUpdates);
    }

    public void leaveHouse() {
        DatabaseReference userRef = this.database.getReference("/users/" + getUserLookupEmail() + "/houseID");
        userRef.removeValue();
        DatabaseReference userRef2 = this.database.getReference("/users/" + getUserLookupEmail() + "/houseName");
        userRef2.removeValue();
    }

    public void inviteUserToHouse(final String invitee_email, final House house) {
        final String email = invitee_email.replaceAll("\\.", ",");
        DatabaseReference userRef = this.database.getReference("/users/" + getUserLookupEmail());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    final String firstName = user.getFirstname();
                    final String lastName = user.getLastname();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference inboxRef = database.getReference("/users/" + email + "/inbox/" + house.getId());
                    String message = firstName + " " + lastName + " has invited you to join " + house.getName() + ".";
                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put("message", message);
                    childUpdates.put("invitedBy", getUserLookupEmail());
                    childUpdates.put("houseName", house.getName());
                    childUpdates.put("houseID", house.getId());
                    inboxRef.updateChildren(childUpdates);
                } else {
                    Log.w(TAG, "inviteUserToHouse:onCancelled1");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.w(TAG, "inviteUserToHouse:onCancelled1", databaseError.toException());
            }
        });
    }

    public void acceptInvitation(InboxMessage message) {
        addUserToHouse(new House(message.getHouseID(), message.getHouseName()));
        DatabaseReference userInboxMessageRef = this.database.getReference("/users/" + getUserLookupEmail() + "/inbox/" + message.getHouseID());
        userInboxMessageRef.removeValue();
    }

    public void rejectInvitation(InboxMessage message) {
        DatabaseReference userInboxMessageRef = this.database.getReference("/users/" + getUserLookupEmail() + "/inbox/" + message.getHouseID());
        userInboxMessageRef.removeValue();
    }

    public String getUserLookupEmail() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        return user.getEmail().replaceAll("\\.", ",");
    }

    public void removeListFromHouse(String houseID, String listID) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID);
        listRef.removeValue();
    }

    public void setListStatus(String houseID, String listID, String status) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/status");
        listRef.setValue(status);
    }

    public void setListPrice(String houseID, String listID, String priceString) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/price");
        listRef.setValue(priceString);
    }
    public void setListOwner(String houseID, String listID, String owner) {
        DatabaseReference listRef = this.database.getReference("/houses/" + houseID + "/lists/" + listID + "/owner");
        listRef.setValue(owner);
    }
}
