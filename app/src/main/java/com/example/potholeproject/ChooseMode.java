package com.example.potholeproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class ChooseMode extends AppCompatActivity {

    private boolean civilianSelected = true;

    private ImageButton cButton;
    private ImageButton aButton;
    private Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_mode);

        cButton = findViewById(R.id.civilianButton);
        aButton = findViewById(R.id.authorityButton);
        proceed = findViewById(R.id.continueButton);

        setButtonText();
        setActiveButton();

        Button.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view.getId() == R.id.civilianButton){
                    civilianSelected = true;
                } else {
                    civilianSelected = false;
                }

                setActiveButton();
                setButtonText();
            }
        };

        cButton.setOnClickListener(listener);
        aButton.setOnClickListener(listener);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if(civilianSelected == true){
                    intent = new Intent(ChooseMode.this,MainActivity.class);
                } else {
                    intent = new Intent(ChooseMode.this, AdminDashboardActivity.class);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });
    }

    private void setActiveButton() {
        if(civilianSelected){
            cButton.setBackgroundColor(getResources().getColor(R.color.active));
            aButton.setBackgroundColor(getResources().getColor(R.color.inactive));
        } else {
            cButton.setBackgroundColor(getResources().getColor(R.color.inactive));
            aButton.setBackgroundColor(getResources().getColor(R.color.active));
        }
    }

    private void setButtonText() {
        if(civilianSelected == true){
            proceed.setText("Login as Civilian");
        } else {
            proceed.setText("Login as Authority");
        }
    }
}
