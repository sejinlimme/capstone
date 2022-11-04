package com.cookandroid.couselingaiscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CounselingCallActivity_logout extends AppCompatActivity{
    protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.counselingcallscreen_logout);

            ImageButton recBtn = (ImageButton) findViewById(R.id.recBtn);
            ImageButton stopBtn = (ImageButton) findViewById(R.id.stopBtn);
            ImageButton stop_btn = (ImageButton) findViewById(R.id.stop_btn);


        stop_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        recBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recBtn.setVisibility(View.INVISIBLE);
                stopBtn.setVisibility(View.VISIBLE);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recBtn.setVisibility(View.VISIBLE);
                stopBtn.setVisibility(View.INVISIBLE);
            }
        });
    }


}
