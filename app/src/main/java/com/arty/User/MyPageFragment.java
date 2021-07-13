package com.arty.User;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.arty.Main.MainActivity;
import com.arty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;

public class MyPageFragment extends Fragment {
    private static String TAG = "MyPageFragment";

    private FirebaseAuth        mAuth;
    private UserApiClient       mKakao;
    private AuthApiClient       mKakaoAuth;
    private FirebaseFirestore   mDB;

    private Button btn_logout, btn_withdraw, btn_add;
    private String userId;
    private TextView showUserId;

    private MyPageViewModel myPageViewModel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"ON CREATE --> MyPageFragment");
        ((MainActivity)getActivity()).navigation = "myPage";

        myPageViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getActivity().getApplication())).get(MyPageViewModel.class);

        mAuth       = FirebaseAuth.getInstance();
        mKakao      = UserApiClient.getInstance();
        mKakaoAuth  = AuthApiClient.getInstance();
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

        userId = ((MainActivity)getActivity()).userId;

        showUserId = viewGroup.findViewById(R.id.tv_show_id);
        if(userId == null || userId.equals(null)) {
            showUserId.setText("GUEST 님");
        } else {
            showUserId.setText(userId + " 님");
        }

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
                    myPageViewModel.firebaseLogout();
                } else {
                    myPageViewModel.kakaoTalkLogout();
                }
                // goToLoginActivity();
                goToMainActivity();
            }
        });

        btn_withdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    myPageViewModel.firebaseWithdrawal();
                } else {
                    myPageViewModel.kakaoTalkWithdrawal();
                }
            }
        });

        myPageViewModel.getWithdrawalResult().observeForever(aBoolean -> {
            if(aBoolean) {
                Toast.makeText(getContext(),"회원탈퇴 완료",Toast.LENGTH_SHORT).show();
                // goToLoginActivity();
                goToMainActivity();
            }
        });
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(getActivity(), Login.class);
        startActivity(intent);
        getActivity().finish();
    }

    private void goToMainActivity() {
        Intent intent = new Intent(getActivity(), MainActivity.class);
        startActivity(intent);
        getActivity().finish();
    }
}