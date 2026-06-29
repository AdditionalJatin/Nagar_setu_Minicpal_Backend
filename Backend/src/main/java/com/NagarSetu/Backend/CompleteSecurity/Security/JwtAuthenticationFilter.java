package com.NagarSetu.Backend.CompleteSecurity.Security;

import com.NagarSetu.Backend.Entities.User;
import com.NagarSetu.Backend.Entities.UserStatus;
import com.NagarSetu.Backend.User.UserRepository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthUtil authUtil;
    private final UserRepository userRepository;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            log.info("incoming request : {}", request.getRequestURI());
            final String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = authorizationHeader.substring(7);
            UUID userId = authUtil.getUserId(token);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
                if(user.getStatus()== UserStatus.BLOCKED){
                    log.warn("Blocked user attempted access: {}", userId);
                    // Throw a specific exception to be handled by your GlobalExceptionHandler
                    throw new RuntimeException("ACCOUNT_BLOCKED");
                }


                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.info("User authenticated successfully: {}", userId);

            }
            filterChain.doFilter(request, response);
        }  catch (io.jsonwebtoken.ExpiredJwtException ex) {
            log.warn("JWT expired");
            handlerExceptionResolver.resolveException(request, response, null, ex);

        }
        catch (io.jsonwebtoken.JwtException ex) {
            log.warn("Invalid JWT");
            handlerExceptionResolver.resolveException(request, response, null, ex);

        }
        catch (Exception ex) {
            log.error("Authentication error", ex);
            handlerExceptionResolver.resolveException(request, response, null, ex);

        }
    }

}
