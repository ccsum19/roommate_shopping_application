package edu.uga.cs.roommate_shopping_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.uga.cs.roommate_shopping_app.db.DAO;
import edu.uga.cs.roommate_shopping_app.dom.User;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private String TAG = "mainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mDrawerLayout = this.findViewById(R.id.drawer_layout);

        NavigationView navigationView = findViewById(R.id.nav_view);
        if (savedInstanceState == null) {
            DAO dao = DAO.getInstance();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail() + "/houseID");
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String houseKey = dataSnapshot.getValue(String.class);
                    if (houseKey == null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomlessFragment()).commit();
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomFragment()).commit();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomlessFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_list);
        }
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        Intent intent;
                        switch (menuItem.getItemId()) {
                            case R.id.nav_logout:
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                mAuth.signOut();
                                intent = new Intent(getApplicationContext(), SplashActivity.class);
                                startActivity(intent);
                                break;

                            case R.id.nav_list:
                                DAO dao = DAO.getInstance();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail() + "/houseID");
                                userRef.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        String houseKey = dataSnapshot.getValue(String.class);
                                        if (houseKey == null) {
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomlessFragment()).commit();
                                        } else {
                                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RoomFragment()).commit();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                                break;

                            case R.id.nav_settings:
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                                break;

                            case R.id.nav_list_pending:
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PendingFragment()).commit();
                                break;

                            case R.id.nav_list_completed:
                                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new CompletedFragment()).commit();
                                break;
                        }
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();
                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here
                        return true;
                    }
                }
        );
        DAO dao = DAO.getInstance();
        final View header = navigationView.getHeaderView(0);

        TextView emailTextView = header.findViewById(R.id.nav_email);
        emailTextView.setText(dao.getUserLookupEmail().replaceAll(",", "."));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    TextView firstLastTextView = header.findViewById(R.id.nav_profile_name);
                    String firstName = user.getFirstname() != null ? user.getFirstname() : "";
                    String lastName = user.getLastname() != null ? user.getLastname() : "";
                    firstLastTextView.setText(firstName + " " + lastName);
                    TextView houseIDTextView = header.findViewById(R.id.house_id);
                    String houseIDString = user.getHouseID() != null ? "House ID: " + user.getHouseID() : "No House ID!";
                    houseIDTextView.setText(houseIDString);
                    String houseName = user.getHouseName() != null ? user.getHouseName() : "Split List";
                    actionbar.setTitle(houseName);
                } else Log.d(TAG, "null");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;

            case R.id.action_add:
                Intent intent = new Intent(this, NewShoppingListActivity.class);
                startActivity(intent);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        DAO dao = DAO.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("/users/" + dao.getUserLookupEmail());
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getHouseID().equals("")) {
                        MenuItem plus_item = menu.findItem(R.id.action_add);
                        plus_item.setVisible(false);
                    }
                } else Log.d(TAG, "null");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        // inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }
}

