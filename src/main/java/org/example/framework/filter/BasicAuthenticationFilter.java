package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.app.service.UserService;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.*;

import java.io.IOException;

public class BasicAuthenticationFilter extends HttpFilter {
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

        //пробовал в postman отправлять бейс64 запрос, там приходит "Basic 23jOJROKDFSLK$"DFS"
        //поэтому принял это как образец

        final var base64LogPass = req.getHeader("Authorization");
        if (base64LogPass == null) {
            super.doFilter(req, res, chain);
            return;
        }

        UserService provider = (UserService) this.provider;
        var token = provider.getTokenFromBase64LogPass(base64LogPass);

        try {
            final var authentication = this.provider.authenticate(new TokenAuthentication(token, null));
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
}
