package com.arty.User;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.arty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

public class MyPageFragment extends Fragment {
    private static String TAG = "MyPageFragment";
    private static String COLLECTION_PATH = "USER_ACCOUNT";

    private FirebaseAuth        mAuth;
    private UserApiClient       mKakao;
    private FirebaseFirestore   mDB;

    Button btn_logout, btn_withdraw, btn_add;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"ON CREATE --> MyPageFragment");

        mAuth       = FirebaseAuth.getInstance();
        mKakao      = UserApiClient.getInstance();
        mDB         = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"ON ON CREATE VIEW --> MyPageFragment");
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_mypage, container, false);

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
                if(mAuth.getCurrentUser() != null) {
                    firebaseLogout();
                } else {
                    kakaoTalkLogout();
                }
                mDB.terminate();
                goToLogin();
            }
        });

        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    firebaseWithdraw();
                } else {
                    kakaoTalkWithdraw();
                }

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

    // 파이어베이스 회원탈퇴
    private void firebaseWithdraw() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete().addOnCompleteListener(task -> {
            Log.d(TAG,"----------------------------------------------------");
            Log.d(TAG, "파이어베이스 회원탈퇴 [Email : " +user.getEmail()+ "]");
            Log.d(TAG,"----------------------------------------------------");

            mDB
            .collection(COLLECTION_PATH)
            .whereEqualTo("email",user.getEmail())
            .get()
            .addOnCompleteListener(tasks -> {
                if(tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        String documentId = document.getId();
                        Log.d(TAG, "파이어베이스 회원탈퇴 문서 ID : [" +documentId+ "]");

                        mDB.collection(COLLECTION_PATH).document(documentId).delete()
                        .addOnCompleteListener(task1 -> {
                            Log.d(TAG,"파이어베이스 " + user + " 회원의 탈퇴가 완료 되었습니다.");
                        });
                    }
                }
            });
        });

    }

    // 카카오톡 로그아웃
    private void kakaoTalkLogout() {
        mKakao = UserApiClient.getInstance();
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

    Function2<User, Throwable, Unit> function2 = new Function2<User, Throwable, Unit>() {
        @Override
        public Unit invoke(User user, Throwable throwable) {
            mDB
            .collection(COLLECTION_PATH)
            .whereEqualTo("kakaoId",user.getId())
            .get()
            .addOnCompleteListener(tasks -> {
                if(tasks.isSuccessful()) {
                    for (QueryDocumentSnapshot document : tasks.getResult()) {
                        String documentId = document.getId();
                        Log.d(TAG, "카카오톡 회원탈퇴 문서 ID : [" +documentId+ "]");

                        mDB.collection(COLLECTION_PATH).document(documentId).delete()
                        .addOnCompleteListener(task1 -> {
                            Log.d(TAG,"카카오톡 " + user + " 회원의 탈퇴가 완료 되었습니다.");
                        });
                    }
                }
            });
            return null;
        }
    };

    // 카카오톡 회원탈퇴
    public void kakaoTalkWithdraw(){
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
}