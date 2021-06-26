package com.arty.User;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;

import com.arty.R;

import java.util.UUID;

@RequiresApi(api = Build.VERSION_CODES.P)
public class RegisterKakao extends CommonAuth {
    private static String TAG = "RegisterKakao";
    final String randomKey = UUID.randomUUID().toString();

    private RegisterViewModel   registerViewModel;

    private TextView userId, alertUserId;
    private Button signUp, chkUserId;

    private boolean isUserIdOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_kakao_register);

        registerViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(RegisterViewModel.class);

        userId      = findViewById(R.id.edit_kakao_userId);
        alertUserId = findViewById(R.id.alert_kakao_userId);
        signUp      = findViewById(R.id.btn_kakao_signUp);
        chkUserId   = findViewById(R.id.btn_chk_userId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        userId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isUserIdOk) {
                    Log.d(TAG,"아이디 텍스트 체인지");
                    isUserIdOk = false;
                }
            }
        });

        signUp.setOnClickListener(view -> {
            if (isUserIdOk) {
                signUpToKakao();
            } else {
                Toast.makeText(getApplicationContext(),"입력정보를 다시 확인하세요.",Toast.LENGTH_SHORT).show();
            }
        });

        registerViewModel.getAuthLiveData().observeForever(aBoolean -> {
            if(aBoolean) {
                if(aBoolean) {
                    Log.d(TAG,"카카오톡 회원가입 성공 시그널 받았음.");
                    goToLoginActivity();
                } else {
                    Log.d(TAG,"카카오톡 회원가입 실패 시그널 받았음.");
                }
            }
        });

        chkUserId.setOnClickListener(v -> validationUserId());

        registerViewModel.getValidUserId().observeForever(aBoolean -> {
            isUserIdOk = aBoolean;
            if(aBoolean) {
                Log.d(TAG,"카카오톡 생성가능 유저아이디 시그널 받았음.");
                alertUserId.setText("사용가능 합니다.");
                alertUserId.setTextColor(Color.YELLOW);
            } else {
                Log.d(TAG,"카카오톡 생성불가능 유저아이디 시그널 받았음.");
                alertUserId.setText("사용할 수 없는 닉네임 입니다.");
                alertUserId.setTextColor(Color.RED);
            }
        });
    }

    /*
     * 파이어베이스 회원가입
     * */
    private void signUpToKakao() {
        String strUserId = userId.getText().toString();
        mKakao.me((user, throwable) -> {
            registerViewModel.kakaoRegister(randomKey, user.getId(), user.getKakaoAccount().getEmail(), strUserId);
            return null;
        });
    }

    /*
     * 사용자아이디 체크
     * */
    private void validationUserId() {
        String strUserId  = userId.getText().toString();

        if(strUserId == null || strUserId.equals("")) {
            alertUserId.setText("닉네임을 입력하세요.");
            alertUserId.setTextColor(Color.RED);
            isUserIdOk = false;
        } else{
            registerViewModel.validUserId(strUserId);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mKakao.unlink(throwable -> {
            Log.d(TAG,"카카오톡 계정 생성중 뒤로가기 클릭");
            goToLoginActivity();

            if(throwable != null) {
                Log.d(TAG,"----------------------------------------------------");
                Log.d(TAG, "카카오톡 에러 발생" + throwable.getMessage());
                Log.d(TAG,"----------------------------------------------------");
            }
            return null;
        });
    }
}