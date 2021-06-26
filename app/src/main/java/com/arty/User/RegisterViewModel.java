package com.arty.User;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

@RequiresApi(api = Build.VERSION_CODES.P)
public class RegisterViewModel extends AndroidViewModel {

    private RegisterRepository registerRepository;
    private MutableLiveData<Boolean> authLiveData;
    private MutableLiveData<Boolean> validEmail, validUserId;

    public RegisterViewModel(Application application) {
        super(application);

        registerRepository = new RegisterRepository(application);
        authLiveData = registerRepository.getAuthLiveData();
        validEmail = registerRepository.getvalidLiveData();
        validUserId = registerRepository.getValidUserId();
    }

    public MutableLiveData<Boolean> getAuthLiveData() {
        return authLiveData;
    }

    public MutableLiveData<Boolean> getValidEmail() {
        return validEmail;
    }

    public MutableLiveData<Boolean> getValidUserId() {
        return validUserId;
    }

    public void firebaseRegister(String email, String password, String userId) {
        registerRepository.firebaseRegister(email, password, userId);
    }

    public void kakaoRegister(String uuId, long kakaoId, String email, String userId) {
        registerRepository.kakaoRegister(uuId, kakaoId, email, userId);
    }

    public void validEmail(String email) {
        registerRepository.validEmail(email);
    }

    public void validUserId(String userId) {
        registerRepository.validUserId(userId);
    }
}
