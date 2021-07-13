package com.arty.User;


import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

@RequiresApi(api = Build.VERSION_CODES.P)
public class LoginViewModel extends AndroidViewModel {

    private LoginRepository authRepository;
    private MutableLiveData<String>     mutableLiveData;

    public LoginViewModel(Application application) {
        super(application);
        authRepository = new LoginRepository(application);

        mutableLiveData = authRepository.getMutableLiveData();
    }

    public MutableLiveData<String> getMutableLiveData() {
        return mutableLiveData;
    }

    public void register(String email, String password) {
        authRepository.register(email, password);
    }

    public void login(String email, String password) {
        authRepository.login(email, password);
    }

    public void kakaoLogin() {
        authRepository.kakaoLogin();
    }


}
