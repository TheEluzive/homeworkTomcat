package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.domain.Card;
import org.example.app.domain.User;
import org.example.app.exception.IllegalAccessCardsException;
import org.example.app.service.CardService;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;
import org.example.framework.security.Authentication;
import org.example.framework.security.Roles;

import java.io.IOException;
import java.util.logging.Level;
import java.util.regex.Matcher;

@Log
@RequiredArgsConstructor
public class CardHandler { // Servlet -> Controller -> Service (domain) -> domain
    private final CardService service;
    private final Gson gson;

    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        int a = 10;
        try {
            // cards.getAll?ownerId=1
            final var user = UserHelper.getUser(req);
            final var data = service.getAllByOwnerId(user.getId());
            resp.setHeader("Content-Type", "application/json");
            resp.getWriter().write(gson.toJson(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        final var cardId = Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group("cardId"));

        final var userId = UserHelper.getUser(req).getId();
        final var data = service.getByID(cardId, userId);

        resp.setHeader("Content-Type", "application/json");
        try {
            //TODO: data.getID() - get CARD ID, NOT USER ID FIX!!!
            if (data.getId() != userId && !UserHelper.isRoles(req, Roles.ROLE_ADMIN))
                throw new IllegalAccessCardsException("User with " + userId + "cant access to card " + cardId);
            resp.getWriter().write(gson.toJson(data));
        } catch (IllegalAccessCardsException illegalAccessCardsException) {
            illegalAccessCardsException.printStackTrace();
        } catch (Exception e) { //TODO: fix me???
            e.printStackTrace();
        }

        log.log(Level.INFO, "getById");
    }

    public void order(HttpServletRequest req, HttpServletResponse resp) {
    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
    }
}
