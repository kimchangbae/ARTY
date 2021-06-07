package com.arty.User;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.arty.Common.Common;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.UUID;

public class KakaoJoin extends AppCompatActivity {
    final String CollectionPath = "USER_ACCOUNT";
    final String randomKey = UUID.randomUUID().toString();

    TextView edit_kakao_userNm;
    boolean userNm_kakao_result = false;
    private FirebaseFirestore   firebaseFirestore;


    UserAccount userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kakao_join);

        edit_kakao_userNm = findViewById(R.id.edit_kakao_userNm);

        userAccount = (UserAccount) getIntent().getSerializableExtra("userAccount");


        Button btn_kakao_signUp = findViewById(R.id.btn_kakao_signUp);
        btn_kakao_signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validationUserNm(view)) {
                    userAccount.setUserNm(edit_kakao_userNm.getText().toString());
                    singUpForKAKAO(userAccount.getKakaoId(), userAccount.getEmail());
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("KakaoJoin","[KakaoJoin.onPause]");

        firebaseFirestore.terminate();

        finish();
    }

    private void singUpForKAKAO(long kakaoId, String kakaoEmail) {
        firebaseFirestore
                .collection(CollectionPath)
                .document(randomKey)
                .set(userAccount)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {

                            goToLogin();
                        }
                    }
                });
    }

    private void goToLogin() {
        Intent intent = new Intent(KakaoJoin.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public boolean validationUserNm(View view) {
        TextView alert_kakao_userNm = findViewById(R.id.alert_kakao_userNm);
        String str_userNm = edit_kakao_userNm.getText().toString();

        if(str_userNm == null || str_userNm.isEmpty()) {
            alert_kakao_userNm.setText("닉네임을 입력하세요.");
            alert_kakao_userNm.setTextColor(Color.RED);
        } else{
            firebaseFirestore = FirebaseFirestore.getInstance();
            firebaseFirestore
                    .collection(CollectionPath)
                    .whereEqualTo("userNm",str_userNm)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {
                            if(task.getResult().getDocuments().isEmpty()) {
                                alert_kakao_userNm.setText("사용가능 합니다.");
                                alert_kakao_userNm.setTextColor(Color.YELLOW);
                                userNm_kakao_result = true;
                            } else {
                                alert_kakao_userNm.setText("사용할 수 없는 닉네임 입니다.");
                                alert_kakao_userNm.setTextColor(Color.RED);
                            }
                        }
                    });
        }
        return userNm_kakao_result;
    }
}