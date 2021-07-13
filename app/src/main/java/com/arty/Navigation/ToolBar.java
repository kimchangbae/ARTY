package com.arty.Navigation;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.arty.Book.PlantBook;
import com.arty.R;

public class ToolBar extends Fragment {

    private TextView    searchContent;
    private ImageButton button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.navigation_tool_bar, container, false);

        searchContent   = viewGroup.findViewById(R.id.et_search_plant);
        button          = viewGroup.findViewById(R.id.imageButton2);

        return viewGroup;
    }

    @Override
    public void onStart() {
        super.onStart();

        button.setOnClickListener(v -> {
            plantSearch();
        });
    }

    private void plantSearch() {
        String plantName = searchContent.getText().toString();
        Toast.makeText(getContext(),"[" + plantName + "] 검색", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), PlantBook.class);
        intent.putExtra("plantName", plantName);
        startActivity(intent);
        getActivity().finish();
    }
}