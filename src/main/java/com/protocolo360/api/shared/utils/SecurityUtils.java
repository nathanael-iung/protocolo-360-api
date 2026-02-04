package com.protocolo360.api.shared.utils;

import com.protocolo360.api.modules.auth.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }

    public Long getCurrentUserId() {
        User user = getCurrentUser();
        return (user != null) ? user.getId() : null;
    }
}