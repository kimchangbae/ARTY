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

import com.arty.Common.TimeComponent;
import com.arty.Navigation.NavigationBottom;
import com.arty.Navigation.ToolBar;
import com.arty.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kakao.sdk.auth.AuthApiClient;
import com.kakao.sdk.user.UserApiClient;
import com.kakao.sdk.user.model.User;

import kotlin.Unit;
import kotlin.jvm.functions.Function2;

public class MainActivity extends AppCompatActivity {
    static final String TAG         = "MainActivity";

    private long clickTime = 0;
    final static String COLLECTION_PATH = "USER_ACCOUNT";

    FragmentManager fragmentManager;
    FragmentTransaction transaction;

    ToolBar             toolBar;
    NavigationBottom    bottomNavigation;

    protected FirebaseFirestore     mDB;
    protected   FirebaseAuth        mAuth;
    protected UserApiClient         mKakao;
    private AuthApiClient           mKakaoAuth;

    public static String userId;
    public static String navigation;

    public TimeComponent timeComponent;

    public MainActivity() {
        navigation = "main";
        userId = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeComponent = new TimeComponent();

        mAuth       = FirebaseAuth.getInstance();
        mKakao      = UserApiClient.getInstance();
        mKakaoAuth  = AuthApiClient.getInstance();
        mDB         = FirebaseFirestore.getInstance();

        fragmentManager     = getSupportFragmentManager();
        transaction         = fragmentManager.beginTransaction();

        toolBar             = (ToolBar)  fragmentManager.findFragmentById(R.id.toolBar);
        bottomNavigation    = (NavigationBottom) fragmentManager.findFragmentById(R.id.navigation);

        Log.d(TAG,"접속해서 유저아이디 조회하기");
        searchUserId();
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

    public void searchUserId() {
        Log.d(TAG,"MainActivity.searchUserId");
        if(mAuth.getCurrentUser() != null) {
            Log.d(TAG,"mAuth ID ---> " + mAuth.getCurrentUser().getEmail());
            getUserId(mAuth.getCurrentUser().getEmail());
        } else if(mKakaoAuth.hasToken()) {
            mKakao.me(new Function2<User, Throwable, Unit>() {
                @Override
                public Unit invoke(User user, Throwable throwable) {
                    if(user != null) {
                        Log.d(TAG,"mKakao ID ---> " + user.getId());
                        getUserId(user.getId());
                    }
                    return null;
                }
            });
        }
    }

    public void getUserId(String email) {
        mDB.collection(COLLECTION_PATH)
            .whereEqualTo("email",email)
            .get()
            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(Task<QuerySnapshot> task) {
                    if(task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userId = (String) document.getData().get("userId");
                            Log.d(TAG, "DB 에서 검색된 사용자 아이디(파이어베이스) : " + userId);
                        }
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void getUserId(long kakaoId) {
        mDB.collection(COLLECTION_PATH)
        .whereEqualTo("kakaoId",kakaoId)
        .get()
        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        userId = (String) document.getData().get("userId");
                        Log.d(TAG, "DB 에서 검색된 사용자 아이디(카카오) : " + userId);

                    }
                }
            }
        }).addOnFailureListener(e -> e.printStackTrace());
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