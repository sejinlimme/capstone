package com.cookandroid.couselingaiscreen;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MarkActivity extends AppCompatActivity {

    private RatingBar mark;
    private DatabaseReference mRatingBarCh;
    private Button markstore;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.markscreen);

        mark = (RatingBar) findViewById(R.id.performanceEvaluation);
        markstore = (Button) findViewById(R.id.markstore);
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mRatingBarCh = rootRef.child("ratings");

        mark.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float level, boolean reselected) {
                mRatingBarCh.child("ratings").setValue(String.valueOf(level));
            }
        });
    }
}
