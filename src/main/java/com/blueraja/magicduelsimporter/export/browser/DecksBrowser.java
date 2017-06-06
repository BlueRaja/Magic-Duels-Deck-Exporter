package com.blueraja.magicduelsimporter.export.browser;

import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;

import java.io.IOException;

import static com.blueraja.magicduelsimporter.export.browser.DeckBrowser.getTotalCards;

public class DecksBrowser {

    public String browse(MagicDuelsDeckManager magicDuelsDeckManager) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Decks:\n");
        for (Deck deck : magicDuelsDeckManager.getDecks()) {
            stringBuilder.append(" - ").append(deck.getName())
                    .append(" (").append(getTotalCards(deck)).append(")\n");
        }
        return stringBuilder.toString();
    }

}
