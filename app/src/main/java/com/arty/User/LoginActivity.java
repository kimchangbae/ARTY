package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.Common.Common;
import com.arty.Qna.QnaMainActivity;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;


import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.regex.Pattern;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    final String CollectionPath = "USER_ACCOUNT";

    private FirebaseFirestore   firebaseFirestore;
    private FirebaseAuth mAuth;

    boolean isKakaoUser = false;
    private UserApiClient userApiClient;
    KakaoApplication kakaoApplication = new KakaoApplication();

    public Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        common = new Common();

    }

    @Override
    protected void onStart() {
        super.onStart();

        if(mAuth.getCurrentUser() != null) {
            Log.d(TAG, "유저명 ----------> ["+mAuth.getCurrentUser().getEmail()+"]");
            //goToMain();
        } else {
            Log.d(TAG, "접속 유저 없음");
        }
    }

    public void goToMain() {
        Intent intent = new Intent(LoginActivity.this, QnaMainActivity.class);
        startActivity(intent);
        finish();
    }

    // 일반 로그인
    public void btn_login(View view) {
        TextView edit_lgn_email = findViewById(R.id.edit_lgn_email);
        TextView edit_lgn_pswd = findViewById(R.id.edit_lgn_pswd);
        String email = edit_lgn_email.getText().toString();
        String password = edit_lgn_pswd.getText().toString();

        Log.d("UserJoin", "email : " + email);
        Log.d("UserJoin", "password : " + password);

        if(common.validationEmail(email)) {
            try {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Log.d("UserJoin", "로그인 성공");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d("UserJoin","currentUser --->" + user.getEmail());
                                    goToMain();
                                } else {
                                    Toast.makeText(getApplicationContext(),"이메일과 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            } catch(Exception e) {
                Toast.makeText(getApplicationContext(),"이메일 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(),"이메일 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    // 카카오 로그인 버튼 클릭 이벤트
    public void btn_kakao_login(View view) {
        userApiClient = UserApiClient.getInstance();

        // 사용자 기기에 카톡이 설치되어 있는지 확인.
        boolean isInstallKaTalk = userApiClient.isKakaoTalkLoginAvailable(LoginActivity.this);
        Log.d(TAG,"[카카오톡 설치여부----> " + isInstallKaTalk + "]");

        if(isInstallKaTalk) {
            // 카카오톡으로 로그인
            //userApiClient.loginWithKakaoTalk(LoginActivity.this,callback);

            // 카카오 계정으로 로그인
            userApiClient.loginWithKakaoAccount(LoginActivity.this,kakaoApplication.callback2);
        } else {
            userApiClient.loginWithKakaoAccount(LoginActivity.this,callback);
        }
    }

    // 카카오 로그인 연동
    Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {

        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            Log.d(TAG,"[callback 호출]");
            if(oAuthToken != null) {
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        if(user != null) {
                            Log.d(TAG, "카카오 사용자 정보(이메일 : " +user.getKakaoAccount().getEmail()+")");
                            Log.d(TAG, "카카오 사용자 정보(닉네임 : " +user.getKakaoAccount().getProfile().getNickname()+")");

                            if(!getUserDataForKakao(user)) {
                                //setUserDataForKakao(user);
                                //Intent intent = new Intent(LoginActivity.this,KakaoJoin.class);
                                //startActivity(intent);
                            } else {
                                //goToMain();
                            }
                        }
                        if(throwable != null) {
                            Log.d(TAG, "카카오톡 사용자 정보 요청 실패!!");
                        }
                        return null;
                    }
                });

                //Intent intent = new Intent(LoginActivity.this, QnaMainActivity.class);
                //startActivityForResult(intent, 101);
                //finish();
            }
            if(throwable != null) {
                Log.d(TAG, "카카오 로그인 실패(throwable : " +throwable.getMessage()+")");
            }
            return null;
        }
    };



    public void setUserDataForKakao(User kakaoUser) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        final String randomKey = UUID.randomUUID().toString();

        UserAccount user = new UserAccount();
        user.setuId(randomKey);
        user.setKakaoId(kakaoUser.getId());
        user.setEmail(kakaoUser.getKakaoAccount().getEmail());
        user.setUserNm(kakaoUser.getKakaoAccount().getProfile().getNickname());

        firebaseFirestore
                .collection(CollectionPath)
                .document(randomKey)
                .set(user);
    }

    public boolean getUserDataForKakao(User kakaoUser) {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore
                .collection(CollectionPath)
                .whereEqualTo("kakaoId",kakaoUser.getId())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document : task.getResult()) {
                                isKakaoUser = true;
                                Log.d(TAG,"document --> " + document.getData());
                            }
                        }
                    }
                });
        return isKakaoUser;
    }

    public void userJoin(View view) {
        Intent intent = new Intent(this, UserJoin.class);
        startActivity(intent);
    }

    public void logout(View view) {
        UserApiClient.getInstance().logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                if(throwable != null) {
                    Log.e(TAG,"카카오톡 로그아웃 실패" + throwable.getMessage());
                } else {
                    Log.e(TAG,"카카오톡 로그아웃 성공!!!");
                    Intent intent = new Intent();
                }
                return null;
            }
        });
    }
}