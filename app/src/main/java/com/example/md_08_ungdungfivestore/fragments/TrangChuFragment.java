package com.example.md_08_ungdungfivestore.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.R;
import com.example.md_08_ungdungfivestore.XemChiTiet;
import com.example.md_08_ungdungfivestore.adapters.ProductAdapter;
import com.example.md_08_ungdungfivestore.models.Product;
import com.example.md_08_ungdungfivestore.services.ProductApiService;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TrangChuFragment extends Fragment {

    private EditText timKiemEditText;
    private RecyclerView rcvProducts;
    private ProductAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private ProductApiService apiService;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_trangchu, container, false);

        timKiemEditText = view.findViewById(R.id.timKiemEditText);
        rcvProducts = view.findViewById(R.id.rcvProducts);
        rcvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new ProductAdapter(requireContext(), productList, new ProductAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Product product) {
                if (product != null) {
                    Intent intent = new Intent(requireContext(), XemChiTiet.class);
                    intent.putExtra("product", product);
                    startActivity(intent);
                } else {
                    Toast.makeText(requireContext(), "Sản phẩm không tồn tại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onAddClick(Product product) {
                if (product != null) {
                    Toast.makeText(requireContext(), "Đã thêm: " + product.getName(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onDeleteClick(Product product) {
                // Not used in home page
            }
        });

        // HIDE delete button on home page
        adapter.setShowDeleteButton(false);

        rcvProducts.setAdapter(adapter);
        setupApiService();
        fetchProducts();

        return view;
    }

    private void setupApiService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5001/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ProductApiService.class);
    }

    private void fetchProducts() {
        Call<List<Product>> call = apiService.getAllProducts();
        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (!isAdded())
                    return;
                if (response.isSuccessful() && response.body() != null) {
                    productList.clear();
                    productList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(requireContext(), "Không tải được sản phẩm", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                if (!isAdded())
                    return;
                Toast.makeText(requireContext(), "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
