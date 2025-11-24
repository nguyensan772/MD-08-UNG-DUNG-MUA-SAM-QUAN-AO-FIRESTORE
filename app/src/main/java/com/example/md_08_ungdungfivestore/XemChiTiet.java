package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.md_08_ungdungfivestore.adapters.ImagePagerAdapter;
import com.example.md_08_ungdungfivestore.models.Product;

import java.util.ArrayList;
import java.util.List;

public class XemChiTiet extends AppCompatActivity {

    private ViewPager2 viewPagerImages;
    private LinearLayout layoutIndicator;
    private ImageButton btnBack;
    private TextView tvName, tvPrice, tvDesc;
    private Button btnSizeM, btnSizeL, btnSizeXL;
    private Button btnColorRed, btnColorBlue, btnColorBlack;
    private Button btnAddToCart;

    private Product product;
    private List<String> imageUrls = new ArrayList<>();
    private int selectedSize = -1;   // 0=M,1=L,2=XL
    private int selectedColor = -1;  // 0=Red,1=Blue,2=Black

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xem_chi_tiet);

        anhXa();

        // Lấy Product từ Intent
        product = (Product) getIntent().getSerializableExtra("product");
        if (product != null) {
            tvName.setText(product.getName());
            tvPrice.setText(String.format("%.0f VND", product.getPrice()));

            // Mô tả
            if (product.getDescription() != null && !product.getDescription().isEmpty()) {
                StringBuilder desc = new StringBuilder();
                for (Product.Description d : product.getDescription()) {
                    desc.append(d.getField()).append(": ").append(d.getValue()).append("\n");
                }
                tvDesc.setText(desc.toString());
            } else {
                tvDesc.setText("Không có mô tả chi tiết.");
            }

            // Ảnh sản phẩm
            imageUrls.clear();
            if (product.getImage() != null && !product.getImage().isEmpty()) {
                imageUrls.add(product.getImage());
            }
            if (product.getImages() != null && !product.getImages().isEmpty()) {
                imageUrls.addAll(product.getImages());
            }

        } else {
            Toast.makeText(this, "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupViewPager();
        setupSizeButtons();
        setupColorButtons();
        setupBackButton();

        btnAddToCart.setOnClickListener(v -> {
            if(selectedSize==-1 || selectedColor==-1){
                Toast.makeText(this,"Vui lòng chọn size và màu",Toast.LENGTH_SHORT).show();
                return;
            }

            String size = selectedSize==0?"M":selectedSize==1?"L":"XL";
            String color = selectedColor==0?"Đỏ":selectedColor==1?"Xanh":"Đen";

            Toast.makeText(this, "Chọn: "+size+" - "+color, Toast.LENGTH_SHORT).show();

            // TODO: Gửi size + color + product lên API giỏ hàng
        });
    }

    private void anhXa() {
        viewPagerImages = findViewById(R.id.viewPagerImages);
        layoutIndicator = findViewById(R.id.layoutIndicator);
        btnBack = findViewById(R.id.btnBack);
        tvName = findViewById(R.id.tvProductName);
        tvPrice = findViewById(R.id.tvProductPrice);
        tvDesc = findViewById(R.id.tvProductDesc);
        btnSizeM = findViewById(R.id.btnSizeM);
        btnSizeL = findViewById(R.id.btnSizeL);
        btnSizeXL = findViewById(R.id.btnSizeXL);
        btnColorRed = findViewById(R.id.btnColorRed);
        btnColorBlue = findViewById(R.id.btnColorBlue);
        btnColorBlack = findViewById(R.id.btnColorBlack);
        btnAddToCart = findViewById(R.id.btnAddToCart);
    }

    private void setupViewPager() {
        ImagePagerAdapter adapter = new ImagePagerAdapter(this, imageUrls);
        viewPagerImages.setAdapter(adapter);

        setupIndicatorDots(imageUrls.size());
        viewPagerImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicatorDots(position);
            }
        });
    }

    private void setupIndicatorDots(int count){
        layoutIndicator.removeAllViews();
        for(int i=0;i<count;i++){
            View dot = new View(this);
            int size = 16;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size,size);
            params.setMargins(8,0,8,0);
            dot.setLayoutParams(params);
            dot.setBackgroundResource(i==0?R.drawable.btn_size_selected :R.drawable.size_unselected);
            layoutIndicator.addView(dot);
        }
    }

    private void updateIndicatorDots(int selectedPosition){
        for(int i=0;i<layoutIndicator.getChildCount();i++){
            View dot = layoutIndicator.getChildAt(i);
            dot.setBackgroundResource(i==selectedPosition?R.drawable.btn_size_selected :R.drawable.size_unselected);
        }
    }

    private void setupSizeButtons(){
        Button[] sizeButtons = {btnSizeM,btnSizeL,btnSizeXL};
        for(int i=0;i<sizeButtons.length;i++){
            final int index=i;
            sizeButtons[i].setOnClickListener(v->{
                selectedSize=index;
                for(Button b:sizeButtons){
                    b.setBackgroundResource(R.drawable.btn_size_unselected);
                    b.setTextColor(0xFFFFFFFF);
                }
                sizeButtons[index].setBackgroundResource(R.drawable.btn_size_selected);
                sizeButtons[index].setTextColor(0xFFFFFFFF);
            });
        }
    }

    private void setupColorButtons(){
        Button[] colorButtons={btnColorRed,btnColorBlue,btnColorBlack};
        for(int i=0;i<colorButtons.length;i++){
            final int index=i;
            colorButtons[i].setOnClickListener(v->{
                selectedColor=index;
                for(Button b:colorButtons){
                    b.setBackgroundResource(R.drawable.btn_color_unselected);
                    b.setTextColor(0xFFFFFFFF);
                }
                colorButtons[index].setBackgroundResource(R.drawable.btn_color_selected);
                colorButtons[index].setTextColor(0xFFFFFFFF);
            });
        }
    }

    private void setupBackButton(){
        btnBack.setOnClickListener(v->finish());
    }
}
