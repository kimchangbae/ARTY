package com.arty.User;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import com.arty.Main.MainActivity;
import com.arty.R;
import com.google.firebase.auth.FirebaseUser;
import com.kakao.sdk.auth.model.OAuthToken;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

@RequiresApi(api = Build.VERSION_CODES.P)
public class Login extends CommonAuth {
    private static final String TAG = "Login";

    private long clickTime = 0;

    private TextView edit_email, edit_password;

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loginViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(LoginViewModel.class);

        edit_email      = findViewById(R.id.edit_lgn_email);
        edit_password   = findViewById(R.id.edit_lgn_pswd);
    }


    @Override
    protected void onStart() {
        super.onStart();

        firstUserCheck();

        loginViewModel.getMutableLiveData().observeForever(userId -> {
            Log.d(TAG,"로그인 getMutableLiveData 옵저빙...");
            if(userId != null) {
                Log.d(TAG,"userId 옵저빙 결과 --> " + userId);
                if(userId.equals("this user is first time")) {
                    Log.d(TAG,"신규 카카오 회원");
                    goTokakaoRegister();
                } else {
                    goToMainActivity();
                }
            } else {
                setContentView(R.layout.user_login);
            }
        });
    }

    /*
     * 초기 진입시 유저 체크
     * */
    public void firstUserCheck() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser != null) {
            Log.d(TAG, "파이어베이스 유저 입장!!!! ----------> [" + firebaseUser.getEmail() + "]");
            goToMainActivity();
        } else {
            Log.d(TAG, "----------------------------------------------------");
            Log.d(TAG, "Login - 파이어베이스 유저가 아닙니다.");
            Log.d(TAG, "----------------------------------------------------");

            mKakao.accessTokenInfo((accessTokenInfo, throwable) -> {
                if (accessTokenInfo != null) {
                    Log.d(TAG, "----------------------------------------------------");
                    Log.d(TAG, "카카오톡 유저 입장!!!! ----------> [" + accessTokenInfo.getId() + "]");
                    goToMainActivity();
                } else {
                    Log.d(TAG, "----------------------------------------------------");
                    Log.d(TAG, "Login - 카카오톡 유저가 아닙니다.");

                    setContentView(R.layout.user_login);
                }
                Log.d(TAG, "----------------------------------------------------");
                return null;
            });
        }
    }

    /*
     * 파이어베이스 로그인
     * */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(View v) {
        edit_email      = findViewById(R.id.edit_lgn_email);
        edit_password   = findViewById(R.id.edit_lgn_pswd);
        String email    = edit_email.getText().toString();
        String password = edit_password.getText().toString();
        if(validationEmail(email)) {
            loginViewModel.login(email, password);
        } else {
            Toast.makeText(getApplicationContext(),"이메일과 비밀번호를 다시 확인하세요.", Toast.LENGTH_SHORT).show();
        }

    }

    /*
     * 카카오톡 로그인
     * */
    public void kakaoLogin(View view) {
        if(mKakao.isKakaoTalkLoginAvailable(this)) {
            mKakao.loginWithKakaoTalk(this, new Function2<OAuthToken, Throwable, Unit>() {
                @Override
                public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                    if(oAuthToken != null)
                        loginViewModel.kakaoLogin();
                    else
                        Log.d(TAG, "카카오톡 로그인 실패!!" + throwable.getMessage());
                    return null;
                }
            });
        }
    }

    /*
     * 로그인 화면에서 뒤로가기 이벤트
     * */

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 뒤로가기 버튼 클릭 시

        if(keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return true;
            /*
            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                Toast.makeText(getApplicationContext(), "프로그램이 종료 되었습니다.", Toast.LENGTH_SHORT).show();
                killApplication();

                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
             */
        }
        return false;
    }

    /*
     * 로그인 화면이 꺼질경우
     * */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"------------LOGIN ACTIVITY DESTROY-------------");
        finish();
    }

    /*
     * 파이어베이스 회원가입
     * */
    public void goToFirebaseRegister(View view) {
        goToFirebaseRegister();
    }

    /*
     * 파이어베이스 비밀번호 찾기
     * */
    public void findPassword(View view) {
        Toast.makeText(getApplicationContext(),"구현중 이에요.", Toast.LENGTH_SHORT).show();
    }
}