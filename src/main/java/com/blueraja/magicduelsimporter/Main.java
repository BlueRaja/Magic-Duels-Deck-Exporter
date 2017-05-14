package com.blueraja.magicduelsimporter;

import com.blueraja.magicduelsimporter.export.*;
import com.blueraja.magicduelsimporter.export.browser.ExporterBrowser;

import java.util.Arrays;

import static com.blueraja.magicduelsimporter.Main.Modality.*;

public class Main {

    public enum Modality {
        BROWSE,
        ASSIST_TO_DUELS,
        DUELS_TO_ASSIST,
        DUELS_TO_DECKBOX,
        DECKBOX_TO_DUELS
    }


    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            printUsage();
        }

        if (args.length >= 1) {
            try {
                Modality.valueOf(args[0]);
            } catch (IllegalArgumentException e) {
                error(
                        "MODALITY " + args[0] + " Not found.\n"+
                        "Available modalities are: " + Arrays.asList(Modality.values())
                );
            }
        }

        String[] otherArguments = Arrays.copyOfRange(args, 1, args.length);
        switch (Modality.valueOf(args[0])) {
            case BROWSE:
                ExporterBrowser.main(otherArguments);
                break;

            case ASSIST_TO_DUELS:
                ExporterAssistToDuels.main(otherArguments);
                break;

            case DUELS_TO_ASSIST:
                ExporterDuelsToAssist.main(otherArguments);
                break;

            case DUELS_TO_DECKBOX:
                ExporterDuelsToDeckbox.main(otherArguments);
                break;

            case DECKBOX_TO_DUELS:
                ExporterDeckboxToDuels.main(otherArguments);
                break;
        }

    }

    private static void printUsage() {
        System.out.println(
                "\n" +
                "magic-duels-deck-exporter.jar MODALITY options" + "\n" +
                "\n" +
                "Available modalities are: " + Arrays.asList(Modality.values()) + "\n" +
                "\n" +
                BROWSE + " <path-to-magic-duels-profile>" + "\n" +
                ASSIST_TO_DUELS + " <path-to-magic-assist-workspace> <path-to-magic-duels-profile>" + "\n" +
                DUELS_TO_ASSIST + " <path-to-magic-assist-workspace> <path-to-magic-duels-profile>" + "\n" +
                DUELS_TO_DECKBOX + " <path-to-deckbox-out-file> <path-to-magic-duels-profile>" + "\n" +
                DECKBOX_TO_DUELS + " <path-to-deckbox-deck-file> <path-to-magic-duels-profile>" + "\n" +
                "\n"
        );
    }

    private static void error(String message) {
        System.err.println("\nERROR: " + message + "\n");
        System.exit(-1);
    }

}
