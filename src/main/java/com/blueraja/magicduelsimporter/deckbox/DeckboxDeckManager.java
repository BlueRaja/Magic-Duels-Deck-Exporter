package com.blueraja.magicduelsimporter.deckbox;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.utils.FileUtils;

import java.io.FileNotFoundException;

public class DeckboxDeckManager {

    public void writeCardsToFile(Deck ownedCards, String deckboxOutFilePath) throws FileNotFoundException {
        FileUtils.writeToFile(deckboxOutFilePath, asString(ownedCards));
    }

    private String asString(Deck ownedCards) {
        StringBuilder stringBuilder = new StringBuilder();

        for (CardData cardData : ownedCards.getCards()) {
            stringBuilder.append(ownedCards.getCardCount(cardData)).append(" ").append(cardData.getDisplayName()).append("\n");
        }

        return stringBuilder.toString();
    }

}