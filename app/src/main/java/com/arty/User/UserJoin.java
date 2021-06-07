package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

public class UserJoin extends AppCompatActivity {
    final static String TAG = "UserJoin";

    EditText userNm, email;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    final String CollectionPath = "USER_ACCOUNT";

    boolean userNm_result, email_result, pswd_result = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_join);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        email       = findViewById(R.id.edit_lgn_email);
        userNm      = findViewById(R.id.edit_lgn_userNm);

        Button btn_signUp = findViewById(R.id.btn_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validationEmail(view) && validationUserNm(view) && validationPswd(view)) {
                    signUp(view);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            reload();
        }
    }

    public void signUp(View view) {
        EditText editText_email     = findViewById(R.id.edit_lgn_email);
        EditText editText_pswd      = findViewById(R.id.edit_password);
        String email                = editText_email.getText().toString();
        String password             = editText_pswd.getText().toString();

        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();

                            signUpToFireStore(user.getUid());
                        } else {
                            Log.w("UserJoin", "Authentication Write:failure", task.getException());
                        }
                    }
                });
    }

    private void signUpToFireStore(String uId) {
        UserAccount user = new UserAccount();
        user.setuId(uId);
        user.setUserNm(userNm.getText().toString());
        user.setEmail(email.getText().toString());

        firebaseFirestore
                .collection(CollectionPath)
                .document(uId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            goToLogin();
                        }
                    }
                });
    }

    private void reload(){}

    private void goToLogin() {
        Intent intent = new Intent(UserJoin.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean validationEmail(View view) {
        TextView alert_email = findViewById(R.id.alert_email);
        String str_email = email.getText().toString();
        Pattern pattern = Patterns.EMAIL_ADDRESS;

        if(pattern.matcher(str_email).matches()) {
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection(CollectionPath)
                    .whereEqualTo("email",str_email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.getResult().getDocuments().isEmpty()) {
                                alert_email.setText("사용가능한 이메일 입니다.");
                                alert_email.setTextColor(Color.YELLOW);
                                email_result = true;
                            } else {
                                alert_email.setText("이미 존재하는 이메일 입니다.");
                                alert_email.setTextColor(Color.RED);
                            }
                        }
                    });

        } else {
            alert_email.setText("이메일 형식을 다시 확인해주세요.");
            alert_email.setTextColor(Color.RED);
        }

        return email_result;
    }

    public boolean validationUserNm(View view) {
        TextView alert_userNm = findViewById(R.id.alert_userNm);
        String str_userNm = userNm.getText().toString();

        if(str_userNm == null || str_userNm.isEmpty()) {
            alert_userNm.setText("닉네임을 입력하세요.");
            alert_userNm.setTextColor(Color.RED);
        } else{
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection(CollectionPath)
                    .whereEqualTo("userNm",str_userNm)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.getResult().getDocuments().isEmpty()) {
                                alert_userNm.setText("사용가능 합니다.");
                                alert_userNm.setTextColor(Color.YELLOW);
                                userNm_result = true;
                            } else {
                                alert_userNm.setText("사용할 수 없는 닉네임 입니다.");
                                alert_userNm.setTextColor(Color.RED);
                            }
                        }
                    });
        }
        return userNm_result;
    }

    public boolean validationPswd(View view) {
        TextView alert_pswd2 = findViewById(R.id.alert_pswd2);
        TextView pswd = findViewById(R.id.edit_password);
        TextView pswd_check = findViewById(R.id.edit_pswd_check);

        String str_pswd = pswd.getText().toString();
        String str_pswd_check = pswd_check.getText().toString();

        if(!str_pswd.equals(str_pswd_check)) {
            alert_pswd2.setText("비밀번호가 일치하지 않습니다.");
            alert_pswd2.setTextColor(Color.RED);
        } else if(str_pswd.length() < 8) {
            alert_pswd2.setText("비밀번호가 너무 짧습니다.");
            alert_pswd2.setTextColor(Color.RED);
        } else {
            alert_pswd2.setText("사용가능한 비밀번호 입니다.");
            alert_pswd2.setTextColor(Color.YELLOW);
            pswd_result = true;
        }

        return pswd_result;
    }


}