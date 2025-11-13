package co.edu.uniquindio.stayNow.config;

import co.edu.uniquindio.stayNow.handlers.ChatSocketHandler;
import co.edu.uniquindio.stayNow.security.JWTUtils;
import co.edu.uniquindio.stayNow.services.implementation.AuthServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.socket.config.annotation.*;

import java.util.List;
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer, WebSocketConfigurer {

    private final JWTUtils jwtUtils;
    private final AuthServiceImp authService;
    private final ChatSocketHandler chatSocketHandler;
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/chat-websocket")
                .setAllowedOriginPatterns("*")
                .setAllowedOrigins("http://localhost:4200");
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatSocketHandler, "/chat-websocket")
                .setAllowedOrigins("http://localhost:4200") // tu frontend
                .withSockJS(); // si quieres compatibilidad vieja, puedes quitarlo para socket puro
    }
    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String token = accessor.getFirstNativeHeader("Authorization");
                    if (token != null && token.startsWith("Bearer ")) {
                        token = token.substring(7);
                        try {
                            String userId = jwtUtils.parseJwt(token).getBody().getSubject();
                           // String userId = jwtUtils.parseJwt(token).getPayload().getSubject();

                            accessor.setUser(new UsernamePasswordAuthenticationToken(userId, null, List.of()));
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Token inválido");
                        }
                    }
                }

                return message;
            }
        });
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic/chat");
        registry.setApplicationDestinationPrefixes("/app");
    }
}
