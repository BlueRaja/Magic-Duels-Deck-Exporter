package com.blueraja.magicduelsimporter;

import com.blueraja.magicduelsimporter.export.ExporterAssistToDuels;
import com.blueraja.magicduelsimporter.export.ExporterDuelsToAssist;
import com.blueraja.magicduelsimporter.export.ExporterDuelsToDeckbox;

import java.util.Arrays;

import static com.blueraja.magicduelsimporter.Main.Modality.*;

public class Main {

    public enum Modality {
        ASSIST_TO_DUELS,
        DUELS_TO_ASSIST,
        DUELS_TO_DECKBOX
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
            case ASSIST_TO_DUELS:
                ExporterAssistToDuels.main(otherArguments);
                break;

            case DUELS_TO_ASSIST:
                ExporterDuelsToAssist.main(otherArguments);
                break;

            case DUELS_TO_DECKBOX:
                ExporterDuelsToDeckbox.main(otherArguments);
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
                ASSIST_TO_DUELS + " <path-to-magic-assist-workspace> <path-to-magic-duels-profile>" + "\n" +
                DUELS_TO_ASSIST + " <path-to-magic-assist-workspace> <path-to-magic-duels-profile>" + "\n" +
                DUELS_TO_DECKBOX + " <path-to-deckbox-out-file> <path-to-magic-duels-profile>" + "\n" +
                "\n"
        );
    }

    private static void error(String message) {
        System.err.println("\nERROR: " + message + "\n");
        System.exit(-1);
    }

}
