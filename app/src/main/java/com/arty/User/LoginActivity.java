package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.Common.Common;
import com.arty.Qna.QnaMainActivity;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.kakao.sdk.user.model.User;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.util.exception.KakaoException;


import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
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

    boolean isEmptyKakaoUser;
    private UserApiClient userApiClient;
    KakaoApplication kakaoApplication = new KakaoApplication();

    public Common common;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        common = new Common();
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
        super.onBackPressed();

        Log.d(TAG, "뒤로가기");

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


        // 사용자 기기에 카톡이 설치되어 있는지 확인.
        if(userApiClient.isKakaoTalkLoginAvailable(LoginActivity.this)) {
            Log.d(TAG,"[KAKAO TALK INSTALL OK]");
            // 카카오 계정으로 로그인
            userApiClient.loginWithKakaoAccount(LoginActivity.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    Log.d(TAG,"[CALL BACK]");
                    if(oAuthToken != null) {
                        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                            @Override
                            public Unit invoke(User user, Throwable throwable) {
                                Log.d(TAG,"[ME CALL BACK]");
                                if(user != null) {
                                    Log.d(TAG, "카카오 사용자 등록여부(" +user.getId()+")");
                                    Log.d(TAG, "카카오 사용자 정보(이메일 : " +user.getId()+")");
                                    Log.d(TAG, "카카오 사용자 정보(이메일 : " +user.getKakaoAccount().getEmail()+")");

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
                                                        Log.d(TAG,"등록되지 않은 카카오톡 유저 입니다.");
                                                        intent = new Intent(LoginActivity.this, KakaoJoin.class);

                                                        intent.putExtra("userAccount",userAccount);
                                                        startActivity(intent);
                                                    } else {
                                                        //기존 카톡 유저 처리
                                                        Log.d(TAG,"등록된 카카오톡 유저 입니다.");
                                                        intent = new Intent(LoginActivity.this, QnaMainActivity.class);
                                                        finish();
                                                    }
                                                    startActivity(intent);
                                                }
                                            });
                                }
                                if(throwable != null) {
                                    Log.d(TAG, "카카오톡 사용자 정보 요청 실패!!" + throwable.getMessage());
                                }
                                return null;
                            }
                        });
                    }
                    if(throwable != null) {
                        Log.d(TAG, "카카오 로그인 실패(throwable : " +throwable.getMessage()+")");
                    }
                    return null;
                }
            });
        }
    }




    public void setUserDataForKakao(User kakaoUser) {
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

    public boolean getUserDataForKakao(long kakaoId) {
        firebaseFirestore
            .collection(CollectionPath)
            .whereEqualTo("kakaoId",kakaoId)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for(QueryDocumentSnapshot document : task.getResult()) {
                            isEmptyKakaoUser = true;
                            Log.d(TAG,"document --> " + document.getData());
                        }
                    }
                }
            });
        return isEmptyKakaoUser;
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