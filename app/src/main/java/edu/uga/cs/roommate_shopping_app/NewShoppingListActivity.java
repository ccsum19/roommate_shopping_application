package edu.uga.cs.roommate_shopping_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import edu.uga.cs.roommate_shopping_app.db.DAO;

public class NewShoppingListActivity extends AppCompatActivity {
    private Button createButton;
    private TextView listNameText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_shopping_list);

        createButton = findViewById(R.id.createButton);
        listNameText = findViewById(R.id.listnameEditText);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String listName = listNameText.getText().toString();
                if (!listName.equals("")) {
                    DAO dao = DAO.getInstance();
                    dao.addHouseList(listName);
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(NewShoppingListActivity.this, "Listname cannot be empty.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
