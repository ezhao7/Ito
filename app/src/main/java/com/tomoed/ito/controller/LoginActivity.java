package com.tomoed.ito.controller;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.tomoed.ito.R;
import com.tomoed.ito.model.User;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailField, passwordField;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private static final String TAG = "Login_Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailField = findViewById(R.id.login_edittext_email);
        passwordField = findViewById(R.id.login_edittext_password);
        findViewById(R.id.login_button_register).setOnClickListener(this);
        findViewById(R.id.login_button_login).setOnClickListener(this);

        setupFirebaseAuth();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.login_button_login) {
            //Check if the fields are filled out.
            if (!isEmpty(emailField.getText().toString()) && !isEmpty(passwordField.getText().toString())) {
                Log.d(TAG, "onClick: Attempting to authenticate.");
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailField.getText().toString(), passwordField.getText().toString())
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d(TAG, "onComplete: Authentication complete.");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "onFailure: Authentication failed.");
                    }
                });
            } else {
                Toast.makeText(LoginActivity.this, "You didn't fill in all the fields.", Toast.LENGTH_SHORT).show();
            }
        } else if (i == R.id.login_button_register) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            this.finish();
        }
    }

    public void setupFirebaseAuth(){
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //Check if email is verified.
                    if (user.isEmailVerified()) {
                        Log.d(TAG, "onAuthStateChanged: Signed in " + user.getUid());
                        Toast.makeText(LoginActivity.this, "Authenticated with: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                        getUserAccountData();
                    } else {
                        Toast.makeText(LoginActivity.this, "Email is not verified.\nCheck your inbox.", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged: Signed out.");
                }
            }
        };
    }

    public void getUserAccountData(){
        Log.d(TAG, "getUserAccountData: Getting the user account data.");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("users")
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    User user = (User) singleSnapshot.getValue(User.class);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("USER", user);
                    startActivity(intent);
                    finish();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, databaseError.toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }

    public boolean isEmpty(String string) {
        return string.equals("");
    }
}


