package com.blueraja.magicduelsimporter.deckbox;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.utils.FileUtils;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DeckboxDeckManager {

    private final CardDataManager cardDataManager;

    public DeckboxDeckManager(CardDataManager cardDataManager) {
        this.cardDataManager = cardDataManager;
    }

    public void writeCardsToFile(Deck ownedCards, String deckboxOutFilePath) throws FileNotFoundException {
        FileUtils.writeToFile(deckboxOutFilePath, asString(ownedCards));
    }

    public Deck loadDeck(String deckboxDeckFilePath) throws IOException {
        return fromString(
                FileUtils.getBaseName(deckboxDeckFilePath),
                FileUtils.getFileAsString(deckboxDeckFilePath)
        );
    }

    String asString(Deck ownedCards) {
        StringBuilder stringBuilder = new StringBuilder();

        for (CardData cardData : ownedCards.getCards()) {
            stringBuilder.append(ownedCards.getCardCount(cardData)).append(" ").append(cardData.getDisplayName()).append("\n");
        }

        return stringBuilder.toString();
    }

    Deck fromString(String deckName, String deckContent) {
        Deck deck = new Deck(deckName);

        String[] lines = deckContent.split("\n");

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                int firstSpaceIndex = line.indexOf(" ");
                int cardsCount = Integer.parseInt(line.substring(0, firstSpaceIndex));
                String cardName = line.substring(firstSpaceIndex + 1);

                CardData cardData = cardDataManager.getCard(cardName);
                deck.addCard(cardData, cardsCount);
            }
        }

        return deck;
    }
}