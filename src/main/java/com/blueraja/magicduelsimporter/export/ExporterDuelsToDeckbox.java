package com.blueraja.magicduelsimporter.export;

import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.deckbox.DeckboxDeckManager;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.blueraja.magicduelsimporter.Main.Modality.DUELS_TO_DECKBOX;

public class ExporterDuelsToDeckbox {
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (args.length != 2) {
            System.out.println(DUELS_TO_DECKBOX + " <path-to-deckbox-out-file> <path-to-magic-duels-profile>");
            return;
        }

        String deckboxOutFilePath = args[0];
        String duelsProfilePath = args[1];

        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager, duelsProfilePath);
        Deck ownedCards = magicDuelsDeckManager.getOwnedCards();

        DeckboxDeckManager deckboxDeckManager = new DeckboxDeckManager(cardDataManager);
        deckboxDeckManager.writeCardsToFile(ownedCards, deckboxOutFilePath);

        System.out.println("Duels --> Deckbox completed successfully.");
        System.out.println("See file: " + deckboxOutFilePath);
    }
}