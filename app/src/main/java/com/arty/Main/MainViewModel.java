package com.arty.Main;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;


public class MainViewModel extends AndroidViewModel {
    private MainRepository mainRepository;
    private MutableLiveData<String> userId;

    public MainViewModel(Application application) {
        super(application);

        mainRepository = new MainRepository(application);
        userId = mainRepository.getUserId();
    }

    public MutableLiveData<String> getUserId() {
        return userId;
    }

    public void getUserId(String email) {
        mainRepository.getUserId(email);
    }

    public void getUserId(long kakaoId) {
        mainRepository.getUserId(kakaoId);
    }
}
