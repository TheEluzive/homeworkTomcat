package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.TransactionDto;
import org.example.app.exception.*;
import org.example.app.service.CardService;
import org.example.app.util.UserHelper;
import org.example.framework.attribute.RequestAttributes;

import java.io.IOException;
import java.util.regex.Matcher;

import static org.example.app.handler.HandlerHelper.CONTENT_TYPE;
import static org.example.app.handler.HandlerHelper.CONTENT_TYPE_JSON;

@Log
@RequiredArgsConstructor
public class CardHandler { // Servlet -> Controller -> Service (domain) -> domain
    private final CardService service;
    private final Gson gson;


    public void getAll(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var user = UserHelper.getUser(req);//тянется из авторизации
            final var data = service.getAllByOwnerId(user.getId()); //проверка не нужна в таком случае
            resp.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            resp.getWriter().write(gson.toJson(data));

        } catch (IOException e) {
            e.printStackTrace();
            throw new CardsGetAllException(e);

        }
    }

    public void getById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var cardId = parseLongRequest(req, "cardId");
            service.isLegalAccess(cardId, req);

            final var data = service.getByID(cardId);
            resp.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            resp.getWriter().write(gson.toJson(data));

        } catch (Exception e) {
            e.printStackTrace();
            throw new CardGetByIdException(e);
        }
    }


    public void order(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var card = service.order(UserHelper.getUser(req).getId());
            resp.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            resp.getWriter().write(gson.toJson(card));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CardNotOrderedException(e);
        }
    }

    public void blockById(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var cardId = parseLongRequest(req, "cardId");
            service.isLegalAccess(cardId, req);
            final var result = service.blockById(cardId);


        } catch (Exception e) {
            e.printStackTrace();
            throw new CardBlockByIdException(e);
        }
    }

    public void transaction(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var requestDto = gson.fromJson(req.getReader(), TransactionDto.class);
            //TODO checks
            if (requestDto.getValue() <= 0)
                throw new TransactionValueZeroOrNegativeException();


            service.isLegalTransaction(requestDto.getFromCardNumber(), req);
            final var data = service.transaction(requestDto);
            //return updated card from that was transaction
            resp.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            resp.getWriter().write(gson.toJson(data));
        } catch (Exception e) {
            e.printStackTrace();
            throw new CardTransactionException(e);
        }

    }


    public Long parseLongRequest(HttpServletRequest req, String group) {
        try {
            return Long.parseLong(((Matcher) req.getAttribute(RequestAttributes.PATH_MATCHER_ATTR)).group(group));
        } catch (Exception e) {
            e.printStackTrace();
            return -1L;
        }
    }


}
