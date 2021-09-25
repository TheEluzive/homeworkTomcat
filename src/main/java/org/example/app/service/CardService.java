package org.example.app.service;

import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.repository.CardRepository;

import javax.swing.text.html.Option;
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
    Optional<Integer> ownerId = cardRepository.getOwnerID(cardId);
    return ownerId.orElse(-1);
  }

  public int blockById(Long cardId){
    return cardRepository.blockById(cardId);
  }
}
