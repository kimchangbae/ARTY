package com.arty.User;

import android.content.Intent;
import android.os.Build;
import android.util.Patterns;

import androidx.appcompat.app.AppCompatActivity;

import com.arty.Main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.kakao.sdk.user.UserApiClient;

public class CommonAuth extends AppCompatActivity {
    final static String COLLECTION_PATH = "USER_ACCOUNT";

    String pswdPattern  = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@$!%*#?&]).{8,15}.$";
    String pswdPattern2 = "((?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[^a-zA-Z0-9가-힣]).{8,16})";

    protected   FirebaseFirestore   mDB;
    protected   FirebaseAuth        mAuth;
    protected   UserApiClient       mKakao;

    public CommonAuth() {
        mDB     = FirebaseFirestore.getInstance();
        mAuth   = FirebaseAuth.getInstance();
        mKakao  = UserApiClient.getInstance();
    }

    protected void killApplication() {
        // 태스크를 백그라운드로 이동
        moveTaskToBack(true);

        // 액티비티 종료 + 태스크 리스트에서 지우기
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }

        // 앱 프로세스 종료
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public boolean validationEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
    }

    public void goToFirebaseRegister() {
        Intent intent = new Intent(this, RegisterFirebase.class);
        startActivity(intent);
    }

    public void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void goTokakaoRegister() {
        Intent intent = new Intent(this, RegisterKakao.class);
        startActivity(intent);
        finish();
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }
}
