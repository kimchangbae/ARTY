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
    private static String TAG = "MyPage";

    private FirebaseAuth mAuth;
    private UserApiClient userApiClient;

    @Override
    protected void onPause() {
        super.onPause();
        firebaseLogout();

    }

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
        int hashCode = userApiClient.hashCode();
        Log.d("MyPage","MyPage.kakaoUser의 해쉬코드 [" +hashCode+ "]");


        Button btn_logout = findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 사용자 로그아웃
                Log.d(TAG,"로그아웃");
                kakaoTalkLogout();
                goToMain();
            }
        });

        Button btn_delete_user = findViewById(R.id.btn_delete_user);
        btn_delete_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 회원탈퇴
                kakaoTalkUnlink();
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
        userApiClient = UserApiClient.getInstance();
        userApiClient.logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                Log.d(TAG,"----------------------------------------------------");
                Log.d("AuthApplication", "logoutFunction");
                Log.d(TAG,"----------------------------------------------------");

                if(throwable != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d("AuthApplication", "카카오톡 에러 발생" + throwable.getMessage());
                    Log.d(TAG,"----------------------------------------------------");
                }

                return null;
            }
        });
    }

    //
    public void kakaoTalkUnlink(){
        userApiClient = UserApiClient.getInstance();
        userApiClient.me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                Log.d(TAG,"----------------------------------------------------");
                Log.d(TAG,"계정삭제 대상 : " + user.getKakaoAccount().getEmail());
                Log.d(TAG,"----------------------------------------------------");
                if(throwable != null) {
                    throwable.getMessage();
                }
                return null;
            }

        });
        userApiClient.unlink(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                Log.d("AuthApplication", "logoutFunction");

                if(throwable != null)
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d("AuthApplication", "카카오톡 에러 발생" + throwable.getMessage());
                Log.d(TAG,"----------------------------------------------------");
                return null;
            }
        });
    }


    public void firebaseLogout() {
        mAuth.getInstance().signOut();
        Log.d(TAG,"----------------------------------------------------");
        Log.d("MyPage", "파이어베이스 로그아웃");
        Log.d(TAG,"----------------------------------------------------");
    }

    private void goToMain() {
        Intent intent = new Intent(MyPage.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}