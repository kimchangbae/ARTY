package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.Common.Common;
import com.arty.Qna.QnaMainActivity;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.kakao.sdk.user.model.User;


import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    final String CollectionPath = "USER_ACCOUNT";

    private FirebaseFirestore   firebaseFirestore;
    private FirebaseAuth mAuth;

    private UserApiClient userApiClient;

    public Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        common = new Common();
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userApiClient = UserApiClient.getInstance();


        onStart();

        setContentView(R.layout.activity_login);

    }

    private long clickTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 뒤로가기 버튼 클릭 시
        if(keyCode == KeyEvent.KEYCODE_BACK) {


            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                Toast.makeText(getApplicationContext(), "프로그램이 종료 되었습니다.", Toast.LENGTH_SHORT).show();
                killARTY();

                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }


    protected void killARTY() {
        // 태스크를 백그라운드로 이동
        moveTaskToBack(true);

        // 액티비티 종료 + 태스크 리스트에서 지우기
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }

        // 앱 프로세스 종료
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public void onStart() {
        super.onStart();

        // 파이어베이스 유저 체크
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "파이어베이스 유저정보 ----------> [" + mAuth.getCurrentUser().getEmail() + "]");
            goToMain();
        } else {
            Log.d(TAG, "----------------------------------------------------");
            Log.d(TAG, "LoginActivity - 파이어베이스 유저정보 없음");
            Log.d(TAG, "----------------------------------------------------");
        }

        // 카톡 유저 체크
        userApiClient.accessTokenInfo(new Function2<AccessTokenInfo, Throwable, Unit>() {
            @Override
            public Unit invoke(AccessTokenInfo accessTokenInfo, Throwable throwable) {

                if (accessTokenInfo != null) {
                    Log.d(TAG, "카카오톡 유저정보 ----------> [" + accessTokenInfo + "]");
                    goToMain();
                } else {
                    Log.d(TAG, "----------------------------------------------------");
                    Log.d(TAG, "LoginActivity - 카카오톡 유저정보 없음");
                    Log.d(TAG, "----------------------------------------------------");
                }
                return null;
            }
        });
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
        userApiClient       = UserApiClient.getInstance();

        userApiClient.accessTokenInfo(new Function2<AccessTokenInfo, Throwable, Unit>() {
            @Override
            public Unit invoke(AccessTokenInfo accessTokenInfo, Throwable throwable) {
                if(accessTokenInfo != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d(TAG,"accessTokenInfo ---> [" +accessTokenInfo+"]");
                    Log.d(TAG,"----------------------------------------------------");
                }
                return null;
            }
        });

        // 사용자 기기에 카톡이 설치되어 있는지 확인.
        if(userApiClient.isKakaoTalkLoginAvailable(LoginActivity.this)) {
            int hashCode = userApiClient.hashCode();
            Log.d(TAG,"----------------------------------------------------");
            Log.d(TAG,TAG+".카카오 사용자 해쉬코드 [" +hashCode+ "]");
            Log.d(TAG,"----------------------------------------------------");

            // 카카오 계정으로 로그인
            //userApiClient.loginWithKakaoAccount(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
            // 카카오톡으로 로그인
            userApiClient.loginWithKakaoTalk(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {

                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if(oAuthToken != null) {
                        Log.d(TAG,"----------------------------------------------------");
                        Log.d(TAG,"[콜백 함수 호출]");
                        Log.d(TAG,"oAuthToken : "+oAuthToken);
                        Log.d(TAG,"----------------------------------------------------");
                        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                            @Override
                            public Unit invoke(User user, Throwable throwable) {
                                Log.d(TAG,"----------------------------------------------------");
                                Log.d(TAG,"[ME CALL BACK]");
                                Log.d(TAG,"----------------------------------------------------");
                                if(user != null) {
                                    Log.d(TAG,"----------------------------------------------------");
                                    Log.d(TAG, "카카오 사용자 정보[" +user+"]");
                                    Log.d(TAG,"----------------------------------------------------");

                                    UserAccount userAccount = new UserAccount();
                                    userAccount.setKakaoId(user.getId());
                                    userAccount.setEmail(user.getKakaoAccount().getEmail());


                                    firebaseFirestore   = FirebaseFirestore.getInstance();
                                    firebaseFirestore
                                            .collection(CollectionPath)
                                            .whereEqualTo("kakaoId",user.getId())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(Task<QuerySnapshot> task) {
                                                    Intent intent;

                                                    if(task.getResult().isEmpty()) {
                                                        //신규 카톡 유저 처리
                                                        Log.d(TAG,"----------------------------------------------------");
                                                        Log.d(TAG,"등록되지 않은 카카오톡 유저 입니다.");
                                                        Log.d(TAG,"----------------------------------------------------");
                                                        intent = new Intent(LoginActivity.this, KakaoJoin.class);

                                                        intent.putExtra("userAccount",userAccount);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        //기존 카톡 유저 처리
                                                        Log.d(TAG,"----------------------------------------------------");
                                                        Log.d(TAG,"등록된 카카오톡 유저 입니다.");
                                                        Log.d(TAG,"----------------------------------------------------");
                                                        intent = new Intent(LoginActivity.this, QnaMainActivity.class);
                                                        finish();
                                                    }
                                                    startActivity(intent);
                                                }
                                            });
                                }
                                if(throwable != null) {
                                    Log.d(TAG,"----------------------------------------------------");
                                    Log.d(TAG, "카카오톡 사용자 정보 요청 실패!!" + throwable.getMessage());
                                    Log.d(TAG,"----------------------------------------------------");
                                }
                                return null;
                            }
                        });
                    }
                    if(throwable != null) {
                        Log.d(TAG,"----------------------------------------------------");
                        Log.d(TAG, "카카오 로그인 실패(throwable : " +throwable.getMessage()+")");
                        Log.d(TAG,"----------------------------------------------------");
                    }
                    return null;
                }
            });
        }
    }

    public void userJoin(View view) {
        Intent intent = new Intent(this, UserJoin.class);
        startActivity(intent);
    }

    public void findPassword(View view) {
        Toast.makeText(getApplicationContext(),"구현중 이에요.", Toast.LENGTH_SHORT).show();
        return;
    }
}