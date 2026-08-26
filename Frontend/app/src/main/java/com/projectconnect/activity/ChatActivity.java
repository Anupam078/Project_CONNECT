package com.projectconnect.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.projectconnect.R;
import com.projectconnect.adapter.ChatAdapter;
import com.projectconnect.model.ChatMessage;
import com.projectconnect.network.WebSocketClient;
import com.projectconnect.util.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements WebSocketClient.ChatMessageListener {

    private RecyclerView rvChat;
    private EditText etMessage;
    private Button btnSend;
    private ChatAdapter chatAdapter;
    private WebSocketClient webSocketClient;
    private SharedPreferencesHelper sharedPrefsHelper;
    private Long orderId;
    private String shopName;
    private List<ChatMessage> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getIntentData();
        initViews();
        initServices();
        setupRecyclerView();
        setupClickListeners();
        connectWebSocket();
    }

    private void getIntentData() {
        orderId = getIntent().getLongExtra("order_id", -1);
        shopName = getIntent().getStringExtra("shop_name");
        if (shopName != null) {
            getSupportActionBar().setTitle("Chat - " + shopName);
        }
    }

    private void initViews() {
        rvChat = findViewById(R.id.rvChat);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);
    }

    private void initServices() {
        sharedPrefsHelper = new SharedPreferencesHelper(this);
        webSocketClient = new WebSocketClient(this);
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(messages, sharedPrefsHelper.getUserId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvChat.setLayoutManager(layoutManager);
        rvChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnSend.setOnClickListener(v -> sendMessage());
    }

    private void connectWebSocket() {
        String serverUrl = "ws://10.0.2.2:8080/ws/chat?userId=" + sharedPrefsHelper.getUserId();
        webSocketClient.connect(serverUrl);
    }

    private void sendMessage() {
        String messageText = etMessage.getText().toString().trim();
        if (messageText.isEmpty()) {
            return;
        }

        // For simplicity, we'll assume the receiver ID is different from sender
        // In a real app, you'd get this from the order or shop details
        Long receiverId = orderId; // This should be the actual receiver ID
        
        ChatMessage message = new ChatMessage();
        message.setSenderId(sharedPrefsHelper.getUserId());
        message.setReceiverId(receiverId);
        message.setOrderId(orderId);
        message.setMessage(messageText);

        webSocketClient.sendMessage(message);
        etMessage.setText("");
    }

    @Override
    public void onMessageReceived(ChatMessage message) {
        runOnUiThread(() -> {
            messages.add(message);
            chatAdapter.notifyItemInserted(messages.size() - 1);
            rvChat.scrollToPosition(messages.size() - 1);
        });
    }

    @Override
    public void onConnectionError(String error) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Connection error: " + error, Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.disconnect();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}