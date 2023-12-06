package org.example.recheche;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class LierUnFichier {
    private String path;
    private List<String[]> sections;
    private AtomicInteger currentSectionIndex = new AtomicInteger(0);
    private AtomicBoolean wordFound = new AtomicBoolean(false);
    private AtomicInteger ligneSection = new AtomicInteger(0);
    private Collection<Thread> threads;
    private  int numberOfPages ;


    public LierUnFichier(String path) throws IOException {
        this.path = path;
        this.sections = divideIntoSections();
        threads = new ArrayList<>();

    }

    private List<String[]> divideIntoSections() throws IOException {
        List<String[]> sections = new ArrayList<>();
        PDDocument document = PDDocument.load(new File(path));

        // Obtention du nombre total de pages
         numberOfPages = document.getNumberOfPages();

        // Calcul du nombre de pages par section (1/5 du total)
        int pagesPerSection = numberOfPages / 5;

        // Début du découpage
        int startPage = 0;

        while (startPage < numberOfPages) {
            // Fin de la section (ajusté pour ne pas dépasser le nombre total de pages)
            int endPage = Math.min(startPage + pagesPerSection, numberOfPages);

            // Extraction des pages de la section
            String[] section = extractPages(document, startPage, endPage);

            // Ajout de la section à la liste
            sections.add(section);

            // Mise à jour de la page de début pour la prochaine section
            startPage = endPage;
        }

        // Fermeture du document PDF
        document.close();

        return sections;
    }

    private String[] extractPages(PDDocument document, int startPage, int endPage) throws IOException {
        List<String> pages = new ArrayList<>();
        for (int i = startPage; i < endPage; i++) {
            // Extraction du texte de chaque page
            pages.add(extractTextFromPage(document, i));
        }
        return pages.toArray(new String[0]);
    }

    private String extractTextFromPage(PDDocument document, int pageIndex) throws IOException {
        // Création de l'extracteur de texte
        PDFTextStripper pdfTextStripper = new PDFTextStripper();

        // Définition de la plage de pages à extraire (une seule page à la fois)
        pdfTextStripper.setStartPage(pageIndex + 1);
        pdfTextStripper.setEndPage(pageIndex + 1);

        // Extraction du texte de la page
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
                },"MonTread: "+currentSectionIndex);
                t.start();
                threads.add(t);
            }
        }

        // Attendez que tous les threads se terminent
        for (Thread t : threads) {
            t.join();
        }
    }
    public void searchInSection(int indexSection,String targetPhrase) throws InterruptedException {


            String[] section = sections.get(indexSection);
              System.out.println("Thread"+ Thread.currentThread().getName());
            for (int pageIndex = 0; pageIndex < section.length; pageIndex++) {
                if(Thread.interrupted()) return;
                String pageText = section[pageIndex];

                if (containsPhrase(section[pageIndex],targetPhrase)) {
                    wordFound.set(true);
                    int absolutePageIndex = computeAbsolutePageIndex(indexSection, pageIndex,numberOfPages);
                    System.out.println("Phrase trouvée dans la page " + absolutePageIndex+"");
                    System.out.println("Voici une partie de la page :"+":"+Thread.currentThread().getName());
                   System.out.println(pageText);
                  Thread.currentThread().interrupt();// Sortir de la boucle si la phrase est trouvée dans cette page
                }
            }





    }
    public static Boolean containsPhrase(String text, String phrase) {
        String[] motsPhrase = phrase.split(" ");

        // Vérifier que le premier et le dernier mot de la phrase sont présents dans le texte
        if (text.contains(motsPhrase[0]) && text.contains(motsPhrase[motsPhrase.length - 1])) {
            String[] tabText = text.split(" ");
            // Convertir text en ensemble pour une recherche plus efficace
            // Vérifiez chaque mot de la phrase dans la section
            for (String mot : motsPhrase) {
                // Utilisez une variable pour indiquer si le mot a été trouvé
                boolean motTrouve = false;

                // Vérifiez chaque mot de la section

                    // Comparaison sans tenir compte de la casse (ignoreCase)
                    if (text.contains(mot)) {
                        motTrouve = true;
                        break; // Sortez de la boucle dès que le mot est trouvé dans la section
                    }


                // Si le mot n'est pas trouvé dans la section, retournez false
                if (!motTrouve) {
                    System.out.println(mot);
                    return false;
                }
            }

            // Si tous les mots de la phrase sont trouvés dans la section, retournez true
            return true;
        }
        return false;
    }




    private int computeAbsolutePageIndex(int sectionIndex, int pageIndexInSection, int totalPages) {
        // Supposons que chaque section a 10 pages (modifiable selon votre structure)
        int pagesPerSection = 10;

        // Calcul de la page absolue dans le livre
        return (sectionIndex * pagesPerSection) + pageIndexInSection + 1;
    }
}