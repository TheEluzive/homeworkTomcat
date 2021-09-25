package org.example.app.repository;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
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
    return  jdbcTemplate.queryOne(
            "SELECT id, number, balance, \"ownerId\" FROM cards WHERE \"id\" = ? AND active = TRUE",
            cardRowMapper,
            cardId
    );
  }

  public Optional<Integer> getOwnerID(long cardid){
    RowMapper<Integer> rowMapper =  resultSet -> resultSet.getInt("ownerId");
    // language=PostgreSQL

    return jdbcTemplate.queryOne(
            "SELECT \"ownerId\" FROM cards WHERE  \"id\" = ? AND active = TRUE",
    rowMapper,
    cardid
    );
  }

  public int blockById(Long cardId){
    // language=PostgreSQL
    return jdbcTemplate.update(
            "UPDATE cards set active = FALSE where \"id\" = ?",
            cardId
    );
  }
}
