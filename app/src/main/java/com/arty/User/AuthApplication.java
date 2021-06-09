package com.arty.User;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.arty.Qna.QnaMainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.AccessTokenInfo;
import com.kakao.sdk.user.model.User;

import java.util.UUID;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class AuthApplication extends Application {
    static final String TAG = "AuthApplication";

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"----------------------------------------------------");
        Log.d(TAG,"AuthApplication START");

        // Android SDK 를 사용하기 위해 네이티브 앱키로 초기화 진행
        KakaoSdk.init(this,"4309433de6aa50ed57287c86ce90b7af");

        Log.d(TAG,"AuthApplication END");
        Log.d(TAG,"----------------------------------------------------");
    }

    public void getHash() {
        // 카카오 디버그해시키 생성을 위한 처리
        // String debugKeyHash = Utility.INSTANCE.getKeyHash(this);
        // Log.d("AuthApplication","debugKeyHash ---> [" +debugKeyHash+"]");
    }
}
