package com.blueraja.magicduelsimporter.magicduels;
// @author Spirolone
import java.io.*;

public class MagicDuelsDeck {
    public static final int LAND_PLAINS = 0;
    public static final int LAND_ISLANDS = 1;
    public static final int LAND_SWAMPS = 2;
    public static final int LAND_MOUNTAINS = 3;
    public static final int LAND_FORESTS = 4;

    public float[] values;
    public String name;
    public int[][] cards;
    public byte[] lands;
    public byte icon1;
    public byte icon2;
    public int archetype;
    public byte[] cardsCosts;
    public byte deckNumber;
    public int onlineDeckNumber;
    
    public MagicDuelsDeck() {
        values = new float[4];
        cards = new int[4][100];
        lands = new byte[5];
        cardsCosts = new byte[7];
        archetype = -1;
    }
}