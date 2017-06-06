package com.blueraja.magicduelsimporter.export.browser;

import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.magicduels.MagicDuelsDeckManager;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static com.blueraja.magicduelsimporter.Main.Modality.ASSIST_TO_DUELS;

public class ExporterBrowser {

    private static final String COMMAND_DECKS = "decks";
    private static final String COMMAND_DECK = "deck";

    public static void main(String[] args)
            throws IOException, SAXException, ParserConfigurationException, TransformerException {
        if (args.length < 1) {
            System.out.println(ASSIST_TO_DUELS + " <path-to-magic-duels-profile>");
            return;
        }

        if (args.length < 2) {
            System.out.println("Run one of the following commands:");
            System.out.println("  " + ASSIST_TO_DUELS + " <path-to-magic-duels-profile> " + COMMAND_DECKS);
            System.out.println("  " + ASSIST_TO_DUELS + " <path-to-magic-duels-profile> " + COMMAND_DECK + "<deck-name>");
            return;
        }

        String duelsProfilePath = args[0];
        String command = args[1];

        CardDataManager cardDataManager = new CardDataManager();
        cardDataManager.readXml();

        MagicDuelsDeckManager magicDuelsDeckManager = new MagicDuelsDeckManager(cardDataManager, duelsProfilePath);

        if (command.equals(COMMAND_DECKS)) {
            DecksBrowser browser = new DecksBrowser();
            String out = browser.browse(magicDuelsDeckManager);
            System.out.println(out);

        } else if (command.equals(COMMAND_DECK)) {
            if (args.length < 3) {
                System.out.println("Missing deck-name:");
                System.out.println("  " + ASSIST_TO_DUELS + " <path-to-magic-duels-profile> " + COMMAND_DECK + "<deck-name>");
                return;
            }

            DeckBrowser browser = new DeckBrowser();
            String out = browser.browse(magicDuelsDeckManager, args[2]);
            System.out.println(out);


        } else {
            System.err.println("Command " + command + " not valid.");
        }
    }
}
