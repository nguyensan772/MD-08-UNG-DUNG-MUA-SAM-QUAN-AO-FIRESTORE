package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView; // Cần import TextView
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat; // Cần import ContextCompat
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.XemChiTiet;
import com.example.md_08_ungdungfivestore.SearchActivity;
import com.example.md_08_ungdungfivestore.adapters.ProductAdapter;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.ProductApiService;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.YeuThichManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrangChuFragment extends Fragment {

    private static final String TAG = "TrangChuFragment";
    private EditText timKiemEditText;
    private RecyclerView rcvProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ProductApiService apiService;
    private YeuThichManager yeuThichManager;

    // ⭐ KHAI BÁO SỬ DỤNG TextView CHO CÁC NÚT LỌC ⭐
    private TextView tvAll, tvNam, tvNu, tvTreEm;
    private TextView currentSelectedCategoryButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yeuThichManager = new YeuThichManager(ApiClientYeuThich.getYeuThichService(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trangchu, container, false);

        timKiemEditText = view.findViewById(R.id.timKiemEditText);
        rcvProducts = view.findViewById(R.id.rcvProducts);
        rcvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        // ⭐ ÁNH XẠ CÁC NÚT LỌC VỚI ID MỚI (tvAll, tvNam, tvNu, tvTreEm) ⭐
        tvAll = view.findViewById(R.id.tvAll);
        tvNam = view.findViewById(R.id.tvNam);
        tvNu = view.findViewById(R.id.tvNu);
        tvTreEm = view.findViewById(R.id.tvTreEm);
        // ⭐ KẾT THÚC ÁNH XẠ ⭐

        // LOGIC ĐỂ CHUYỂN SANG SEARCHACTIVITY KHI CLICK VÀO EDITTEXT
        timKiemEditText.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SearchActivity.class);
            startActivity(intent);
        });

        timKiemEditText.setFocusable(false);

        adapter = new ProductAdapter(requireContext(), productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                if (product != null && product.getId() != null && !product.getId().isEmpty()) {
                    Log.d(TAG, "Item Clicked. Product ID: " + product.getId());

                    Intent intent = new Intent(requireContext(), XemChiTiet.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                } else {
                    Log.e(TAG, "Error: Product object or ID is null/empty on click.");
                    Toast.makeText(requireContext(), "Lỗi: Sản phẩm không có ID.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAddClick(Product product) {
                if (product != null && product.getId() != null && !product.getId().isEmpty()) {
                    Log.d(TAG, "Add Clicked. Product ID: " + product.getId());
                    addToWishlist(product);
                } else {
                    Log.e(TAG, "Error: Product object or ID is null/empty on add click.");
                    Toast.makeText(requireContext(), "Lỗi: Không thể thêm sản phẩm không có ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        rcvProducts.setAdapter(adapter);
        setupApiService();

        // ⭐ THIẾT LẬP LÓGIC CLICK VÀ TẢI DỮ LIỆU BAN ĐẦU ⭐
        setupCategoryListeners();

        // Tải sản phẩm ban đầu: ALL
        fetchProductsAndHandleUI(null, tvAll);

        return view;
    }

    // Phương thức setupCategoryListeners giữ nguyên nhưng gọi các TextView mới
    private void setupCategoryListeners() {
        tvAll.setOnClickListener(v -> fetchProductsAndHandleUI(null, v));
        tvNam.setOnClickListener(v -> fetchProductsAndHandleUI("Nam", v));
        tvNu.setOnClickListener(v -> fetchProductsAndHandleUI("Nữ", v));
        tvTreEm.setOnClickListener(v -> fetchProductsAndHandleUI("Trẻ em", v));
    }

    // PHƯƠNG THỨC GỌI LỌC VÀ CẬP NHẬT UI CÙNG LÚC
    private void fetchProductsAndHandleUI(@Nullable String category, View selectedView) {
        fetchProductsByCategory(category);
        updateCategoryButtonState(selectedView);
    }

    private void fetchProductsByCategory(@Nullable String category) {
        Call<List<Product>> call;

        // 1. Nếu category là null hoặc "All", gọi API lấy tất cả
        if (category == null || category.equalsIgnoreCase("All")) {
            call = apiService.getAllProducts();
            Log.d(TAG, "Gọi API: Tải TẤT CẢ sản phẩm.");
        }
        // 2. Ngược lại, gọi API lọc theo danh mục
        else {
            call = apiService.getProductsByCategory(category);
            Log.d(TAG, "Gọi API: Lọc theo danh mục: " + category);
        }

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Đã tải " + productList.size() + " sản phẩm.");
                } else {
                    Log.e(TAG, "Lỗi tải sản phẩm: Response not successful or body is null. Code: " + response.code());
                    Toast.makeText(requireContext(), "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                    productList.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded()) return;
                Log.e(TAG, "Lỗi mạng khi tải sản phẩm: " + t.getMessage(), t);
                Toast.makeText(requireContext(), "Lỗi mạng khi lọc: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ⭐ PHƯƠNG THỨC CẬP NHẬT TRẠNG THÁI NÚT LỌC (SỬ DỤNG MÀU TỪ RESOURCES) ⭐
    private void updateCategoryButtonState(View selectedView) {
        if (currentSelectedCategoryButton != null) {
            // Đặt nút cũ về trạng thái mặc định (màu chữ tab_unselected)
            currentSelectedCategoryButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.tab_unselected));
        }

        // Đặt nút mới về trạng thái đang chọn (màu chữ tab_selected)
        ((TextView) selectedView).setTextColor(ContextCompat.getColor(requireContext(), R.color.tab_selected));

        currentSelectedCategoryButton = (TextView) selectedView;

        // Ghi chú: Vì bạn sử dụng R.drawable.bg_tab_text cho tất cả các nút, nên chỉ cần thay đổi màu chữ.
        // Nếu bạn muốn thay đổi background, bạn cần tạo hai Drawable khác nhau và sử dụng setBackgroundResource() ở đây.
    }

    private void addToWishlist(Product product) {
        // ... (Logic thêm vào yêu thích giữ nguyên)
        yeuThichManager.addToWishlist(product.getId(), new YeuThichManager.ToggleCallback() {
            @Override
            public void onSuccess(String message, boolean isAdded) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                if (!isAdded()) return;
                Toast.makeText(requireContext(), "Lỗi thêm yêu thích: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setupApiService() {
        // ... (Logic setup Retrofit giữ nguyên)
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ProductApiService.class);
    }
}