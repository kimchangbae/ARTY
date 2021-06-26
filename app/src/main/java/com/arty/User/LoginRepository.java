package com.arty.User;

import android.app.Application;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class LoginRepository {
    private static final String TAG = "LoginRepository";

    private Application         app;
    private FirebaseAuth        mAuth;
    private FirebaseFirestore   mDB;
    private UserApiClient       mKakao;

    private MutableLiveData<String> mutableLiveData;


    public LoginRepository(Application app) {
        this.app = app;

        mAuth   = FirebaseAuth.getInstance();
        mDB     = FirebaseFirestore.getInstance();
        // mKakao  = UserApiClient.getInstance();

        mutableLiveData = new MutableLiveData<>();
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }



    public void register(String email, String password) {
        // TODO 회원가입
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void login(String email, String password) {
        // TODO 로그인

        mAuth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener(app.getMainExecutor(), task -> {
            if(task.isSuccessful()) {
                FirebaseUser user = mAuth.getCurrentUser();
                mutableLiveData.postValue(user.getEmail());
                Log.d(TAG,"파이어베이스 로그인 성공 [" + user.getEmail() +"]");

            } else {
                Toast.makeText(app,"로그인 실패",Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*
        TODO kakao 도메인 사용자가 아닌경우 커스텀 토큰 발급이 필요하다.
            2021-06-18 MEMO
            커스텀 토큰 발급을 위해서는 firebase admin sdk를 빌드해야 하는데 auth 와 동시에 사용이 불가능하다.
        * */
    public void kakaoLogin() {
        UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
            @Override
            public Unit invoke(User user, Throwable throwable) {
                Log.d(TAG,"----------------------------------------------------");
                Log.d(TAG,"[ME CALL BACK]");
                Log.d(TAG,"----------------------------------------------------");
                if(user != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d(TAG, "카카오 사용자 정보[" +user+"]");
                    Log.d(TAG,"----------------------------------------------------");

                    mDB.collection("USER_ACCOUNT")
                    .whereEqualTo("kakaoId",user.getId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(Task<QuerySnapshot> task) {

                            if(task.getResult().isEmpty()) {
                                //신규 카카오톡 유저 처리
                                Log.d(TAG,"----------------------------------------------------");
                                Log.d(TAG,"DB에 등록되지 않은 카카오톡 유저 입니다.");
                                mutableLiveData.postValue("this user is first time");
                                Log.d(TAG,"----------------------------------------------------");
                            } else {
                                //기존 카카오톡 유저 처리
                                Log.d(TAG,"----------------------------------------------------");
                                Log.d(TAG,"DB에 등록된 카카오톡 유저 입니다.");
                                Log.d(TAG,"----------------------------------------------------");

                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    String userId = (String) document.getData().get("userId");
                                    Log.d(TAG, "DB 에서 조회된 사용자 아이디 : " + userId);
                                    mutableLiveData.postValue(userId);
                                }
                            }
                        }
                    });
                }
                if(throwable != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d(TAG, "카카오톡 사용자 정보 요청 실패!!" + throwable.getMessage());
                    Log.d(TAG,"----------------------------------------------------");
                }
                return null;
            }
        });
    }
}
