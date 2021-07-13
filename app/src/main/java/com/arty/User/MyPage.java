package com.arty.User;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;

import com.arty.R;

public class MyPage extends CommonAuth {
    private static final String TAG = "MyPageActivity";
    private MyPageViewModel myPageViewModel;
    private LoginViewModel  loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_my_page);

        myPageViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MyPageViewModel.class);
        loginViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(LoginViewModel.class);
    }
}