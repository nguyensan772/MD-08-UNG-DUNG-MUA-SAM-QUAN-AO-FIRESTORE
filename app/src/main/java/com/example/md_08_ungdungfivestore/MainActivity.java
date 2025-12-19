package com.example.md_08_ungdungfivestore;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.md_08_ungdungfivestore.fragments.GioHangFragment;
import com.example.md_08_ungdungfivestore.fragments.TrangCaNhanFragment;
import com.example.md_08_ungdungfivestore.fragments.TrangChuFragment;
import com.example.md_08_ungdungfivestore.fragments.YeuThichFragment;
import com.example.md_08_ungdungfivestore.utils.AuthManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {
    FrameLayout layout;
    BottomNavigationView menu;
    Toolbar toolbar;
    TextView tieuDe, tvNotificationCount;

    ImageView iconUser, iconBell;

    // Biến Socket
    private Socket mSocket;

    // ⭐ BIẾN ĐẾM THÔNG BÁO CHƯA ĐỌC ⭐
    private int countNotif = 0;

    private final GioHangFragment gioHangFragment = new GioHangFragment();
    private final TrangChuFragment trangChuFragment = new TrangChuFragment();
    private final YeuThichFragment yeuThichFragment = new YeuThichFragment();
    private final TrangCaNhanFragment trangCaNhanFragment = new TrangCaNhanFragment();

    private final ActivityResultLauncher<Intent> checkoutResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.trangChuFrameLayout);
                    if (currentFragment instanceof GioHangFragment) {
                        ((GioHangFragment) currentFragment).fetchCartItems();
                        Toast.makeText(this, "Giỏ hàng đã được làm mới.", Toast.LENGTH_SHORT).show();
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        anhXa();
        setupSocket();

        setSupportActionBar(toolbar);
        if (savedInstanceState == null) {
            taiFragment(trangChuFragment);
        }
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // XỬ LÝ CLICK ICON USER
        iconUser.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, ManThongTinCaNhan.class));
        });

        // ⭐ XỬ LÝ CLICK ICON CHUÔNG (RESET SỐ) ⭐
        iconBell.setOnClickListener(v -> {
            countNotif = 0; // Reset biến đếm
            tvNotificationCount.setText("0");
            tvNotificationCount.setVisibility(View.GONE); // Ẩn số đi

            Intent intent = new Intent(MainActivity.this, ManThongBao.class);
            startActivity(intent);
        });

        menu.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.navTrangChu) {
                taiFragment(trangChuFragment);
                tieuDe.setText("Trang Chủ");
            } else if (id == R.id.navYeuThich) {
                taiFragment(yeuThichFragment);
                tieuDe.setText("Yêu thích");
            } else if (id == R.id.navGioHang) {
                taiFragment(gioHangFragment);
                tieuDe.setText("Giỏ hàng");
            } else if (id == R.id.navNguoiDung) {
                taiFragment(trangCaNhanFragment);
                tieuDe.setText("Người dùng");
            }
            return true;
        });
    }

    private void setupSocket() {
        try {
            // Đảm bảo URL này khớp với Node.js của bạn
            mSocket = IO.socket("http://10.0.2.2:5001");
            mSocket.connect();

            String token = AuthManager.getToken(this);
            if (token != null) {
                mSocket.emit("register", token);
            }

            mSocket.on("new_notification", args -> {
                runOnUiThread(() -> {
                    // ⭐ LOGIC NẢY SỐ ⭐
                    countNotif++;
                    tvNotificationCount.setText(String.valueOf(countNotif));
                    tvNotificationCount.setVisibility(View.VISIBLE);

                    try {
                        JSONObject data = (JSONObject) args[0];
                        Toast.makeText(this, "🔔 " + data.getString("title"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void anhXa() {
        layout = findViewById(R.id.trangChuFrameLayout);
        menu = findViewById(R.id.menuTrangChuBottom);
        toolbar = findViewById(R.id.toolBarTrangChu);
        tieuDe = findViewById(R.id.tieuDeTextView);
        iconUser = findViewById(R.id.iconUser);
        iconBell = findViewById(R.id.iconBell);
        tvNotificationCount = findViewById(R.id.tvNotificationCount);
    }

    public void taiFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.trangChuFrameLayout, fragment).commit();
    }

    public ActivityResultLauncher<Intent> getCheckoutResultLauncher() {
        return checkoutResultLauncher;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off();
        }
    }
}