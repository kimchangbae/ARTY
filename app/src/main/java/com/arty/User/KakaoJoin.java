package com.arty.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.arty.Main.MainActivity;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class KakaoJoin extends AppCompatActivity {
    private static String TAG = "KakaoJoin";
    static String CollectionPath = "USER_ACCOUNT";
    final String randomKey = UUID.randomUUID().toString();


    private FirebaseFirestore   mDB;
    private FirebaseAuth        mAuth;
    private UserApiClient       userApiClient;

    UserAccount                 userAccount;

    TextView userId;
    boolean isUserIdOk = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_kakao_join);

        mDB = FirebaseFirestore.getInstance();
        userApiClient = UserApiClient.getInstance();

        userId = findViewById(R.id.edit_kakao_userId);
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

        Button btn_signUp = findViewById(R.id.btn_kakao_signUp);
        btn_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validationUserId(view)) {
                    userApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                        @Override
                        public Unit invoke(User user, Throwable throwable) {
                            userAccount = new UserAccount();
                            userAccount.setUuId(randomKey);
                            userAccount.setUserId(userId.getText().toString());
                            userAccount.setKakaoId(user.getId());
                            userAccount.setEmail(user.getKakaoAccount().getEmail());
                            // mAuth.setTenantId(userId.getText().toString());
                            signUpForKakao(userAccount);
                            return null;
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(),"입력정보를 다시 확인하세요.",Toast.LENGTH_SHORT).show();
                }
                //userAccount.setUserId(userId.getText().toString());
                //singUpForKakao(userAccount.getKakaoId(), userAccount.getEmail());
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("KakaoJoin","[KakaoJoin.onPause]");

        // firebaseFirestore.terminate();

        //finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG,"카카오 조인에서 뒤로가기");

        userApiClient.unlink(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                Log.d("AuthApplication", "logoutFunction");

                if(throwable != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d("AuthApplication", "카카오톡 에러 발생" + throwable.getMessage());
                    Log.d(TAG,"----------------------------------------------------");
                }

                return null;
            }
        });

        Intent intent = new Intent(getApplicationContext(), Login.class);
        startActivity(intent);
        finish();
    }

    private void signUpForKakao(UserAccount userAccount) {
        mDB.collection(CollectionPath)
            .document(userAccount.getUuId())
            .set(userAccount)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        goToActivity("main");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.getMessage();
            }
        });
    }

    // 카카오 사용자아이디 형식 체크
    public boolean validationUserId(View view) {
        TextView alertUserId = findViewById(R.id.alert_kakao_userId);
        String strUserId        = userId.getText().toString();

        if(strUserId == null || strUserId.isEmpty()) {
            alertUserId.setText("닉네임을 입력하세요.");
            alertUserId.setTextColor(Color.RED);
        } else {
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

    private void goToActivity(String path) {
        Intent intent = null;

        if(path != null && path.equals("login")) {
            intent = new Intent(KakaoJoin.this, Login.class);
        } else if (path != null && path.equals("main")){
            intent = new Intent(KakaoJoin.this, MainActivity.class);
        }
        startActivity(intent);
        finish();
    }
}