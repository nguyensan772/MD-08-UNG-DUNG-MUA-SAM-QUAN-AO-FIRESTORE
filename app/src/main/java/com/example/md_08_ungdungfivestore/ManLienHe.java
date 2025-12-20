package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class ManLienHe extends AppCompatActivity {

    private EditText tenEditText, emailEditText, noiDungEditText;
    private MaterialButton guiButton, goiButton, emailButton, mapButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lien_he);

        tenEditText = findViewById(R.id.tenEditText);
        emailEditText = findViewById(R.id.emailEditText);
        noiDungEditText = findViewById(R.id.noiDungEditText);
        guiButton = findViewById(R.id.guiButton);
        goiButton = findViewById(R.id.goiButton);
        emailButton = findViewById(R.id.emailButton);
        mapButton = findViewById(R.id.mapButton);

        // Gửi góp ý
        guiButton.setOnClickListener(v -> {
            String ten = tenEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String noiDung = noiDungEditText.getText().toString().trim();

            if (ten.isEmpty() || email.isEmpty() || noiDung.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Đã gửi liên hệ: " + noiDung, Toast.LENGTH_LONG).show();
                tenEditText.setText("");
                emailEditText.setText("");
                noiDungEditText.setText("");
            }
        });

        // Gọi điện
        goiButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0584720666")); // số hotline
            startActivity(intent);
        });

        // Gửi email
        emailButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:hotro@fivestore.com"));
            intent.putExtra(Intent.EXTRA_SUBJECT, "Liên hệ từ ứng dụng");
            intent.putExtra(Intent.EXTRA_TEXT, "Xin chào, tôi muốn liên hệ...");
            startActivity(intent);
        });

        // Mở bản đồ
        mapButton.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("FiveStore-Hà Nội");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);
        });
    }
}
