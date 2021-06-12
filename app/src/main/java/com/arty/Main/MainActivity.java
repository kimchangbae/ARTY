package com.arty.Main;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

import com.arty.Navigation.NavigationBottom;
import com.arty.Navigation.ToolBar;
import com.arty.R;

public class MainActivity extends AppCompatActivity {
    static final String TAG         = "MainActivity";
    private long clickTime = 0;

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    ToolBar             toolBar;
    NavigationBottom    bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        transaction     = fragmentManager.beginTransaction();

        toolBar = (ToolBar)  fragmentManager.findFragmentById(R.id.toolBar);
        bottomNavigation = (NavigationBottom) fragmentManager.findFragmentById(R.id.navigation);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG,"결과값 --->" + requestCode);
        Log.d(TAG,"결과값 --->" + resultCode);
        Log.d(TAG,"결과값 --->" + data.getData());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 뒤로가기 버튼 클릭 시
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(SystemClock.elapsedRealtime() - clickTime < 2000) {
                Toast.makeText(getApplicationContext(), "프로그램이 종료 되었습니다.", Toast.LENGTH_SHORT).show();
                killARTY();

                return true;
            }
            clickTime = SystemClock.elapsedRealtime();
            Toast.makeText(getApplicationContext(), "한번 더 누르면 종료 됩니다.", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"---------------------------");
        Log.d(TAG,"MAIN ACTIVITY DISTROY");
        Log.d(TAG,"---------------------------");
    }

    protected void killARTY() {
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
}