package com.arty.Main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.arty.R;


public class MainFragment extends Fragment {
    private static String TAG = "MainFragment";
    private SwipeRefreshLayout refreshLayout = null;
    private String userId;
    Button btn_add;
    ImageButton vanner1, vanner2;
    FragmentManager fragmentManager;
    FragmentTransaction transaction;
    Vanner1Fragment vanner1Fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate--------------");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView--------------");
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_main, container, false);
        ((MainActivity)getActivity()).navigation = "main";
        refreshLayout = viewGroup.findViewById(R.id.refreshMainLayout);
        userId = ((MainActivity)getActivity()).userId;

        btn_add = getActivity().findViewById(R.id.btn_add);
        btn_add.setVisibility(View.INVISIBLE);

        vanner1 = viewGroup.findViewById(R.id.vannerImage1);
        vanner2 = viewGroup.findViewById(R.id.vannerImage2);

        vanner1Fragment = new Vanner1Fragment();

        fragmentManager = getActivity().getSupportFragmentManager();
        transaction     = fragmentManager.beginTransaction();


        return viewGroup;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        btn_add.setVisibility(View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();

        vanner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"배너 1 클릭", Toast.LENGTH_SHORT).show();
                transaction.replace(R.id.frame, vanner1Fragment).commit();
            }
        });

        vanner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(),"배너 2 클릭", Toast.LENGTH_SHORT).show();
            }
        });
    }
}