package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.Common.Common;
import com.arty.Main.MainActivity;

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

    private UserApiClient       mKakaoClient;

    public Common common;

    private TextView edit_lgn_email, edit_lgn_pswd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        common = new Common();
        mAuth           = FirebaseAuth.getInstance();
        mDB             = FirebaseFirestore.getInstance();
        mKakaoClient    = UserApiClient.getInstance();

        edit_lgn_email = findViewById(R.id.edit_lgn_email);
        edit_lgn_pswd  = findViewById(R.id.edit_lgn_pswd);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"onStart----------START");

        // 파이어베이스 유저 체크
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "파이어베이스 유저 입장!! ----------> [" + firebaseUser.getEmail() + "]");
            getUserId(firebaseUser.getEmail());
            goToMainActivity();
        } else {
            Log.d(TAG, "----------------------------------------------------");
            Log.d(TAG, "Login - 파이어베이스 유저가 아닙니다.");
            Log.d(TAG, "----------------------------------------------------");

            // 카카오톡 유저 체크
            mKakaoClient.accessTokenInfo((accessTokenInfo, throwable) -> {
                if (accessTokenInfo != null) {
                    Log.d(TAG, "----------------------------------------------------");
                    Log.d(TAG, "카카오톡 유저 입장!! ----------> [" + accessTokenInfo.getId() + "]");
                    Log.d(TAG, "----------------------------------------------------");
                    getUserId(accessTokenInfo.getId());
                    goToMainActivity();
                } else {
                    Log.d(TAG, "----------------------------------------------------");
                    Log.d(TAG, "Login - 카카오톡 유저가 아닙니다.");
                    Log.d(TAG, "----------------------------------------------------");

                    setContentView(R.layout.user_login);
                }
                return null;
            });
        }

        Log.d(TAG,"onStart----------END");
    }

    private void getUserId(String email) {
        mDB.collection(COLLECTION_PATH)
            .whereEqualTo("email",email)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = (String) document.getData().get("userId");
                        mAuth.setTenantId(userId);
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디 : " + mAuth.getTenantId());
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void getUserId(long kakaoId) {
        mDB.collection(COLLECTION_PATH)
            .whereEqualTo("kakaoId",kakaoId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = (String) document.getData().get("userId");
                        mAuth.setTenantId(userId);
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디 : " + mAuth.getTenantId());
                    }
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
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
        Log.d(TAG,"------------LOGIN ACTIVITY DESTROY-------------");
        finish();
    }

    // 파이어베이스 로그인
    public void firebaseLogin(View view) {
        edit_lgn_email      = findViewById(R.id.edit_lgn_email);
        edit_lgn_pswd       = findViewById(R.id.edit_lgn_pswd);
        String email        = edit_lgn_email.getText().toString();
        String password     = edit_lgn_pswd.getText().toString();

        Log.d(TAG,"email : [" + email + "] , pswd : [" + password +"]");

        if(validationEmail(email)) {
            mAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Log.d(TAG,"파이어베이스 로그인 성공 !! ---> [" + user.getEmail() +"]");
                    getUserId(user.getEmail());
                    goToMainActivity();
                } else {
                    Toast.makeText(getApplicationContext(),"이메일과 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),"이메일 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validationEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    // 카카오톡 로그인(kakao 도메인 사용자)
    private void kakaoLoginUseKakaoDomain(View view) {
        if(mKakaoClient.isKakaoTalkLoginAvailable(Login.this)) {
            mKakaoClient.loginWithKakaoTalk(Login.this,callback);
        }
    }

    Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            UserJoin userJoin = new UserJoin();
            if(oAuthToken != null) {
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
                                                //신규 카카오톡 유저 처리
                                                Log.d(TAG,"----------------------------------------------------");
                                                Log.d(TAG,"DB에 등록되지 않은 카카오톡 유저 입니다.");
                                                Log.d(TAG,"----------------------------------------------------");
                                                kakaoJoin();
                                            } else {
                                                //기존 카카오톡 유저 처리
                                                Log.d(TAG,"----------------------------------------------------");
                                                Log.d(TAG,"DB에 등록된 카카오톡 유저 입니다.");
                                                Log.d(TAG,"----------------------------------------------------");

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String userId = (String) document.getData().get("userId");
                                                    mAuth.setTenantId(userId);
                                                    Log.d(TAG, "DB 에서 조회된 사용자 아이디 : " + mAuth.getTenantId());

                                                    goToMainActivity();
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
            return null;
        }
    };


    // 카카오 로그인
    public void kakaoLogin(View view) {
        // 사용자 기기에 카톡이 설치되어 있는지 확인.
        if(mKakaoClient.isKakaoTalkLoginAvailable(Login.this)) {
            // 카카오톡으로 로그인
            mKakaoClient.loginWithKakaoTalk(Login.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if(oAuthToken != null) {
                        Log.d(TAG,"----------------------------------------------------");
                        Log.d(TAG,"oAuthToken : "+oAuthToken.getAccessToken());
                        Log.d(TAG,"----------------------------------------------------");

                        // 커스텀 토큰 발급 TODO kakao 도메인 사용자가 아닌경우 커스텀 토큰 발급이 필요하다.
                        mAuth.signInWithCustomToken(oAuthToken.getAccessToken()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Log.d(TAG,"----------------------------------------------------");
                                    Log.d(TAG,"카카오톡 커스텀 토큰 발급 성공");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Log.d(TAG,"[유저정보 : " +user.getUid()+ "]");
                                    Log.d(TAG,"[유저정보 : " +user.getEmail()+ "]");
                                    Log.d(TAG,"[유저정보 : " +user.getPhoneNumber()+ "]");
                                    Log.d(TAG,"[유저정보 : " +user.getProviderId()+ "]");
                                    Log.d(TAG,"----------------------------------------------------");
                                } else {
                                    Log.d(TAG,"----------------------------------------------------");
                                    Log.d(TAG,"카카오톡 커스텀 토큰 발급 실패");
                                    Log.d(TAG,"----------------------------------------------------");
                                }
                            }
                        });

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
                                                    //신규 카카오톡 유저 처리
                                                    Log.d(TAG,"----------------------------------------------------");
                                                    Log.d(TAG,"DB에 등록되지 않은 카카오톡 유저 입니다.");
                                                    Log.d(TAG,"----------------------------------------------------");
                                                    kakaoJoin();
                                                } else {
                                                    //기존 카카오톡 유저 처리
                                                    Log.d(TAG,"----------------------------------------------------");
                                                    Log.d(TAG,"DB에 등록된 카카오톡 유저 입니다.");
                                                    Log.d(TAG,"----------------------------------------------------");

                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        String userId = (String) document.getData().get("userId");
                                                        mAuth.setTenantId(userId);
                                                        Log.d(TAG, "DB 에서 조회된 사용자 아이디 : " + mAuth.getTenantId());

                                                       goToMainActivity();
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

    private void kakaoJoin() {
        Intent intent = new Intent(Login.this, KakaoJoin.class);
        startActivity(intent);
        finish();
    }

    public void goToMainActivity() {
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void findPassword(View view) {
        Toast.makeText(getApplicationContext(),"구현중 이에요.", Toast.LENGTH_SHORT).show();
        return;
    }
}