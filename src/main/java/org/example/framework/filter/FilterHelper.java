package org.example.framework.filter;

import jakarta.servlet.http.HttpServletRequest;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AnonymousAuthentication;
import org.example.framework.security.Authentication;

public class FilterHelper {
    protected static boolean authenticationIsRequired(HttpServletRequest req) {
        final var existingAuth = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        return AnonymousAuthentication.class.isAssignableFrom(existingAuth.getClass());
    }
}
