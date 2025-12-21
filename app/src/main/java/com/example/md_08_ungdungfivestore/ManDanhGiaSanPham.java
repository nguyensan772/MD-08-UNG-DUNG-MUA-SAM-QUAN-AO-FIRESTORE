package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
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

public class ManDanhGiaSanPham extends AppCompatActivity {

    private ImageView imgBack, imgProduct;
    private TextView tvProductName;
    private RatingBar ratingBar;
    private EditText edtContent;
    private Button btnSubmit;

    private String productId, productName, productImage;
    private static final String BASE_URL = "http://10.0.2.2:5001/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_danh_gia_san_pham);
        initView();

        if (getIntent() != null) {
            productId = getIntent().getStringExtra("PRODUCT_ID");
            productName = getIntent().getStringExtra("PRODUCT_NAME");
            productImage = getIntent().getStringExtra("PRODUCT_IMAGE");
        }

        setupData();
        imgBack.setOnClickListener(v -> finish());
        btnSubmit.setOnClickListener(v -> submitReview());
    }

    private void initView() {
        imgBack = findViewById(R.id.imgBack);
        imgProduct = findViewById(R.id.imgProductReview);
        tvProductName = findViewById(R.id.tvProductNameReview);
        ratingBar = findViewById(R.id.ratingBar);
        edtContent = findViewById(R.id.edtCommentContent);
        btnSubmit = findViewById(R.id.btnSubmitReview);
    }

    private void setupData() {
        tvProductName.setText(productName != null ? productName : "Sản phẩm");
        Glide.with(this).load(productImage).placeholder(R.drawable.ic_kids1).into(imgProduct);
    }

    private void submitReview() {
        if (productId == null || productId.trim().isEmpty()) {
            Toast.makeText(this, "Lỗi: Không tìm thấy ID sản phẩm!", Toast.LENGTH_SHORT).show();
            return;
        }

        float rating = ratingBar.getRating();
        String content = edtContent.getText().toString().trim();
        if (rating == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao!", Toast.LENGTH_SHORT).show();
            return;
        }

        String token = AuthManager.getToken(this);
        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang gửi...");

        // Gửi lên productId, Gson sẽ tự chuyển thành "product_id" nhờ @SerializedName
        Comment comment = new Comment(productId, rating, content);

        createServiceWithAuth(token).addComment(comment).enqueue(new Callback<Comment>() {
            @Override
            public void onResponse(Call<Comment> call, Response<Comment> response) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("GỬI ĐÁNH GIÁ");

                if (response.isSuccessful()) {
                    Toast.makeText(ManDanhGiaSanPham.this, "Đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else if (response.code() == 409) {
                    Toast.makeText(ManDanhGiaSanPham.this, "Bạn đã đánh giá sản phẩm này rồi!", Toast.LENGTH_LONG).show();
                } else {
                    Log.e("API_ERROR", "Code: " + response.code() + " Path: " + call.request().url());
                    Toast.makeText(ManDanhGiaSanPham.this, "Lỗi server: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Comment> call, Throwable t) {
                btnSubmit.setEnabled(true);
                btnSubmit.setText("GỬI ĐÁNH GIÁ");
                Toast.makeText(ManDanhGiaSanPham.this, "Lỗi kết nối mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private CommentApiService createServiceWithAuth(String token) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request newRequest = chain.request().newBuilder()
                            .addHeader("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(newRequest);
                }).build();

        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CommentApiService.class);
    }
}