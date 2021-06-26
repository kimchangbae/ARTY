package com.arty.User;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MyPageRepository {
    private static final String TAG = "MyPageRepository";

    private Application app;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDB;
    private UserApiClient       mKakao;

    private MutableLiveData<Boolean> withdrawalResult;

    public MyPageRepository(Application app) {
        this.app = app;
        withdrawalResult = new MutableLiveData<>();

        mAuth = FirebaseAuth.getInstance();
        mDB = FirebaseFirestore.getInstance();
        mKakao = UserApiClient.getInstance();
    }

    public MutableLiveData<Boolean> getWithdrawalResult() {
        return withdrawalResult;
    }

    public void firebaseLogout() {
        mAuth.signOut();
    }

    public void firebaseWithdrawal() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(task -> {
            Log.d(TAG,"----------------------------------------------------");
            Log.d(TAG, "파이어베이스 회원탈퇴 [Email : " +user.getEmail()+ "]");
            Log.d(TAG,"----------------------------------------------------");

            mDB
            .collection("USER_ACCOUNT")
            .whereEqualTo("email",user.getEmail())
            .get()
            .addOnCompleteListener(tasks -> {
                if(tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        String documentId = document.getId();
                        Log.d(TAG, "파이어베이스 회원탈퇴 문서 ID : [" +documentId+ "]");

                        mDB.collection("USER_ACCOUNT").document(documentId).delete()
                        .addOnCompleteListener(task1 -> {
                            withdrawalResult.postValue(true);
                        });
                    }
                }
            });
        });
    }

    public void kakaoTalkLogout() {
        mKakao.logout(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                Log.d(TAG,"----------------------------------------------------");
                Log.d(TAG, "카카오톡 로그아웃");
                Log.d(TAG,"----------------------------------------------------");

                if(throwable != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d(TAG, "카카오톡 로그아웃 에러 발생" + throwable.getMessage());
                    Log.d(TAG,"----------------------------------------------------");
                }
                return null;
            }
        });
    }

    // 카카오톡 회원탈퇴
    public void kakaoTalkWithdrawal(){
        UserApiClient.getInstance().me(function2);
        mKakao.unlink(throwable -> {
            Log.d(TAG, "카카오톡 회원탈퇴");

            if(throwable != null) {
                Log.d(TAG,"----------------------------------------------------");
                Log.d("AuthApplication", "카카오톡 회원탈퇴 에러 발생" + throwable.getMessage());
                Log.d(TAG,"----------------------------------------------------");
            }

            return null;
        });
    }

    Function2<User, Throwable, Unit> function2 = new Function2<User, Throwable, Unit>() {
        @Override
        public Unit invoke(User user, Throwable throwable) {
            mDB
            .collection("USER_ACCOUNT")
            .whereEqualTo("kakaoId",user.getId())
            .get()
            .addOnCompleteListener(tasks -> {
                if(tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        String documentId = document.getId();
                        Log.d(TAG, "카카오톡 회원탈퇴 문서 ID : [" +documentId+ "]");

                        mDB.collection("USER_ACCOUNT").document(documentId).delete()
                        .addOnCompleteListener(task1 -> {
                            withdrawalResult.postValue(true);
                        });
                    }
                }
            });
            return null;
        }
    };
}
