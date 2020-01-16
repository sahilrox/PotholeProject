package com.example.potholeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class SignUpActivity extends AppCompatActivity {

    private String TAG="FIREBASE";
    private TextInputEditText name, email, pass, mobile, confpass;
    private Button signup;
    private FirebaseAuth mAuth;
    private FirebaseStorage firebaseStorage;
    private String sname, semail, spass, smobile, sconfpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getSupportActionBar().setTitle("Sign Up");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        mobile = findViewById(R.id.mobile);
        pass = findViewById(R.id.pass);
        confpass = findViewById(R.id.confpass);
        signup = findViewById(R.id.sign_up);

        mAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFields()) {

                    //upload to database
                     sname = name.getText().toString().trim();
                     semail = email.getText().toString().trim();
                     spass = pass.getText().toString().trim();
                     smobile = mobile.getText().toString().trim();
                     sconfpass = confpass.getText().toString().trim();


                    mAuth.createUserWithEmailAndPassword(semail,spass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                sendEmailVerification();
                            } else {
                                Log.w(TAG, "Registration failed", task.getException());
                                Toast.makeText(SignUpActivity.this, task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void sendEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        sendUserData();
                        Toast.makeText(SignUpActivity.this, "Registration Successful",Toast.LENGTH_SHORT).show();
                        Toast.makeText(SignUpActivity.this,"Verification mail sent",Toast.LENGTH_SHORT).show();
                        mAuth.signOut();
                        finish();
                        startActivity(new Intent(SignUpActivity.this,MainActivity.class));
                    }
                    else {
                        Toast.makeText(SignUpActivity.this, "Error in sending verification mail", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean validateFields() {
        boolean result = false;

        sname = name.getText().toString().trim();
        semail = email.getText().toString().trim();
        spass = pass.getText().toString().trim();
        smobile = mobile.getText().toString().trim();
        sconfpass = confpass.getText().toString().trim();

        if(sname.isEmpty() || spass.isEmpty() || sconfpass.isEmpty() || semail.isEmpty() || smobile.isEmpty()) {
            Toast.makeText(this, "Fill all necessary details",Toast.LENGTH_SHORT).show();
        }
        else if(!(spass.equals(sconfpass))) {
            Toast.makeText(this, "Passwords do not match",Toast.LENGTH_SHORT).show();
        }
        else {
            result = true;
        }
        return result;
    }

    private void sendUserData() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("Users");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UserProfile userProfile = new UserProfile(sname, semail, smobile, spass);
                myRef.child(mAuth.getUid()).setValue(userProfile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Toast.makeText(SignUpActivity.this,"Testing database",Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
