package com.arty.Qna;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.arty.R;

public class QnaPopup extends Activity {

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.qna_popup);

        //Toast.makeText(getApplicationContext(), "QnaPopup Call", Toast.LENGTH_SHORT).show();

        // 팝업이 올라오면 배경 블러처리
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.7f;
        layoutParams.height = 500;
        layoutParams.width = 1000;
        getWindow().setAttributes(layoutParams);

        // 액티비티 바깥화면이 클릭되어도 종료되지 않게 설정하기
        this.setFinishOnTouchOutside(false);
    }

    public void btnSickClick(View v) {
        intent = new Intent(getApplicationContext(), QnaWrite.class);
        intent.putExtra("type", "1");
        startActivityForResult(intent,1);
        finish();
    }
    public void btnCuriousClick(View v) {
        intent = new Intent(getApplicationContext(), QnaWrite.class);
        intent.putExtra("type", "2");
        startActivityForResult(intent,1);
        finish();
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
}