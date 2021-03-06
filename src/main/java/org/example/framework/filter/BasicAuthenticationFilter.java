package org.example.framework.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.AuthenticationException;
import org.example.framework.security.AuthenticationProvider;
import org.example.framework.security.BasicAuthentication;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.example.framework.filter.FilterHelper.authenticationIsRequired;

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

        final var encodedUsernamePass = req.getHeader("Authorization");
        if (encodedUsernamePass == null) {
            super.doFilter(req, res, chain);
            return;
        }


        final var base64Credentials = encodedUsernamePass.substring("Basic".length()).trim();
        final var logPassDecoded = Base64.getDecoder().decode(base64Credentials);
        final var logPass = new String(logPassDecoded, StandardCharsets.UTF_8);
        // logPass = username:password
        final String[] values = logPass.split(":", 2);
        final var principal = values[0];
        final var credential = values[1];


        try {
            final var authentication = this.provider.authenticate(new BasicAuthentication(principal, credential));
            req.setAttribute(RequestAttributes.AUTH_ATTR, authentication);
        } catch (AuthenticationException e) {
            res.sendError(401);
            return;
        }

        super.doFilter(req, res, chain);
    }


}
