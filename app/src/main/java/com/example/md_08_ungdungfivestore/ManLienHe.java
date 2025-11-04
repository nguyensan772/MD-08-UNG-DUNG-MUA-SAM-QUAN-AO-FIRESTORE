package com.example.md_08_ungdungfivestore;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ManLienHe extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText edtHoTen, edtSDT, edtEmail, edtDiaChi, edtNoiDung;
    private Button btnGui;
    private TextView tvPhone, tvEmail, tvAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ⚠️ Đổi cho đúng tên file layout:
        setContentView(R.layout.activity_lien_he);

        bindViews();
        wireBasicActions();
    }

    private void bindViews() {
        btnBack     = findViewById(R.id.btnBack);
        edtHoTen    = findViewById(R.id.edtHoTen);
        edtSDT      = findViewById(R.id.edtSDT);
        edtEmail    = findViewById(R.id.edtEmail);
        edtDiaChi   = findViewById(R.id.edtDiaChi);
        edtNoiDung  = findViewById(R.id.edtNoiDung);
        btnGui      = findViewById(R.id.btnGui);
        tvPhone     = findViewById(R.id.tvPhone);
        tvEmail     = findViewById(R.id.tvEmail);
        tvAddress   = findViewById(R.id.tvAddress);
    }

    private void wireBasicActions() {
        // Nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Gửi Email
        btnGui.setOnClickListener(v -> onSendContact());

        // Gọi điện
        tvPhone.setOnClickListener(v -> {
            String phone = extractPhone(tvPhone.getText().toString());
            if (!TextUtils.isEmpty(phone)) {
                startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone)));
            }
        });

        // Gửi mail
        tvEmail.setOnClickListener(v -> {
            String to = extractEmail(tvEmail.getText().toString());
            openEmailApp(to, "[Hỏi đáp] Từ màn Liên hệ", "");
        });

        // Mở bản đồ
        tvAddress.setOnClickListener(v -> {
            String addr = tvAddress.getText().toString().replace("Địa chỉ:", "").trim();
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(addr));
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            try {
                startActivity(mapIntent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=" + Uri.encode(addr))));
            }
        });
    }

    // ====== HÀM GỬI LIÊN HỆ ======
    private void onSendContact() {
        String hoTen   = edtHoTen.getText().toString().trim();
        String sdt     = edtSDT.getText().toString().trim();
        String email   = edtEmail.getText().toString().trim();
        String diaChi  = edtDiaChi.getText().toString().trim();
        String noiDung = edtNoiDung.getText().toString().trim();

        if (TextUtils.isEmpty(hoTen)) {
            edtHoTen.setError("Vui lòng nhập họ tên");
            edtHoTen.requestFocus();
            return;
        }
        if (!isValidPhone(sdt)) {
            edtSDT.setError("Số điện thoại không hợp lệ");
            edtSDT.requestFocus();
            return;
        }
        if (!isValidEmail(email)) {
            edtEmail.setError("Email không hợp lệ");
            edtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(noiDung)) {
            edtNoiDung.setError("Vui lòng nhập nội dung liên hệ");
            edtNoiDung.requestFocus();
            return;
        }

        String to = extractEmail(tvEmail.getText().toString());
        if (TextUtils.isEmpty(to)) to = "lienhe@quancaphe.com";

        String subject = "[Liên hệ] " + hoTen + " - " + sdt;
        String body = "Họ tên: " + hoTen + "\nSĐT: " + sdt + "\nEmail: " + email +
                "\nĐịa chỉ: " + diaChi + "\n--------------------------\nNội dung:\n" + noiDung;

        openEmailApp(to, subject, body);
    }

    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPhone(String phone) {
        String digits = phone.replaceAll("[^0-9]", "");
        return digits.length() >= 9 && digits.length() <= 11;
    }

    private String extractPhone(String text) {
        return text.replaceAll("[^0-9]", "");
    }

    private String extractEmail(String text) {
        text = text.trim();
        int idx = text.indexOf(':');
        String maybe = (idx >= 0) ? text.substring(idx + 1).trim() : text;
        if (Patterns.EMAIL_ADDRESS.matcher(maybe).matches()) return maybe;
        java.util.regex.Matcher m = Patterns.EMAIL_ADDRESS.matcher(text);
        return m.find() ? m.group() : "";
    }

    private void openEmailApp(String to, String subject, String body) {
        try {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setData(Uri.parse("mailto:" + Uri.encode(to)));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
            emailIntent.putExtra(Intent.EXTRA_TEXT, body);
            startActivity(Intent.createChooser(emailIntent, "Chọn ứng dụng Email"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Không tìm thấy ứng dụng Email trên máy", Toast.LENGTH_LONG).show();
        }
    }
}
