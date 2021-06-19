package com.arty.Main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.arty.R;

public class Vanner1Fragment extends Fragment {
    Button btn_add;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_vanner1, container, false);

        btn_add = getActivity().findViewById(R.id.btn_add);
        btn_add.setVisibility(View.INVISIBLE);

        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        btn_add.setVisibility(View.VISIBLE);
    }
}