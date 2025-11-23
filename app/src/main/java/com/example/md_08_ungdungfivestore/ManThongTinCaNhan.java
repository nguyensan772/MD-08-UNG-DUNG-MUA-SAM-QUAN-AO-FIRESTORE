package com.example.md_08_ungdungfivestore;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class ManThongTinCaNhan extends AppCompatActivity {

    private ImageButton quayLaiBtn;
    private ImageView imgAvatar;
    private TextInputEditText edtName, edtEmail, edtPhone, edtAddress;
    private Button btnSave;

    private SharedPreferences prefs;

    private static final String PREF_NAME      = "user_profile";
    private static final String KEY_NAME       = "name";
    private static final String KEY_EMAIL      = "email";
    private static final String KEY_PHONE      = "phone";
    private static final String KEY_ADDRESS    = "address";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ca_nhan); // đổi đúng tên layout của bạn

        // Khởi tạo SharedPreferences
        prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Ánh xạ view
        quayLaiBtn  = findViewById(R.id.quayLaiBtn);
        imgAvatar   = findViewById(R.id.imgAvatar);
        edtName     = findViewById(R.id.edtName);
        edtEmail    = findViewById(R.id.edtEmail);
        edtPhone    = findViewById(R.id.edtPhone);
        edtAddress  = findViewById(R.id.edtAddress);
        btnSave     = findViewById(R.id.btnSave);

        // Nút quay lại: đóng màn hình này → quay về màn trước (CaiDatActivity)
        quayLaiBtn.setOnClickListener(v -> finish());

        // Load dữ liệu đã lưu (nếu có)
        loadUserInfo();

        // Xử lý nút Lưu thông tin
        btnSave.setOnClickListener(v -> saveUserInfo());
    }

    private void loadUserInfo() {
        String name    = prefs.getString(KEY_NAME, "");
        String email   = prefs.getString(KEY_EMAIL, "");
        String phone   = prefs.getString(KEY_PHONE, "");
        String address = prefs.getString(KEY_ADDRESS, "");

        edtName.setText(name);
        edtEmail.setText(email);   // đang disable nên chỉ hiển thị
        edtPhone.setText(phone);
        edtAddress.setText(address);

        // TODO: sau này nếu có avatar thật thì load ở đây (từ URL hoặc Uri)
    }

    private void saveUserInfo() {
        String name    = edtName.getText()    != null ? edtName.getText().toString().trim()    : "";
        String email   = edtEmail.getText()   != null ? edtEmail.getText().toString().trim()   : "";
        String phone   = edtPhone.getText()   != null ? edtPhone.getText().toString().trim()   : "";
        String address = edtAddress.getText() != null ? edtAddress.getText().toString().trim() : "";

        // Validate đơn giản
        if (name.isEmpty()) {
            edtName.setError("Vui lòng nhập họ tên");
            edtName.requestFocus();
            return;
        }

        if (phone.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại");
            edtPhone.requestFocus();
            return;
        }

        // Có thể thêm validate phone (độ dài, chỉ số, …) nếu cần

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PHONE, phone);
        editor.putString(KEY_ADDRESS, address);
        editor.apply();

        Toast.makeText(this, "Lưu thông tin thành công", Toast.LENGTH_SHORT).show();
    }
}
