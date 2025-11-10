package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.md_08_ungdungfivestore.MainActivity;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.ThanhToanActivity;

public class GioHangFragment extends Fragment {
    private RecyclerView gioHangRecyclerView;
    private TextView tongSoTienTxt;
    private AppCompatButton thanhToanBtn;

    public GioHangFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gio_hang, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        anhXa(view);

        thanhToanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent thanhToanIntent = new Intent(getContext(), ThanhToanActivity.class);
                startActivity(thanhToanIntent);
            }
        });
    }

    private void anhXa(View view) {
        gioHangRecyclerView = view.findViewById(R.id.gioHangRecyclerView);
        tongSoTienTxt = view.findViewById(R.id.tongSoTienTxt);
        thanhToanBtn = view.findViewById(R.id.thanhToanBtn);
    }
}