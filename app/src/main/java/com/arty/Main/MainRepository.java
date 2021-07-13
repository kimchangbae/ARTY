package com.arty.Main;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.user.UserApiClient;

public class MainRepository {
    private final static String TAG = "MainRepository";
    private Application app;

    private FirebaseFirestore   mDB;
    private FirebaseAuth        mAuth;
    private UserApiClient       mKakao;

    private MutableLiveData<String> userId;

    public MainRepository(Application app) {
        this.app = app;

        mDB = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mKakao = UserApiClient.getInstance();

        userId = new MutableLiveData<>();
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }

    public void getUserId(String email) {
        mDB.collection("USER_ACCOUNT")
        .whereEqualTo("email",email)
        .get()
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String user = (String) document.getData().get("userId");
                    userId.postValue(user);
                    Log.d(TAG, "DB 에서 검색된 사용자 아이디(파이어베이스) : " + user);
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }

    public void getUserId(long kakaoId) {
        mDB.collection("USER_ACCOUNT")
        .whereEqualTo("kakaoId",kakaoId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String user = (String) document.getData().get("userId");
                        userId.postValue(user);
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디(카카오) : " + user);
                    }
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
    }
}
