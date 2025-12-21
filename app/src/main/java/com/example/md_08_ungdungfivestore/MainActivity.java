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

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {

    // Khai báo View
    private FrameLayout layout;
    private BottomNavigationView menu;
    private Toolbar toolbar;
    private TextView tieuDe, tvNotificationCount;
    private ImageView iconUser, iconBell;
    private FrameLayout layoutBell;

    // Biến Socket và đếm thông báo
    private Socket mSocket;
    private int countNotif = 0;

    // Khai báo các Fragment
    private final GioHangFragment gioHangFragment = new GioHangFragment();
    private final TrangChuFragment trangChuFragment = new TrangChuFragment();
    private final YeuThichFragment yeuThichFragment = new YeuThichFragment();
    private final TrangCaNhanFragment trangCaNhanFragment = new TrangCaNhanFragment();

    // Launcher xử lý kết quả
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

        // 1. Ánh xạ View
        anhXa();

        // 2. Thiết lập Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // 3. Kết nối Socket.io
        setupSocket();

        // --- ĐÃ TẮT CHẾ ĐỘ DEMO ĐỂ TEST THẬT ---
        // countNotif = 5;
        updateBadgeDisplay();
        // ----------------------------------------

        // 4. Load Fragment mặc định
        if (savedInstanceState == null) {
            taiFragment(trangChuFragment);
            tieuDe.setText("Trang Chủ");
        }

        // --- CÁC SỰ KIỆN CLICK ---
        iconUser.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ManThongTinCaNhan.class);
            startActivity(intent);
        });

        View.OnClickListener notificationClickListener = v -> {
            countNotif = 0; // Reset số
            updateBadgeDisplay();
            Intent intent = new Intent(MainActivity.this, ManThongBao.class);
            startActivity(intent);
        };
        if (iconBell != null) iconBell.setOnClickListener(notificationClickListener);
        if (layoutBell != null) layoutBell.setOnClickListener(notificationClickListener);

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

    // --- ⭐ HÀM KHỞI TẠO SOCKET (ĐÃ SỬA) ⭐ ---
    private void setupSocket() {
        try {
            // ⚠️ LƯU Ý: Nếu chạy máy ảo dùng 10.0.2.2. Nếu chạy điện thoại thật phải dùng IP LAN (ví dụ 192.168.1.x)
            mSocket = IO.socket("http://10.0.2.2:5001");

            // 1. Lắng nghe sự kiện kết nối thành công
            mSocket.on(Socket.EVENT_CONNECT, args -> {
                Log.d("SOCKET_CHECK", "✅ Đã kết nối thành công tới Server!");

                // ⭐ QUAN TRỌNG: Gửi UserID thay vì Token
                // Bạn cần đảm bảo AuthManager có hàm lấy UserId.
                // Nếu chưa có hàm getUserId(), hãy tạm thời copy cứng ID từ MongoDB vào đây để test:
                // String userId = "65a...";

                String userId = AuthManager.getUserId(this); // <-- Sửa dòng này

                if (userId != null) {
                    mSocket.emit("register", userId);
                    Log.d("SOCKET_CHECK", "Đã gửi lệnh register với UserID: " + userId);
                } else {
                    Log.e("SOCKET_CHECK", "⚠️ Không tìm thấy UserID! Socket sẽ không nhận được thông báo.");
                }
            });

            // Lắng nghe lỗi kết nối
            mSocket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                Log.e("SOCKET_CHECK", "❌ Lỗi kết nối Socket: " + args[0]);
            });

            // 2. Lắng nghe sự kiện "new_notification" từ Server
            mSocket.on("new_notification", args -> {
                Log.d("SOCKET_CHECK", "🔔 Đã nhận được thông báo từ Server!");
                runOnUiThread(() -> {
                    // Tăng số lượng và cập nhật UI
                    countNotif++;
                    updateBadgeDisplay();

                    try {
                        JSONObject data = (JSONObject) args[0];
                        String title = data.has("title") ? data.getString("title") : "Thông báo mới";
                        Toast.makeText(this, "🔔 " + title, Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });

            mSocket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void updateBadgeDisplay() {
        if (tvNotificationCount == null) return;
        if (countNotif > 0) {
            tvNotificationCount.setVisibility(View.VISIBLE);
            tvNotificationCount.setText(countNotif > 99 ? "99+" : String.valueOf(countNotif));
        } else {
            tvNotificationCount.setVisibility(View.GONE);
        }
    }

    public void anhXa() {
        layout = findViewById(R.id.trangChuFrameLayout);
        menu = findViewById(R.id.menuTrangChuBottom);
        toolbar = findViewById(R.id.toolBarTrangChu);
        tieuDe = findViewById(R.id.tieuDeTextView);
        iconUser = findViewById(R.id.iconUser);
        layoutBell = findViewById(R.id.layoutBell);
        iconBell = findViewById(R.id.iconBell);
        tvNotificationCount = findViewById(R.id.tvNotificationCount);
    }

    public void taiFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.trangChuFrameLayout, fragment)
                .commit();
    }

    public ActivityResultLauncher<Intent> getCheckoutResultLauncher() {
        return checkoutResultLauncher;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("new_notification");
        }
    }
}