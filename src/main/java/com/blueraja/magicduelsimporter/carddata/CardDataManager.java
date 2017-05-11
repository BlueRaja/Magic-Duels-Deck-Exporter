package com.blueraja.magicduelsimporter.carddata;

import com.blueraja.magicduelsimporter.utils.FileUtils;
import com.blueraja.magicduelsimporter.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CardDataManager {
    public static abstract class Lands {

        public static final CardData FOREST = new CardData("Forest", -1, 383241);
        public static final CardData ISLAND = new CardData("Island", -2, 383281);
        public static final CardData MOUNTAIN = new CardData("Mountain", -3, 383315);
        public static final CardData PLAINS = new CardData("Plains", -4, 383346);
        public static final CardData SWAMP = new CardData("Swamp", -5, 383408);
    }
    public List<CardData> _cardEntries = new ArrayList<>();

    public void addEntry(String name, int idMagicDuels, int idMagicAssist) {
        _cardEntries.add(new CardData(name, idMagicDuels, idMagicAssist));
    }

    public void clear() {
        _cardEntries.clear();
    }

    public List<CardData> getAllCards() {
        return _cardEntries;
    }

    public List<CardData> getAllCardsWithBasicLands() {
        List<CardData> allCardsAndLands = new ArrayList<>();
        allCardsAndLands.addAll(getAllLands());
        allCardsAndLands.addAll(getAllCards());
        return allCardsAndLands;
    }

    public CardData getCard(String cardName) {
        for (CardData cardEntry : getAllCardsWithBasicLands()) {
            if (cardEntry.getDisplayName().equals(cardName)) {
                return cardEntry;
            }
        }
        throw new RuntimeException("Card " + cardName + " not found.");
    }

    public List<CardData> getAllLands() {
        return Arrays.asList(new CardData[] {Lands.FOREST, Lands.ISLAND, Lands.MOUNTAIN, Lands.PLAINS, Lands.SWAMP});
    }

    public boolean isLand(CardData cardData) {
        for(CardData land: getAllLands()) {
            if(land.equals(cardData)) {
                return true;
            }
        }
        return false;
    }

    public Optional<CardData> getDataForMagicDuelsId(int id) {
        return _cardEntries.stream()
                .filter(card -> card.idMagicDuels == id)
                .findFirst();
    }

    public Optional<CardData> getDataForMagicAssistId(int id) {
        Optional<CardData> landCard = getAllLands().stream()
                .filter(card -> card.idMagicAssist == id)
                .findFirst();
        if(landCard.isPresent()) {
            return landCard;
        }

        return _cardEntries.stream()
                .filter(card -> card.idMagicAssist == id)
                .findFirst();
    }

    public void writeXml() throws IOException, ParserConfigurationException, TransformerException {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream("CardData.xml"), "utf-8"))) {
            writer.write(toXmlString());
        }
    }

    public void readXml() throws ParserConfigurationException, IOException, SAXException {
        this.clear();

        Document doc = FileUtils.getFileAsXMLDocument("CardData.xml");
        NodeList cards = doc.getElementsByTagName("card");
        for(int i = 0; i < cards.getLength(); i++) {
            Element element = (Element)cards.item(i);
            String name = element.getAttribute("name");
            String idMagicDuelsStr = element.getAttribute("idMagicDuels");
            String idMagicAssistStr = element.getAttribute("idMagicAssist");

            int idMagicDuels = Integer.parseInt(idMagicDuelsStr);
            int idMagicAssist = Integer.parseInt(idMagicAssistStr);

            this.addEntry(name, idMagicDuels, idMagicAssist);
        }
    }

    private String toXmlString() throws TransformerException {
        Document doc = XmlUtils.getNewXmlDocument();
        Element rootElement = doc.createElement("cards");
        doc.appendChild(rootElement);

        for(CardData card: _cardEntries) {
            Element cardElement = doc.createElement("card");
            cardElement.setAttribute("name", card.displayName);
            cardElement.setAttribute("idMagicDuels", Integer.toString(card.idMagicDuels));
            cardElement.setAttribute("idMagicAssist", Integer.toString(card.idMagicAssist));
            rootElement.appendChild(cardElement);
        }

        return XmlUtils.documentToString(doc);
    }
}