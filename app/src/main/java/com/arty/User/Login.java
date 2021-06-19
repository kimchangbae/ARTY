package com.arty.User;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.auth.TokenManager;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class Login extends CommonAuth {
    private static String TAG = "Login";

    private long clickTime = 0;

    private     TextView edit_lgn_email, edit_lgn_pswd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        edit_lgn_email = findViewById(R.id.edit_lgn_email);
        edit_lgn_pswd  = findViewById(R.id.edit_lgn_pswd);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG,"Login onStart----------START");

        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "파이어베이스 유저 입장!! ----------> [" + firebaseUser.getEmail() + "]");
            // getUserId(firebaseUser.getEmail());
            goToMainActivity();
        } else {
            Log.d(TAG, "----------------------------------------------------");
            Log.d(TAG, "Login - 파이어베이스 유저가 아닙니다.");
            Log.d(TAG, "----------------------------------------------------");

            mKakao.accessTokenInfo((accessTokenInfo, throwable) -> {
                if (accessTokenInfo != null) {
                    Log.d(TAG, "----------------------------------------------------");
                    Log.d(TAG, "카카오톡 유저 입장!! ----------> [" + accessTokenInfo.getId() + "]");
                    Log.d(TAG, "----------------------------------------------------");
                    // getUserId(accessTokenInfo.getId());
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"------------LOGIN ACTIVITY DESTROY-------------");
        finish();
    }

    /*
    * 파이어베이스 로그인
    * */
    public void firebaseLogin(View view) {
        edit_lgn_email      = findViewById(R.id.edit_lgn_email);
        edit_lgn_pswd       = findViewById(R.id.edit_lgn_pswd);
        String email        = edit_lgn_email.getText().toString();
        String password     = edit_lgn_pswd.getText().toString();

        Log.d(TAG,"파이어베이스 셋팅----------");
        Log.d(TAG,""+mAuth.getFirebaseAuthSettings().toString());
        if(validationEmail(email)) {
            Log.d(TAG,"로그인 요청 데이터 --> email : [" + email + "] , pswd : [" + password +"]");
            Log.d(TAG, "CurrentUser : [" + mAuth.getCurrentUser() + "], TenantId : [" + mAuth.getTenantId() + "]");

            mAuth
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();

                        Log.d(TAG,"파이어베이스 로그인 성공 !! ---> [" + user.getEmail() +"]");
                        getUserId(user.getEmail());
                        goToMainActivity();
                    } else {
                        Toast.makeText(getApplicationContext(),"이메일과 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(getApplicationContext(),"이메일 확인하세요.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * 카카오톡 로그인
     * */
    public void kakaoLogin(View view) {
        // 사용자 기기에 카톡이 설치되어 있는지 확인.
        if(mKakao.isKakaoTalkLoginAvailable(Login.this)) {
            // 카카오톡으로 로그인
            mKakao.loginWithKakaoTalk(Login.this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if(oAuthToken != null) {
                        Log.d(TAG,"----------------------------------------------------");
                        Log.d(TAG,"oAuthToken : "+oAuthToken.getAccessToken());
                        Log.d(TAG,"TokenManager : "+TokenManager.getInstance().getToken().getAccessToken());
                        Log.d(TAG,"----------------------------------------------------");

                        /*
                        TODO kakao 도메인 사용자가 아닌경우 커스텀 토큰 발급이 필요하다.
                            2021-06-18 MEMO
                            커스텀 토큰 발급을 위해서는 firebase admin sdk를 빌드해야 하는데 auth 와 동시에 사용이 불가능하다.
                        * */

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

                                            if(task.getResult().isEmpty()) {
                                                //신규 카카오톡 유저 처리
                                                Log.d(TAG,"----------------------------------------------------");
                                                Log.d(TAG,"DB에 등록되지 않은 카카오톡 유저 입니다.");
                                                Log.d(TAG,"----------------------------------------------------");
                                                goTokakaoAuth();
                                            } else {
                                                //기존 카카오톡 유저 처리
                                                Log.d(TAG,"----------------------------------------------------");
                                                Log.d(TAG,"DB에 등록된 카카오톡 유저 입니다.");
                                                Log.d(TAG,"----------------------------------------------------");

                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String userId = (String) document.getData().get("userId");
                                                    Log.d(TAG, "DB 에서 조회된 사용자 아이디 : " + userId);
                                                    // mAuth.setTenantId(userId);
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

    public void goToFirebaseAuth(View view) {
        goToUserJoin();
    }

    public void findPassword(View view) {
        Toast.makeText(getApplicationContext(),"구현중 이에요.", Toast.LENGTH_SHORT).show();
        return;
    }
}