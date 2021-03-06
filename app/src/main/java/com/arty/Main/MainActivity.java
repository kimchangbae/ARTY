package com.arty.Main;

import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.arty.Common.TimeComponent;
import com.arty.Navigation.NavigationBottom;
import com.arty.Navigation.ToolBar;
import com.arty.R;
import com.google.firebase.auth.FirebaseAuth;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private long clickTime = 0;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    private ToolBar             toolBar;
    private NavigationBottom    bottomNavigation;

    protected  FirebaseAuth        mAuth;
    protected  UserApiClient       mKakao;

    public static String userId;
    public static String navigation;

    public TimeComponent timeComponent;
    private MainViewModel mainViewModel;

    public MainActivity() {
        Log.d(TAG,"Init--Main");
        navigation = "main";
        userId = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeComponent = new TimeComponent();
        mainViewModel = new ViewModelProvider(this,
                new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(MainViewModel.class);

        mAuth       = FirebaseAuth.getInstance();
        mKakao      = UserApiClient.getInstance();

        // fragmentManager     = getSupportFragmentManager();
        // transaction         = fragmentManager.beginTransaction();

        // toolBar             = (ToolBar)  fragmentManager.findFragmentById(R.id.toolBar);
        // bottomNavigation    = (NavigationBottom) fragmentManager.findFragmentById(R.id.navigation);

        Log.d(TAG,"???????????? ??????????????? ????????????");

        searchUserId();

        mainViewModel.getUserId().observeForever(userId -> {
            if(userId != null && !userId.equals("")) {
                this.userId = userId;
            }
        });
    }

    private void searchUserId() {
        if(mAuth.getCurrentUser() != null) {
            Log.d(TAG,"mAuth ID ---> " + mAuth.getCurrentUser().getEmail());
            mainViewModel.getUserId(mAuth.getCurrentUser().getEmail());
        } else if(AuthApiClient.getInstance().hasToken()) {
            mKakao.me(new Function2<User, Throwable, Unit>() {
                @Override
                public Unit invoke(User user, Throwable throwable) {
                    if(user != null) {
                        Log.d(TAG,"mKakao ID ---> " + user.getId());
                        mainViewModel.getUserId(user.getId());
                    }
                    return null;
                }
            });
        }
    }



    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // ???????????? ?????? ?????? ???
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                Toast.makeText(getApplicationContext(), "??????????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
                killApplication();

                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplicationContext(), "?????? ??? ????????? ?????? ?????????.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
    private void killApplication() {
        // ???????????? ?????????????????? ??????
        moveTaskToBack(true);

        // ???????????? ?????? + ????????? ??????????????? ?????????
        if (Build.VERSION.SDK_INT >= 21) {
            finishAndRemoveTask();
        } else {
            finish();
        }

        // ??? ???????????? ??????
        android.os.Process.killProcess(android.os.Process.myPid());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"---------------------------");
        Log.d(TAG,"MAIN ACTIVITY DISTROY");
        Log.d(TAG,"---------------------------");
    }
}