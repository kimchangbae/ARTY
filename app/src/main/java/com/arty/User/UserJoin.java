package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

public class UserJoin extends AppCompatActivity {
    final static String TAG = "UserJoin";
    String pswdPattern = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$";
    String pswdPattern2 = "((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9가-힣]).{8,16})";

    TextView userId, email, pswd;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDB;
    final String CollectionPath = "USER_ACCOUNT";

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
        setContentView(R.layout.user_join);

        mAuth   = FirebaseAuth.getInstance();
        mDB     = FirebaseFirestore.getInstance();

        email       = findViewById(R.id.edit_lgn_email);
        userId      = findViewById(R.id.edit_userId);
        pswd        = findViewById(R.id.edit_pswd);



        Button btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationEmail(view) && validationUserId(view) && isPswdOk) {
                    signUpToAuth();
                } else {
                    Toast.makeText(getApplicationContext(),"입력정보를 다시 확인하세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                TextView alert_pswd = findViewById(R.id.alert_pswd);
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
    }

    private void signUpToAuth() {
        String strEmail     = email.getText().toString();
        String password     = pswd.getText().toString();

        mAuth.createUserWithEmailAndPassword(strEmail,password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()) {
                        Log.d(TAG,"Authentication 사용자 등록 성공");
                        FirebaseUser user = mAuth.getCurrentUser();
                        signUpToFirestore(user.getUid());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d("UserJoin", "Authentication 사용자 등록 실패" + e.getMessage());
            }
        });
    }

    private void signUpToFirestore(String uuId) {
        UserAccount user = new UserAccount();
        user.setUuId(uuId);
        user.setUserId(userId.getText().toString());
        user.setEmail(email.getText().toString());

        mDB.collection(CollectionPath)
            .document(uuId)
            .set(user)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Log.d(TAG,"파이어스토어 사용자 등록 성공");
                        goToLoginActivity();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.d(TAG,"파이어스토어 사용자 등록 실패 : " + e.getMessage());
            }
        });
    }

    // 이메일 형식 체크
    public boolean validationEmail(View view) {
        TextView alertEmail = findViewById(R.id.alert_email);
        String strEmail     = email.getText().toString();

        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if(pattern.matcher(strEmail).matches()) {
            mDB.collection(CollectionPath)
                .whereEqualTo("email",strEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG,"validationEmail");
                        if(task.getResult().getDocuments().isEmpty()) {
                            isEmailOk = true;
                            alertEmail.setText("사용가능한 이메일 입니다.");
                            alertEmail.setTextColor(Color.YELLOW);
                        } else {
                            isEmailOk = false;
                            alertEmail.setText("이미 존재하는 이메일 입니다.");
                            alertEmail.setTextColor(Color.RED);
                        }
                    }
                });
        } else {
            alertEmail.setText("이메일 형식을 다시 확인해주세요.");
            alertEmail.setTextColor(Color.RED);
        }
        return isEmailOk;
    }

    // 사용자아이디 형식 체크
    public boolean validationUserId(View view) {
        TextView alertUserId    = findViewById(R.id.alert_userId);
        String strUserId        = userId.getText().toString();

        if(strUserId == null || strUserId.isEmpty()) {
            alertUserId.setText("닉네임을 입력하세요.");
            alertUserId.setTextColor(Color.RED);
        } else{
            mDB.collection(CollectionPath)
                .whereEqualTo("userId",strUserId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        Log.d(TAG,"validationUserId");
                        if(task.getResult().getDocuments().isEmpty()) {
                            isUserIdOk = true;
                            alertUserId.setText("사용가능 합니다.");
                            alertUserId.setTextColor(Color.YELLOW);
                        } else {
                            isUserIdOk = false;
                            alertUserId.setText("사용할 수 없는 닉네임 입니다.");
                            alertUserId.setTextColor(Color.RED);
                        }
                    }
                });
        }
        return isUserIdOk;
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(UserJoin.this, Login.class);
        startActivity(intent);
        finish();
    }

}