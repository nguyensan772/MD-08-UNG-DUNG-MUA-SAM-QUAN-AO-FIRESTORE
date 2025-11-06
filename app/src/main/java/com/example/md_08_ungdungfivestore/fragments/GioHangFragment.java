package com.example.md_08_ungdungfivestore.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;

public class GioHangFragment extends Fragment {
    RecyclerView listSanPham ;
    TextView tongTien , nutThanhToan;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return  inflater.inflate(R.layout.fragment_gio_hang,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        anhXa(view);
        nutThanhToan.setOnClickListener(view1 -> {
            Toast.makeText(view.getContext(), "Hello", Toast.LENGTH_SHORT).show();
        });
    }

    public void anhXa(View view){
        listSanPham = view.findViewById(R.id.gioHangRecyclerView);
        tongTien = view.findViewById(R.id.tongSoTienTextView);
        nutThanhToan = view.findViewById(R.id.nutThanhToanTextView);
    }
}
