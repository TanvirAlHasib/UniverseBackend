package com.amr.uniVerse.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final WebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Frontend will connect to ws://localhost:8080/ws
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*"); // Later, restrict to your frontend URL
        // .withSockJS(); // Uncomment if using SockJS on the frontend
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // Messages sent TO clients go through /topic/...
        registry.enableSimpleBroker("/topic");

        // Messages sent FROM clients are prefixed with /app/...
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        // Authenticate every incoming WebSocket message using our interceptor
        registration.interceptors(webSocketAuthInterceptor);
    }
}