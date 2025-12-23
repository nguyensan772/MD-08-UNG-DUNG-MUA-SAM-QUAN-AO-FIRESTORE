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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatSupportActivity extends AppCompatActivity {

    private static final String TAG = "ChatSupportActivity";
    
    // Server URL - Port 5001 matches the backend server
    private static final String BASE_URL = "http://10.0.2.2:5001";
    private static final String SOCKET_URL = "http://10.0.2.2:5001";
    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

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
    private String socketRoom;
    private String userId;
    private String authToken;

    // HTTP Client
    private OkHttpClient httpClient;

    // Chat mode: "bot" or "admin"
    private String currentMode = "bot";

    // Flag to prevent duplicate loading
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_support);

        initViews();
        setupRecyclerView();
        setupListeners();
        initHttpClient();
        
        // Get user ID and token from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        userId = prefs.getString("userId", null);
        authToken = prefs.getString("token", null);
        
        // Debug log
        Log.d(TAG, "User ID: " + userId);
        Log.d(TAG, "Auth Token exists: " + (authToken != null && !authToken.isEmpty()));
        
        // If no userId, generate a temporary one
        if (userId == null || userId.isEmpty()) {
            userId = UUID.randomUUID().toString();
            Log.d(TAG, "Generated temp userId: " + userId);
        }
        
        // Initialize socket connection first
        initSocket();
        
        // Check if user is logged in
        if (authToken == null || authToken.isEmpty()) {
            Log.w(TAG, "No auth token - showing offline mode");
            Toast.makeText(this, "Đang ở chế độ offline. Đăng nhập để kết nối với server.", Toast.LENGTH_LONG).show();
            // Show offline welcome message instead of exiting
            addOfflineWelcomeMessage();
        } else {
            // Start chat support session via API
            startChatSupport();
        }
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

    private void initHttpClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    private void switchMode(String mode) {
        if (mode.equals(currentMode) || isLoading) {
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

        // Disconnect from current room
        leaveRoom();
        chatAdapter.clearMessages();
        
        // Start new chat support session with new mode
        startChatSupport();
    }

    /**
     * Start chat support session via REST API
     * POST /api/chat-support/start
     */
    private void startChatSupport() {
        if (isLoading) return;
        isLoading = true;
        
        showConnectionStatus("Đang tải cuộc trò chuyện...");
        
        String url = BASE_URL + "/api/chat-support/start";
        Log.d(TAG, "Starting chat support - URL: " + url);
        Log.d(TAG, "Chat type: " + currentMode);

        try {
            JSONObject body = new JSONObject();
            body.put("chat_type", currentMode);

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();
            
            Log.d(TAG, "Sending request to: " + url);

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Start chat support failed: " + e.getMessage());
                    Log.e(TAG, "Failed URL: " + url);
                    e.printStackTrace();
                    runOnUiThread(() -> {
                        isLoading = false;
                        hideConnectionStatus();
                        Toast.makeText(ChatSupportActivity.this, 
                                "Không thể kết nối server: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        // Show offline mode
                        addOfflineWelcomeMessage();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    isLoading = false;
                    String responseBody = response.body() != null ? response.body().string() : "";
                    
                    Log.d(TAG, "Response code: " + response.code());
                    Log.d(TAG, "Response body: " + responseBody);
                    
                    runOnUiThread(() -> {
                        hideConnectionStatus();
                        
                        if (response.isSuccessful()) {
                            try {
                                JSONObject json = new JSONObject(responseBody);
                                if (json.optBoolean("success", false)) {
                                    Log.d(TAG, "Chat started successfully");
                                    handleChatStartResponse(json.getJSONObject("data"));
                                } else {
                                    String message = json.optString("message", "Có lỗi xảy ra");
                                    Log.e(TAG, "API returned error: " + message);
                                    Toast.makeText(ChatSupportActivity.this, message, Toast.LENGTH_SHORT).show();
                                    addOfflineWelcomeMessage();
                                }
                            } catch (JSONException e) {
                                Log.e(TAG, "Parse response error: " + e.getMessage());
                                addOfflineWelcomeMessage();
                            }
                        } else {
                            Log.e(TAG, "Start chat HTTP error: " + response.code() + " - " + responseBody);
                            String errorMsg = "Lỗi kết nối: " + response.code();
                            if (response.code() == 401) {
                                errorMsg = "Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.";
                            } else if (response.code() == 404) {
                                errorMsg = "API không tồn tại. Kiểm tra server.";
                            }
                            Toast.makeText(ChatSupportActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                            addOfflineWelcomeMessage();
                        }
                    });
                }
            });
        } catch (JSONException e) {
            isLoading = false;
            Log.e(TAG, "JSON error: " + e.getMessage());
            hideConnectionStatus();
            addOfflineWelcomeMessage();
        }
    }

    /**
     * Handle response from start chat support API
     */
    private void handleChatStartResponse(JSONObject data) {
        try {
            // Get room info
            JSONObject roomInfo = data.getJSONObject("room");
            roomId = roomInfo.getString("room_id");
            socketRoom = roomInfo.getString("socket_room");
            
            Log.d(TAG, "Chat started - Room ID: " + roomId + ", Socket Room: " + socketRoom);
            
            // Load chat history
            JSONArray messages = data.optJSONArray("messages");
            if (messages != null && messages.length() > 0) {
                loadChatHistory(messages);
            }
            
            // Join socket room
            joinRoom();
            
        } catch (JSONException e) {
            Log.e(TAG, "Handle chat start error: " + e.getMessage());
            addOfflineWelcomeMessage();
        }
    }

    /**
     * Load chat history from API response
     */
    private void loadChatHistory(JSONArray messages) {
        try {
            for (int i = 0; i < messages.length(); i++) {
                JSONObject msg = messages.getJSONObject(i);
                
                String messageText = msg.optString("message", "");
                String senderType = msg.optString("sender_type", "bot");
                boolean isFromUser = msg.optBoolean("is_from_user", false) || "user".equals(senderType);
                long timestamp = 0;
                
                // Parse timestamp
                String timestampStr = msg.optString("timestamp", "");
                if (!timestampStr.isEmpty()) {
                    try {
                        // ISO 8601 format parsing would require more complex handling
                        // For now, we'll use current time if parsing fails
                        timestamp = System.currentTimeMillis();
                    } catch (Exception e) {
                        timestamp = System.currentTimeMillis();
                    }
                }
                
                ChatMessage chatMessage = new ChatMessage(
                        msg.optString("id", ""),
                        messageText,
                        isFromUser ? userId : msg.optString("sender_id", ""),
                        senderType,
                        timestamp
                );
                chatMessage.setFromUser(isFromUser);
                
                chatAdapter.addMessage(chatMessage);
            }
            
            scrollToBottom();
            
        } catch (JSONException e) {
            Log.e(TAG, "Load chat history error: " + e.getMessage());
        }
    }

    private void initSocket() {
        try {
            IO.Options options = new IO.Options();
            options.forceNew = true;
            options.reconnection = true;
            options.reconnectionAttempts = 5;
            options.reconnectionDelay = 1000;
            options.query = "token=" + authToken;

            socket = IO.socket(SOCKET_URL, options);

            socket.on(Socket.EVENT_CONNECT, onConnect);
            socket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            
            // New socket events from backend
            socket.on("newMessage", onNewMessage);
            socket.on("newAdminMessage", onNewAdminMessage);
            socket.on("joinedRoom", onRoomJoined);
            socket.on("userJoinedRoom", onUserJoinedRoom);
            socket.on("userLeftRoom", onUserLeftRoom);
            socket.on("userTyping", onUserTyping);
            socket.on("roomClosed", onRoomClosed);
            socket.on("error", onSocketError);
            
            // Legacy events for backward compatibility
            socket.on("message", onLegacyMessage);
            socket.on("bot_response", onBotResponse);
            socket.on("admin_response", onAdminResponse);

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
        
        // Register user with socket
        registerUser();
        
        // If we have a room, join it
        if (roomId != null && !roomId.isEmpty()) {
            joinRoom();
        }
    });

    private final Emitter.Listener onDisconnect = args -> runOnUiThread(() -> {
        Log.d(TAG, "Socket disconnected");
        showConnectionStatus("Mất kết nối. Đang thử lại...");
    });

    private final Emitter.Listener onConnectError = args -> runOnUiThread(() -> {
        Log.e(TAG, "Socket connection error");
        showConnectionStatus("Không thể kết nối server");
    });

    /**
     * Handle new message from socket (new backend format)
     * Only handles bot messages - admin messages are handled by onNewAdminMessage
     */
    private final Emitter.Listener onNewMessage = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String msgRoomId = data.optString("room_id", "");
                
                // Only process messages for current room
                if (!msgRoomId.equals(roomId)) {
                    return;
                }
                
                JSONObject messageObj = data.optJSONObject("message");
                if (messageObj == null) {
                    // Try to parse as direct message
                    messageObj = data;
                }
                
                String messageText = messageObj.optString("message", messageObj.optString("text", ""));
                String senderId = messageObj.optString("sender_id", "");
                String senderType = messageObj.optString("sender_type", "bot");
                boolean isFromUser = messageObj.optBoolean("is_from_user", false);
                
                // Skip admin messages - they're handled by onNewAdminMessage
                if ("admin".equals(senderType)) {
                    Log.d(TAG, "Skipping admin message in onNewMessage (handled by onNewAdminMessage)");
                    return;
                }
                
                // Don't show own messages (they're already added locally)
                if (isFromUser || senderId.equals(userId)) {
                    return;
                }
                
                // Extract message ID if available
                String messageId = messageObj.optString("id", messageObj.optString("_id", ""));
                long timestamp = messageObj.optLong("timestamp", 0);
                if (timestamp == 0) {
                    timestamp = System.currentTimeMillis();
                }
                
                ChatMessage chatMessage;
                if (!messageId.isEmpty()) {
                    chatMessage = new ChatMessage(messageId, messageText, senderId, senderType, timestamp);
                } else {
                    chatMessage = new ChatMessage(messageText, false, senderType);
                    chatMessage.setTimestamp(timestamp);
                }
                
                chatAdapter.addMessage(chatMessage);
                scrollToBottom();
                
            } catch (Exception e) {
                Log.e(TAG, "New message parse error: " + e.getMessage());
            }
        }
    });

    /**
     * Handle admin message specifically
     */
    private final Emitter.Listener onNewAdminMessage = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String msgRoomId = data.optString("room_id", "");
                
                if (!msgRoomId.equals(roomId)) {
                    return;
                }
                
                JSONObject messageObj = data.optJSONObject("message");
                if (messageObj != null) {
                    String messageText = messageObj.optString("message", messageObj.optString("text", ""));
                    String senderId = messageObj.optString("sender_id", "");
                    
                    // Extract message ID if available
                    String messageId = messageObj.optString("id", messageObj.optString("_id", ""));
                    long timestamp = messageObj.optLong("timestamp", 0);
                    if (timestamp == 0) {
                        timestamp = System.currentTimeMillis();
                    }
                    
                    ChatMessage chatMessage;
                    if (!messageId.isEmpty()) {
                        chatMessage = new ChatMessage(messageId, messageText, senderId, "admin", timestamp);
                    } else {
                        chatMessage = new ChatMessage(messageText, false, "admin");
                        chatMessage.setTimestamp(timestamp);
                    }
                    
                    chatAdapter.addMessage(chatMessage);
                    scrollToBottom();
                }
            } catch (Exception e) {
                Log.e(TAG, "Admin message parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onRoomJoined = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                boolean success = data.optBoolean("success", false);
                String joinedRoomId = data.optString("roomId", "");
                
                if (success) {
                    Log.d(TAG, "Successfully joined room: " + joinedRoomId);
                }
            } catch (Exception e) {
                Log.e(TAG, "Room joined parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onUserJoinedRoom = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String joinedUserId = data.optString("userId", "");
                String userType = data.optString("userType", "User");
                
                // Show notification if admin joined
                if ("Admin".equals(userType) && "admin".equals(currentMode)) {
                    Toast.makeText(ChatSupportActivity.this, 
                            "Nhân viên hỗ trợ đã vào phòng chat", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "User joined parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onUserLeftRoom = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String reason = data.optString("reason", "left");
                
                if ("offline".equals(reason) && "admin".equals(currentMode)) {
                    // Admin went offline
                    ChatMessage systemMsg = new ChatMessage(
                            "Nhân viên hỗ trợ đã rời phòng chat. Tin nhắn của bạn sẽ được lưu lại.", 
                            false, "bot");
                    chatAdapter.addMessage(systemMsg);
                    scrollToBottom();
                }
            } catch (Exception e) {
                Log.e(TAG, "User left parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onUserTyping = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                boolean isTyping = data.optBoolean("isTyping", false);
                String typingUserId = data.optString("userId", "");
                
                // Don't show typing indicator for own user
                if (!typingUserId.equals(userId)) {
                    // You can implement typing indicator UI here
                    if (isTyping) {
                        showConnectionStatus("Đang nhập...");
                    } else {
                        hideConnectionStatus();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Typing parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onRoomClosed = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String closedRoomId = data.optString("room_id", "");
                
                if (closedRoomId.equals(roomId)) {
                    Toast.makeText(ChatSupportActivity.this, 
                            "Phòng chat đã đóng", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Log.e(TAG, "Room closed parse error: " + e.getMessage());
            }
        }
    });

    private final Emitter.Listener onSocketError = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String message = data.optString("message", "Lỗi không xác định");
                Toast.makeText(ChatSupportActivity.this, message, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "Socket error parse: " + e.getMessage());
            }
        }
    });

    // Legacy event handlers for backward compatibility
    private final Emitter.Listener onLegacyMessage = args -> runOnUiThread(() -> {
        if (args.length > 0) {
            try {
                JSONObject data = (JSONObject) args[0];
                String message = data.getString("message");
                String senderId = data.optString("senderId", "");
                String senderType = data.optString("senderType", "bot");

                if (!senderId.equals(userId)) {
                    ChatMessage chatMessage = new ChatMessage(message, false, senderType);
                    chatAdapter.addMessage(chatMessage);
                    scrollToBottom();
                }
            } catch (JSONException e) {
                Log.e(TAG, "Legacy message parse error: " + e.getMessage());
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

    /**
     * Register user with socket server
     */
    private void registerUser() {
        if (socket != null && socket.connected() && userId != null) {
            socket.emit("register", userId);
            socket.emit("joinUser", userId);
            Log.d(TAG, "User registered: " + userId);
        }
    }

    /**
     * Join chat room via socket
     */
    private void joinRoom() {
        if (socket != null && socket.connected() && roomId != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("roomId", roomId);
                data.put("userId", userId);
                data.put("userType", "User");
                socket.emit("joinChatSupportRoom", data);
                Log.d(TAG, "Joining room: " + roomId);
            } catch (JSONException e) {
                Log.e(TAG, "Join room error: " + e.getMessage());
            }
        }
    }

    /**
     * Leave chat room via socket
     */
    private void leaveRoom() {
        if (socket != null && socket.connected() && roomId != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("roomId", roomId);
                data.put("userId", userId);
                socket.emit("leaveChatSupportRoom", data);
                Log.d(TAG, "Leaving room: " + roomId);
            } catch (JSONException e) {
                Log.e(TAG, "Leave room error: " + e.getMessage());
            }
        }
    }

    /**
     * Send message via REST API
     */
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

        // Send typing indicator stop
        sendTypingIndicator(false);

        // Send message via API
        if (roomId != null && !roomId.isEmpty()) {
            sendMessageToServer(messageText);
        } else {
            // Offline mode - simulate response
            simulateResponse(messageText);
        }
    }

    /**
     * Send message to server via REST API
     * POST /api/chat-support/rooms/:roomId/messages
     */
    private void sendMessageToServer(String messageText) {
        try {
            JSONObject body = new JSONObject();
            body.put("message", messageText);

            RequestBody requestBody = RequestBody.create(body.toString(), JSON);
            
            String url = BASE_URL + "/api/chat-support/rooms/" + roomId + "/messages";
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .post(requestBody)
                    .build();

            httpClient.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Send message failed: " + e.getMessage());
                    runOnUiThread(() -> {
                        Toast.makeText(ChatSupportActivity.this, 
                                "Không thể gửi tin nhắn", Toast.LENGTH_SHORT).show();
                    });
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Send message error: " + response.code() + " - " + responseBody);
                        runOnUiThread(() -> {
                            Toast.makeText(ChatSupportActivity.this, 
                                    "Lỗi gửi tin nhắn", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        Log.d(TAG, "Message sent successfully");
                        // Bot response will come via socket
                    }
                }
            });
        } catch (JSONException e) {
            Log.e(TAG, "Send message JSON error: " + e.getMessage());
        }
    }

    /**
     * Send typing indicator to server
     */
    private void sendTypingIndicator(boolean isTyping) {
        if (socket != null && socket.connected() && roomId != null) {
            try {
                JSONObject data = new JSONObject();
                data.put("roomId", roomId);
                data.put("userId", userId);
                data.put("isTyping", isTyping);
                
                socket.emit(isTyping ? "typing" : "stopTyping", data);
            } catch (JSONException e) {
                Log.e(TAG, "Typing indicator error: " + e.getMessage());
            }
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

    private void addOfflineWelcomeMessage() {
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
        if (tvConnectionStatus != null) {
            tvConnectionStatus.setText(status);
            tvConnectionStatus.setVisibility(View.VISIBLE);
        }
    }

    private void hideConnectionStatus() {
        if (tvConnectionStatus != null) {
            tvConnectionStatus.setVisibility(View.GONE);
        }
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
    protected void onResume() {
        super.onResume();
        // Reconnect socket if needed
        if (socket != null && !socket.connected()) {
            socket.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Send stop typing when leaving
        sendTypingIndicator(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            leaveRoom();
            
            // Remove all socket listeners
            socket.off(Socket.EVENT_CONNECT, onConnect);
            socket.off(Socket.EVENT_DISCONNECT, onDisconnect);
            socket.off(Socket.EVENT_CONNECT_ERROR, onConnectError);
            socket.off("newMessage", onNewMessage);
            socket.off("newAdminMessage", onNewAdminMessage);
            socket.off("joinedRoom", onRoomJoined);
            socket.off("userJoinedRoom", onUserJoinedRoom);
            socket.off("userLeftRoom", onUserLeftRoom);
            socket.off("userTyping", onUserTyping);
            socket.off("roomClosed", onRoomClosed);
            socket.off("error", onSocketError);
            socket.off("message", onLegacyMessage);
            socket.off("bot_response", onBotResponse);
            socket.off("admin_response", onAdminResponse);
            
            socket.disconnect();
        }
        
        if (httpClient != null) {
            httpClient.dispatcher().executorService().shutdown();
            httpClient.connectionPool().evictAll();
        }
    }
}
