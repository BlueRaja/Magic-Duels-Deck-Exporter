package com.blueraja.magicduelsimporter.export;

import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.exceptions.DeckError;
import com.blueraja.magicduelsimporter.exceptions.InvalidDecksException;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicassist.MagicAssistDeckManager;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

public class ExporterAssistToDuels {
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        CardDataManager cardDataManager = new CardDataManager();
        MagicAssistDeckManager magicAssistDeckManager = new MagicAssistDeckManager(cardDataManager);
        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager);

        cardDataManager.readXml();
        try {
            Iterable<Deck> decks = magicAssistDeckManager.getDecks();
            magicDuelsDeckManager.writeDecks(decks);
        } catch (InvalidDecksException e) {
            System.out.println("FAILED - some decks are invalid: ");
            for(DeckError error: e.getDeckErrors()) {
                System.out.println(" Deck '" + error.getDeck().getName() + "' - card '"
                        + error.getCard().displayName + "' - " + error.getMessage());
            }
        }
    }
}
