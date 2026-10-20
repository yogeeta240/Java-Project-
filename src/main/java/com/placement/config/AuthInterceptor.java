package com.placement.config;

import com.placement.model.Role;
import com.placement.model.User;
import com.placement.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    public AuthInterceptor() {
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(true);
        String path = request.getRequestURI();
        String lowerPath = path.toLowerCase();

        // Allow static assets, error pages, and login/register without session enforcement
        if (lowerPath.startsWith("/css/") || lowerPath.startsWith("/js/") || lowerPath.startsWith("/images/") ||
                lowerPath.equals("/error") || lowerPath.equals("/") ||
                lowerPath.equals("/index") || lowerPath.equals("/design-system") || lowerPath.equals("/design-system.html") ||
                lowerPath.startsWith("/auth/login") || lowerPath.startsWith("/auth/register") ||
                lowerPath.equals("/login") || lowerPath.equals("/register") ||
                lowerPath.equals("/logout") || lowerPath.startsWith("/auth/logout")) {
            return true;
        }

        // 1. Enforce authentication on protected portal routes
        if (session.getAttribute("userId") == null) {
            response.sendRedirect("/login?unauthorized=true");
            return false;
        }

        // 2. Enforce strict Role-Based Access Control (RBAC)
        String userRole = (String) session.getAttribute("userRole");
        if (userRole == null) {
            response.sendRedirect("/login?unauthorized=true");
            return false;
        }

        if (lowerPath.startsWith("/student") && !userRole.equalsIgnoreCase("STUDENT")) {
            if (userRole.equalsIgnoreCase("RECRUITER")) {
                response.sendRedirect("/recruiter/dashboard?accessDenied=true");
            } else {
                response.sendRedirect("/admin/dashboard?accessDenied=true");
            }
            return false;
        }

        if (lowerPath.startsWith("/recruiter") && !userRole.equalsIgnoreCase("RECRUITER")) {
            if (userRole.equalsIgnoreCase("STUDENT")) {
                response.sendRedirect("/student/dashboard?accessDenied=true");
            } else {
                response.sendRedirect("/admin/dashboard?accessDenied=true");
            }
            return false;
        }

        if (lowerPath.startsWith("/admin") && !userRole.equalsIgnoreCase("ADMIN")) {
            if (userRole.equalsIgnoreCase("STUDENT")) {
                response.sendRedirect("/student/dashboard?accessDenied=true");
            } else {
                response.sendRedirect("/recruiter/dashboard?accessDenied=true");
            }
            return false;
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        if (modelAndView != null && !modelAndView.getViewName().startsWith("redirect:")) {
            HttpSession session = request.getSession(false);
            if (session != null && session.getAttribute("userId") != null) {
                modelAndView.addObject("currentUserId", session.getAttribute("userId"));
                modelAndView.addObject("currentUserEmail", session.getAttribute("userEmail"));
                modelAndView.addObject("userName", session.getAttribute("userName"));
                modelAndView.addObject("currentRole", session.getAttribute("userRole"));
            } else {
                // Public or unauthenticated fallback
                modelAndView.addObject("userName", "Portal Guest");
                modelAndView.addObject("currentRole", "GUEST");
            }
        }
    }
}
