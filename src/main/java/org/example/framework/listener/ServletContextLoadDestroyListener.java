package org.example.framework.listener;

import com.google.gson.Gson;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.ServletSecurity;
import org.example.app.handler.CardHandler;
import org.example.app.handler.UserHandler;
import org.example.app.jpa.JpaTransactionTemplate;
import org.example.app.repository.CardRepository;
import org.example.app.repository.UserRepository;
import org.example.app.service.CardService;
import org.example.app.service.UserService;
import org.example.framework.attribute.ContextAttributes;
import org.example.framework.servlet.Handler;
import org.example.jdbc.JdbcTemplate;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;

import javax.naming.InitialContext;
import javax.persistence.Persistence;
import javax.sql.DataSource;
import java.util.Map;
import java.util.regex.Pattern;

import static org.example.framework.http.Methods.*;

@ServletSecurity
public class ServletContextLoadDestroyListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContextListener.super.contextInitialized(sce);
        try {
            final var context = sce.getServletContext();

            final var initialContext = new InitialContext();
            final var dataSource = ((DataSource) initialContext.lookup("java:/comp/env/jdbc/db"));
            final var jdbcTemplate = new JdbcTemplate(dataSource);

            final var entityManagerFactory = Persistence.createEntityManagerFactory("default");
            final var jpaTransactionTemplate = new JpaTransactionTemplate(entityManagerFactory);

            final var gson = new Gson();

            final var userRepository = new UserRepository(jdbcTemplate);
            final var passwordEncoder = new Argon2PasswordEncoder();
            final var keyGenerator = new Base64StringKeyGenerator(64);
            final var userService = new UserService(userRepository, jpaTransactionTemplate, passwordEncoder, keyGenerator);
            context.setAttribute(ContextAttributes.AUTH_PROVIDER_ATTR, userService);
            context.setAttribute(ContextAttributes.ANON_PROVIDER_ATTR, userService);
            final var userHandler = new UserHandler(userService, gson);

            final var cardRepository = new CardRepository(jdbcTemplate);
            final var cardService = new CardService(cardRepository);
            final var cardHandler = new CardHandler(cardService, gson);

            final var routes = Map.<Pattern, Map<String, Handler>>of(
                    Pattern.compile("^rest/cards/getAll"), Map.of(GET, cardHandler::getAll),
                    Pattern.compile("^/rest/cards/order"), Map.of(POST, cardHandler::order),
                    Pattern.compile("^/rest/cards/blockById/(?<cardId>\\d+)$"), Map.of(DELETE, cardHandler::blockById),
                    Pattern.compile("^/rest/cards/id/(?<cardId>\\d+)$"), Map.of(GET, cardHandler::getById),
                    Pattern.compile("^/rest/users/register$"), Map.of(POST, userHandler::register),
                    Pattern.compile("^/rest/users/login$"), Map.of(POST, userHandler::login),
                    Pattern.compile("^/cards/transaction"), Map.of(POST, cardHandler::transaction),
                    Pattern.compile("^/rest/users/recovery/getToken$"), Map.of(POST, userHandler::getTokenRecovery),
                    Pattern.compile("^/rest/users/recovery/setPassword$"), Map.of(POST, userHandler::setNewPassword)
            );
            context.setAttribute(ContextAttributes.ROUTES_ATTR, routes);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ContextInitializationException(e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ServletContextListener.super.contextDestroyed(sce);
        // TODO: init dependencies
    }
}
