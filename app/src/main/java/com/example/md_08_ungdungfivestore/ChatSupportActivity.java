package com.example.md_08_ungdungfivestore;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.md_08_ungdungfivestore.adapters.ChatMessageAdapter;
import com.example.md_08_ungdungfivestore.models.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class ChatSupportActivity extends AppCompatActivity {

    private static final String TAG = "ChatSupportActivity";
    
    // TODO: Replace with your actual server URL
    private static final String SOCKET_URL = "http://10.0.2.2:3000";

    // Views
    private RecyclerView rvChatMessages;
    private EditText etMessage;
    private ImageButton btnSend;
    private ImageView btnBack;
    private TextView btnModeBot;
    private TextView btnModeAdmin;
    private TextView tvConnectionStatus;

    // Adapter
    private ChatMessageAdapter chatAdapter;

    // Socket
    private Socket socket;
    private String roomId;
    private String userId;

    // Chat mode: "bot" or "admin"
    private String currentMode = "bot";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);

        initViews();
        setupRecyclerView();
        setupListeners();
        
        // Get user ID from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", UUID.randomUUID().toString());
        
        // Generate unique room ID for this session
        generateRoomId();
        
        // Initialize socket connection
        initSocket();
    }

    private void initViews() {
        rvChatMessages = findViewById(R.id.rvChatMessages);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
        btnBack = findViewById(R.id.btnBack);
        btnModeBot = findViewById(R.id.btnModeBot);
        btnModeAdmin = findViewById(R.id.btnModeAdmin);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatMessageAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnSend.setOnClickListener(v -> sendMessage());

        btnModeBot.setOnClickListener(v -> switchMode("bot"));

        btnModeAdmin.setOnClickListener(v -> switchMode("admin"));
    }

    private void switchMode(String mode) {
        if (mode.equals(currentMode)) {
            return;
        }

        currentMode = mode;

        // Update UI
        if ("bot".equals(mode)) {
            btnModeBot.setBackgroundResource(R.drawable.bg_mode_selected);
            btnModeBot.setTextColor(getResources().getColor(R.color.white));
            btnModeAdmin.setBackgroundResource(R.drawable.bg_mode_unselected);
            btnModeAdmin.setTextColor(getResources().getColor(R.color.tab_unselected));
        } else {
            btnModeAdmin.setBackgroundResource(R.drawable.bg_mode_selected);
            btnModeAdmin.setTextColor(getResources().getColor(R.color.white));
            btnModeBot.setBackgroundResource(R.drawable.bg_mode_unselected);
            btnModeBot.setTextColor(getResources().getColor(R.color.tab_unselected));
        }

        // Disconnect from current room and create new room
        leaveRoom();
        chatAdapter.clearMessages();
        generateRoomId();
        joinRoom();

        // Show welcome message for new mode
        addWelcomeMessage();
    }

    private void generateRoomId() {
        roomId = "support_" + currentMode + "_" + userId + "_" + System.currentTimeMillis();
    }

    private void initSocket() {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = 5;
            options.reconnectionDelay = 1000;

            socket = IO.socket(SOCKET_URL, options);

            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.on("message", onNewMessage);
            socket.on("bot_response", onBotResponse);
            socket.on("admin_response", onAdminResponse);
            socket.on("room_joined", onRoomJoined);

            socket.connect();

            showConnectionStatus("Đang kết nối...");

        } catch (URISyntaxException e) {
            Log.e(TAG, "Socket URI error: " + e.getMessage());
            Toast.makeText(this, "Lỗi kết nối server", Toast.LENGTH_SHORT).show();
        }
    }

    private final Emitter.Listener onConnect = args -> runOnUiThread(() -> {
        Log.d(TAG, "Socket connected");
        hideConnectionStatus();
        joinRoom();
        addWelcomeMessage();
    });

    private final Emitter.Listener onDisconnect = args -> runOnUiThread(() -> {
        Log.d(TAG, "Socket disconnected");
        showConnectionStatus("Mất kết nối. Đang thử lại...");
    });

    private final Emitter.Listener onConnectError = args -> runOnUiThread(() -> {
        Log.e(TAG, "Socket connection error");
        showConnectionStatus("Không thể kết nối server");
        
        // Add offline message for demo purposes
        addOfflineWelcomeMessage();
    });

    private final Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String message = data.getString("message");
                String senderId = data.optString("senderId", "");
                String senderType = data.optString("senderType", "bot");

                // Don't show own messages (they're already added)
                if (!senderId.equals(userId)) {
                    ChatMessage chatMessage = new ChatMessage(message, false, senderType);
                    chatAdapter.addMessage(chatMessage);
                    scrollToBottom();
                }
            } catch (JSONException e) {
                Log.e(TAG, "JSON parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onBotResponse = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String message = data.getString("message");

                ChatMessage chatMessage = new ChatMessage(message, false, "bot");
                chatAdapter.addMessage(chatMessage);
                scrollToBottom();
            } catch (JSONException e) {
                Log.e(TAG, "Bot response parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onAdminResponse = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String message = data.getString("message");

                ChatMessage chatMessage = new ChatMessage(message, false, "admin");
                chatAdapter.addMessage(chatMessage);
                scrollToBottom();
            } catch (JSONException e) {
                Log.e(TAG, "Admin response parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onRoomJoined = args -> runOnUiThread(() -> {
        Log.d(TAG, "Joined room: " + roomId);
    });

    private void joinRoom() {
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("roomId", roomId);
                data.put("userId", userId);
                data.put("mode", currentMode);
                socket.emit("join_room", data);
            } catch (JSONException e) {
                Log.e(TAG, "Join room error: " + e.getMessage());
            }
        }
    }

    private void leaveRoom() {
        if (socket != null && socket.connected() && roomId != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("roomId", roomId);
                data.put("userId", userId);
                socket.emit("leave_room", data);
            } catch (JSONException e) {
                Log.e(TAG, "Leave room error: " + e.getMessage());
            }
        }
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        // Add message to UI immediately
        ChatMessage userMessage = new ChatMessage(messageText, true, "user");
        chatAdapter.addMessage(userMessage);
        scrollToBottom();

        // Clear input
        etMessage.setText("");
        hideKeyboard();

        // Send to server
        if (socket != null && socket.connected()) {
            try {
                JSONObject data = new JSONObject();
                data.put("roomId", roomId);
                data.put("userId", userId);
                data.put("message", messageText);
                data.put("mode", currentMode);
                data.put("timestamp", System.currentTimeMillis());
                socket.emit("send_message", data);
            } catch (JSONException e) {
                Log.e(TAG, "Send message error: " + e.getMessage());
            }
        } else {
            // Offline mode - simulate response
            simulateResponse(messageText);
        }
    }

    private void simulateResponse(String userMessage) {
        // Simulate a response after a short delay when offline
        rvChatMessages.postDelayed(() -> {
            String response;
            if ("bot".equals(currentMode)) {
                response = getBotSimulatedResponse(userMessage);
            } else {
                response = "Admin đang bận. Vui lòng đợi trong giây lát...";
            }
            ChatMessage botMessage = new ChatMessage(response, false, currentMode);
            chatAdapter.addMessage(botMessage);
            scrollToBottom();
        }, 1000);
    }

    private String getBotSimulatedResponse(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        if (lowerMessage.contains("xin chào") || lowerMessage.contains("hello") || lowerMessage.contains("hi")) {
            return "Xin chào! Tôi có thể giúp gì cho bạn?";
        } else if (lowerMessage.contains("đơn hàng") || lowerMessage.contains("order")) {
            return "Để kiểm tra đơn hàng, bạn vui lòng vào mục 'Đơn hàng' trong phần Cài đặt.";
        } else if (lowerMessage.contains("thanh toán") || lowerMessage.contains("payment")) {
            return "Chúng tôi hỗ trợ thanh toán qua VNPay và thanh toán khi nhận hàng (COD).";
        } else if (lowerMessage.contains("giao hàng") || lowerMessage.contains("ship")) {
            return "Thời gian giao hàng thường từ 2-5 ngày làm việc tùy khu vực.";
        } else if (lowerMessage.contains("đổi trả") || lowerMessage.contains("return")) {
            return "Bạn có thể đổi trả sản phẩm trong vòng 7 ngày kể từ ngày nhận hàng nếu sản phẩm còn nguyên tem mác.";
        } else if (lowerMessage.contains("cảm ơn") || lowerMessage.contains("thank")) {
            return "Rất vui được hỗ trợ bạn! Nếu cần thêm hỗ trợ, đừng ngại liên hệ nhé.";
        } else {
            return "Cảm ơn bạn đã liên hệ. Tôi sẽ chuyển yêu cầu của bạn cho nhân viên hỗ trợ. Bạn có thể chuyển sang chế độ Admin để được hỗ trợ trực tiếp.";
        }
    }

    private void addWelcomeMessage() {
        String welcomeMessage;
        if ("bot".equals(currentMode)) {
            welcomeMessage = "Xin chào! Tôi là Bot hỗ trợ. Tôi có thể giúp bạn trả lời các câu hỏi thường gặp về đơn hàng, thanh toán, giao hàng. Bạn cần hỗ trợ gì?";
        } else {
            welcomeMessage = "Xin chào! Bạn đã kết nối với nhân viên hỗ trợ. Vui lòng mô tả vấn đề của bạn, chúng tôi sẽ phản hồi sớm nhất có thể.";
        }
        
        ChatMessage welcomeMsg = new ChatMessage(welcomeMessage, false, currentMode);
        chatAdapter.addMessage(welcomeMsg);
        scrollToBottom();
    }

    private void addOfflineWelcomeMessage() {
        hideConnectionStatus();
        String welcomeMessage;
        if ("bot".equals(currentMode)) {
            welcomeMessage = "Xin chào! Tôi là Bot hỗ trợ (chế độ offline). Tôi có thể giúp bạn trả lời các câu hỏi thường gặp.";
        } else {
            welcomeMessage = "Hiện không thể kết nối server. Vui lòng thử lại sau hoặc liên hệ hotline: 1900-xxxx";
        }
        
        ChatMessage welcomeMsg = new ChatMessage(welcomeMessage, false, currentMode);
        chatAdapter.addMessage(welcomeMsg);
        scrollToBottom();
    }

    private void showConnectionStatus(String status) {
        tvConnectionStatus.setText(status);
        tvConnectionStatus.setVisibility(View.VISIBLE);
    }

    private void hideConnectionStatus() {
        tvConnectionStatus.setVisibility(View.GONE);
    }

    private void scrollToBottom() {
        if (chatAdapter.getItemCount() > 0) {
            rvChatMessages.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            leaveRoom();
            socket.off(Socket.EVENT_CONNECT, onConnect);
            socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.off("message", onNewMessage);
            socket.off("bot_response", onBotResponse);
            socket.off("admin_response", onAdminResponse);
            socket.off("room_joined", onRoomJoined);
            socket.disconnect();
        }
    }
}

