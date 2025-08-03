package org.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.dto.security.AuthenticationRequest;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter
        extends AbstractAuthenticationProcessingFilter {

    private final JwtTokenUtil jwtTokenUtil;
    public static final PathPatternRequestMatcher loginPath =
            PathPatternRequestMatcher.withDefaults()
                    .matcher(HttpMethod.POST, "/api/v1/users/login");

    private final ObjectMapper objectMapper = new ObjectMapper();


    public JwtAuthenticationFilter(JwtTokenUtil jwtTokenUtil,
                                   AuthenticationManager authenticationManager) {
        super(loginPath, authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {

        Authentication authentication = AuthenticationMapToken(request);

        if (authentication == null) {
            return null;
        }
        Authentication authenticate = getAuthenticationManager()
                .authenticate(authentication);

        if (authenticate == null) {
            throw new ServletException("Authentication failed");
        }
        return authenticate;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult)
            throws IOException, ServletException {

        CustomUserDetails principal = (CustomUserDetails) authResult.getPrincipal();

        String token = jwtTokenUtil.generateToken(principal);

        response.addHeader("Authorization", "Bearer " + token);

        response.getWriter().write("{\"token\": \"" + token + "\"}");

        response.setContentType("application/json");
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed)
            throws IOException, ServletException {

        response.getWriter().write(
                "{\"error\": \"Invalid username or password\"}");

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
    }

    Authentication AuthenticationMapToken(HttpServletRequest request)
            throws IOException {
        AuthenticationRequest authenticationRequest =
                objectMapper.readValue(request.getInputStream(),
                        AuthenticationRequest.class);

        return new UsernamePasswordAuthenticationToken(
                authenticationRequest.email(),
                authenticationRequest.password());
    }
}
