package com.blueraja.magicduelsimporter.exceptions;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.magicassist.Deck;

public class DeckError {
    private final Deck _deck;
    private final CardData _card;
    private final String _message;

    public DeckError(Deck deck, CardData card, String message) {
        _deck = deck;
        _card = card;
        _message = message;
    }

    public Deck getDeck() {
        return _deck;
    }

    public CardData getCard() {
        return _card;
    }

    public String getMessage() {
        return _message;
    }
}
