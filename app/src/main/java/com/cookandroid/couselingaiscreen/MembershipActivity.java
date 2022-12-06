package com.cookandroid.couselingaiscreen;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;


public class MembershipActivity extends AppCompatActivity{

    private static final String TAG = "MembershipActivity";
    EditText mName, mIdText, mPasswordText, mPasswordcheckText;
    Button mregisterBtn;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.membershipscreen);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //파이어베이스 접근 설정
        firebaseAuth =  FirebaseAuth.getInstance();

        mName = findViewById(R.id.personName);
        mIdText = findViewById(R.id.id2);
        mPasswordText = findViewById(R.id.password);
        mPasswordcheckText = findViewById(R.id.password2);
        mregisterBtn = findViewById(R.id.Membership_button);

        mregisterBtn.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view){
                //가입 정보 가져오기
                final String id = mIdText.getText().toString().trim();
                String pwd = mPasswordText.getText().toString().trim();
                String pwdcheck = mPasswordcheckText.getText().toString().trim();

                if(pwd.equals(pwdcheck)){
                    //파이어베이스에 신규계정 등록하기OnCompleteListener
                    firebaseAuth.createUserWithEmailAndPassword(id, pwd).addOnCompleteListener(MembershipActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //가입 성공시
                            if (task.isSuccessful()){
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                String email = user.getEmail();
                                String uid = user.getUid();
                                String name = mName.getText().toString().trim();

                                //해쉬맵 테이블을 파이어베이스 데이터베이스에 저장
                                HashMap<Object,String> hashMap = new HashMap<>();

                                hashMap.put("uid",uid);
                                hashMap.put("email",email);
                                hashMap.put("name",name);

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                DatabaseReference reference = database.getReference("Users");
                                reference.child(uid).setValue(hashMap);

                                //가입이 이루어져을시 가입 화면을 빠져나감.
                                Intent intent = new Intent(MembershipActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(MembershipActivity.this, "회원가입에 성공하셨습니다.", Toast.LENGTH_SHORT).show();
                            } else{
                                Toast.makeText(MembershipActivity.this, "이미 존재하는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                return;  //해당 메소드 진행을 멈추고 빠져나감
                            }
                        }
                    });
                    //비밀번호 오류시
                } else{
                    Toast.makeText(MembershipActivity.this, "비밀번호가 틀렸습니다. 다시 입력해 주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

    }

}
