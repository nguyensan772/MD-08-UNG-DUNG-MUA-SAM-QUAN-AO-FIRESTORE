package com.example.md_08_ungdungfivestore;

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

import com.example.md_08_ungdungfivestore.models.Comment;
import com.example.md_08_ungdungfivestore.services.CommentApiService;
import com.example.md_08_ungdungfivestore.utils.AuthManager;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DanhGia extends AppCompatActivity {
    private ImageView anhSanPhamImg;
    private TextView tenSanPhamTextView, giaTienTextView, tenKhachHangTextView, diaChiTextView;
    private RatingBar danhGiaRatingBar;
    private EditText nhanXetEditText;
    private Button guiDanhGiaButton;
    private ImageButton quayLaiBtn;

    private String productId; // Giả sử bạn nhận được ID qua intent

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

        // Lấy productId từ Intent (Giống ManChiTietDonHang gửi qua)
        productId = getIntent().getStringExtra("PRODUCT_ID");

        quayLaiBtn.setOnClickListener(view -> finish());

        guiDanhGiaButton.setOnClickListener(view -> {
            thucHienGuiDanhGia();
        });
    }

    private void thucHienGuiDanhGia() {
        float rating = danhGiaRatingBar.getRating();
        String content = nhanXetEditText.getText().toString().trim();
        String token = AuthManager.getToken(this);

        if (token == null) {
            Toast.makeText(this, "Vui lòng đăng nhập!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn sao!", Toast.LENGTH_SHORT).show();
            return;
        }

        Comment comment = new Comment(productId, rating, content);

        // Tạo Retrofit tạm thời để gửi (Hoặc dùng một ApiClient dùng chung)
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                }).build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5001/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        CommentApiService api = retrofit.create(CommentApiService.class);
        api.addComment(comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(DanhGia.this, "Gửi thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(DanhGia.this, "Lỗi gửi đánh giá", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                Toast.makeText(DanhGia.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
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
}