package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.dto.TransactionDto;
import org.example.app.exception.CardNotFoundException;
import org.example.app.exception.NotEnoughMoneyException;
import org.example.jdbc.JdbcTemplate;
import org.example.jdbc.RowMapper;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardRepository {
    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<Card> cardRowMapper = resultSet -> new Card(
            resultSet.getLong("id"),
            resultSet.getString("number"),
            resultSet.getLong("balance")
    );

    public List<Card> getAllByOwnerId(long ownerId) {
        // language=PostgreSQL
        return jdbcTemplate.queryAll(
                "SELECT id, number, balance FROM cards WHERE \"ownerId\" = ? AND active = TRUE",
                cardRowMapper,
                ownerId
        );


    }

    public Optional<Card> getById(long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.queryOne(
                "SELECT id, number, balance, \"ownerId\" FROM cards WHERE \"id\" = ? AND active = TRUE",
                cardRowMapper,
                cardId
        );
    }

    public Optional<Long> getOwnerID(long cardid) {
        RowMapper<Long> rowMapper = resultSet -> resultSet.getLong("ownerId");
        // language=PostgreSQL

        return jdbcTemplate.queryOne(
                "SELECT \"ownerId\" FROM cards WHERE  \"id\" = ? AND active = TRUE",
                rowMapper,
                cardid
        );
    }

    public int blockById(Long cardId) {
        // language=PostgreSQL
        return jdbcTemplate.update(
                "UPDATE cards set active = FALSE where \"id\" = ?",
                cardId
        );
    }

    public long order(Long ownerId) {
        String randomNumber = Long.toString(System.currentTimeMillis());
        RowMapper<Long> rowMapper = resultSet -> resultSet.getLong("id");

        // language=PostgreSQL
        Optional<Long> optional = jdbcTemplate.queryOne(
                "INSERT INTO " +
                        "cards(\"ownerId\", number, balance, active) " +
                        "VALUES ( ? , ?, 0, true) returning id",
                rowMapper, ownerId, randomNumber
        );
        return optional.orElse(-1L);
    }

    public Optional<Card> transaction(TransactionDto transaction) {
        //TODO: do transaction like sql-transaction too
        //TODO: checks
        RowMapper<Long> rowMapper = resultSet -> resultSet.getLong("balance");

        // language=PostgreSQL
        final var currentBalance = jdbcTemplate.queryOne(
                "SELECT balance FROM cards WHERE number = ? ",
                rowMapper,
                transaction.getFromCardNumber()
        );

        if (currentBalance.orElseThrow(CardNotFoundException::new) < transaction.getValue())
            throw new NotEnoughMoneyException();

        // language=PostgreSQL
        jdbcTemplate.update("UPDATE cards SET balance=balance - ? WHERE number = ?; ",
                transaction.getValue(),
                transaction.getFromCardNumber()
        );

        // language=PostgreSQL
        jdbcTemplate.update("UPDATE cards SET balance=balance + ? WHERE number = ?; ",
                transaction.getValue(),
                transaction.getToCardNumber()
        );

        final var fromCardId = getCardIdByNumber(transaction.getFromCardNumber());
        return getById(fromCardId);
    }


    public Long getCardIdByNumber(String number) {
        RowMapper<Long> rowMapperId = resultSet -> resultSet.getLong("id");
        // language=PostgreSQL
        return  jdbcTemplate.queryOne(
                "Select id from cards where number = ?",
                rowMapperId,
                number
        ).orElseThrow(CardNotFoundException::new);


    }
}
