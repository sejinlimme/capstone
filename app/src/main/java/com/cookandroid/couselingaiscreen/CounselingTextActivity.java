package com.cookandroid.couselingaiscreen;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CounselingTextActivity extends AppCompatActivity {

    private static final String baseURL = "http://192.168.123.105:5000";
    private EditText InputChat;
    private Button SendText, GetBtn;
    private TextView GetInfom;

    Retrofit mRetrofit = new Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    CounselingTextAPI counselingTextAPI = mRetrofit.create(CounselingTextAPI.class);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselingtextscreen);

        InputChat = findViewById(R.id.chatting);
        SendText = findViewById(R.id.send);
        GetInfom = findViewById(R.id.getInform);
        GetBtn = findViewById(R.id.getBtn);

        SendText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = InputChat.getText().toString();
                textCallback(text);
            }
        });
    }

    public void textCallback(String chatText) {
        counselingTextAPI.getAIReply(chatText).enqueue(new Callback<CounselingTextDTO>() {
            @Override
            public void onResponse(Call<CounselingTextDTO> call, Response<CounselingTextDTO> response) {
                //서버에서 데이터 요청 성공시
                CounselingTextDTO result = response.body();
                Log.d("testt", "결과는 ${result}");


            }

            @Override
            public void onFailure(Call<CounselingTextDTO> call, Throwable t) {
                //서버 요청 실패
                t.printStackTrace();
                Log.d("testt", "에러입니다. ${t.message}");
            }
        });
    }
}