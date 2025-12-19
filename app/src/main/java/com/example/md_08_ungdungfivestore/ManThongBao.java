
package com.example.md_08_ungdungfivestore;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.md_08_ungdungfivestore.adapters.NotificationAdapter;
import com.example.md_08_ungdungfivestore.models.Notification;
import com.example.md_08_ungdungfivestore.services.ThongBaoApiClient;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManThongBao extends AppCompatActivity {
    private RecyclerView rvNotif;
    private NotificationAdapter adapter;
    private List<Notification> notificationList = new ArrayList<>();
    private ImageView btnBack;

    // ⭐ THÊM BIẾN SOCKET
    private Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_man_thong_bao);

        rvNotif = findViewById(R.id.rvNotification);
        btnBack = findViewById(R.id.btnBackNotif);

        rvNotif.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationAdapter(notificationList, this);
        rvNotif.setAdapter(adapter);

        btnBack.setOnClickListener(v -> finish());

        // 1. Load dữ liệu cũ từ API
        loadNotifications();

        // 2. Kết nối Socket để nhận dữ liệu mới real-time
        setupSocket();
    }

    private void setupSocket() {
        try {
            // URL server của ông
            mSocket = IO.socket("http://10.0.2.2:5001");
            mSocket.connect();

            // Lắng nghe sự kiện "new_notification" từ server gửi về
            mSocket.on("new_notification", onNewNotification);

            Log.d("SOCKET_DEBUG", "Đang kết nối Socket...");
        } catch (URISyntaxException e) {
            Log.e("SOCKET_DEBUG", "Lỗi URL Socket: " + e.getMessage());
        }
    }

    // ⭐ XỬ LÝ KHI CÓ THÔNG BÁO MỚI TỪ SOCKET
    private Emitter.Listener onNewNotification = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(() -> {
                try {
                    JSONObject data = (JSONObject) args[0];
                    Log.d("SOCKET_DEBUG", "Dữ liệu nhận được: " + data.toString());

                    // Chuyển JSON thành Object Notification
                    Notification newNotif = new Gson().fromJson(data.toString(), Notification.class);

                    // Thêm vào đầu danh sách
                    notificationList.add(0, newNotif);
                    adapter.notifyItemInserted(0);
                    rvNotif.scrollToPosition(0);

                } catch (Exception e) {
                    Log.e("SOCKET_DEBUG", "Lỗi xử lý socket: " + e.getMessage());
                }
            });
        }
    };

    private void loadNotifications() {
        ThongBaoApiClient.getClient(this).getDanhSachThongBao().enqueue(new Callback<List<Notification>>() {
            @Override
            public void onResponse(Call<List<Notification>> call, Response<List<Notification>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notificationList.clear();
                    notificationList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Log.e("API_ERROR", "Response Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Notification>> call, Throwable t) {
                Log.e("API_ERROR", "Failure: " + t.getMessage());
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // ⭐ NGẮT KẾT NỐI KHI THOÁT
        if (mSocket != null) {
            mSocket.disconnect();
            mSocket.off("new_notification", onNewNotification);
        }
    }
}