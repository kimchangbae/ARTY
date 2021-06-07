package com.arty.User;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class KakaoApplication extends Application {
    static final String TAG = "KakaoApplication";

    private FirebaseFirestore   firebaseFirestore;
    final String CollectionPath = "USER_ACCOUNT";


    @Override
    public void onCreate() {
        super.onCreate();

        // Android SDK 를 사용하기 위해 네이티브 앱키로 초기화 진행
        KakaoSdk.init(this,"4309433de6aa50ed57287c86ce90b7af");

        firebaseFirestore = FirebaseFirestore.getInstance();


        // String debugKeyHash = Utility.INSTANCE.getKeyHash(this);
        // Log.d("KakaoApplication","debugKeyHash ---> [" +debugKeyHash+"]");
    }

    // 카카오 로그인 콜백 함수
    Function2<OAuthToken, Throwable, Unit> callback = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            Log.d(TAG,"[callback 호출]");
            if(oAuthToken != null) {
                UserApiClient.getInstance().me(meFunction);
            }
            if(throwable != null) {
                Log.d(TAG, "카카오 로그인 실패(throwable : " +throwable.getMessage()+")");
            }
            return null;
        }
    };


    Function2<User, Throwable, Unit> meFunction = new Function2<User, Throwable, Unit>() {
        @Override
        public Unit invoke(User user, Throwable throwable) {
            Log.d(TAG,"[ME_callback 호출]");
            if(user != null) {
                Log.d(TAG, "카카오 사용자 정보(이메일 : " +user.getId()+")");
                Log.d(TAG, "카카오 사용자 정보(이메일 : " +user.getKakaoAccount().getEmail()+")");

                firebaseFirestore = FirebaseFirestore.getInstance();

                firebaseFirestore
                        .collection(CollectionPath)
                        .whereEqualTo("kakaoId",user.getId())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(Task<QuerySnapshot> task) {
                                if(task.getResult().isEmpty()) {
                                    //신규 카톡 유저 처리
                                    Log.d(TAG,"등록되지 않은 카카오톡 유저 입니다.");

                                } else {
                                    //기존 카톡 유저 처리
                                    Log.d(TAG,"등록된 카카오톡 유저 입니다.");
                                }
                            }
                        });

            }
            if(throwable != null) {
                Log.d(TAG, "카카오톡 사용자 정보 요청 실패!!" + throwable.getMessage());
            }
            return null;
        }
    };

    public void startMethod() {
        Intent intent = new Intent(KakaoApplication.this, KakaoJoin.class);
        startActivity(intent);
    }


    public void setUserDataForKakao(User kakaoUser) {
        final String randomKey = UUID.randomUUID().toString();

        UserAccount user = new UserAccount();
        user.setuId(randomKey);
        user.setKakaoId(kakaoUser.getId());
        user.setEmail(kakaoUser.getKakaoAccount().getEmail());
        user.setUserNm(kakaoUser.getKakaoAccount().getProfile().getNickname());

        firebaseFirestore
                .collection(CollectionPath)
                .document(randomKey)
                .set(user);
    }
}
