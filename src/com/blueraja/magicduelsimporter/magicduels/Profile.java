package com.blueraja.magicduelsimporter.magicduels;
// @author Spirolone
import java.io.*;
import java.nio.*;
import java.nio.file.Files;

public class Profile {

    public File profileFile;
    public byte[] content;
    
    public Profile(File profileFile) throws FileNotFoundException, IOException {
        this.profileFile = profileFile;
        byte[] fileContent = new byte[(int)profileFile.length()];
        RandomAccessFile profileRAFile = new RandomAccessFile(profileFile, "r");
        profileRAFile.read(fileContent);
        profileRAFile.close();
        for (int i=fileContent.length-1; i>=1; i--)
            fileContent[i] ^= fileContent[i-1];
        fileContent[0] ^= fileContent[fileContent.length-1];
        content = new byte[fileContent.length-28];
        System.arraycopy(fileContent, 0, content, 0, 0x842);
        System.arraycopy(fileContent, 0x85E, content, 0x842, fileContent.length-0x85E);
        int fieldLen = (content[0x45A] & 0xFF) + (((content[0x45B] ^ content[0x45A]) & 0xFF)<<8);
        for (int i=0x459+fieldLen; i>=0x45B; i--)
            content[i] ^= content[i-1];
        content[0x45A] ^= content[0x459+fieldLen];
    }
    
    public byte[] readCards() {
        byte[] cards = new byte[1024];
        int offset = (content[0x45E] & 0xFF) + ((content[0x45F] & 0xFF)<<8) + 0x4E0;
        for (int i=0; i<512; i++) {
            cards[2*i] = (byte)(content[offset+i] & 0x0F);
            cards[2*i+1] = (byte)((content[offset+i]>>4) & 0x0F);
        }
        return cards;
    }
    
    public void writeCards(byte[] cards) {
        int offset = (content[0x45E] & 0xFF) + ((content[0x45F] & 0xFF)<<8) + 0x4E0;
        for (int i=0; i<(cards.length/2); i++)
            content[offset+i] = (byte)((cards[2*i] & 0x0F) + ((cards[2*i+1]<<4) & 0xF0));
    }
    
    public Deck readDeck(int deckPos) {
        int offset = 0x122C + (504*deckPos);
        Deck myDeck = new Deck();
        byte[] deckContent = new byte[4];
        for (int i=0; i<4; i++) {
            System.arraycopy(content, i*4+offset, deckContent, 0, 4);
            myDeck.values[i] = ByteBuffer.wrap(deckContent).order(ByteOrder.LITTLE_ENDIAN).getFloat();
        }
        deckContent = new byte[30];
        System.arraycopy(content, offset+16, deckContent, 0, 30);
        char[] deckName = new char[15];
        for (int i=0; i<15; i++)
            deckName[i] = (char)((deckContent[i*2] & 0xFF) + ((deckContent[i*2+1] & 0xFF)<<8));
        myDeck.name = (new String(deckName)).trim();
        deckContent = new byte[400];
        System.arraycopy(content, offset+48, deckContent, 0, 400);
        int card;
        for (int i=0; i<100; i++) {
            card = (deckContent[i*4+2] & 0xFF) + ((deckContent[i*4+3] & 0xFF)<<8);
            myDeck.cards[0][i] = card>>3;
            myDeck.cards[1][i] = card & 0x07;
            myDeck.cards[2][i] = deckContent[i*4];
            myDeck.cards[3][i] = deckContent[i*4+1];
        }
        for (int i=0; i<5; i++)
            myDeck.lands[i] = content[i*4+offset+448];
        myDeck.icon1 = content[offset+488];
        myDeck.icon2 = content[offset+489];
        myDeck.archetype = (content[offset+490] & 0xFF) + ((content[offset+491] & 0xFF)<<8);
        if (myDeck.archetype > 0x7FFF)
            myDeck.archetype -= 0x10000;
        for (int i=0; i<7; i++)
            myDeck.cardsCosts[i] = content[i+offset+492];
        myDeck.deckNumber = content[offset+499];
        deckContent = new byte[4];
        System.arraycopy(content, offset+500, deckContent, 0, 4);
        myDeck.onlineDeckNumber = (int)bytesToNum(deckContent, 4);
        return myDeck;
    }
    
