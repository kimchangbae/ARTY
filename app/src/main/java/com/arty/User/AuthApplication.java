package com.arty.User;

import android.app.Application;
import android.util.Log;

import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;

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
        String debugKeyHash = Utility.INSTANCE.getKeyHash(this);
        Log.d("AuthApplication","debugKeyHash ---> [" +debugKeyHash+"]");
    }
}
