package com.blueraja.magicduelsimporter.magicassist;

import com.blueraja.magicduelsimporter.carddata.CardData;
import com.blueraja.magicduelsimporter.carddata.CardDataManager;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MagicAssistDeckManager {
    private final CardDataManager _cardDataManager;
    private String _workspacePath;

    public MagicAssistDeckManager(CardDataManager cardDataManager, String workspacePath) throws IOException {
        _cardDataManager = cardDataManager;
        _workspacePath = workspacePath;

        File workspaceDirectory = new File(_workspacePath);
        if(!workspaceDirectory.isDirectory()) {
            throw new IOException("Workspace directory could not be found at " + _workspacePath);
        }
    }

    public List<Deck> getDecks() throws IOException, SAXException, ParserConfigurationException {
        List<Deck> returnMe = new ArrayList<>();
        for(File file: getMagicAssistDeckFiles()) {
            returnMe.add(magicAssistDeckFileToDeck(file));
        }
        return returnMe;
    }

    private Iterable<File> getMagicAssistDeckFiles() {
        String path = Paths.get(_workspacePath, "Decks").toAbsolutePath().toString();
        return FileUtils.getAllFilesWithExtension(path, ".xml");
    }

    private Deck magicAssistDeckFileToDeck(File magicAssistDeckFile) throws ParserConfigurationException, SAXException, IOException {
        Document doc = FileUtils.getFileAsXMLDocument(magicAssistDeckFile);
        String name = doc.getElementsByTagName("name").item(0).getTextContent();
        NodeList cards = doc.getElementsByTagName("mcp");
        Deck deck = new Deck(name);

        for(int i = 0; i < cards.getLength(); i++) {
            Element cardElement = (Element) cards.item(i);
            String idStr = cardElement.getElementsByTagName("id").item(0).getTextContent();
            String countStr = cardElement.getElementsByTagName("count").item(0).getTextContent();

            int id = Integer.parseInt(idStr);
            int count = Integer.parseInt(countStr);
            Optional<CardData> cardData = _cardDataManager.getDataForMagicAssistId(id);

            if(cardData.isPresent()) {
                deck.addCard(cardData.get(), count);
            }
        }

        return deck;
    }

    public void writeDeckToMagicAssistDeckFile(Deck deck, boolean isCollection)
            throws FileNotFoundException, TransformerException {
        String path = Paths.get(_workspacePath, (isCollection ? "Collections" : "Decks"), deck.getName() + ".xml")
                .toAbsolutePath().toString();
        String xml = getXmlStringFromDeck(deck, isCollection);
        FileUtils.writeToFile(path, xml);
    }

    private String getXmlStringFromDeck(Deck deck, boolean isCollection) throws TransformerException {
        Document doc = XmlUtils.getNewXmlDocument();
        Element rootElement = doc.createElement("cards");
        doc.appendChild(rootElement);

        addMetadata(deck, doc, rootElement, isCollection);
        addCards(deck, doc, rootElement, isCollection);

        return XmlUtils.documentToString(doc);
    }

    private void addCards(Deck deck, Document doc, Element rootElement, boolean isCollection) {
        Element listElement = doc.createElement("list");
        rootElement.appendChild(listElement);

        for(CardData cardData: deck.getCards()) {
            Element mcpElement = doc.createElement("mcp");
            listElement.appendChild(mcpElement);

            Element cardElement = doc.createElement("card");
            mcpElement.appendChild(cardElement);

            Element idElement = doc.createElement("id");
            idElement.setTextContent(Integer.toString(cardData.idMagicAssist));
            cardElement.appendChild(idElement);

            Element nameElement = doc.createElement("name");
            nameElement.setTextContent(cardData.displayName);
            cardElement.appendChild(nameElement);

            Element countElement = doc.createElement("count");
            countElement.setTextContent(Integer.toString(deck.getCardCount(cardData)));
            mcpElement.appendChild(countElement);

            Element locationElement = doc.createElement("location");
            locationElement.setTextContent((isCollection ? "Collections/" : "Decks/") + deck.getName());
            mcpElement.appendChild(locationElement);
        }
    }

    private void addMetadata(Deck deck, Document doc, Element rootElement, boolean isCollection) {
        Element nameElement = doc.createElement("name");
        nameElement.setTextContent(deck.getName());
        rootElement.appendChild(nameElement);

        Element keyElement = doc.createElement("key");
        keyElement.setTextContent((isCollection ? "Collections/" : "Decks/") + deck.getName());
        rootElement.appendChild(keyElement);

        Element typeElement = doc.createElement("type");
        typeElement.setTextContent(isCollection ? "collection" : "deck");
        rootElement.appendChild(typeElement);

        Element propertiesElement = doc.createElement("properties");
        rootElement.appendChild(propertiesElement);

        Element readonlyPropertyElement = doc.createElement("property");
        readonlyPropertyElement.setAttribute("name", "readonly");
        readonlyPropertyElement.setAttribute("value", "false");
        propertiesElement.appendChild(readonlyPropertyElement);

        Element virtualPropertyElement = doc.createElement("property");
        virtualPropertyElement.setAttribute("name", "virtual");
        virtualPropertyElement.setAttribute("value", isCollection ? "false" : "true");
        propertiesElement.appendChild(virtualPropertyElement);
    }
}
