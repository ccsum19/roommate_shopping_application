package edu.uga.cs.roommate_shopping_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.User;

public class SettingsFragment extends Fragment {

    TextView editFirstName;
    TextView editLastName;
    Button changeNameButton;
    Button leaveRoomButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        editFirstName = rootView.findViewById(R.id.settingsEditFirstName);
        editLastName = rootView.findViewById(R.id.settingsEditLastName);
        changeNameButton = rootView.findViewById(R.id.changeNameButton);
        leaveRoomButton = rootView.findViewById(R.id.leaveRoomButton);

//        if(HOUSE ID EXISTS){
//            leaveRoomButton.setVisibility(View.VISIBLE);
//        }
//        else{
//            leaveRoomButton.setVisibility(View.INVISIBLE);
//        }

        changeNameButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                final DAO dao = DAO.getInstance();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail());
                userRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        String firstNameField = editFirstName.getText().toString();
                        String lastNameField = editLastName.getText().toString();
                        User user = dataSnapshot.getValue(User.class);
                        if(firstNameField.equals("")){
                            firstNameField = user.getFirstname();
                        }
                        if(lastNameField.equals("")){
                            lastNameField = user.getLastname();
                        }

                        dao.updateUserInfo(dao.getUserLookupEmail(), firstNameField, lastNameField, user.getUsername());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        leaveRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                DAO dao = DAO.getInstance();
                dao.leaveHouse();
                leaveRoomButton.setVisibility(View.INVISIBLE);
            }
        });

        return rootView;
    }
}
