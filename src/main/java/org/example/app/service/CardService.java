package org.example.app.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.dto.TransactionDto;
import org.example.app.exception.IllegalAccessCardsException;
import org.example.app.repository.CardRepository;
import org.example.app.util.UserHelper;
import org.example.framework.security.Roles;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class CardService {
    private final CardRepository cardRepository;

    public List<Card> getAllByOwnerId(long ownerId) {

        return cardRepository.getAllByOwnerId(ownerId);
    }

    public Card getByID(Long cardId) {
        Optional<Card> card = cardRepository.getById(cardId);
        return card.orElse(null);

    }

    public long getOwnerID(Long cardId) {
        Optional<Long> ownerId = cardRepository.getOwnerID(cardId);
        return ownerId.orElse(-1L);
    }

    public int blockById(Long cardId) {
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

    public Optional<Card> transaction(TransactionDto transaction) {
        return cardRepository.transaction(transaction);

    }

    public void isLegalAccess(long cardId, HttpServletRequest req) throws IllegalAccessCardsException {

        final var ownerId = getOwnerID(cardId);
        final var authorizedUserId = UserHelper.getUser(req).getId();


        final var isAdmin = UserHelper.isRoles(req, Roles.ROLE_ADMIN);
        if (ownerId != authorizedUserId && !isAdmin)
            throw new IllegalAccessCardsException("User with " + authorizedUserId +
                    "cant access to card " + cardId);
    }

    public void isLegalTransaction(long cardId, HttpServletRequest req) throws IllegalAccessCardsException {
        final var ownerId = getOwnerID(cardId);
        final var authorizedUserId = UserHelper.getUser(req).getId();
        if (ownerId != authorizedUserId)
            throw new IllegalAccessCardsException("User with " + authorizedUserId +
                    "cant access to card " + cardId);
    }


}
