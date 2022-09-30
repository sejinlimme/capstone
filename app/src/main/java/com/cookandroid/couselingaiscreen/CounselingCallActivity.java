package com.cookandroid.couselingaiscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CounselingCallActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselingcallscreen);


        ImageButton stop_btn = (ImageButton) findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MarkActivity.class);
                startActivity(intent);
            }
        });



        ImageButton speak_btn = (ImageButton) findViewById(R.id.speak_btn);
        speak_btn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplicationContext(), MarkActivity.class);
                startActivity(intent);
            }
        });

    }
}