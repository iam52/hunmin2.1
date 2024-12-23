package com.hunmin.domain.handler;

import com.hunmin.domain.dto.chat.ChatMessageDTO;
import com.hunmin.domain.dto.member.CustomUserDetails;
import com.hunmin.domain.entity.Member;
import com.hunmin.domain.jwt.JWTUtil;
import com.hunmin.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Log4j2
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final MemberRepository memberRepository;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        //websocket 연결시
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            if (jwtUtil.isExpired(jwtToken)){
                String role = jwtUtil.getRole(jwtToken);
                ChatMessageDTO chatMessageDTO=(ChatMessageDTO)message.getPayload();
                Member foundMember = memberRepository.findById(chatMessageDTO.getMemberId()).orElseThrow(MemberException.NOT_FOUND::get);
                CustomUserDetails customUserDetails = new CustomUserDetails(foundMember);

                Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null,
                        customUserDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        return message;
    }
}
