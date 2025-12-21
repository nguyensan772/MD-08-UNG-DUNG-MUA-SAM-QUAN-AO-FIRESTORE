package com.example.md_08_ungdungfivestore.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.DangNhap;
import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.adapters.FavoriteAdapter;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.ApiClientYeuThich;
import com.example.md_08_ungdungfivestore.services.YeuThichManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class YeuThichFragment extends Fragment {

    private RecyclerView danhSachYeuThichRecyclerView;
    private TextView tvEmptyYeuThich;
    private FavoriteAdapter favoriteAdapter;
    private YeuThichManager yeuThichManager;
    private List<Product> favoriteProducts = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        yeuThichManager = new YeuThichManager(ApiClientYeuThich.getYeuThichService(getContext()));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_man_yeu_thich, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        danhSachYeuThichRecyclerView = view.findViewById(R.id.danhSachYeuThichRecyclerView);
        tvEmptyYeuThich = view.findViewById(R.id.tvEmptyYeuThich);
        danhSachYeuThichRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        //Kiểm tra đăng nhập
        if (sharedPreferences.getString("isLogin", "0").equals("0")) {
            showLogoutDialog();
        }else {
            favoriteAdapter = new FavoriteAdapter(getContext(), favoriteProducts, yeuThichManager, product -> {
                yeuThichManager.removeFromWishlist(product.getId(), new YeuThichManager.ToggleCallback() {
                    @Override
                    public void onSuccess(String message, boolean isAdded) {
                        if (!isAdded) {
                            favoriteProducts.remove(product);
                            favoriteAdapter.notifyDataSetChanged();
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                            updateEmptyView();
                        }
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(getContext(), "Lỗi xóa yêu thích: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            });

            danhSachYeuThichRecyclerView.setAdapter(favoriteAdapter);
        }


    }
    private void showLogoutDialog() {
        DialogDangNhap dialog = DialogDangNhap.newInstance(
                "Đăng nhập",
                "Bạn có chắc chắn muốn đăng nhập không?",
                new DialogDangNhap.OnDialogAction() {
                    @Override
                    public void onConfirm() {
                        startActivity(new Intent(getContext(), DangNhap.class));
                        getActivity().finish();
                    }

                    @Override
                    public void onCancel() {

                    }
                }
        );

        // Hiển thị Dialog
        dialog.show(getActivity().getSupportFragmentManager(), "custom_dialog");
    }

    @Override
    public void onResume() {
        super.onResume();
        loadFavoriteProducts();
    }

    private void updateEmptyView() {
        if (favoriteProducts.isEmpty()) {
            tvEmptyYeuThich.setVisibility(View.VISIBLE);
            danhSachYeuThichRecyclerView.setVisibility(View.GONE);
        } else {
            tvEmptyYeuThich.setVisibility(View.GONE);
            danhSachYeuThichRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void loadFavoriteProducts() {
        yeuThichManager.getMyWishlist(new YeuThichManager.CallbackMap() {
            @Override
            public void onSuccess(Map<String, Object> responseMap) {
                List<Product> products = YeuThichManager.parseProductList(responseMap);
                favoriteProducts.clear();
                favoriteProducts.addAll(products);
                favoriteAdapter.notifyDataSetChanged();
                updateEmptyView();
            }

            @Override
            public void onError(String error) {
                Log.e("YeuThichFragment", "Load Wishlist Error: " + error);
                updateEmptyView();
            }
        });
    }
}
