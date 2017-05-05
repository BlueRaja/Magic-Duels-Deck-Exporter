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

import static com.blueraja.magicduelsimporter.Main.Modality.ASSIST_TO_DUELS;

public class ExporterAssistToDuels {
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (args.length != 2) {
            System.out.println(ASSIST_TO_DUELS + " <path-to-magic-assist-workspace> <path-to-magic-duels-profile>");
            return;
        }

        String assistWorkspacePath = args[0];
        String duelsProfilePath = args[1];

        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        MagicAssistDeckManager magicAssistDeckManager = new MagicAssistDeckManager(cardDataManager, assistWorkspacePath);
        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager, duelsProfilePath);

        try {
            Iterable<Deck> decks = magicAssistDeckManager.getDecks();
            magicDuelsDeckManager.writeDecks(decks);
            System.out.println("Assistant --> Duels completed successfully");
        } catch (InvalidDecksException e) {
            System.out.println("FAILED - some decks are invalid: ");
            for(DeckError error: e.getDeckErrors()) {
                System.out.println(" Deck '" + error.getDeck().getName()
                        + (error.getCard() != null ? "' - card '" + error.getCard().displayName : "")
                        + "' - " + error.getMessage());
            }
        }
    }
}
