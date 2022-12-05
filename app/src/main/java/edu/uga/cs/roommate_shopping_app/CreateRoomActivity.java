package edu.uga.cs.roommate_shopping_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.House;

public class CreateRoomActivity extends AppCompatActivity {

    private TextView roomName;
    private Button confirmCreateRoomButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);

        roomName = findViewById(R.id.roomNameEditText);
        confirmCreateRoomButton = findViewById(R.id.confirmCreateRoomButton);

        confirmCreateRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createRoom(roomName.getText().toString());
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }

    public void createRoom(String name){
        DAO dao = DAO.getInstance();
        String key = dao.addHouse(name);
        dao.addUserToHouse(new House(key, name));
    }
}