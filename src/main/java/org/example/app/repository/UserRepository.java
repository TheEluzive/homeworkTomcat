package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.User;
import org.example.app.domain.UserWithPassword;
import org.example.app.entity.UserEntity;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;
import org.springframework.security.crypto.keygen.Base64StringKeyGenerator;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<User> rowMapper = resultSet -> new User(
            resultSet.getLong("id"),
            resultSet.getString("username")
    );

    private final RowMapper<String> rowMapperRoles = resultSet -> resultSet.getString("role");

    private final RowMapper<UserWithPassword> rowMapperWithPassword = resultSet -> new UserWithPassword(
            resultSet.getLong("id"),
            resultSet.getString("username"),
            resultSet.getString("password")
    );

    private final RowMapper<String> rowMapperToken = resultSet -> resultSet.getString("token");
    private final RowMapper<Long> rowMapperId = resultSet -> resultSet.getLong("id");


    public Optional<User> getByUsername(String username) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ?", rowMapper, username);
    }

    public Optional<User> getByUsernamePassword(String username, String hash) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne("SELECT id, username FROM users WHERE username = ? AND password = ?", rowMapper, username, hash);
    }

    public Optional<UserWithPassword> getByUsernameWithPassword(EntityManager entityManager, EntityTransaction transaction, String username) {
        // em, emt - closeable
        return entityManager.createNamedQuery(UserEntity.FIND_BY_USERNAME, UserEntity.class)
                .setParameter("username", username)
                .setMaxResults(1)
                .getResultStream()
                .map(o -> new UserWithPassword(o.getId(), o.getUsername(), o.getPassword()))
                .findFirst();
        // language=PostgreSQL
        // return jdbcTemplate.queryOne("SELECT id, username, password FROM users WHERE username = ?", rowMapperWithPassword, username);
    }

    /**
     * saves user to db
     *
     * @param id       - user id, if 0 - insert, if not 0 - update
     * @param username
     * @param hash
     */
    // TODO: DuplicateKeyException <-
    public Optional<User> save(long id, String username, String hash) {
        // language=PostgreSQL
        return id == 0 ? jdbcTemplate.queryOne(
                """
                        INSERT INTO users(username, password) VALUES (?, ?) RETURNING id, username
                        """,
                rowMapper,
                username, hash
        ) : jdbcTemplate.queryOne(
                """
                        UPDATE users SET username = ?, password = ? WHERE id = ? RETURNING id, username
                        """,
                rowMapper,
                username, hash, id
        );
    }

    public Optional<User> findByToken(String token) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                """
                        SELECT u.id, u.username FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        WHERE t.token = ?
                        """,
                rowMapper,
                token
        );
    }

    public List<String> getRoles(String token) {

        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                """
                        SELECT r.role FROM tokens t
                        JOIN users u ON t."userId" = u.id
                        JOIN user_roles ur ON ur."user" = u.id
                        JOIN roles r on ur.role = r.id
                        WHERE t.token = ?
                                        
                        """,
                rowMapperRoles,
                token
        );
    }

    public List<String> getRolesByUsername(String username) {

        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                """
                        SELECT r.role from users u
                        JOIN user_roles ur ON ur."user" = u.id
                        JOIN roles r on ur.role = r.id
                        WHERE u.username = ?
                                        
                        """,
                rowMapperRoles,
                username
        );
    }

    public void saveToken(long userId, String token) {
        // query - SELECT'ов (ResultSet)
        // update - ? int/long

        // language=PostgreSQL
        jdbcTemplate.update(
                """
                        INSERT INTO tokens(token, "userId") VALUES (?, ?)
                        """,
                token, userId
        );
    }

    public Optional<String> getRecoveryToken(String login) {


        final String token = new Base64StringKeyGenerator(32).generateKey().substring(0, 6);

        // language=PostgreSQL
        final var tokenId = jdbcTemplate.queryOne(
                "INSERT INTO token_recovery(token,login) VALUES (?, ?) returning id",
                rowMapperId,
                token,
                login
        );

        //language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT token from token_recovery where id = ?",
                rowMapperToken,
                tokenId.orElse(-1L)
        );

        //insert into table token(return token)
    }

    public Optional<String> getLoginByRecoveryToken(String code) {
        RowMapper<String> rowMapper = resultSet -> resultSet.getString("login");
        //language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT login from token_recovery where token =  ?",
                rowMapper,
                code
        );
    }

    public void refreshToken(String token) {
        // TODO: var timestamp = new Timestamp(System.currentTimeMillis());
        //language=PostgreSQL
        jdbcTemplate.update(
                "update tokens set created = current_timestamp\n where token = ?",
                token
        );
    }


    public Timestamp getTokenCreatedTime(String token) {

        RowMapper<Timestamp> longRowMapper = resultSet -> resultSet.getTimestamp("created");
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT created from tokens where token = ?",
                longRowMapper,
                token
        ).orElseThrow();
    }
}
