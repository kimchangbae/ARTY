package com.arty.User;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.auth.IApplicationConfig;
import com.kakao.auth.KakaoAdapter;
import com.kakao.auth.KakaoSDK;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.util.helper.CommonProtocol;

public class AuthApplication extends Application {
    static final String TAG = "AuthApplication";
    private static AuthApplication self;



    @Override
    public void onCreate() {
        super.onCreate();
        self = this;
        Log.d(TAG,"----------------------------------------------------");
        Log.d(TAG,"AuthApplication START");

        FirebaseApp.initializeApp(this);

        // Android SDK 를 사용하기 위해 네이티브 앱키로 초기화 진행
        KakaoSdk.init(this,"4309433de6aa50ed57287c86ce90b7af");

        Log.d(TAG,"AuthApplication END");
        Log.d(TAG,"----------------------------------------------------");
    }

    public void getHash() {
        // 카카오 디버그해시키 생성을 위한 처리
        String debugKeyHash = Utility.INSTANCE.getKeyHash(this);
        Log.d("AuthApplication","debugKeyHash ---> [" +debugKeyHash+"]");
    }
}
