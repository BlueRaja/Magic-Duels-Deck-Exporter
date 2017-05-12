package com.blueraja.magicduelsimporter.export.browser;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;

import java.io.IOException;
import java.util.Optional;

public class DeckBrowser {

    public String browse(MagicDuelsDeckManager magicDuelsDeckManager, String deckname) throws IOException {
        Optional<Deck> optionalDeck = magicDuelsDeckManager.getDeck(deckname);

        if (!optionalDeck.isPresent()) {
            return "Deck " + deckname + " not found.";

        } else {
            Deck deck = optionalDeck.get();
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("Deck ").append(deckname)
                    .append(" (").append(getTotalCards(deck)).append(")\n");

            for (CardData cardData : deck.getCards()) {
                stringBuilder.append("  ").append(deck.getCardCount(cardData)).append(" ")
                        .append(cardData.getDisplayName()).append("\n");
            }

            return stringBuilder.toString();
        }
    }

    static int getTotalCards(Deck deck) {
        int totalCards = 0;
        for (CardData cardData : deck.getCards()) {
            totalCards += deck.getCardCount(cardData);
        }
        return totalCards;
    }

}
