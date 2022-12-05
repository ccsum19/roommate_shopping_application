package edu.uga.cs.roommate_shopping_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.House;

public class JoinRoomActivity extends AppCompatActivity {

    private Button confirmJoinRoomButton;
    private TextView roomID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_room);

        confirmJoinRoomButton = findViewById(R.id.confirmJoinRoomButton);
        roomID = findViewById(R.id.roomIDEditText);

        confirmJoinRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                joinRoom(roomID.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void joinRoom(final String key){
        final DAO dao = DAO.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("/houses/" + key);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                House house = dataSnapshot.getValue(House.class);
                if (house == null){
                    Toast.makeText(JoinRoomActivity.this, "No house exists with that ID!", Toast.LENGTH_SHORT).show();
                }
                else{
                    dao.addUserToHouse(new House(key, house.getName()));
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
