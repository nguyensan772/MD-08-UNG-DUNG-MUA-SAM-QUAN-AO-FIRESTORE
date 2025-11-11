package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ManDangKy extends AppCompatActivity {

    TextView nutDangKyTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_man_dang_ky);


       nutDangKyTextView=findViewById(R.id.nutDangKyTextView);

nutDangKyTextView.setOnClickListener(v -> {
    Intent intent=new Intent(ManDangKy.this,DangNhap.class);

    startActivity(intent);
    finish();
});
    }
}