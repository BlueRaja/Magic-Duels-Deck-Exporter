package com.blueraja.magicduelsimporter.magicassist;

import com.blueraja.magicduelsimporter.carddata.CardData;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Deck {
    private final String _name;
    private Map<CardData, Integer> _cards = new HashMap<>();

    private float scoreSpeed;
    private float scoreStrength;
    private float scoreControl;
    private float scoreSynergy;
    private int onlineDeckNumber;

    public Deck(String name) {
        _name = name;
    }

    public void addCard(CardData cardData, int count) {
        if(!_cards.containsKey(cardData)) {
            _cards.put(cardData, 0);
        }
        _cards.put(cardData, _cards.get(cardData)+count);
    }

    public void removeCard(CardData cardData, int count) {
        if(!_cards.containsKey(cardData)) {
            throw new IllegalArgumentException("Card not in deck cannot be removed!");
        }

        int numInDeck = _cards.get(cardData);
        if(numInDeck - count <= 0) {
            _cards.remove(cardData);
        } else {
            _cards.put(cardData, _cards.get(cardData)-count);
        }
    }

    public Set<CardData> getCards() {
        return _cards.keySet();
    }

    public int getCardCount(CardData cardData) {
        return _cards.containsKey(cardData) ? _cards.get(cardData) : 0;
    }

    public String getName() {
        return _name;
    }

    public float getScoreSpeed() {
        return scoreSpeed;
    }

    public void setScoreSpeed(float scoreSpeed) {
        this.scoreSpeed = scoreSpeed;
    }

    public float getScoreStrength() {
        return scoreStrength;
    }

    public void setScoreStrength(float scoreStrength) {
        this.scoreStrength = scoreStrength;
    }

    public float getScoreControl() {
        return scoreControl;
    }

    public void setScoreControl(float scoreControl) {
        this.scoreControl = scoreControl;
    }

    public float getScoreSynergy() {
        return scoreSynergy;
    }

    public void setScoreSynergy(float scoreSynergy) {
        this.scoreSynergy = scoreSynergy;
    }

    public int getOnlineDeckNumber() {
        return onlineDeckNumber;
    }

    public void setOnlineDeckNumber(int onlineDeckNumber) {
        this.onlineDeckNumber = onlineDeckNumber;
    }

    public String getDescription() {
        return String.format("Speed: %.3f\nStrength: %.3f\nControl: %.3f\nSynergy: %.3f\nOnline Deck Number: %d",
                getScoreSpeed(), getScoreStrength(), getScoreControl(), getScoreSynergy(), getOnlineDeckNumber());
    }
}
