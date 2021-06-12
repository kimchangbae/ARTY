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
import com.arty.Main.MainActivity;
import com.arty.Qna.QnaMain;
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
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.kakao.sdk.user.model.User;


import org.jetbrains.annotations.NotNull;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Login extends AppCompatActivity {
    private static String TAG = "Login";
    final static String COLLECTION_PATH = "USER_ACCOUNT";
    private long clickTime = 0;

    private FirebaseFirestore   mDB;
    private FirebaseAuth        mAuth;

    private UserApiClient userApiClient;

    public Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        common = new Common();
        mAuth           = FirebaseAuth.getInstance();
        mDB             = FirebaseFirestore.getInstance();
        userApiClient   = UserApiClient.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();

        // 1차 파이어베이스 유저 체크
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "파이어베이스 유저정보 ----------> [" + mAuth.getCurrentUser().getEmail() + "]");
            Log.d(TAG, "파이어베이스 TenantId ----------> [" + mAuth.getCurrentUser().getTenantId()+ "]");

            getUserIdAndGo(mAuth.getCurrentUser().getEmail());
        } else {
            // 2차 카카오톡 유저 체크
            Log.d(TAG, "----------------------------------------------------");
            Log.d(TAG, "Login - 파이어베이스 유저정보 없음");
            Log.d(TAG, "----------------------------------------------------");

            userApiClient.accessTokenInfo(new Function2<AccessTokenInfo, Throwable, Unit>() {
                @Override
                public Unit invoke(AccessTokenInfo accessTokenInfo, Throwable throwable) {
                    if (accessTokenInfo != null) {
                        Log.d(TAG, "카카오톡 유저정보 ----------> [" + accessTokenInfo.getId() + "]");
                        getUserIdAndGo2(accessTokenInfo.getId());
                    } else {
                        // 3차 로그인 뷰 생성
                        Log.d(TAG, "----------------------------------------------------");
                        Log.d(TAG, "Login - 카카오톡 유저정보 없음");
                        Log.d(TAG, "----------------------------------------------------");

                        setContentView(R.layout.user_login);
                    }
                    return null;
                }
            });
        }
    }

    private void getUserIdAndGo(String email) {
        mDB = FirebaseFirestore.getInstance();
        mDB.collection(COLLECTION_PATH)
            .whereEqualTo("email",email)
            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mAuth.setTenantId((String) document.getData().get("userId"));
                        Log.d(TAG, "조회한 사용자 아이디 : " + mAuth.getTenantId());
                        goToMain();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"UserId Searching Fail... \n" + e.getMessage());
            }
        });
    }

    private void getUserIdAndGo2(long kakaoId) {
        mDB = FirebaseFirestore.getInstance();
        mDB.collection(COLLECTION_PATH)
                .whereEqualTo("kakaoId",kakaoId)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        mAuth.setTenantId((String) document.getData().get("userId"));
                        Log.d(TAG, "조회한 사용자 아이디 : " + mAuth.getTenantId());
                        goToMain();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG,"UserId Searching Fail... \n" + e.getMessage());
            }
        });
    }

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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"LOGIN 메인페이지 디스트로이");
        finish();
    }


    public void goToMain() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        //Intent intent = new Intent(Login.this, QnaMain.class);
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
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(TAG,"로그인 성공  --->" + user.getEmail());
                            getUserIdAndGo(user.getEmail());
                        } else {
                            Toast.makeText(getApplicationContext(),"이메일과 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        } else {
            Toast.makeText(getApplicationContext(),"이메일 형식을 확인하세요.", Toast.LENGTH_SHORT).show();
        }


    }

    // 카카오 로그인 버튼 클릭 이벤트
    public void btn_kakao_login(View view) {
        userApiClient       = UserApiClient.getInstance();
/*
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
*/
        // 사용자 기기에 카톡이 설치되어 있는지 확인.
        if(userApiClient.isKakaoTalkLoginAvailable(Login.this)) {
            // 카카오톡으로 로그인
            userApiClient.loginWithKakaoTalk(Login.this, new Function2<OAuthToken, Throwable, Unit>() {

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

                                    mDB.collection(COLLECTION_PATH)
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
                                                    intent = new Intent(Login.this, KakaoJoin.class);
                                                    startActivity(intent);
                                                    finish();
                                                } else {
                                                    //기존 카톡 유저 처리
                                                    Log.d(TAG,"----------------------------------------------------");
                                                    Log.d(TAG,"등록된 카카오톡 유저 입니다.");
                                                    Log.d(TAG,"----------------------------------------------------");

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        mAuth.setTenantId((String) document.getData().get("userId"));
                                                        Log.d(TAG, "조회한 사용자 아이디 : " + mAuth.getTenantId());

                                                        goToMain();
                                                    }
                                                }

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