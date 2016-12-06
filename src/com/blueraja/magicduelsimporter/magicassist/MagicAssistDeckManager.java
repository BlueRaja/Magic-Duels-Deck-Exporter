package com.blueraja.magicduelsimporter.magicassist;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
import com.blueraja.magicduelsimporter.carddata.Main;
import com.blueraja.magicduelsimporter.utils.FileUtils;
import com.blueraja.magicduelsimporter.utils.XmlUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MagicAssistDeckManager {
    private final CardDataManager _cardDataManager;

    public MagicAssistDeckManager(CardDataManager cardDataManager) {
        _cardDataManager = cardDataManager;
    }

    public Iterable<File> getMagicAssistDeckFiles() {
        String path = Main.MAGIC_ASSIST_DB_PATH + "/Decks";
        return FileUtils.getAllFilesWithExtension(path, ".xml");
    }

    public Deck magicAssistDeckFileToDeck(File magicAssistDeckFile) throws ParserConfigurationException, SAXException, IOException {
        Document doc = FileUtils.getFileAsXMLDocument(magicAssistDeckFile);
        String name = doc.getElementsByTagName("name").item(0).getTextContent();
        NodeList cards = doc.getElementsByTagName("mcp");
        List<CardWithCount> cardList = new ArrayList<>();
        for(int i = 0; i < cards.getLength(); i++) {
            Element cardElement = (Element) cards.item(i);
            String idStr = cardElement.getElementsByTagName("id").item(0).getTextContent();
            String countStr = cardElement.getElementsByTagName("count").item(0).getTextContent();

            int id = Integer.parseInt(idStr);
            int count = Integer.parseInt(countStr);
            Optional<CardData> cardData = _cardDataManager.getDataForMagicAssistId(id);

            if(cardData.isPresent()) {
                CardWithCount cardWithCount = new CardWithCount();
                cardWithCount.card = cardData.get();
                cardWithCount.count = count;
                cardList.add(cardWithCount);
            }
        }

        Deck deck = new Deck();
        deck.cards = cardList;
        deck.name = name;
        return deck;
    }

    public void deckToMagicAssistDeckFile(Deck deck)
            throws FileNotFoundException, TransformerException {
        String path = Main.MAGIC_ASSIST_DB_PATH + "/Decks/" + deck.name + ".xml";
        String xml = getXmlStringFromDeck(deck);
        FileUtils.writeToFile(path, xml);
    }

    private String getXmlStringFromDeck(Deck deck) throws TransformerException {
        Document doc = XmlUtils.getNewXmlDocument();
        Element rootElement = doc.createElement("cards");
        doc.appendChild(rootElement);

        addMetadata(deck, doc, rootElement);
        addCards(deck, doc, rootElement);

        return XmlUtils.documentToString(doc);
    }

    private void addCards(Deck deck, Document doc, Element rootElement) {
        Element listElement = doc.createElement("list");
        rootElement.appendChild(listElement);

        for(CardWithCount cardWithCount: deck.cards) {
            Element mcpElement = doc.createElement("mcp");
            listElement.appendChild(mcpElement);

            Element cardElement = doc.createElement("card");
            mcpElement.appendChild(cardElement);

            Element idElement = doc.createElement("id");
            idElement.setTextContent(Integer.toString(cardWithCount.card.idMagicAssist));
            cardElement.appendChild(idElement);

            Element nameElement = doc.createElement("name");
            nameElement.setTextContent(cardWithCount.card.displayName);
            cardElement.appendChild(nameElement);

            Element countElement = doc.createElement("count");
            countElement.setTextContent(Integer.toString(cardWithCount.count));
            mcpElement.appendChild(countElement);

            Element locationElement = doc.createElement("location");
            locationElement.setTextContent("Decks/" + deck.name);
            mcpElement.appendChild(locationElement);
        }
    }

    private void addMetadata(Deck deck, Document doc, Element rootElement) {
        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(deck.name);
        rootElement.appendChild(nameElement);

        Element keyElement = doc.createElement("key");
        keyElement.setTextContent("Decks/" + deck.name);
        rootElement.appendChild(keyElement);

        Element typeElement = doc.createElement("type");
        typeElement.setTextContent("deck");
        rootElement.appendChild(typeElement);

        Element propertiesElement = doc.createElement("properties");
        rootElement.appendChild(propertiesElement);

        Element readonlyPropertyElement = doc.createElement("property");
        readonlyPropertyElement.setAttribute("name", "readonly");
        readonlyPropertyElement.setAttribute("value", "false");
        propertiesElement.appendChild(readonlyPropertyElement);

        Element virtualPropertyElement = doc.createElement("property");
        virtualPropertyElement.setAttribute("name", "virtual");
        virtualPropertyElement.setAttribute("value", "true");
        propertiesElement.appendChild(virtualPropertyElement);
    }
}
