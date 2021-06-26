package com.arty.User;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class MyPageViewModel extends AndroidViewModel {
    private MyPageRepository myPageRepository;
    private MutableLiveData<Boolean> withdrawalResult;

    public MyPageViewModel(Application application) {
        super(application);

        myPageRepository = new MyPageRepository(application);
        withdrawalResult = myPageRepository.getWithdrawalResult();
    }

    public MutableLiveData<Boolean> getWithdrawalResult() {
        return withdrawalResult;
    }

    public void firebaseLogout() {
        myPageRepository.firebaseLogout();
    }

    public void firebaseWithdrawal() {
        myPageRepository.firebaseWithdrawal();
    }

    public void kakaoTalkLogout() {
        myPageRepository.kakaoTalkLogout();
    }

    public void kakaoTalkWithdrawal() {
        myPageRepository.kakaoTalkWithdrawal();
    }
}
