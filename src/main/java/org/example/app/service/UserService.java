package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.domain.UserWithPassword;
import org.example.app.dto.*;
import org.example.app.exception.PasswordNotMatchesException;
import org.example.app.exception.RegistrationException;
import org.example.app.exception.TokenDeprecatedException;
import org.example.app.exception.UserNotFoundException;
import org.example.app.jpa.JpaTransactionTemplate;
import org.example.app.repository.UserRepository;
import org.example.framework.security.*;
import org.example.framework.util.KeyValue;
import org.springframework.security.crypto.keygen.StringKeyGenerator;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;

@RequiredArgsConstructor
public class UserService implements AuthenticationProvider, AnonymousProvider {
    private final UserRepository repository;
    private final JpaTransactionTemplate transactionTemplate;
    private final PasswordEncoder passwordEncoder;
    private final StringKeyGenerator keyGenerator;
    private final long tokenLifeInHours = 1L;
    private Collection<String> roles;

    @Override
    public Authentication authenticate(Authentication authentication) {
        if (authentication instanceof TokenAuthentication) {
            final var token = (String) authentication.getPrincipal();
            isTokenAlive(token);
            roles = repository.getRoles(token);

            refreshToken(token);

            return repository.findByToken(token)
                    .map(o -> new TokenAuthentication(o, null, roles, true))
                    .orElseThrow(AuthenticationException::new);
        }

        if (authentication instanceof BasicAuthentication) {
            final var username = (String) authentication.getPrincipal();
            final var password = (String) authentication.getCredentials();
            final var hash = passwordEncoder.encode(password);
            roles = repository.getRolesByUsername(username);


            final var userWithPassword = getUserByLogBass(username,password);
            final var user = repository.getByUsername(userWithPassword.getUsername());

            return user.
                    map(o -> new BasicAuthentication(o, password, roles, true))
                    .orElseThrow(AuthenticationException::new);

        }
        return provide();
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
        //  ??????: ...
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

    public String setNewPassword(RecoveryPasswordNewPasswordDto model) {
        final var login = repository.getLoginByRecoveryToken(model.getCode()).orElseThrow();
        final var hash = passwordEncoder.encode(model.getNewPassword().trim());
        var userId = repository.getByUsername(login).orElseThrow().getId();
        return repository.save(userId, login, hash).orElseThrow(UserNotFoundException::new).getUsername();

    }

    public void refreshToken(String token) {
        repository.refreshToken(token);
    }


    public void isTokenAlive(String token) {
        final var time = repository.getTokenCreatedTime(token).getTime();
        final var currentTime = System.currentTimeMillis();
        final var differenceInHours = (currentTime - time) / 1000 / 3600;
        if (differenceInHours > tokenLifeInHours) {
            throw new TokenDeprecatedException("Token life is over. Get new auth token!");
        }
    }

    private UserWithPassword getUserByLogBass(String username, String password){
        return transactionTemplate.executeInTransaction((entityManager, transaction) -> {
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

            return saved;
        });
    }


}
