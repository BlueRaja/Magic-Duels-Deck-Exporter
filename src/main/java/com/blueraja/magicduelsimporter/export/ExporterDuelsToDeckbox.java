package com.blueraja.magicduelsimporter.export;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicassist.MagicAssistDeckManager;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.blueraja.magicduelsimporter.Main.Modality.DUELS_TO_DECKBOX;

public class ExporterDuelsToDeckbox {
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (args.length != 1) {
            System.out.println(DUELS_TO_DECKBOX + " <path-to-magic-duels-profile>");
            return;
        }

        String duelsProfilePath = args[0];

        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager, duelsProfilePath);
        Deck ownedCards = magicDuelsDeckManager.getOwnedCards();


        for (CardData cardData : ownedCards.getCards()) {
            System.out.println(ownedCards.getCardCount(cardData) + " " + cardData.getDisplayName());
        }

        System.out.println("Duels --> Deckbox completed successfully");
    }
}