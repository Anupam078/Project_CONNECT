package com.projectconnect.network;

import com.google.gson.Gson;
import com.projectconnect.model.ChatMessage;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClient {
    private org.java_websocket.client.WebSocketClient webSocketClient;
    private ChatMessageListener listener;
    private Gson gson;

    public interface ChatMessageListener {
        void onMessageReceived(ChatMessage message);
        void onConnectionError(String error);
    }

    public WebSocketClient(ChatMessageListener listener) {
        this.listener = listener;
        this.gson = new Gson();
    }

    public void connect(String serverUrl) {
        try {
            URI uri = URI.create(serverUrl);
            webSocketClient = new org.java_websocket.client.WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshake) {
                    // Connection opened
                }

                @Override
                public void onMessage(String message) {
                    try {
                        ChatMessage chatMessage = gson.fromJson(message, ChatMessage.class);
                        if (listener != null) {
                            listener.onMessageReceived(chatMessage);
                        }
                    } catch (Exception e) {
                        if (listener != null) {
                            listener.onConnectionError("Failed to parse message: " + e.getMessage());
                        }
                    }
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    // Connection closed
                }

                @Override
                public void onError(Exception ex) {
                    if (listener != null) {
                        listener.onConnectionError(ex.getMessage());
                    }
                }
            };

            webSocketClient.connect();
        } catch (Exception e) {
            if (listener != null) {
                listener.onConnectionError("Failed to connect: " + e.getMessage());
            }
        }
    }

    public void sendMessage(ChatMessage message) {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            String jsonMessage = gson.toJson(message);
            webSocketClient.send(jsonMessage);
        }
    }

    public void disconnect() {
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}