    public void writeDeck(Deck myDeck, int deckPos) {
        int offset = 0x122C + (504*deckPos);
        byte[] deckContent;
        for (int i=0; i<4; i++) {
            deckContent = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(myDeck.values[i]).array();
            System.arraycopy(deckContent, 0, content, i*4+offset, 4);
        }
        char[] deckName = myDeck.name.toCharArray();
        deckContent = new byte[30];
        for (int i=0; i<deckName.length; i++) {
            deckContent[i*2] = (byte)(deckName[i] & 0xFF);
            deckContent[i*2+1] = (byte)((deckName[i]>>8) & 0xFF);
        }
        System.arraycopy(deckContent, 0, content, offset+16, 30);
        deckContent = new byte[400];
        int card;
        for (int i=0; i<100; i++) {
            card = (myDeck.cards[0][i]<<3) + myDeck.cards[1][i];
            deckContent[i*4] = (byte)myDeck.cards[2][i];
            deckContent[i*4+1] = (byte)myDeck.cards[3][i];
            deckContent[i*4+2] = (byte)(card & 0xFF);
            deckContent[i*4+3] = (byte)((card>>8) & 0xFF);
        }
        System.arraycopy(deckContent, 0, content, offset+48, 400);
        for (int i=0; i<5; i++)
            content[i*4+offset+448] = myDeck.lands[i];
        content[offset+488] = myDeck.icon1;
        content[offset+489] = myDeck.icon2;
        if (myDeck.archetype < 0)
            myDeck.archetype += 0x10000;
        content[offset+490] = (byte)(myDeck.archetype & 0xFF);
        content[offset+491] = (byte)((myDeck.archetype>>8) & 0xFF);
        for (int i=0; i<7; i++)
            content[i+offset+492] = myDeck.cardsCosts[i];
        content[offset+499] = myDeck.deckNumber;
        deckContent = numToBytes(myDeck.onlineDeckNumber, 4);
        System.arraycopy(deckContent, 0, content, offset+500, 4);
    }
    
    public void save() throws FileNotFoundException, IOException {
        byte[] contentTemp = new byte[content.length];
        byte[] fileContent = new byte[content.length+28];
        System.arraycopy(content, 0, contentTemp, 0, content.length);
        int fieldLen = (content[0x45A] & 0xFF) + ((content[0x45B] & 0xFF)<<8);
        contentTemp[0x45A] ^= contentTemp[0x459+fieldLen];
        for (int i=0x45B; i<=0x459+fieldLen; i++)
            contentTemp[i] ^= contentTemp[i-1];
        System.arraycopy(contentTemp, 0, fileContent, 0, 0x842);
        System.arraycopy(contentTemp, 0x842, fileContent, 0x85E, fileContent.length-0x85E);
        fileContent[0x85A] = (byte)0xE8; fileContent[0x85B] = (byte)0x03;
        fileContent[0] ^= fileContent[fileContent.length-1];
        for (int i=1; i<=fileContent.length-1; i++)
            fileContent[i] ^= fileContent[i-1];
        File bakFile = new File(profileFile.toString()+".bak");
        bakFile.delete();
        Files.move(profileFile.toPath(), bakFile.toPath());
        RandomAccessFile profileRAFile = new RandomAccessFile(profileFile, "rw");
        profileRAFile.write(fileContent);
        profileRAFile.close();
        // Compute Hash
        long hash = 0x811C9DC5L;
        for (int i=0; i<fileContent.length; i++) {
            hash = (hash * 0x1000193) & 0xFFFFFFFFL;
            hash ^= fileContent[i] & 0xFF;
        }
        RandomAccessFile hashRAFile = new RandomAccessFile("hash.file", "rw");
        hashRAFile.write(numToBytes(hash, 4));
        hashRAFile.close();
    }
    
    public void exportProfile(String fileName) throws FileNotFoundException, IOException {
        File profileContentFile = new File(fileName);
        File bakFile = new File(fileName+".bak");
        bakFile.delete();
        if (profileContentFile.exists())
            Files.move(profileContentFile.toPath(), bakFile.toPath());
        RandomAccessFile profileContentRAFile = new RandomAccessFile(profileContentFile, "rw");
        profileContentRAFile.write(content);
        profileContentRAFile.close();
    }
    
    public void importProfile(String fileName) throws FileNotFoundException, IOException {
        File profileContentFile = new File(fileName);
        RandomAccessFile profileContentRAFile = new RandomAccessFile(profileContentFile, "r");
        content = new byte[(int)profileContentFile.length()];
        profileContentRAFile.read(content);
        profileContentRAFile.close();
    }
    
