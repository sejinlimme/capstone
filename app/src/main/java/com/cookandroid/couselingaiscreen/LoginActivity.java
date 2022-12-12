package com.cookandroid.couselingaiscreen;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends AppCompatActivity {

    Button mLoginBtn, mRegisterBtn;
    EditText mEmailText, mPasswordText;
    private FirebaseAuth firebaseAuth;
    //HashMap data = new HashMap();
    //String url = "http://192.168.219.106:5000/uid";
    //RequestQueue mRequestQueue;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginscreen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        firebaseAuth = FirebaseAuth.getInstance();

        mLoginBtn = findViewById(R.id.login_button);
        mRegisterBtn = findViewById(R.id.membership_btn);
        mEmailText = findViewById(R.id.EmailAddress);
        mPasswordText = findViewById(R.id.Password);

        //가입 버튼 눌렀을 때
        mRegisterBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, MembershipActivity.class));
            }
        });

        //로그인 버튼 눌렀을 때
        mLoginBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                String email = mEmailText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                firebaseAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            private String uid;

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){

                                    /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    if (user != null)
                                    {
                                        this.uid = user.getUid();
                                        data.put("uid", uid);
                                        postData(data);
                                    }*/

                                    Intent intent = new Intent(LoginActivity.this, CounselingChoiceActivity_login.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
/*
    public void postData(HashMap data) {
        mRequestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        jsonobj.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(

                20000 ,

                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(jsonobj);
    }*/

}
