package com.springboot.aldiabackjava.config.Socket;

import com.springboot.aldiabackjava.JWT.JwtTokenService;
import com.springboot.aldiabackjava.repositories.userRepositories.IUserRepository;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptor authChannelInterceptor;
    private final JwtTokenService jwtTokenService;
    private final IUserRepository userRepository;

    public WebSocketConfig(AuthChannelInterceptor authChannelInterceptor,
                           JwtTokenService jwtTokenService,
                           IUserRepository userRepository) {
        this.authChannelInterceptor = authChannelInterceptor;
        this.jwtTokenService = jwtTokenService;
        this.userRepository = userRepository;
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Prefijos para destinos de mensajes
        config.enableSimpleBroker("/topic", "/queue"); // Destinos públicos
        config.setApplicationDestinationPrefixes("/app"); // Prefijo para métodos @MessageMapping
        config.setUserDestinationPrefix("/user"); // Para mensajes privados
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // Endpoint para conexiones WebSocket
                .setAllowedOriginPatterns("*") // Ajusta según tus necesidades CORS
                .withSockJS() // Soporte para fallback con SockJS
                .setInterceptors(new HttpSessionHandshakeInterceptor()); // Interceptor para sesiones HTTP
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptor); // Interceptor de autenticación JWT
    }

    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        registration.taskExecutor().corePoolSize(4); // Configuración de hilos para mensajes salientes
    }
}
