package com.arty.Navigation;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.arty.Main.MainFragment;
import com.arty.Qna.QnaFragment;
import com.arty.R;
import com.arty.User.MyPageFragment;

public class NavigationBottom extends Fragment {

    Button mainPage, aiPage, freePage, marketPage, myPage;

    MainFragment mainFragment;
    QnaFragment qnaMainFragment;
    MyPageFragment myPageFragment;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.navigation_bottom, container, false);

        mainPage    = viewGroup.findViewById(R.id.btn_main);        // 홈
        aiPage      = viewGroup.findViewById(R.id.btn_ai);          // AI진단(Q&A)
        freePage    = viewGroup.findViewById(R.id.btn_free);        // 자랑하기
        marketPage  = viewGroup.findViewById(R.id.btn_market);      // 마켓
        myPage      = viewGroup.findViewById(R.id.btn_myPage);      // 마이페이지

        mainFragment    = new MainFragment();
        qnaMainFragment = new QnaFragment();
        myPageFragment  = new MyPageFragment();

        fragmentManager = getActivity().getSupportFragmentManager();
        transaction     = fragmentManager.beginTransaction();

        transaction.replace(R.id.frame, mainFragment).commit();

        mainPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction     = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, mainFragment).commit();
            }
        });

        aiPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction     = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, qnaMainFragment).commit();
            }
        });

        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transaction     = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, myPageFragment).commit();
            }
        });

        return viewGroup;
    }
}