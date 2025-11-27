package com.example.md_08_ungdungfivestore;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.md_08_ungdungfivestore.models.Comment;
import com.example.md_08_ungdungfivestore.services.ApiClient;
import com.example.md_08_ungdungfivestore.services.CommentApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DanhGia extends AppCompatActivity {
    private ImageView anhSanPhamImg;
    private TextView tenSanPhamTextView, giaTienTextView, tenKhachHangTextView, diaChiTextView;
    private RatingBar danhGiaRatingBar;
    private EditText nhanXetEditText;
    private Button guiDanhGiaButton;
    private ImageButton quayLaiBtn;
    private CommentApiService apiService;
    private String productId;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_danh_gia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();
        tichHopAPI();
        loadDuLieu();

        quayLaiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        guiDanhGiaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guiDanhGia();
            }
        });
    }

    private void anhXa() {
        anhSanPhamImg = findViewById(R.id.anhSanPhamImg);
        tenSanPhamTextView = findViewById(R.id.tenSanPhamTextView);
        giaTienTextView = findViewById(R.id.giaTienTextView);
        tenKhachHangTextView = findViewById(R.id.tenKhachHangTextView);
        diaChiTextView = findViewById(R.id.diaChiTextView);
        danhGiaRatingBar = findViewById(R.id.danhGiaRatingBar);
        nhanXetEditText = findViewById(R.id.nhanXetEditText);
        guiDanhGiaButton = findViewById(R.id.guiDanhGiaButton);
        quayLaiBtn = findViewById(R.id.quayLaiBtn);
    }

    private void tichHopAPI() {
        apiService = ApiClient.getClient().create(CommentApiService.class);
    }

    private void loadDuLieu() {
        Intent intent = getIntent();
        productId = intent.getStringExtra("PRODUCT_ID");
        String productName = intent.getStringExtra("PRODUCT_NAME");
        String productPrice = intent.getStringExtra("PRODUCT_PRICE");
        String productImageUrl = intent.getStringExtra("PRODUCT_IMAGE_URL");
        String diaChiKhachHang = intent.getStringExtra("DIA_CHI");

        SharedPreferences preferences = getSharedPreferences("USER_INFO", MODE_PRIVATE);
        userId = preferences.getString("USER_ID", null);
        String tenNguoiDung = preferences.getString("USER_NAME", "Người dùng");

        tenSanPhamTextView.setText(productName);
        giaTienTextView.setText(productPrice);
        tenKhachHangTextView.setText(tenNguoiDung);
        diaChiTextView.setText(diaChiKhachHang);

        if (productImageUrl != null && !productImageUrl.isEmpty()) {
            Glide.with(this)
                    .load(productImageUrl)
                    .placeholder(R.drawable.ic_kids1)
                    .error(R.drawable.ic_launcher_background)
                    .into(anhSanPhamImg);
        }
    }

    private void guiDanhGia() {
        String comment = nhanXetEditText.getText().toString().trim();
        float rating = danhGiaRatingBar.getRating();

        // Kiểm tra dữ liệu
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao để đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }
        if (comment.isEmpty() || comment.length() < 6) {
            Toast.makeText(this, "Đánh giá không thể để trống hoặc ít hơn 6 ký tự", Toast.LENGTH_SHORT).show();
            return;
        }
        if (userId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin người dùng, vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo đối tượng comment
        Comment newComment = new Comment(productId, userId, comment, rating);

        // Gửi đánh giá
        luuDanhGia(newComment);
    }

    private void luuDanhGia(Comment comment) {
        Call<Comment> call = apiService.addComment(comment);
        call.enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DanhGia.this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DanhGia.this, "Gửi đánh giá thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(DanhGia.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}