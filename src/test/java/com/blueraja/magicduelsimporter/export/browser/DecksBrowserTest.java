package com.blueraja.magicduelsimporter.export.browser;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DecksBrowserTest {

    @InjectMocks
    private DecksBrowser decksBrowser;

    @Mock
    private MagicDuelsDeckManager magicDuelsDeckManager;

    @Test
    public void shouldBrowseDecks() throws Exception {
        // Given
        List<Deck> decks = new ArrayList<>();

        Deck deck1 = new Deck("deck1");
        deck1.addCard(new CardData("Mountain", 1, 1), 1);
        deck1.addCard(new CardData("Plains", 1, 1), 2);

        decks.add(deck1);

        Deck deck2 = new Deck("deck2");
        deck2.addCard(new CardData("Swamp", 1, 1), 10);
        decks.add(deck2);

        given(magicDuelsDeckManager.getDecks()).willReturn(decks);


        // When
        String out = decksBrowser.browse(magicDuelsDeckManager);

        // Then
        assertThat(out).isEqualTo(
                "Decks:" + "\n" +
                " - deck1 (3)" + "\n" +
                " - deck2 (10)" + "\n"
        );
    }
}