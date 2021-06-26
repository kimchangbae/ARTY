package com.arty.User;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.lifecycle.ViewModelProvider;

import com.arty.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

@RequiresApi(api = Build.VERSION_CODES.P)
public class RegisterFirebase extends CommonAuth {
    final static String TAG = "RegisterFirebase";

    private RegisterViewModel   registerViewModel;

    private Button signUp, chkEmail, chkUserId;
    private TextView userId, email, pswd;
    private TextView alertEmail, alertUserId, alert_pswd;

    boolean isEmailOk, isUserIdOk, isPswdOk = false;

    public int CHECK_AGREE_A = 0; // 1번 체크박스
    public int CHECK_AGREE_1 = 0; // 1번 체크박스
    public int CHECK_AGREE_2 = 0; // 2번 체크박스
    public int CHECK_AGREE_3 = 0; // 3번 체크박스

    AppCompatCheckBox checkBoxA;
    AppCompatCheckBox checkBox1;
    AppCompatCheckBox checkBox2;
    AppCompatCheckBox checkBox3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_firebase_register);

        mDB = FirebaseFirestore.getInstance();

        registerViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(RegisterViewModel.class);

        email       = findViewById(R.id.edit_lgn_email);
        userId      = findViewById(R.id.edit_userId);
        pswd        = findViewById(R.id.edit_pswd);

        alertEmail  = findViewById(R.id.alert_email);
        alertUserId = findViewById(R.id.alert_userId);
        alert_pswd  = findViewById(R.id.alert_pswd);

        signUp = findViewById(R.id.btn_firebase_signUp);
        chkEmail = findViewById(R.id.btn_chk_email);
        chkUserId = findViewById(R.id.btn_chk_userId);
    }

    @Override
    protected void onStart() {
        super.onStart();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(isEmailOk) {
                    Log.d(TAG,"이메일 텍스트 체인지");
                    isEmailOk = false;
                }
            }
        });

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

        pswd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(Pattern.matches(pswdPattern2, pswd.getText().toString())) {
                    Log.d(TAG,"validationPassword");
                    isPswdOk = true;
                    alert_pswd.setText("사용가능합니다.");
                    alert_pswd.setTextColor(Color.YELLOW);
                } else {
                    isPswdOk = false;
                    alert_pswd.setText("사용 불가능한 비밀번호 입니다.");
                    alert_pswd.setTextColor(Color.RED);
                }
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if (isEmailOk && isUserIdOk && isPswdOk) {
                if (isEmailOk && isUserIdOk) {
                    signUpToFirebase();
                } else {
                    Toast.makeText(getApplication(),"입력정보를 다시 확인하세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });

        registerViewModel.getAuthLiveData().observeForever(aBoolean -> {
            if(aBoolean) {
                Log.d(TAG,"파이어베이스 회원가입 성공 시그널 받았음.");
                goToLoginActivity();
            } else {
                Log.d(TAG,"파이어베이스 회원가입 실패 시그널 받았음.");
            }
        });

        chkEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationEmail();
            }
        });

        registerViewModel.getValidEmail().observeForever(aBoolean -> {
            isEmailOk = aBoolean;
            if(aBoolean) {
                Log.d(TAG,"파이어베이스 생성가능 이메일 시그널 받았음.");
                alertEmail.setText("사용가능한 이메일 입니다.");
                alertEmail.setTextColor(Color.YELLOW);
            } else {
                Log.d(TAG,"파이어베이스 생성불가능 이메일 시그널 받았음.");
                alertEmail.setText("이미 존재하는 이메일 입니다.");
                alertEmail.setTextColor(Color.RED);
            }
        });

        chkUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validationUserId();
            }
        });

        registerViewModel.getValidUserId().observeForever(aBoolean -> {
            isUserIdOk = aBoolean;
            if(aBoolean) {
                Log.d(TAG,"파이어베이스 생성가능 유저아이디 시그널 받았음.");
                alertUserId.setText("사용가능 합니다.");
                alertUserId.setTextColor(Color.YELLOW);
            } else {
                Log.d(TAG,"파이어베이스 생성불가능 유저아이디 시그널 받았음.");
                alertUserId.setText("사용할 수 없는 닉네임 입니다.");
                alertUserId.setTextColor(Color.RED);
            }
        });
    }

    /*
     * 파이어베이스 회원가입
     * */
    private void signUpToFirebase() {
        String strEmail     = email.getText().toString();
        String strPswd      = pswd.getText().toString();
        String strUserId    = userId.getText().toString();

        registerViewModel.firebaseRegister(strEmail, strPswd, strUserId);
    }

    /*
     * 파이어베이스 이메일 체크
     * */
    private void validationEmail() {
        email       = findViewById(R.id.edit_lgn_email);
        String strEmail     = email.getText().toString();

        if(validationEmail(strEmail)) {
            registerViewModel.validEmail(strEmail);
        } else {
            alertEmail.setText("이메일 형식을 다시 확인해주세요.");
            alertEmail.setTextColor(Color.RED);
        }
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
}