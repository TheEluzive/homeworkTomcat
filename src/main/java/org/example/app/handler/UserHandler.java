package org.example.app.handler;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.example.app.dto.LoginRequestDto;
import org.example.app.dto.RecoveryPasswordFirstDto;
import org.example.app.dto.RecoveryPasswordSecondDto;
import org.example.app.dto.RegistrationRequestDto;
import org.example.app.service.UserService;

import java.io.IOException;
import java.util.logging.Level;

import static org.example.app.handler.HandlerHelper.CONTENT_TYPE;
import static org.example.app.handler.HandlerHelper.CONTENT_TYPE_JSON;

@Log
@RequiredArgsConstructor
public class UserHandler {
    private final UserService service;
    private final Gson gson;

    public void register(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var requestDto = gson.fromJson(req.getReader(), RegistrationRequestDto.class);
            final var responseDto = service.register(requestDto);
            resp.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void login(HttpServletRequest req, HttpServletResponse resp) {
        try {
            log.log(Level.INFO, "register");
            final var requestDto = gson.fromJson(req.getReader(), LoginRequestDto.class);
            final var responseDto = service.login(requestDto);
            resp.setHeader(CONTENT_TYPE, CONTENT_TYPE_JSON);
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getTokenRecovery(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var dto = gson.fromJson(req.getReader(), RecoveryPasswordFirstDto.class);
            final var recoveryToken = service.getRecoveryToken(dto.getLogin());
            log.log(Level.INFO, "Recovery token =" + recoveryToken);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setNewPassword(HttpServletRequest req, HttpServletResponse resp) {
        try {
            final var model = gson.fromJson(req.getReader(), RecoveryPasswordSecondDto.class);
            final var login = service.setNewPassword(model);
            //TODO: delete recovery token
            final var responseDto = service.login(new LoginRequestDto(login, model.getNewPassword()));
            resp.getWriter().write(gson.toJson(responseDto));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
