package com.arty.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arty.Navigation.NavigationBottom;
import com.arty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MyPageFragment extends Fragment {
    private static String TAG = "MyPageFragment";

    private FirebaseAuth    mAuth;
    private UserApiClient   userApiClient;

    Button btn_logout, btn_withdraw, btn_add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth           = FirebaseAuth.getInstance();
        userApiClient   = UserApiClient.getInstance();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_my_page, container, false);

        btn_logout      = viewGroup.findViewById(R.id.btn_logout);
        btn_withdraw    = viewGroup.findViewById(R.id.btn_withdraw);

        btn_add = getActivity().findViewById(R.id.btn_add);
        btn_add.setVisibility(View.INVISIBLE);

        String getUserId = mAuth.getTenantId();
        TextView tv_show_id = viewGroup.findViewById(R.id.tv_show_id);
        tv_show_id.setText(getUserId + " 님");

        return viewGroup;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        btn_add.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoTalkLogout();
                firebaseLogout();
                goToLogin();
            }
        });

        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kakaoTalkWithdraw();
                firebaseWithdraw();
                goToLogin();
            }
        });
    }

    private void goToLogin() {
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    // 파이어베이스 로그아웃
    private void firebaseLogout() {
        mAuth.getInstance().signOut();
        Log.d(TAG,"----------------------------------------------------");
        Log.d(TAG, "파이어베이스 로그아웃");
        Log.d(TAG,"----------------------------------------------------");
    }

    // 카카오톡 로그아웃
    private void kakaoTalkLogout() {
        userApiClient = UserApiClient.getInstance();
        userApiClient.logout(new Function1<Throwable, Unit>() {
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

    // 파이어베이스 회원탈퇴
    private void firebaseWithdraw() {
        Log.d(TAG,"----------------------------------------------------");
        Log.d(TAG, "파이어베이스 회원탈퇴");
        Log.d(TAG,"----------------------------------------------------");
    }

    // 카카오톡 회원탈퇴
    public void kakaoTalkWithdraw(){
        userApiClient = UserApiClient.getInstance();
        userApiClient.unlink(new Function1<Throwable, Unit>() {
            @Override
            public Unit invoke(Throwable throwable) {
                Log.d("AuthApplication", "logoutFunction");

                if(throwable != null) {
                    Log.d(TAG,"----------------------------------------------------");
                    Log.d("AuthApplication", "카카오톡 회원탈퇴 에러 발생" + throwable.getMessage());
                    Log.d(TAG,"----------------------------------------------------");
                }

                return null;
            }
        });
    }
}