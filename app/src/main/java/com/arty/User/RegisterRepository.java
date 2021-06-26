package com.arty.User;

import android.app.Application;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.P)
public class RegisterRepository {
    private static final String TAG = "RegisterRepository";
    private Application app;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDB;

    private MutableLiveData<Boolean>     authLiveData, validEmail, validUserId;

    public RegisterRepository(Application app) {
        this.app = app;

        mAuth   = FirebaseAuth.getInstance();
        mDB     = FirebaseFirestore.getInstance();

        authLiveData    = new MutableLiveData<>();
        validEmail      = new MutableLiveData<>();
        validUserId     = new MutableLiveData<>();
    }

    public MutableLiveData<Boolean> getAuthLiveData() {
        return authLiveData;
    }

    public MutableLiveData<Boolean> getvalidLiveData() {
        return validEmail;
    }

    public MutableLiveData<Boolean> getValidUserId() {
        return validUserId;
    }

    public void firebaseRegister(String email, String password, String userId) {
        mAuth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(app.getMainExecutor(), task -> {
            if(task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                Log.d(TAG,"Authentication 사용자 등록 성공");

                Map<String, String> map = new HashMap<>();
                map.put("uuId",user.getUid());
                map.put("email", email);
                map.put("userId", userId);

                mDB.collection("USER_ACCOUNT")
                .document(user.getUid())
                .set(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(Task<Void> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG,"파이어스토어 사용자 등록 성공");
                            authLiveData.postValue(true);
                        }
                    }
                }).addOnFailureListener(e -> Log.d(TAG,"파이어스토어 사용자 등록 실패 : " + e.getMessage()));
            }
        }).addOnFailureListener(e -> Log.d(TAG, "Authentication 사용자 등록 실패" + e.getMessage()));
    }

    public void kakaoRegister(String uuId, long kakaoId, String email, String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuId",uuId);
        map.put("email", email);
        map.put("userId", userId);
        map.put("kakaoId", kakaoId);

        mDB.collection("USER_ACCOUNT")
        .document(uuId)
        .set(map)
        .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG,"카카오톡 사용자 등록 성공");
                    authLiveData.postValue(true);
                }
            }
        }).addOnFailureListener(e -> Log.d(TAG,"카카오톡 사용자 등록 실패 : " + e.getMessage()));
    }

    public void validEmail(String email) {
        mDB.collection("USER_ACCOUNT")
        .whereEqualTo("email",email)
        .get()
        .addOnCompleteListener(task -> {
            Log.d(TAG,"validationEmail");
            if(task.getResult().getDocuments().isEmpty()) {
                validEmail.postValue(true);
            } else {
                validEmail.postValue(false);
            }
        });
    }

    public void validUserId(String userId) {
        mDB.collection("USER_ACCOUNT")
        .whereEqualTo("userId",userId)
        .get()
        .addOnCompleteListener(task -> {
            Log.d(TAG,"validationUserId");
            if(task.getResult().getDocuments().isEmpty()) {
                validUserId.postValue(true);
            } else {
                validUserId.postValue(false);
            }
        });
    }
}
