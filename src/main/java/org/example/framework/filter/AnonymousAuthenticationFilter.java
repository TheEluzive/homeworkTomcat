package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AnonymousAuthentication;
import org.example.framework.security.AuthenticationException;
import org.example.framework.security.AuthenticationProvider;

import java.io.IOException;

import static org.example.framework.filter.FilterHelper.authenticationIsRequired;

public class AnonymousAuthenticationFilter extends HttpFilter {
    private AuthenticationProvider provider;
    private final String ANONYMOUS_TOKEN = "GQsxIs7qizYfEXgZvru0VbPQZZjpDWfYit3R1zxr5zZfBO0ZP+S4Nd28i/XWzTS1N6sNyodDzz2ia75jhRYUXQ==99999";

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

        //TODO: generate new Anonymous


        try {
            final var authentication = provider.authenticate(new AnonymousAuthentication(ANONYMOUS_TOKEN));
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }


}
