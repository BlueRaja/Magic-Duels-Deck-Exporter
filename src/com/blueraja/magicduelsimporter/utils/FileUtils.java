package com.blueraja.magicduelsimporter.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileUtils {
    public static String getFileAsString(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    public static Document getFileAsXMLDocument(String path)
            throws ParserConfigurationException, SAXException, IOException {
        return getFileAsXMLDocument(new File(path));
    }

    public static Document getFileAsXMLDocument(File file)
            throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(file);
    }

    public static Iterable<File> getAllFilesWithExtension(String folderPath, String extension) {
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if(files == null) {
            return new ArrayList<>();
        }

        return Arrays.asList(files).stream()
                .filter(file -> file.isFile() && file.getName().toLowerCase().endsWith(extension.toLowerCase()))
                .collect(Collectors.toList());
    }

    public static void writeToFile(String path, String text)
            throws FileNotFoundException {
        try(PrintWriter writer = new PrintWriter(path)) {
            writer.println(text);
        }
    }
}
