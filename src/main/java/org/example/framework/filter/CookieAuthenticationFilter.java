package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;

import java.io.IOException;
import java.util.ArrayList;

public class CookieAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;

    @Override
    public void init(FilterConfig config) throws ServletException {
        super.init(config);
        provider = ((AuthenticationProvider) getServletContext().getAttribute(ContextAttributes.AUTH_PROVIDER_ATTR));
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (!authenticationIsRequired(req)) {
            super.doFilter(req, res, chain);
            return;
        }



        final var cookie = parseCookie(req.getCookies(), "token");

        if (cookie == null ) {
            super.doFilter(req, res, chain);
            return;
        }

        try {
            final var authentication = provider.authenticate(new TokenAuthentication(cookie.getValue(), null));
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);


        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }

    private boolean authenticationIsRequired(HttpServletRequest req) {
        final var existingAuth = (Authentication) req.getAttribute(RequestAttributes.AUTH_ATTR);

        if (existingAuth == null || !existingAuth.isAuthenticated()) {
            return true;
        }

        return AnonymousAuthentication.class.isAssignableFrom(existingAuth.getClass());
    }

    private Cookie parseCookie(Cookie[] cookies, String str){

        Cookie cookie = null;

        if(cookies !=null) {
            for(Cookie c: cookies) {
                if(str.equals(c.getName())) {
                    cookie = c;
                    break;
                }
            }
        }
        return cookie;
    }
}