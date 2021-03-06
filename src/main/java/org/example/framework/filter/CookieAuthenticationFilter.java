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
import org.example.framework.security.AuthenticationException;
import org.example.framework.security.AuthenticationProvider;
import org.example.framework.security.TokenAuthentication;

import java.io.IOException;
import java.util.Optional;

import static org.example.framework.filter.FilterHelper.authenticationIsRequired;

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

        final var cookie = parseCookie(req.getCookies());
        if (cookie.isEmpty()) {
            super.doFilter(req, res, chain);
            return;
        }


        final var token = cookie.get().getValue();


        try {
            final var authentication = provider.authenticate(new TokenAuthentication(cookie.get().getValue(), null));
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }


        super.doFilter(req, res, chain);
    }


    private Optional<Cookie> parseCookie(Cookie[] cookies) {

        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("token".equals(c.getName())) {
                    return Optional.of(c);
                }
            }
        }
        return Optional.empty();
    }
}
