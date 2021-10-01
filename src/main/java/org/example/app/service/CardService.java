package org.example.app.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.app.domain.Card;
import org.example.app.dto.TransactionDto;
import org.example.app.exception.CardOrderNotSuccessfullyException;
import org.example.app.exception.CardOwnerNotFoundException;
import org.example.app.exception.CardIllegalAccessException;
import org.example.app.exception.UserNotFoundException;
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
        return card.orElseThrow(UserNotFoundException::new);
    }

    public long getOwnerID(Long cardId) {
        Optional<Long> ownerId = cardRepository.getOwnerID(cardId);
        return ownerId.orElseThrow(CardOwnerNotFoundException::new);
    }

    public int blockById(Long cardId) {
        //TODO: проверить что возвращает метод 0 или н-строк
        return cardRepository.blockById(cardId);
    }

    public Card order(Long ownerId) {
        long cardId = cardRepository.order(ownerId);
        return cardRepository.getById(cardId)
                .orElseThrow(CardOrderNotSuccessfullyException::new);
    }

    public Optional<Card> transaction(TransactionDto transaction) {
        return cardRepository.transaction(transaction);
    }

    public void isLegalAccess(long cardId, HttpServletRequest req) throws CardIllegalAccessException {
        //TODO: make boolean IsAdminHaveAccessToOperation
        final var ownerId = getOwnerID(cardId);
        final var authorizedUserId = UserHelper.getUser(req).getId();


        final var isAdmin = UserHelper.isRoles(req, Roles.ROLE_ADMIN);
        if (ownerId != authorizedUserId && !isAdmin)
            throw new CardIllegalAccessException("User with " + authorizedUserId +
                    "cant access to card " + cardId);
    }

    public void isLegalTransaction(String cardNumber, HttpServletRequest req) throws CardIllegalAccessException {
        final var cardId = cardRepository.getCardIdByNumber(cardNumber);
        final var ownerId = getOwnerID(cardId);

        final var authorizedUserId = UserHelper.getUser(req).getId();
        if (ownerId != authorizedUserId)
            throw new CardIllegalAccessException("User with " + authorizedUserId +
                    "cant access to card " + cardId);
    }


}
