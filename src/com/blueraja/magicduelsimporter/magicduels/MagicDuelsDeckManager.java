package com.blueraja.magicduelsimporter.magicduels;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.exceptions.DeckError;
import com.blueraja.magicduelsimporter.exceptions.InvalidDecksException;
import com.blueraja.magicduelsimporter.magicassist.Deck;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class MagicDuelsDeckManager {
    private final CardDataManager _cardDataManager;
    private final String _profilePath;

    public MagicDuelsDeckManager(CardDataManager cardDataManager, String profilePath) throws IOException {
        _cardDataManager = cardDataManager;
        _profilePath = profilePath;
        File profileFile = new File(_profilePath);
        if(!profileFile.isFile()) {
            throw new IOException("Profile could not be found at " + _profilePath);
        }
    }

    public Deck getOwnedCards() throws IOException {
        Profile profile = getProfile();
        int numCards = _cardDataManager.getAllCards().size();
        byte[] cardsArray = profile.readCards(numCards);
        Deck deck = new Deck("Magic Duels collection");

        for(int i = 0; i < cardsArray.length; i++) {
            Optional<CardData> cardData = _cardDataManager.getDataForMagicDuelsId(i);
            int numOwned = cardsArray[i]&7; //number of cards are determined by 3 LSB
            if(numOwned > 0) {
                if(cardData.isPresent()) {
                    deck.addCard(cardData.get(), numOwned);
                } else {
                    System.out.println("Missing data for magic duels card #" + i + ", which is owned");
                }
            }
        }

        //Add 100 of each mana
        for(CardData landCard: _cardDataManager.getAllLands()) {
            deck.addCard(landCard, 100);
        }

        return deck;
    }

    public Iterable<Deck> getDecks() throws IOException {
        Profile profile = getProfile();
        List<Deck> returnedDecks = new ArrayList<>();

        for (int deckPos=0; deckPos<32; deckPos++) {
            MagicDuelsDeck magicDuelsDeck = profile.readDeck(deckPos);
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
            if(deck.getName() != null && !deck.getName().equals("")) {
                returnedDecks.add(deck);
            }
        }
        return returnedDecks;
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

    private Profile getProfile() throws IOException {
        return new Profile(new File(_profilePath));
    }

    public void writeDecks(Iterable<Deck> decks) throws IOException, InvalidDecksException {
        Set<DeckError> deckErrors = getAllDeckErrors(decks);
        if(!deckErrors.isEmpty()) {
            throw new InvalidDecksException(deckErrors);
        }

        Profile profile = getProfile();
        for(Deck deck: decks) {
            writeDeck(deck, profile);
        }
        profile.save();
    }

    private void writeDeck(Deck deck, Profile profile) {
        int position = getPositionForDeck(deck, profile);

        MagicDuelsDeck magicDuelsDeck = profile.readDeck(position);
        magicDuelsDeck.name = deck.getName();
        clearCards(magicDuelsDeck);

        int i = 0;
        for(CardData cardData: deck.getCards()) {
            if (_cardDataManager.isLand(cardData)) {
                magicDuelsDeck.lands[getLandIndex(cardData)] = (byte)deck.getCardCount(cardData);
            } else {
                magicDuelsDeck.cards[0][i] = cardData.idMagicDuels;
                magicDuelsDeck.cards[1][i] = deck.getCardCount(cardData);
                i++;
            }
        }

        profile.writeDeck(magicDuelsDeck, position);
    }

    private void clearCards(MagicDuelsDeck magicDuelsDeck) {
        for(int i = 0; i < magicDuelsDeck.cards[0].length; i++) {
            magicDuelsDeck.cards[0][i] = 0;
            magicDuelsDeck.cards[1][i] = 0;
        }

        for(int i = 0; i < magicDuelsDeck.lands.length; i++) {
            magicDuelsDeck.lands[i] = 0;
        }
    }

    private int getLandIndex(CardData land) {
        if(land.equals(CardDataManager.Lands.FOREST)) { return MagicDuelsDeck.LAND_FORESTS; }
        if(land.equals(CardDataManager.Lands.ISLAND)) { return MagicDuelsDeck.LAND_ISLANDS; }
        if(land.equals(CardDataManager.Lands.MOUNTAIN)) { return MagicDuelsDeck.LAND_MOUNTAINS; }
        if(land.equals(CardDataManager.Lands.PLAINS)) { return MagicDuelsDeck.LAND_PLAINS; }
        if(land.equals(CardDataManager.Lands.SWAMP)) { return MagicDuelsDeck.LAND_SWAMPS; }

        throw new IllegalArgumentException("getLandIndex must be passed a land!");
    }

    private int getPositionForDeck(Deck deck, Profile profile) throws IllegalStateException {
        //If there is an existing deck with the same name, overwrite that one.  Otherwise create a new one
        int firstEmptyDeck = -1;
        for(int i = 0; i < 32; i++) {
            MagicDuelsDeck magicDuelsDeck = profile.readDeck(i);
            if(isEmptyDeck(magicDuelsDeck)) {
                if(firstEmptyDeck == -1) {
                    firstEmptyDeck = i;
                }
            } else {
                if(magicDuelsDeck.name.equals(deck.getName())) {
                    return i;
                }
            }
        }

        if(firstEmptyDeck == -1) {
            throw new IllegalStateException("No empty deck slots!  Need to delete some decks in Magic Duels first");
        }

        return firstEmptyDeck;
    }

    private boolean isEmptyDeck(MagicDuelsDeck magicDuelsDeck) {
        for(int i = 0; i < magicDuelsDeck.cards[1].length; i++) {
            int numCards = magicDuelsDeck.cards[1][i];
            if(numCards > 0) {
                return false;
            }
        }
        return true;
    }

    private Set<DeckError> getAllDeckErrors(Iterable<Deck> decks) throws IOException {
        Deck entireCollection = getOwnedCards();
        Set<DeckError> errors = new HashSet<>();

        for(Deck deck: decks) {
            errors.addAll(getDeckErrors(deck, entireCollection));
        }

        return errors;
    }

    private Set<DeckError> getDeckErrors(Deck deckToCheck, Deck entireCollection) {
        Set<DeckError> errors = new HashSet<>();
        Set<CardData> allCards = entireCollection.getCards();

        if(deckToCheck.getCards().size() > 100) {
            errors.add(new DeckError(deckToCheck, null, "Cannot have more than 100 cards"));
        }

        for(CardData card: deckToCheck.getCards()) {
            if(!allCards.contains(card)) {
                errors.add(new DeckError(deckToCheck, card, "Card is not owned"));
            } else {
                int numInDeck = deckToCheck.getCardCount(card);
                int numOwned = entireCollection.getCardCount(card);
                if (numInDeck > numOwned) {
                    errors.add(new DeckError(deckToCheck, card, "Added " + numInDeck + " copies, but only " + numOwned + " are owned"));
                }
            }
        }
        return errors;
    }
}