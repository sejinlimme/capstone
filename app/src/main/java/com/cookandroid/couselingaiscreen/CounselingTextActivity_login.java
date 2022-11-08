package com.cookandroid.couselingaiscreen;

import androidx.appcompat.app.AppCompatActivity; //안드로이드의 하위버전을 지원하는 Activity

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;     //여러개의 Window를 가질 수 있고 이러한 Window들은 WindowManager가 관리를 합니다.
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;      //어댑터(adapter)라는 객체를 갖는다는 점입니다. 어댑터는 어댑터 뷰와 자식 뷰들 사이를 이어주는 중간 역할을 합니다.
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;  //Request: 요청객체
import com.android.volley.RequestQueue;
import com.android.volley.Response; //응답 (지정된 유형(예: 문자열, 이미지 또는 JSON)에 따라 전달할 파싱된 응답을 캡슐화합니다.)
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;
//예외구문, 문법적인 구조가 맞지 않을때, 제기슨 구성 이름이 null인경우,반환하는 메소드에서 변환할수 없는 문자열 있는 경우
//상수를 다른 타입에 사용하는 경우, 제이슨 배열에서 사용하는 매개변수인 인텍스가 범위를 벗어난 경우, 제이슨 객체가 없는 경우
import org.json.JSONObject;  //데이터를 구조적으로 표현하기 위한 일종의 포맷(Format)으로 네트워크를 통해 데이터를 주고받을 때 사용되는 경량의 데이터 형식을 의미합니다.

import java.util.HashMap;   //많은 양의 데이터를 저장



public class CounselingTextActivity_login extends AppCompatActivity {

    String message;
    ListView list; //어댑터가 데이터를 가공해서 제공하면 어댑터 뷰는 그 데이터를 출력하는 역할
    EditText messageText;
    Button submitButton;
    ImageButton chattingstopbtn;
    TextView reply;
    String url = "http://192.168.219.106:5000/chat";

    InputMethodManager imm;





    HashMap data = new HashMap();  //HashMap은 저장공간보다 값이 추가로 들어오면 List처럼 저장공간을 추가로 늘리는데 List처럼 저장공간을 한 칸씩 늘리지 않고 약 두배로 늘립니다.

    RequestQueue mRequestQueue;
    StringRequest stringRequest;
    String rep;


    String TAG = CounselingTextActivityAdapter.class.getName();
    private Object Task;
    private boolean task;
    private FirebaseAuth firebaseAuth;
    private Object AuthResult;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);  //값을 유지하며 항상 사용해야 하는 경우라도 화면이 세로모드에서 가로모드로 변경될 경우 전역변수에 설정한 값이 모두 초기화 된다. 이런 경우 변경된 값을 유지할때 사용
        setContentView(R.layout.counselingtextscreen_login);
        //setContent View:XML에 정의된 각 위젯들을 정의된 속성을 지정하고 상하관계에 맞춘 뒤 메모리에 올려야 합니다. 이러한 일련의 작업을 소스상에서 제공하는 게 setContentView() 함수입니다.
        //키보드가 뷰를 밀어올리는 것을 방지
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        final CounselingTextActivityAdapter arrayAdapter = new CounselingTextActivityAdapter(getApplicationContext(), R.layout.counselingtextscreen_send); //final 지연변수 상수화
        final ListView listView = (ListView) findViewById(R.id.chat_history); //얘랑
        listView.setAdapter(arrayAdapter);  //View를 드래그하여 화면에 올리면 그 위에서 다양한 데이터를 올릴 수 있다. 데이터를 올리는 부분에서 뷰와 데이터를 연결해주는 다리역할을 하는것이 어댑터(Adapter) 이다. //얘만 뺴고
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL); //새로운 item 이 add 되었을 때 focus 이동을 할지를 결정하는 속성이다.
        listView.setAdapter(arrayAdapter);

        //to scroll the list view to bottom on data change

        //Observer (옵저버) 는 '관측자' 혹은 '감시자' 라는 뜻이다. 말 그대로 무언가를 감시하는 역할]]]

        arrayAdapter.registerDataSetObserver(new DataSetObserver() {  //registerDataSetObserver(DataSetObserver observer)를 통해서 observer를 Adapter에 연결해야 한다.
            @Override
            public void onChanged() {  //메서드를 정의하는 Observer 객체를 만듭니다.
                super.onChanged();  //부모클래스의 필드값이나 메소드를 직접 부를 때 붙여넣는 겁니다
                listView.setSelection(arrayAdapter.getCount() - 1); //ListView row의 원하는 위치에 화면을 둘수가 있다.
            }
        });

        messageText = (EditText) findViewById(R.id.chatting);
        submitButton = (Button) findViewById(R.id.send);




        submitButton.setOnClickListener(new View.OnClickListener() { //이벤트 리스너를 등록하고 있습니다.
            @Override
            public void onClick(View v) { //layout설정 xml에 추가 되어 View에서 클릭 되었을 떄 정의된 이름의 함수를 호출하여 작동 시킨다.
                message = messageText.getText().toString();  // getText()함수는 String 클래스 타입을 리턴하지 않고, Editable 인터페이스 타입을 리턴합니다. 그래서 String 타입으로 텍스트를 사용하고자 한다면, Editable 인터페이스가 제공하는 toString() 함수를 호출하여 String 타입으로 변환해야 합니다.
                imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

                sendMessage(arrayAdapter, message);

                data.put("message",message);
//                messageText.setText("");
                postData(data, arrayAdapter); //webview에 post로 데이터 넘기는 방법
                messageText.setText(""); //Text Object에 텍스트를 선언 하는 메서드 입니다.
                imm.hideSoftInputFromWindow(messageText.getWindowToken(),0);
            }
        });

        chattingstopbtn = (ImageButton) findViewById(R.id.chattingstopbtn);




        chattingstopbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

                Intent intent = new Intent(CounselingTextActivity_login.this, MarkActivity.class);
                startActivity(intent);
            }

        });

    }


    public void postData(HashMap data, final CounselingTextActivityAdapter adapter){

        mRequestQueue = Volley.newRequestQueue(this);

        //(JsonRequest)JsonArrayRequest,JsonObjectRequest URL을 지정하고 응답에 따라 JSON 객체 또는 배열을 각각 가져옵니다.

        JsonObjectRequest jsonobj = new JsonObjectRequest(Request.Method.POST, url,new JSONObject(data),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) { //respone:서버로부터 응답받을때 사용함(간략한 코드로 서버로 부터 데이터를 받아 올 수 있습니다.)

                        try {
                            final String s = response.getString("message"); // getString: receiveMessage 안에 있는 String 값을 구 할 수가 있다.
                            receiveMessage(adapter, s);
                        } catch (JSONException e) {
                            e.printStackTrace(); //에러 메세지의 발생 근원지를 찾아서 단계별로 에러를 출력
                        }
                    }},
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "Error: " + error.toString());   //오류 로그 보는 줄
                    }
                }
        );



        jsonobj.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(

                20000 ,

                com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES,

                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        mRequestQueue.add(jsonobj);

    }


    private boolean receiveMessage(CounselingTextActivityAdapter adapter, String msg) {
        adapter.add(new ResponseMessage(msg, false));
        return true;
    }


    private boolean sendMessage(CounselingTextActivityAdapter adapter, String msg) {
        adapter.add(new ResponseMessage(msg, true));
        return true;
    }
}