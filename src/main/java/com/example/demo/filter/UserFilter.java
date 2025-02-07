package com.example.demo.filter;

import com.example.demo.entity.Session;
import com.example.demo.service.SessionService;
import com.example.demo.utils.SessionUtils;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

// фильтр для захода в личный кабинет
@WebFilter("/account")
public class UserFilter implements Filter {
    private final SessionService sessionService;

    @Autowired
    public UserFilter(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        Cookie cookieSession = SessionUtils.findSessionCookie((HttpServletRequest) servletRequest);
        if (cookieSession != null) {
            String sessionId = cookieSession.getValue();
            Optional<Session> sessionOptional = sessionService.findById(Long.valueOf(sessionId));
            System.out.println("Cookie is available");
            if (sessionOptional.isPresent() && !sessionOptional.get().getExpiresAt().isBefore(LocalDateTime.now())) {
                servletRequest.setAttribute("session", sessionOptional.get());
                filterChain.doFilter(servletRequest, servletResponse);
                System.out.println("Session is available");
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);}
}
