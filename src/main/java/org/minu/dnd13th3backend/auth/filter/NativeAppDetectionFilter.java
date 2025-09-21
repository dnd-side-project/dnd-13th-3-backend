package org.minu.dnd13th3backend.auth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class NativeAppDetectionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (request.getRequestURI().startsWith("/oauth2/authorization/")) {
            String isNativeApp = request.getParameter("is_native_app");
            if ("true".equals(isNativeApp)) {
                HttpSession session = request.getSession(true);
                session.setAttribute("is_native_app", "true");
            }
        }

        filterChain.doFilter(request, response);
    }
}

