package com.blueraja.magicduelsimporter.magicduels;
// @author Spirolone
import java.io.*;
import java.nio.file.Files;

public class Profile_iOS {

    public File profileFile;
    public byte[] content;
    
    public Profile_iOS(File profileFile) throws FileNotFoundException, IOException {
        this.profileFile = profileFile;
        byte[] fileContent = new byte[(int)profileFile.length()];
        RandomAccessFile profileRAFile = new RandomAccessFile(profileFile, "r");
        profileRAFile.read(fileContent);
        profileRAFile.close();
        for (int i=fileContent.length-1; i>=1; i--)
            fileContent[i] ^= fileContent[i-1];
        fileContent[0] ^= fileContent[fileContent.length-1];
        content = new byte[fileContent.length-28];
        System.arraycopy(fileContent, 0, content, 0, 0xC44);
        System.arraycopy(fileContent, 0xC60, content, 0xC44, fileContent.length-0xC60);
        int fieldLen = (content[0x85C] & 0xFF) + (((content[0x85D] ^ content[0x85C]) & 0xFF)<<8);
        for (int i=0x85B+fieldLen; i>=0x85D; i--)
            content[i] ^= content[i-1];
        content[0x85C] ^= content[0x85B+fieldLen];
    }
    
    public byte[] readCards() {
        byte[] cards = new byte[1024];
        int offset = (content[0x860] & 0xFF) + ((content[0x861] & 0xFF)<<8) + 0x8E2;
        for (int i=0; i<512; i++) {
            cards[2*i] = (byte)(content[offset+i] & 0x0F);
            cards[2*i+1] = (byte)((content[offset+i]>>4) & 0x0F);
        }
        return cards;
    }
    
    public void writeCards(byte[] cards) {
        int offset = (content[0x860] & 0xFF) + ((content[0x861] & 0xFF)<<8) + 0x8E2;
        for (int i=0; i<(cards.length/2); i++)
            content[offset+i] = (byte)((cards[2*i] & 0x0F) + ((cards[2*i+1]<<4) & 0xF0));
    }
    
    public Deck readDeck(int deckPos) {
        int offset = 0x16C8 + (536*deckPos);
        Deck myDeck = new Deck();
        byte[] deckContent = new byte[60];
        System.arraycopy(content, offset, deckContent, 0, 60);
        char[] deckName = new char[15];
        for (int i=0; i<15; i++)
            deckName[i] = (char)(deckContent[i*4] & 0xFF);
        myDeck.name = (new String(deckName)).trim();
        deckContent = new byte[400];
        System.arraycopy(content, offset+64, deckContent, 0, 400);
        int card;
        for (int i=0; i<100; i++) {
            card = (deckContent[i*4+2] & 0xFF) + ((deckContent[i*4+3] & 0xFF)<<8);
            myDeck.cards[0][i] = card>>3;
            myDeck.cards[1][i] = card & 0x07;
            myDeck.cards[2][i] = deckContent[i*4];
            myDeck.cards[3][i] = deckContent[i*4+1];
        }
        for (int i=0; i<5; i++)
            myDeck.lands[i] = content[i*4+offset+464];
        myDeck.icon1 = content[offset+504];
        myDeck.icon2 = content[offset+505];
        myDeck.archetype = (content[offset+506] & 0xFF) + ((content[offset+507] & 0xFF)<<8);
        if (myDeck.archetype > 0x7FFF)
            myDeck.archetype -= 0x10000;
        myDeck.deckNumber = content[offset+516];
        myDeck.onlineDeckNumber  = content[offset+517];
        return myDeck;
    }
    
    public void writeDeck(Deck myDeck, int deckPos) {
        int offset = 0x16C8 + (536*deckPos);
        byte[] deckContent;
        char[] deckName = myDeck.name.toCharArray();
        deckContent = new byte[60];
        for (int i=0; i<deckName.length; i++)
            deckContent[i*4] = (byte)(deckName[i] & 0xFF);
        System.arraycopy(deckContent, 0, content, offset, 60);
        deckContent = new byte[400];
        int card;
        for (int i=0; i<100; i++) {
            card = (myDeck.cards[0][i]<<3) + myDeck.cards[1][i];
            deckContent[i*4] = (byte)myDeck.cards[2][i];
            deckContent[i*4+1] = (byte)myDeck.cards[3][i];
            deckContent[i*4+2] = (byte)(card & 0xFF);
            deckContent[i*4+3] = (byte)((card>>8) & 0xFF);
        }
        System.arraycopy(deckContent, 0, content, offset+64, 400);
        for (int i=0; i<5; i++)
            content[i*4+offset+464] = myDeck.lands[i];
        content[offset+504] = myDeck.icon1;
        content[offset+505] = myDeck.icon2;
        if (myDeck.archetype < 0)
            myDeck.archetype += 0x10000;
        content[offset+506] = (byte)(myDeck.archetype & 0xFF);
        content[offset+507] = (byte)((myDeck.archetype>>8) & 0xFF);
        content[offset+516] = myDeck.deckNumber;
        content[offset+517] = (byte)myDeck.onlineDeckNumber;
    }
    
    public void save() throws FileNotFoundException, IOException {
        byte[] contentTemp = new byte[content.length];
        byte[] fileContent = new byte[content.length+28];
        System.arraycopy(content, 0, contentTemp, 0, content.length);
        int fieldLen = (content[0x85C] & 0xFF) + ((content[0x85D] & 0xFF)<<8);
        contentTemp[0x85C] ^= contentTemp[0x85B+fieldLen];
        for (int i=0x85D; i<=0x85B+fieldLen; i++)
            contentTemp[i] ^= contentTemp[i-1];
        System.arraycopy(contentTemp, 0, fileContent, 0, 0xC44);
        System.arraycopy(contentTemp, 0xC44, fileContent, 0xC60, fileContent.length-0xC60);
        fileContent[0xC5C] = (byte)0xE8; fileContent[0xC5D] = (byte)0x03;
        fileContent[0] ^= fileContent[fileContent.length-1];
        for (int i=1; i<=fileContent.length-1; i++)
            fileContent[i] ^= fileContent[i-1];
        File bakFile = new File(profileFile.toString()+".bak");
        bakFile.delete();
        Files.move(profileFile.toPath(), bakFile.toPath());
        RandomAccessFile profileRAFile = new RandomAccessFile(profileFile, "rw");
        profileRAFile.write(fileContent);
        profileRAFile.close();
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
                fileName = "p1.profile";
            File profileFile = new File(fileName);
            Profile_iOS myProfile = new Profile_iOS(profileFile);
            myProfile.exportProfile(profileFile.toString()+".bin");
            byte[] cards = myProfile.readCards();
            (new RandomAccessFile("Cards.bin", "rw")).write(cards);
//            (new RandomAccessFile("Cards.bin", "r")).read(cards);
//            myProfile.writeCards(cards);
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
//            Deck myDeck = myProfile.readDeck(0);
//            myProfile.writeDeck(myDeck, 0);
//            myProfile.importProfile(profileFile.toString()+".bin");
            myProfile.save();
        }
        catch (Exception e){
            System.out.printf("Error");
        }
    }
    
}
