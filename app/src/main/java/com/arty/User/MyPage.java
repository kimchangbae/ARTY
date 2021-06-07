package com.arty.User;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.arty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Function;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MyPage extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private UserApiClient userApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            Log.d("MyPage", "현재 접속중인 이메일 -----> [" +email+"]");
            Log.d("MyPage", "현재 접속중인 name -----> [" +name+"]");

        }

        userApiClient = UserApiClient.getInstance();


        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseLogout(v);
                kakaoTalkLogout();
                goToMain();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 카카오톡 로그아웃
    public void kakaoTalkLogout() {
        userApiClient.logout(function);
        //userApiClient.unlink(function);
    }

    // 카카오톡 로그아웃 예외처리
    Function1<Throwable, Unit> function = new Function1<Throwable, Unit>() {
        @Override
        public Unit invoke(Throwable throwable) {
            if(throwable != null)
                Log.d("MyPage", "카카오톡 에러 발생" + throwable.getMessage());
            return null;
        }
    };

    public void firebaseLogout(View view) {
        Log.d("MyPage", "파이어베이스 로그아웃");
        mAuth.getInstance().signOut();

        goToMain();
    }

    private void goToMain() {
        Intent intent = new Intent(MyPage.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}