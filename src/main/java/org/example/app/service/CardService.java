package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.dto.TransactionDto;
import org.example.app.repository.CardRepository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {
  private final CardRepository cardRepository;

  public List<Card> getAllByOwnerId(long ownerId) {

    return cardRepository.getAllByOwnerId(ownerId);
  }

  public Card getByID(Long cardId){
    Optional<Card> card= cardRepository.getById(cardId);
    return card.orElse(null);

  }

  public long getOwnerID(Long cardId){
    Optional<Long> ownerId = cardRepository.getOwnerID(cardId);
    return ownerId.orElse(-1L);
  }

  public int blockById(Long cardId){
    return cardRepository.blockById(cardId);
  }

  public Optional<Card> order(Long ownerId) {
    try {
      long cardId = cardRepository.order(ownerId);
      return cardRepository.getById(cardId);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return Optional.empty();
  }

  public Optional<Card> transaction(TransactionDto transaction){
    return cardRepository.transaction(transaction);

  }
}
