package edu.uga.cs.roommate_shopping_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import edu.uga.cs.roommate_shopping_app.db.DAO;

public class SignUpActivity extends AppCompatActivity {
    private static String TAG = "SignUpActivity";
    private Button signUpButton;
    private TextView email;
    private TextView password;
    private TextView firstName;
    private TextView lastName;
    private TextView username;
    String expression = "[a-zA-Z0-9._-]+@[a-z]+.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE);
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        // getSupportActionBar().hide();

        setContentView(R.layout.activity_sign_up);

        signUpButton = findViewById(R.id.signUpButton);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = findViewById(R.id.emailEditText);
                password = findViewById(R.id.passwordEditText);
                firstName = findViewById(R.id.firstNameEditText);
                lastName = findViewById(R.id.lastNameEditText);
                username = findViewById(R.id.usernameEditText);
                signup(email.getText().toString(),
                        password.getText().toString(),
                        username.getText().toString(),
                        firstName.getText().toString(),
                        lastName.getText().toString());
            }
        });
    }

    public void signup(final String email, String password, final String username, final String firstName, final String lastName) {
        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (!email.isEmpty() && password.length()>6) {
            if (email.matches(expression)) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "REGISTER SUCCESS", Toast.LENGTH_SHORT).show();
                                    DAO dao = DAO.getInstance();
                                    dao.updateUserInfo(email, firstName, lastName, username);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {//just to prevent null pointer exception
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(Uri.parse("https://pbs.twimg.com/profile_images/667582437292032000/huhr_Zic_400x400.jpg"))
                                                .build();
                                        user.updateProfile(profileUpdates)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        Log.d(TAG, "User profile updated.");
                                                    }
                                                });
                                    }//if
                                    Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                                    startActivity(intent);
                                } else {
                                    try{
                                        task.getResult();
                                    }catch (Exception e) {
                                        e.printStackTrace();
                                        Log.d("Fail_register_email",e.getMessage());
                                        Toast.makeText(SignUpActivity.this, "Existed email, Login Please", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
            } else {
                Toast.makeText(SignUpActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(SignUpActivity.this, "Check your email or password", Toast.LENGTH_SHORT).show();
        }
    }
}
