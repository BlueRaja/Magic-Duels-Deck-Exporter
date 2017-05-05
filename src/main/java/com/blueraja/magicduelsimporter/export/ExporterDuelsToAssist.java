package com.blueraja.magicduelsimporter.export;

import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.magicassist.Deck;
import com.blueraja.magicduelsimporter.magicassist.MagicAssistDeckManager;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.blueraja.magicduelsimporter.Main.Modality.DUELS_TO_ASSIST;

public class ExporterDuelsToAssist {
    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (args.length != 2) {
            System.out.println(DUELS_TO_ASSIST + " <path-to-magic-assist-workspace> <path-to-magic-duels-profile>");
            return;
        }

        String assistWorkspacePath = args[0];
        String duelsProfilePath = args[1];

        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        MagicAssistDeckManager magicAssistDeckManager = new MagicAssistDeckManager(cardDataManager, assistWorkspacePath);
        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager, duelsProfilePath);

        Deck ownedCards = magicDuelsDeckManager.getOwnedCards();
        magicAssistDeckManager.writeDeckToMagicAssistDeckFile(ownedCards, true);

        magicAssistDeckManager.deleteAllMagicAssistDeckFiles();
        for(Deck deck: magicDuelsDeckManager.getDecks()) {
            magicAssistDeckManager.writeDeckToMagicAssistDeckFile(deck, false);
        }

        System.out.println("Duels --> Assistant completed successfully");
    }
}