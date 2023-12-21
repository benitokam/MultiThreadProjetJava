package org.example.recherche;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Lier_Un_Fichier {
    private String path;
    private List<String[]> sections;
    private AtomicInteger currentSectionIndex = new AtomicInteger(0);
    private AtomicBoolean wordFound = new AtomicBoolean(false);
    private AtomicInteger ligneSection = new AtomicInteger(0);
    private Collection<Thread> threads;
    private int numberOfPages;
    private final Object resultMapLock = new Object();
    private static ConcurrentHashMap<String, String> list = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, List<String>> resultMap = new ConcurrentHashMap<>();

    public Lier_Un_Fichier(String path) throws IOException {
        this.path = path;
        this.sections = divideIntoSections();
        threads = new ArrayList<>();
    }

    private List<String[]> divideIntoSections() throws IOException {
        List<String[]> sections = new ArrayList<>();
        PDDocument document = PDDocument.load(new File(path));

        numberOfPages = document.getNumberOfPages();
        int pagesPerSection = numberOfPages / 5;
        int startPage = 0;

        while (startPage < numberOfPages) {
            int endPage = Math.min(startPage + pagesPerSection, numberOfPages);
            String[] section = extractPages(document, startPage, endPage);
            sections.add(section);
            startPage = endPage;
        }

        document.close();

        return sections;
    }

    private String[] extractPages(PDDocument document, int startPage, int endPage) throws IOException {
        List<String> pages = new ArrayList<>();
        for (int i = startPage; i < endPage; i++) {
            pages.add(extractTextFromPage(document, i));
        }
        return pages.toArray(new String[0]);
    }

    private String extractTextFromPage(PDDocument document, int pageIndex) throws IOException {
        PDFTextStripper pdfTextStripper = new PDFTextStripper();
        pdfTextStripper.setStartPage(pageIndex + 1);
        pdfTextStripper.setEndPage(pageIndex + 1);
        return pdfTextStripper.getText(document);
    }

    public void search(String targetPhrase) throws InterruptedException {
        int totalSections = sections.size();

        while (ligneSection.get() < totalSections) {
            int currentSectionIndex = ligneSection.getAndIncrement();

            if (currentSectionIndex < totalSections) {
                Thread t = new Thread(() -> {
                    try {
                        searchInSection(currentSectionIndex, targetPhrase);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, "MonTread: " + currentSectionIndex);
                t.start();
                threads.add(t);
            }
        }

        for (Thread t : threads) {
            t.join();
        }
    }

    public void searchInSection(int indexSection, String targetPhrase) throws InterruptedException {
        String[] section = sections.get(indexSection);

        System.out.println(Thread.currentThread().getName());
        for (int pageIndex = 0; pageIndex < section.length; pageIndex++) {
            String pageText = section[pageIndex];

            if (containsPhrase(section[pageIndex], targetPhrase)) {
                wordFound.set(true);
                int absolutePageIndex = computeAbsolutePageIndex(indexSection, pageIndex, numberOfPages);

                // Utilisation  du  moniteur pour synchroniser l'accès à resultMap
                synchronized (resultMapLock) {
                    resultMap.computeIfAbsent(targetPhrase, k -> new ArrayList<>()).add("" + absolutePageIndex);
                }
            }
        }
    }

    public static Boolean containsPhrase(String text, String phrase) {
        String[] motsPhrase = phrase.split(" ");

        if (text.contains(motsPhrase[0]) && text.contains(motsPhrase[motsPhrase.length - 1])) {
            String[] tabText = text.split(" ");

            for (String mot : motsPhrase) {
                boolean motTrouve = false;

                if (text.contains(mot)) {
                    motTrouve = true;
                    break;
                }

                if (!motTrouve) {
                    System.out.println(mot);
                    return false;
                }
            }

            return true;
        }
        return false;
    }

    private int computeAbsolutePageIndex(int sectionIndex, int pageIndexInSection, int totalPages) {
        int pagesPerSection = 10;
        return (sectionIndex * pagesPerSection) + pageIndexInSection + 1;
    }

    public void Affichage() {

        resultMap.forEach((key, value) -> {
            System.out.println("Texte : " + key + "\n");
            System.out.println("Texte trouvée dans la page " + value);
        });
    }
}