    public Deck importDeck(String deckFileName, String cardPoolsFileName) throws FileNotFoundException, IOException {
        String cardName;
        byte cardNum;
        String[] cardsNames = new String[100];
        byte[] cardsNums = new byte[100];
        // Create New Deck
        Deck myDeck = new Deck();
        myDeck.name = deckFileName.substring(0, deckFileName.length()-4);
        if (myDeck.name.length() > 15)
            myDeck.name = myDeck.name.substring(0, 15);
        // Read XML File with content of Deck
        BufferedReader in = new BufferedReader(new FileReader(deckFileName));
        int i = 0; int pt = 0;
        String line = in.readLine();
        while (line != null) {
            while ((line != null) && ((pt = line.indexOf("<card>", pt)) == -1))
                line = in.readLine();
            while ((line != null) && ((pt = line.indexOf("<name>", pt)) == -1))
                line = in.readLine();
            if (line != null) {
                pt += 6;
                cardName = line.substring(pt, line.indexOf("</name>", pt));
                cardName = cardName.replace(" ", "_").replace("'", "").replace("-", "").replace(",", "").replace("Æ", "AE").toUpperCase();
                pt += cardName.length() + 7;
                while ((line != null) && ((pt = line.indexOf("<count>", pt)) == -1))
                    line = in.readLine();
                pt += 7;
                cardNum = (byte)Integer.parseInt(line.substring(pt, line.indexOf("</count>", pt)));
                if (cardName.equals("PLAINS"))
                    myDeck.lands[0] = cardNum;
                else if (cardName.equals("ISLAND"))
                    myDeck.lands[1] = cardNum;
                else if (cardName.equals("SWAMP"))
                    myDeck.lands[2] = cardNum;
                else if (cardName.equals("MOUNTAIN"))
                    myDeck.lands[3] = cardNum;
                else if (cardName.equals("FOREST"))
                    myDeck.lands[4] = cardNum;
                else {
                    cardsNames[i] = cardName;
                    cardsNums[i] = cardNum;
                    i++;
                }
            }
        }
        in.close();
        in = new BufferedReader(new FileReader(cardPoolsFileName));
        while ((line = in.readLine()) != null) {
            for (i=0; ((i < 100) && (cardsNames[i] != null)); i++)
                if ((pt = line.indexOf(cardsNames[i])) != -1) {
                    pt = line.indexOf("id=\"", pt+cardsNames[i].length()) + 4;
                    myDeck.cards[0][i] = Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
                    myDeck.cards[1][i] = cardsNums[i];
                }
        }
        in.close();
        return myDeck;
    }
    
    public Deck importDeck_(String deckFileName, String cardPoolsFileName) throws FileNotFoundException, IOException {
        int pt, i, j;
        String line;
        String cardName;
        String[] cardsNames = new String[100];
        byte[] cardsNums = new byte[100];
        // Create New Deck
        Deck myDeck = new Deck();
        myDeck.name = deckFileName.substring(0, deckFileName.length()-4);
        if (myDeck.name.length() > 15)
            myDeck.name = myDeck.name.substring(0, 15);
        // Read XML File with content of Deck
        BufferedReader in = new BufferedReader(new FileReader(deckFileName));
        i = 0;
        while ((line = in.readLine()) != null) {
            pt = 0;
            while ((pt = line.indexOf("<CARD name=\"", pt)) != -1) {
                pt += 12;
                cardName = line.substring(pt, line.indexOf("\"", pt)-7);
                pt += cardName.length();
                j = 0;
                while ((j<i) && !(cardName.equalsIgnoreCase(cardsNames[j])))
                    j++;
                if (j == i) {
                    cardsNames[i] = cardName;
                    cardsNums[i] = 1;
                    i++;
                } else
                    cardsNums[j]++;
            }
            if ((pt = line.indexOf("minPlains=\"")) != -1) {
                pt += 11;
                myDeck.lands[0] = (byte)Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
            }
            if ((pt = line.indexOf("minIsland=\"")) != -1) {
                pt += 11;
                myDeck.lands[1] = (byte)Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
            }
            if ((pt = line.indexOf("minSwamp=\"")) != -1) {
                pt += 10;
                myDeck.lands[2] = (byte)Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
            }
            if ((pt = line.indexOf("minMountain=\"")) != -1) {
                pt += 13;
                myDeck.lands[3] = (byte)Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
            }
            if ((pt = line.indexOf("minForest=\"")) != -1) {
                pt += 11;
                myDeck.lands[4] = (byte)Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
            }
        }
        in.close();
        in = new BufferedReader(new FileReader(cardPoolsFileName));
        while ((line = in.readLine()) != null) {
            i = 0;
            while (cardsNames[i] != null) {
                if ((pt = line.indexOf(cardsNames[i])) != -1) {
                    pt = line.indexOf("id=\"", pt+cardsNames[i].length()) + 4;
                    myDeck.cards[0][i] = Integer.parseInt(line.substring(pt, line.indexOf("\"", pt)));
                    myDeck.cards[1][i] = cardsNums[i];
                }
                i++;
            }
        }
        in.close();
        return myDeck;
    }
    
