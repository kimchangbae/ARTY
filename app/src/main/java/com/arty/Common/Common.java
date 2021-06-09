package com.arty.Common;

import android.app.Activity;
import android.os.Build;
import android.util.Patterns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Common extends Activity {
    public String TO_DAY = new SimpleDateFormat("yyMMdd").format(new Date(System.currentTimeMillis()));

    final String randomKey = UUID.randomUUID().toString();

    public boolean validationEmail(String email) {
        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true;
        }
        return false;
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
