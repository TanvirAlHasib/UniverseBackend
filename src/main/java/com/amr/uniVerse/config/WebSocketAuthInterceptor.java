package com.amr.uniVerse.config;

import com.amr.uniVerse.auth.entity.User;
import com.amr.uniVerse.auth.service.JwtService;
import com.amr.uniVerse.studyroom.entity.MembershipStatus;
import com.amr.uniVerse.studyroom.entity.RoomMembership;
import com.amr.uniVerse.studyroom.repository.RoomMembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final RoomMembershipRepository membershipRepository; // 👈 ADDED

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor == null) return message;

        // 1. Authenticate on CONNECT
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String authHeader = accessor.getFirstNativeHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    String userEmail = jwtService.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(userEmail);

                    if (jwtService.isTokenValid(token, userDetails)) {
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        accessor.setUser(auth);
                    }
                } catch (Exception e) {
                    System.out.println("❌ WS AUTH FAILED (CONNECT): " + e.getMessage());
                    throw new MessagingException("Invalid JWT Token");
                }
            } else {
                throw new MessagingException("Missing Authorization Header");
            }
        }

        // 2. Authorize on SUBSCRIBE (THE NEW SECURITY CHECK)
        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            String destination = accessor.getDestination();
            if (destination != null && destination.startsWith("/topic/room/")) {
                Principal principal = accessor.getUser();
                if (principal == null) {
                    throw new MessagingException("Unauthenticated subscription attempt");
                }

                User currentUser = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
                String roomIdStr = destination.substring("/topic/room/".length());

                try {
                    Long roomId = Long.parseLong(roomIdStr);
                    RoomMembership membership = membershipRepository
                            .findByRoomIdAndUserId(roomId, currentUser.getId())
                            .orElseThrow(() -> new MessagingException("You are not a member of this room"));

                    if (membership.getStatus() != MembershipStatus.APPROVED) {
                        System.out.println("🚫 SUBSCRIPTION BLOCKED: User " + currentUser.getEmail() + " is not approved for room " + roomId);
                        throw new MessagingException("Your membership is not approved");
                    }
                } catch (NumberFormatException e) {
                    throw new MessagingException("Invalid room ID");
                }
            }
        }

        return message;
    }
}