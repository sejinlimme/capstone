package com.cookandroid.couselingaiscreen;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Api;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CounselingTextActivity extends AppCompatActivity {

    private EditText InputChat;
    private Button SendText, GetBtn;
    private TextView GetInfom;
    private String url = "http://192.168.123.105:5000";
    private String POST = "POST";
    private String GET = "GET";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counselingtextscreen);

        InputChat = findViewById(R.id.chatting);
        SendText = findViewById(R.id.send);
        GetInfom = findViewById(R.id.getInform);
        GetBtn = findViewById(R.id.getBtn);

        SendText.setOnClickListener(view -> {
            String text = InputChat.getText().toString();
            if(text.isEmpty()) {
                InputChat.setError("This cannot be empty for post request");
            } else {
                sendRequest(POST, "getname", "name", text);
            }
        });

        GetBtn.setOnClickListener(view -> {
            sendRequest(GET, "getfact", null, null);
        });
    }

    void sendRequest(String type, String method, String paramtext, String param) {
        String fullURL = url+"/"+method+(param==null?"":"/"+param);
        Request request;

        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS).build();

        if(type.equals(POST)) {
            RequestBody formBody = new FormBody.Builder()
                    .add(paramtext, param)
                    .build();

            request = new Request.Builder()
                    .url(fullURL)
                    .post(formBody)
                    .build();
        } else{
            request = new Request.Builder()
                    .url(fullURL)
                    .build();
        }

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                final String responseData = response.body().string();

                CounselingTextActivity.this.runOnUiThread(() -> GetInfom.setText(responseData));
            }
        });
    }
}