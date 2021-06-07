package com.arty.User;

import android.app.Application;
import android.util.Log;

import com.kakao.sdk.auth.model.OAuthToken;
import com.kakao.sdk.common.KakaoSdk;
import com.kakao.sdk.common.util.Utility;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class KakaoApplication extends Application {
    static final String TAG = "KakaoApplication";

    @Override
    public void onCreate() {
        super.onCreate();

        // Android SDK 를 사용하기 위해 네이티브 앱키로 초기화 진행
        KakaoSdk.init(this,"4309433de6aa50ed57287c86ce90b7af");

        // String debugKeyHash = Utility.INSTANCE.getKeyHash(this);
        // Log.d("KakaoApplication","debugKeyHash ---> [" +debugKeyHash+"]");
    }

    // 카카오 로그인 연동
    Function2<OAuthToken, Throwable, Unit> callback2 = new Function2<OAuthToken, Throwable, Unit>() {
        @Override
        public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
            Log.d(TAG,"[callback2 호출]");
            if(oAuthToken != null) {
                UserApiClient.getInstance().me(new Function2<User, Throwable, Unit>() {
                    @Override
                    public Unit invoke(User user, Throwable throwable) {
                        if(user != null) {
                            Log.d(TAG, "카카오 사용자 정보(ID : " +user.getId()+")");
                            Log.d(TAG, "카카오 사용자 정보(이메일 : " +user.getKakaoAccount().getEmail()+")");
                            Log.d(TAG, "카카오 사용자 정보(닉네임 : " +user.getKakaoAccount().getProfile().getNickname()+")");

                        }
                        if(throwable != null) {
                            Log.d(TAG, "카카오톡 사용자 정보 요청 실패!!");
                        }
                        return null;
                    }
                });
            }
            if(throwable != null) {
                Log.d(TAG, "카카오 로그인 실패(throwable : " +throwable.getMessage()+")");
            }
            return null;
        }
    };
}