    static byte[] numToBytes(long num, int dim) {
        byte [] bytesArray = new byte[dim];
        for (int i=0; i<dim; i++)
            bytesArray[i] = (byte)((num>>(8*i)) & 0xFF);
        return bytesArray;
    }
    
    static long bytesToNum(byte[] bytesArray, int dim) {
        long num = 0;
        for (int i=0; i<dim; i++)
            num += (bytesArray[i] & 0xFF)<<(8*i);
        return num;
    }
    
    public static void main(String[] args) {
        try {
            String fileName;
            if (args.length > 0)
                fileName = args[0];
            else
                fileName = "1.profile";
            File profileFile = new File(fileName);
            Profile myProfile = new Profile(profileFile);
            myProfile.exportProfile(profileFile.toString()+".bin");
            byte[] cards = myProfile.readCards();
            (new RandomAccessFile("Cards.bin", "rw")).write(cards);
//            (new RandomAccessFile("Cards.bin", "r")).read(cards);
//            myProfile.writeCards(cards);
//            Deck myDeck = myProfile.readDeck(0);
//            Deck myDeck = myProfile.importDeck("Test.xml", "CardPools.xml");
//            myProfile.writeDeck(myDeck, 0);
//            myProfile.importProfile(profileFile.toString()+".bin");
            System.setOut(new PrintStream(new File("Decks.txt")));
            for (int deckPos=0; deckPos<32; deckPos++) {
                Deck myDeck = myProfile.readDeck(deckPos);
                int offset = (myProfile.content[0x45E] & 0xFF) + ((myProfile.content[0x45F] & 0xFF)<<8) + 0x115C + (504*deckPos);
                byte[] deckContent = new byte[504];
                System.arraycopy(myProfile.content, offset, deckContent, 0, deckContent.length);
                (new RandomAccessFile("Deck_"+deckPos+".bin", "rw")).write(deckContent);
                System.out.printf(myDeck.name+":\r\n");
                System.out.printf("Speed: "+myDeck.values[0]+"\r\n");
                System.out.printf("Strength: "+myDeck.values[1]+"\r\n");
                System.out.printf("Control: "+myDeck.values[2]+"\r\n");
                System.out.printf("Synergy: "+myDeck.values[3]+"\r\n");
                for (int i=0; i<100; i++)
                    if (myDeck.cards[1][i] > 0)
                        System.out.printf("Card: "+myDeck.cards[0][i]+" x"+myDeck.cards[1][i]+"\r\n");
                System.out.printf("Plains: "+myDeck.lands[0]+"\r\n");
                System.out.printf("Islands: "+myDeck.lands[1]+"\r\n");
                System.out.printf("Swamps: "+myDeck.lands[2]+"\r\n");
                System.out.printf("Mountains: "+myDeck.lands[3]+"\r\n");
                System.out.printf("Forests: "+myDeck.lands[4]+"\r\n");
                System.out.printf("Icon1: "+myDeck.icon1+"\r\n");
                System.out.printf("Icon2: "+myDeck.icon2+"\r\n");
                System.out.printf("Archetype: "+myDeck.archetype+"\r\n");
                for (int i=0; i<7; i++)
                    System.out.printf("Cards with cost "+i+": "+myDeck.cardsCosts[i]+"\r\n");
                System.out.printf("Deck Number: "+myDeck.deckNumber+"\r\n");
                System.out.printf("Online Deck Number: "+myDeck.onlineDeckNumber+"\r\n");
                System.out.printf("\r\n\r\n");
            }

            System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out)));

            //TODO: Delete
            //Write a new deck
            Deck myDeck = myProfile.readDeck(3);
            System.out.println("Hello " + myDeck.cards[0][5]);
            for(int i = 0; i < 100; i++) {
                if(myDeck.cards[1][i] > 0) {
                    myDeck.cards[0][i] = 573;
                }
            }
            myProfile.writeDeck(myDeck, 3);
            myProfile.save();


        }
        catch (Exception e){
            System.out.printf("Error");
        }
    }
    
}
