package com.arty.Main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.arty.R;


public class MainFragment extends Fragment {
    private static String TAG = "MainFragment";
    private SwipeRefreshLayout refreshLayout = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);

        refreshLayout = viewGroup.findViewById(R.id.refreshMainLayout);

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();

        Button btn_add = getActivity().findViewById(R.id.btn_add);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Main 추가",Toast.LENGTH_SHORT).show();
            }
        });

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "메인 프레그먼트 새로고침");
                try{
                    Thread.sleep(7000);
                    refreshLayout.setRefreshing(false);
                }
                catch (Exception e){e.printStackTrace();}
            }
        });
    }
}