package com.blueraja.magicduelsimporter.exceptions;

import java.util.Set;
import java.util.stream.Collectors;

public class InvalidDecksException extends Exception {
    private final Set<DeckError> _deckErrors;

    public InvalidDecksException(Set<DeckError> deckErrors) {
        _deckErrors = deckErrors;
    }

    public Iterable<DeckError> getDeckErrors() {
        return _deckErrors.stream()
                .sorted((error1, error2) -> {
                    int comp = error1.getDeck().getName().compareTo(error2.getDeck().getName());
                    if (comp != 0) {
                        return comp;
                    }
                    return error1.getCard().displayName.compareTo(error2.getCard().displayName);
                }).collect(Collectors.toList());
    }
}
