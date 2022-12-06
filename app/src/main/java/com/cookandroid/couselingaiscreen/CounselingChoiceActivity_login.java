package com.cookandroid.couselingaiscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class CounselingChoiceActivity_login extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselingscreenchoice_login);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Button call_btn = (Button) findViewById(R.id.call_btn);
        call_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), CounselingCallActivity_login.class);
                startActivity(intent);
            }
        });

        Button text_btn = (Button) findViewById(R.id.text_btn);
        text_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                Intent intent = new Intent(getApplicationContext(), CounselingTextActivity_login.class);
                startActivity(intent);
            }
        });


    }

}
