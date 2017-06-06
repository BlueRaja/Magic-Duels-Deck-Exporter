package com.blueraja.magicduelsimporter.deckbox;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.shazam.shazamcrest.MatcherAssert;
import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;

public class DeckboxDeckManagerTest {

    private DeckboxDeckManager deckboxDeckManager;

    @Before
    public void setup() throws Exception {
        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        deckboxDeckManager = new DeckboxDeckManager(cardDataManager);
    }

    @Test
    public void asStringShouldTransformADeckAsAString() {
        // given
        Deck deck = new Deck("MyDeck");
        deck.addCard(new CardData("Accursed Spirit", 0, 370811), 1);
        deck.addCard(new CardData("Primal Bellow", 65, 193407), 3);

        // when
        String actualDeck = deckboxDeckManager.asString(deck);

        // then
        Assertions.assertThat(actualDeck).isEqualTo(
                "3 Primal Bellow\n" +
                "1 Accursed Spirit\n"
        );
    }

    @Test
    public void asStringEmpty() {
        // given
        Deck deck = new Deck("MyDeck");

        // when
        String actualDeck = deckboxDeckManager.asString(deck);

        // then
        Assertions.assertThat(actualDeck).isEqualTo("");
    }

    @Test
    public void fromStringShouldParseADeckFromAString() {
        // given
        String deckName = "MyDeck";
        String deckContent =
                "3 Primal Bellow\n" +
                "1 Accursed Spirit\n" +
                "10 Mountain";

        // when
        Deck actual = deckboxDeckManager.fromString(deckName, deckContent);

        // then
        Deck expectedDeck = new Deck("MyDeck");
        expectedDeck.addCard(new CardData("Accursed Spirit", 0, 370811), 1);
        expectedDeck.addCard(new CardData("Primal Bellow", 65, 193407), 3);
        expectedDeck.addCard(new CardData("Mountain", -3, 383315), 10);

        MatcherAssert.assertThat(actual, sameBeanAs(expectedDeck));
    }

    @Test
    public void fromStringEmpty() {
        // given
        String deckName = "MyDeck";
        String deckContent = "";

        // when
        Deck actual = deckboxDeckManager.fromString(deckName, deckContent);

        // then
        Deck expectedDeck = new Deck("MyDeck");

        MatcherAssert.assertThat(actual, sameBeanAs(expectedDeck));
    }

}