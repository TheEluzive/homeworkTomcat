package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.dto.*;
import org.example.app.exception.PasswordNotMatchesException;
import org.example.app.exception.RegistrationException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.jpa.JpaTransactionTemplate;
import org.example.app.repository.UserRepository;
import org.example.framework.security.*;
import org.example.framework.util.KeyValue;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collection;

@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider {
    private final UserRepository repository;
    private final JpaTransactionTemplate transactionTemplate;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator keyGenerator;
    private final long tokenLifeInHours = 1L;

    @Override
    public Authentication authenticate(Authentication authentication) {
        final var token = (String) authentication.getPrincipal();

        Collection<String> roles = new ArrayList<>();
        for (Long n : repository.getRoles(token)) {
            roles.add(Roles.getById(n));
        }


        return repository.findByToken(token)
                // TODO: add user roles
                .map(o -> new TokenAuthentication(o, null, roles, true))
                .orElseThrow(AuthenticationException::new);
    }

    @Override
    public AnonymousAuthentication provide() {
        return new AnonymousAuthentication(new User(
                -1,
                "anonymous"
        ));
    }

    public RegistrationResponseDto register(RegistrationRequestDto requestDto) {
        // TODO login:
        //  case-sensitivity: coursar Coursar
        //  cleaning: "  Coursar   "
        //  allowed symbols: [A-Za-z0-9]{2,60}
        //  mis...: Admin, Support, root, ...
        //  мат: ...
        // FIXME: check for nullability
        final var username = requestDto.getUsername().trim().toLowerCase();
        // TODO password:
        //  min-length: 8
        //  max-length: 64
        //  non-dictionary
        final var password = requestDto.getPassword().trim();
        final var hash = passwordEncoder.encode(password);
        final var token = keyGenerator.generateKey();
        final var saved = repository.save(0, username, hash).orElseThrow(RegistrationException::new);
        final var saveBase64LogPas = "Basic " + passwordEncoder.encode(username + password);

        repository.saveBase64LogPas(saveBase64LogPas);
        repository.saveToken(saved.getId(), token);
        return new RegistrationResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public LoginResponseDto login(LoginRequestDto requestDto) {
        final var username = requestDto.getUsername().trim().toLowerCase();
        final var password = requestDto.getPassword().trim();

        final var result = transactionTemplate.executeInTransaction((entityManager, transaction) -> {
            final var saved = repository.getByUsernameWithPassword(
                    entityManager,
                    transaction,
                    username
            ).orElseThrow(UserNotFoundException::new);

            // TODO: be careful - slow
            if (!passwordEncoder.matches(password, saved.getPassword())) {
                // FIXME: Security issue
                throw new PasswordNotMatchesException();
            }

            final var token = keyGenerator.generateKey();
            repository.saveToken(saved.getId(), token);
            return new KeyValue<>(token, saved);
        });

        // FIXME: Security issue

        final var token = result.getKey();
        final var saved = result.getValue();
        return new LoginResponseDto(saved.getId(), saved.getUsername(), token);
    }

    public String getRecoveryToken(String login) {
        return repository.getRecoveryToken(login).orElse("");
    }

    public String setNewPassword(RecoveryPasswordSecondDto model) {
        final var login = repository.getLoginByRecoveryToken(model.getCode()).orElseThrow();
        final var hash = passwordEncoder.encode(model.getNewPassword().trim());
        var userId = repository.getByUsername(login).orElseThrow().getId();
        return repository.save(userId, login, hash).orElseThrow().getUsername();

    }

    public void refreshToken(String oldToken) {
        repository.refreshToken(oldToken, keyGenerator.generateKey());
    }

    public String getTokenFromBase64LogPass(String base64LogPas) {
        return repository.getTokenByBase64(base64LogPas);
    }

    public void isTokenAlive(String token) {
        final var time = repository.getTokenCreatedTime(token).getTime();
        final var currentTime = System.currentTimeMillis();
        final var differenceInHours = (currentTime - time) / 1000 / 3600;
        if (differenceInHours > tokenLifeInHours) {
            throw new RuntimeException("Token life is over. Get new auth token!");
        }
    }


}
