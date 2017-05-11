package com.blueraja.magicduelsimporter.magicassist;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeck;

import java.util.Optional;

public class ToDeckTransformer {

    private final CardDataManager _cardDataManager;

    public ToDeckTransformer(CardDataManager cardDataManager) {
        this._cardDataManager = cardDataManager;
    }

    public Deck transform(MagicDuelsDeck magicDuelsDeck) {
        Deck deck = new Deck(magicDuelsDeck.name);
        for (int i=0; i<100; i++) {
            int cardId = magicDuelsDeck.cards[0][i];
            int numCards = magicDuelsDeck.cards[1][i];
            if(numCards > 0) {
                Optional<CardData> cardData = _cardDataManager.getDataForMagicDuelsId(cardId);
                if(cardData.isPresent()) {
                    deck.addCard(cardData.get(), numCards);
                } else {
                    System.out.println("Missing data for magic duels card #" + cardId + " which is required for deck " + magicDuelsDeck.name);
                }
            }
        }
        addLands(magicDuelsDeck, deck);
        return deck;
    }

    private void addLands(MagicDuelsDeck magicDuelsDeck, Deck deck) {
        addLand(magicDuelsDeck, deck, MagicDuelsDeck.LAND_FORESTS, CardDataManager.Lands.FOREST);
        addLand(magicDuelsDeck, deck, MagicDuelsDeck.LAND_ISLANDS, CardDataManager.Lands.ISLAND);
        addLand(magicDuelsDeck, deck, MagicDuelsDeck.LAND_MOUNTAINS, CardDataManager.Lands.MOUNTAIN);
        addLand(magicDuelsDeck, deck, MagicDuelsDeck.LAND_PLAINS, CardDataManager.Lands.PLAINS);
        addLand(magicDuelsDeck, deck, MagicDuelsDeck.LAND_SWAMPS, CardDataManager.Lands.SWAMP);
    }

    private void addLand(MagicDuelsDeck magicDuelsDeck, Deck deck, int landOffset, CardData landCardData) {
        int numLand = magicDuelsDeck.lands[landOffset];
        if(numLand > 0) {
            deck.addCard(landCardData, numLand);
        }
    }
}
