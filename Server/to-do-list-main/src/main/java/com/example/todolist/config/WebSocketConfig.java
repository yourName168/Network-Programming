package com.example.todolist.config;

import com.example.todolist.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    @Autowired
    private UserRepository userRepository;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Cấu hình các điểm đến của message broker
        config.enableSimpleBroker("/topic"); // Đường dẫn cho server gửi tin nhắn đi
        config.setApplicationDestinationPrefixes("/app"); // Đường dẫn cho client gửi tin nhắn đến
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws").setAllowedOrigins("*");
    }

    // Sự kiện khi kết nối WebSocket
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Lấy username từ header khi client kết nối
        String username = headerAccessor.getFirstNativeHeader("username");

        if (username != null) {
            // Lưu username vào session attribute
            headerAccessor.getSessionAttributes().put("username", username);
            System.out.println("Người dùng kết nối WebSocket: " + username);
        }
    }
    // Sự kiện khi ngắt kết nối WebSocket
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        // Lấy username từ session attribute
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        userRepository.updateStatus("offline", username);
        if (username != null) {
            System.out.println("Người dùng đã ngắt kết nối: " + username);
        } else {
            // In ra nếu username không được lưu trong session
            System.out.println("Session ID đã ngắt kết nối: " + headerAccessor.getSessionId());
        }
    }
}