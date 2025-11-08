package com.example.md_08_ungdungfivestore;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class ManThongTinCaNhan extends AppCompatActivity {

    private ImageButton btnBack;
    private ImageView imgAvatar;
    private TextInputLayout tilName, tilEmail, tilPhone, tilAddress;
    private TextInputEditText edtName, edtEmail, edtPhone, edtAddress;
    private Button btnSave;

    private static final String PREFS = "user_profile";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_AVATAR_URI = "avatar_uri";

    private Uri avatarUri = null;

    // Chọn ảnh từ thư viện
    private final ActivityResultLauncher<String> pickImage =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    avatarUri = uri;
                    imgAvatar.setImageURI(uri);
                }
            });

    // Xin quyền đọc ảnh (API <33: READ_EXTERNAL_STORAGE, API 33+: READ_MEDIA_IMAGES)
    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    pickImage.launch("image/*");
                } else {
                    Toast.makeText(this, "Bạn cần cấp quyền để chọn ảnh.", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thong_tin_ca_nhan); // đặt tên file XML này đúng với project của bạn

        mapViews();
        wireEvents();
        loadProfileToForm();
        setSaveEnabledWhenChanged();
    }

    private void mapViews() {
        btnBack   = findViewById(R.id.btnBack);
        imgAvatar = findViewById(R.id.imgAvatar);

        tilName   = (TextInputLayout) findViewById(getIdByName("text_input_layout", "Họ và tên", R.id.edtName));
        tilEmail  = (TextInputLayout) edtWrapperOf(R.id.edtEmail);
        tilPhone  = (TextInputLayout) edtWrapperOf(R.id.edtPhone);
        tilAddress= (TextInputLayout) edtWrapperOf(R.id.edtAddress);

        edtName   = findViewById(R.id.edtName);
        edtEmail  = findViewById(R.id.edtEmail);
        edtPhone  = findViewById(R.id.edtPhone);
        edtAddress= findViewById(R.id.edtAddress);
        btnSave   = findViewById(R.id.btnSave);
    }

    // Trợ giúp: lấy TextInputLayout bọc quanh EditText
    private TextInputLayout edtWrapperOf(int editId) {
        View v = findViewById(editId);
        if (v != null && v.getParent() instanceof View) {
            View parent = (View) v.getParent().getParent(); // EditText -> box -> TextInputLayout
            if (parent instanceof TextInputLayout) return (TextInputLayout) parent;
        }
        return null;
    }

    // Không bắt buộc: demo lấy wrapper của edtName nếu muốn setError
    private int getIdByName(String type, String hint, int fallbackId) {
        return fallbackId; // Giữ đơn giản: dùng trực tiếp fallbackId
    }

    private void wireEvents() {
        // Back: nếu mở từ màn Cài đặt thì finish(); nếu không, có thể điều hướng tường minh
        btnBack.setOnClickListener(v -> {
            // Nếu bạn muốn về màn Cài đặt một cách tường minh:
            // Intent i = new Intent(this, CaiDatActivity.class);
            // i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // startActivity(i);
            finish();
        });

        // Chọn avatar khi bấm ảnh
        imgAvatar.setOnClickListener(v -> requestAndPickImage());

        // Lưu
        btnSave.setOnClickListener(v -> {
            clearErrors();
            if (!validate()) return;
            saveProfile();
        });
    }

    private void requestAndPickImage() {
        String permission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        } else {
            permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        }

        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED) {
            pickImage.launch("image/*");
        } else {
            requestPermissionLauncher.launch(permission);
        }
    }

    private void clearErrors() {
        if (tilName != null) tilName.setError(null);
        if (tilPhone != null) tilPhone.setError(null);
        if (tilAddress != null) tilAddress.setError(null);
    }

    private boolean validate() {
        boolean ok = true;

        String name = str(edtName);
        String phone = str(edtPhone);

        if (TextUtils.isEmpty(name)) {
            if (tilName != null) tilName.setError("Vui lòng nhập họ và tên");
            ok = false;
        }

        if (!TextUtils.isEmpty(phone) && !phone.matches("^(0|\\+84)(\\d){8,10}$")) {
            if (tilPhone != null) tilPhone.setError("Số điện thoại không hợp lệ (VD: 098xxxxxxx)");
            ok = false;
        }

        return ok;
    }

    private void saveProfile() {
        btnSave.setEnabled(false);

        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);
        sp.edit()
                .putString(KEY_NAME, str(edtName))
                .putString(KEY_EMAIL, str(edtEmail)) // edtEmail đang disabled nhưng vẫn lưu khi đã có sẵn
                .putString(KEY_PHONE, str(edtPhone))
                .putString(KEY_ADDRESS, str(edtAddress))
                .putString(KEY_AVATAR_URI, avatarUri != null ? avatarUri.toString() : "")
                .apply();

        Toast.makeText(this, "Đã lưu thông tin.", Toast.LENGTH_SHORT).show();
        btnSave.setEnabled(true);
        setResult(RESULT_OK);
        // giữ nguyên màn hình, hoặc đóng nếu bạn muốn:
        // finish();
    }

    private void loadProfileToForm() {
        SharedPreferences sp = getSharedPreferences(PREFS, MODE_PRIVATE);

        String name = sp.getString(KEY_NAME, "");
        String email = sp.getString(KEY_EMAIL, "");
        String phone = sp.getString(KEY_PHONE, "");
        String address = sp.getString(KEY_ADDRESS, "");
        String avatar = sp.getString(KEY_AVATAR_URI, "");

        edtName.setText(name);
        edtEmail.setText(email);
        edtPhone.setText(phone);
        edtAddress.setText(address);

        if (!TextUtils.isEmpty(avatar)) {
            avatarUri = Uri.parse(avatar);
            imgAvatar.setImageURI(avatarUri);
        } else {
            // fallback: dùng drawable mặc định đã set trong XML
        }
    }

    private void setSaveEnabledWhenChanged() {
        TextWatcher watcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                btnSave.setEnabled(true);
            }
            @Override public void afterTextChanged(Editable s) {}
        };
        edtName.addTextChangedListener(watcher);
        edtPhone.addTextChangedListener(watcher);
        edtAddress.addTextChangedListener(watcher);
        // Email là read-only
    }

    private String str(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}
