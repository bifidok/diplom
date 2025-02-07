package com.example.demo.utils;

import com.example.demo.entity.Session;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;
import java.time.LocalDateTime;

public class SessionUtils {
    private static final String COOKIE_SESSION_ID_NAME = "sessionId";

    public static Cookie findSessionCookie(HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        Cookie cookieSession = null;
        if (cookies != null) {
            for (var cookie : cookies) {
                if (COOKIE_SESSION_ID_NAME.equals(cookie.getName())) {
                    cookieSession = cookie;
                    break;
                }
            }
        }
        return cookieSession;
    }

    public static void clearCookies(HttpServletRequest req, HttpServletResponse resp) {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                resp.addCookie(cookie);
            }
        }
    }

    public static void addCookie(Session session, HttpServletResponse resp) {
        String sessionId = session.getId().toString();
        Cookie cookie = new Cookie(COOKIE_SESSION_ID_NAME, sessionId);
        int cookieSeconds = (int) Duration.between(LocalDateTime.now(), session.getExpiresAt()).toSeconds();
        cookie.setMaxAge(cookieSeconds);
        resp.addCookie(cookie);
    }
}
