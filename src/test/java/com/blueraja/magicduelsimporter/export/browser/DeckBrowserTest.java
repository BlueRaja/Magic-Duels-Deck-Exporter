package com.blueraja.magicduelsimporter.export.browser;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DeckBrowserTest {

    @InjectMocks
    private DeckBrowser deckBrowser;

    @Mock
    private MagicDuelsDeckManager magicDuelsDeckManager;

    @Test
    public void shouldGiveErrorIfNoDeck() throws Exception {
        // Given
        String deckName = "deck1";
        given(magicDuelsDeckManager.getDeck(deckName)).willReturn(Optional.empty());

        // When
        String out = deckBrowser.browse(magicDuelsDeckManager, deckName);

        // Then
        assertThat(out).isEqualTo("Deck " + deckName + " not found.");
    }

    @Test
    public void shouldBrowseDeck() throws Exception {
        // Given
        String deckName = "deck1";
        Deck deck = new Deck(deckName);
        deck.addCard(new CardData("Mountain", 1, 1), 1);
        deck.addCard(new CardData("Plains", 1, 1), 2);

        given(magicDuelsDeckManager.getDeck(deckName)).willReturn(Optional.of(deck));

        // When
        String out = deckBrowser.browse(magicDuelsDeckManager, deckName);

        // Then
        assertThat(out).isEqualTo(
                "Deck " + deckName + " (3)" + "\n" +
                "  1 Mountain" + "\n" +
                "  2 Plains" + "\n"
        );
    }
}