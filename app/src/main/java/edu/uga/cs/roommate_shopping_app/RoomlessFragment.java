package edu.uga.cs.roommate_shopping_app;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class RoomlessFragment extends Fragment {

    Button createRoomButton;
    Button joinRoomButton;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =inflater.inflate(R.layout.fragment_roomless, container, false);

        createRoomButton = rootView.findViewById(R.id.createRoomButton);
        joinRoomButton = rootView.findViewById(R.id.joinRoomButton);

        // Takes user to CreateRoomActivity
        createRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), CreateRoomActivity.class);
                startActivity(intent);
            }
        });

        // Takes user to JoinRoomActivity
        joinRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), JoinRoomActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
