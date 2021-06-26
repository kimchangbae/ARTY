package com.arty.Navigation;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.arty.FreeBoard.FreeBoardFragment;
import com.arty.Main.MainActivity;
import com.arty.Main.MainFragment;
import com.arty.Qna.QnaFragment;
import com.arty.R;
import com.arty.User.MyPageFragment;

import static android.content.ContentValues.TAG;

public class NavigationBottom extends Fragment {

    Button mainPage, aiPage, freePage, marketPage, myPage;

    MainFragment        mainFragment;
    QnaFragment         qnaMainFragment;
    FreeBoardFragment   freeBoardFragment;
    MyPageFragment      myPageFragment;

    FragmentManager     fragmentManager;
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

        mainFragment        = new MainFragment();
        qnaMainFragment     = new QnaFragment();
        freeBoardFragment   = new FreeBoardFragment();
        myPageFragment      = new MyPageFragment();

        fragmentManager = getActivity().getSupportFragmentManager();
        transaction     = fragmentManager.beginTransaction();

        String navigation = ((MainActivity)getActivity()).navigation;
        if(navigation.equals("main"))   transaction.replace(R.id.frame, mainFragment).commit();
        if(navigation.equals("ai"))     transaction.replace(R.id.frame, qnaMainFragment).commit();
        if(navigation.equals("free"))   transaction.replace(R.id.frame, freeBoardFragment).commit();
        //if(navigaton.equals("market")) transaction.replace(R.id.frame, mainFragment).commit();
        if(navigation.equals("myPage")) transaction.replace(R.id.frame, myPageFragment).commit();

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();

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
                Log.d(TAG,"OnClickListener----> AI진단 호출");
                transaction     = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, qnaMainFragment).commit();
            }
        });

        freePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"OnClickListener----> 자유게시판 호출");
                transaction     = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, freeBoardFragment).commit();
            }
        });

        myPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"OnClickListener----> 마이페이지 호출");
                transaction     = fragmentManager.beginTransaction();
                transaction.replace(R.id.frame, myPageFragment).commit();
            }
        });
    }
}