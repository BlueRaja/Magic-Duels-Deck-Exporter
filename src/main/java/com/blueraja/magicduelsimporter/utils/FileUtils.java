package com.blueraja.magicduelsimporter.utils;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FileUtils {

    public static String getFileAsString(String path)
            throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded);
    }

    public static String toUnix(String string) {
        return string.replaceAll("\r", "");
    }

    public static Document getFileAsXMLDocument(InputStream inputStream)
            throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
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

    public static String getBaseName(String filename) {
        File file = new File(filename);
        String basename = file.getName();

        Pattern compile = Pattern.compile("([^.]*)\\.?.*$");
        Matcher matcher = compile.matcher(basename);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new RuntimeException("Filename [" + filename + "] not valid.");
    }
}
