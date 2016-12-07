package com.blueraja.magicduelsimporter.carddata;

import com.blueraja.magicduelsimporter.utils.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Main {
    private static class MagicAssistEntry {
        public String originalName;
        public String normalizedName;
        public int id;
    }

    public static final String MAGIC_ASSIST_DB_PATH = "C:\\Users\\BlueRaja\\Documents\\MagicAssistantWorkspace\\magiccards";
    public static final String MAGIC_DUELS_CARD_POOL = "C:\\Users\\BlueRaja\\Downloads\\CardPool.xml";
    public static Map<String, MagicAssistEntry> _nameToMagicAssistEntry = new HashMap<>();
    public static CardDataManager _cardDataManager = new CardDataManager();

    private static void LoadAllMagicAssistCards()
            throws IOException, SAXException, ParserConfigurationException {
        String path = MAGIC_ASSIST_DB_PATH + "/MagicDB";
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

    private static void LoadMagicDuelsCardPool()
            throws Exception {
        Document doc = FileUtils.getFileAsXMLDocument(MAGIC_DUELS_CARD_POOL);
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

    public static String normalizeName(String name) {
        //There is a card named "Anchor to the Æther"
        name = name.replace("Æ", "AE");

        return name.replaceAll("[^a-zA-Z]", "").toUpperCase();
    }

    public static void main(String[] args)
            throws Exception {
        LoadAllMagicAssistCards();
        LoadMagicDuelsCardPool();
        _cardDataManager.writeXml();
    }
}
