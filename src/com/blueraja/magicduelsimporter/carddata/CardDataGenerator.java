package com.blueraja.magicduelsimporter.carddata;

import com.blueraja.magicduelsimporter.utils.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CardDataGenerator {
    private static class MagicAssistEntry {
        public String originalName;
        public String normalizedName;
        public int id;
    }

    public static Map<String, MagicAssistEntry> _nameToMagicAssistEntry = new HashMap<>();
    public static CardDataManager _cardDataManager = new CardDataManager();

    private static void LoadAllMagicAssistCards(String assistWorkspacePath)
            throws IOException, SAXException, ParserConfigurationException {
        String path = Paths.get(assistWorkspacePath, "magiccards/MagicDB").toAbsolutePath().toString();
        if(!new File(path).isDirectory()) {
            throw new IOException("Could not find directory " + path);
        }

        for (final File file : FileUtils.getAllFilesWithExtension(path, ".xml")) {
            LoadMagicAssistCardsForFile(file);
        }
    }

    private static void LoadMagicAssistCardsForFile(File file)
            throws ParserConfigurationException, IOException, SAXException {
        Document doc = FileUtils.getFileAsXMLDocument(file);
        NodeList nodes = doc.getElementsByTagName("mc");

        for(int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element)nodes.item(i);
            String name = element.getElementsByTagName("name").item(0).getTextContent();
            String idStr = element.getElementsByTagName("id").item(0).getTextContent();
            int id = Integer.parseInt(idStr);

            name = cleanupName(name);
            String normalizedName = normalizeName(name);

            if(!_nameToMagicAssistEntry.containsKey(normalizedName)) {
                MagicAssistEntry entry = new MagicAssistEntry();
                entry.id = id;
                entry.originalName = name;
                entry.normalizedName = normalizedName;
                _nameToMagicAssistEntry.put(normalizedName, entry);
            }
        }
    }

    private static void LoadMagicDuelsCardPool(String cardpoolPath)
            throws Exception {
        if(!new File(cardpoolPath).isFile()) {
            throw new IOException("Cardpool.xml cannot be found at " + cardpoolPath);
        }

        Document doc = FileUtils.getFileAsXMLDocument(cardpoolPath);
        NodeList cards = doc.getElementsByTagName("card");
        for(int i = 0; i < cards.getLength(); i++) {
            Element element = (Element)cards.item(i);
            String name = element.getAttribute("name");
            String idStr = element.getAttribute("id");

            String normalizedName = normalizeName(name);
            int id = Integer.parseInt(idStr);

            if(!_nameToMagicAssistEntry.containsKey(normalizedName)) {
                throw new Exception("Card is missing: " + name + " (" + normalizedName + ")");
            }
            MagicAssistEntry magicAssistEntry = _nameToMagicAssistEntry.get(normalizedName);
            _cardDataManager.addEntry(magicAssistEntry.originalName, id, magicAssistEntry.id);
        }
    }

    private static String cleanupName(String name) {
        //There is a card named "Anchor to the Æther"
        return name.replace("Æ", "Ae");
    }

    private static String normalizeName(String name) {
        name = cleanupName(name);
        return name.replaceAll("[^a-zA-Z]", "").toUpperCase();
    }

    public static void main(String[] args)
            throws Exception {
        if(args.length != 2) {
            System.out.println("Format: CardDataGenerator <path-to-magic-assist-workspace> <path-to-CardPool.xml>");
            return;
        }

        String assistWorkspacePath = args[0];
        String cardpoolPath = args[1];

        LoadAllMagicAssistCards(assistWorkspacePath);
        LoadMagicDuelsCardPool(cardpoolPath);
        _cardDataManager.writeXml();
    }
}
