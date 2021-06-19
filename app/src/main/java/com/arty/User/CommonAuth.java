package com.arty.User;

import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.arty.Main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.user.UserApiClient;

import static android.content.ContentValues.TAG;

public class CommonAuth extends AppCompatActivity {
    final static String COLLECTION_PATH = "USER_ACCOUNT";

    protected   FirebaseFirestore   mDB;
    protected   FirebaseAuth        mAuth;
    protected   UserApiClient       mKakao;

    public CommonAuth() {
        mDB     = FirebaseFirestore.getInstance();
        mAuth   = FirebaseAuth.getInstance();
        mKakao  = UserApiClient.getInstance();
    }

    public void getUserId(String email) {
        mDB.collection(COLLECTION_PATH)
            .whereEqualTo("email",email)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String userId = (String) document.getData().get("userId");
                            Log.d(TAG, "DB 에서 검색된 사용자 아이디(파이어베이스) : " + userId);
                            String userIds = null;
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

    public void getUserId(long kakaoId) {
        mDB.collection(COLLECTION_PATH)
        .whereEqualTo("kakaoId",kakaoId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String userId = (String) document.getData().get("userId");
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디(카카오) : " + userId);

                    }
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
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

    public boolean validationEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    public void goToUserJoin() {
        Intent intent = new Intent(this, FirebaseAuth.class);
        startActivity(intent);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goTokakaoAuth() {
        Intent intent = new Intent(this, KakaoAuth.class);
        startActivity(intent);
        finish();
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
