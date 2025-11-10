package com.example.md_08_ungdungfivestore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;

public class YeuThichFragment extends Fragment {
    private RecyclerView danhSachYeuThichRecyclerView;

    public YeuThichFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.activity_man_yeu_thich, container ,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhXa(view);
    }

    private void anhXa(View view) {
        danhSachYeuThichRecyclerView = view.findViewById(R.id.danhSachYeuThichRecyclerView);
    }
}
