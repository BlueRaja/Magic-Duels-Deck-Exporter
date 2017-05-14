package com.blueraja.magicduelsimporter.export;

import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.deckbox.DeckboxDeckManager;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import com.blueraja.magicduelsimporter.utils.FileUtils;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.blueraja.magicduelsimporter.Main.Modality.DUELS_TO_DECKBOX;

public class ExporterDeckboxToDuels {
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (args.length != 2) {
            System.out.println(DUELS_TO_DECKBOX + " <path-to-deckbox-deck-file> <path-to-magic-duels-profile>");
            return;
        }

        String deckboxDeckFilePath = args[0];
        String duelsProfilePath = args[1];

        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        DeckboxDeckManager deckboxDeckManager = new DeckboxDeckManager(cardDataManager);
        Deck deckboxDeck = deckboxDeckManager.loadDeck(deckboxDeckFilePath);

        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager, duelsProfilePath);

        magicDuelsDeckManager.writeDeck(deckboxDeck);

        System.out.println("Deckbox --> Duels completed successfully.");
        System.out.println("Deck '" + FileUtils.getBaseName(deckboxDeckFilePath) + "' successfully created/updated.");
    }
}