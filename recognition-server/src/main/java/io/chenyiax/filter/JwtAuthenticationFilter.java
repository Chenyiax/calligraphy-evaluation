package io.chenyiax.filter;

import io.chenyiax.exception.JwtException;
import io.chenyiax.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * This component is a JWT authentication filter that extends OncePerRequestFilter.
 * It is used to validate JWT tokens in each incoming request and set the authentication information
 * in the Spring Security context if the token is valid.
 */
@Component
@RequiredArgsConstructor
// Inheriting from OncePerRequestFilter ensures that the filter is executed only once per request,
// which is used to quickly write JWT verification rules.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Utility class for JWT operations, such as parsing tokens.
     * It is automatically injected by Spring.
     */
    private final JwtUtils jwtUtils;

    /**
     * This method is the core logic of the filter, which is executed for each request.
     * It extracts the JWT from the request header, validates it, and sets the authentication information
     * in the Spring Security context if the token is valid.
     *
     * @param request     The HTTP request object.
     * @param response    The HTTP response object.
     * @param filterChain The filter chain used to pass the request and response to the next filter.
     * @throws JwtException If jwt parse error - related error occurs.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // First, extract the JWT from the request header.
        String authorization = request.getHeader("Authorization");
        // Check if the header contains a JWT and the format is correct.
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String token = authorization.substring(7);
            // Start parsing the token into a UserDetails object.
            // If the result is null, it means the parsing failed and the JWT is invalid.
            UserDetails user = jwtUtils.parseToken(token);
            System.out.println(user);
            if (user != null) {
                // Use UsernamePasswordAuthenticationToken as the entity and fill in relevant user information.
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Set the configured Authentication object to the SecurityContext, indicating that the authentication is completed.
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new JwtException("jwt parse error");
            }
        }
        // Finally, pass the request and response to the next filter in the chain.
        filterChain.doFilter(request, response);
    }
}
