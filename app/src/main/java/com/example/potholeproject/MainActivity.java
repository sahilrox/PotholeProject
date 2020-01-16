package com.example.potholeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private String TAG = "FIREBASE";
    private FirebaseAuth mAuth;
    private EditText email, password;
    private ProgressDialog progressDialog;
    private Button login, signup;
    private View mainLayout;
    private TextView forgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainLayout = findViewById(R.id.mainLayout);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        email = findViewById(R.id.email);
        password = findViewById(R.id.pass);

        login = findViewById(R.id.login_btn);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().isEmpty() || password.getText().toString().isEmpty())
                {
                    Snackbar.make(mainLayout,"Fill email and password",Snackbar.LENGTH_SHORT).show();
                }
                else
                {
                    validate(email.getText().toString(),password.getText().toString());
                }
            }
        });

        forgotPassword = findViewById(R.id.forgotPass);
        progressDialog = new ProgressDialog(this);

        email.setText("");
        password.setText("");

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPassActivity.class));
            }
        });

        signup = findViewById(R.id.sign_up_btn);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SignUpActivity.class));
            }
        });
    }

    private void validate(String email, String pass) {
        progressDialog.setMessage("Verifying User...");
        progressDialog.show();

        mAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    progressDialog.dismiss();
                    checkEmailVerification();
                }
                else {
                    String errorMsg = showError(task.getException());
                    Snackbar.make(mainLayout, errorMsg, Snackbar.LENGTH_SHORT).show();
                    Log.w("Task error",task.getException().toString());
                    progressDialog.dismiss();
                }
            }
        });

    }

    private String showError(Exception exception) {
        String msg = "Login Failed";
        if (exception.getLocalizedMessage().trim().equalsIgnoreCase("There is no user record corresponding to this identifier. The user may have been deleted.")) {
            msg = "Login Failed, Invalid email ID";
        }
        else if(exception.getLocalizedMessage().trim().equalsIgnoreCase("The email address is badly formatted.")) {
            msg = "Email Address is badly formatted";
        }
        else if(exception.getLocalizedMessage().trim().equalsIgnoreCase("The password is invalid or the user does not have a password.")) {
            msg = "Login failed, Invalid password";
        }
        return msg;
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.


    }

    private void checkEmailVerification() {
        FirebaseUser user = mAuth.getCurrentUser();
        boolean emailflag = user.isEmailVerified();

        if(emailflag) {
            finish();
            Snackbar.make(mainLayout, "Login Successful",Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this,SubmitInfo.class));
        }
        else {
            Snackbar.make(mainLayout, "Verify your email", Snackbar.LENGTH_SHORT).show();
            mAuth.signOut();
        }
    }
}
