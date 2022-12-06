package com.cookandroid.couselingaiscreen;

import android.content.Context;
import android.os.Bundle;
import android.os.PowerManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MarkActivity extends AppCompatActivity {

    //<uses-permission android:name="android.permission.WAKE_LOCK" />


    String str;
    RatingBar ratingBar;
    Button markstore;
    DatabaseReference mRatingBarCh;
    PowerManager powerManager;
    PowerManager.WakeLock wakeLock;

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    //private DatabaseReference databaseReference = database.getReference();

    private DatabaseReference mRootRef = database.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markscreen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        ratingBar = (RatingBar) findViewById(R.id.performanceEvaluation);
        markstore = (Button) findViewById(R.id.markstore);
        //DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        //final DatabaseReference mRatingBarCh = rootRef.child("rating");


        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                markstore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        str = String.valueOf(rating);
                        str = Float.toString(rating);




                        mRootRef.child("rating").setValue(str)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {


                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        //PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
                                        //PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                                        //        "MyApp::MyWakelockTag");
                                        //wakeLock.acquire();

                                        Toast.makeText(getApplicationContext(), "별점은: " + str, Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(), "별점 저장에 실패하셨습니다.", Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                        //float rating = ratingBar.getRating();
                        //mRatingBarCh.child("rating").setValue(String.valueOf(rating));
                });
                //Toast.makeText(getApplicationContext(), "New Rating: " + rating, Toast.LENGTH_SHORT).show();
                //str = String.valueOf(rating);
                //str = Float.toString(rating);

            }
        });



    }


}
