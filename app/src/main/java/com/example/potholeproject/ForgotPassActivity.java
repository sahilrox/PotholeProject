package com.example.potholeproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassActivity extends AppCompatActivity {

    private EditText resetEmail;
    private Button resetPass;
    private FirebaseAuth mAuth;
    private View forgotLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);
        getSupportActionBar().setTitle("Forgot Password");
        forgotLayout = findViewById(R.id.forgotPass);

        resetEmail = findViewById(R.id.reset_email);
        resetPass = findViewById(R.id.reset_pass);
        mAuth = FirebaseAuth.getInstance();

        resetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = resetEmail.getText().toString().trim();
                if(email.isEmpty()) {
                    Snackbar.make(forgotLayout,"Enter your email ID",Snackbar.LENGTH_SHORT).show();
                }
                else {
                    mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Snackbar.make(forgotLayout,"Password Reset email sent",Snackbar.LENGTH_SHORT).show();
                                finish();
                                startActivity(new Intent(ForgotPassActivity.this, MainActivity.class));
                            }
                            else {
                                Toast.makeText(ForgotPassActivity.this,task.getException().getLocalizedMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
