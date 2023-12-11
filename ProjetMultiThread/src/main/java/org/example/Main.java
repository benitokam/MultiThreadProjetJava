package org.example;

import org.example.recheche.LierUnFichier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {



        long startTime = System.currentTimeMillis();
        LierUnFichier fichier = new LierUnFichier("/Users/mamadoucirecamara/Desktop/TP_Master/MultiThreadProjetJava/ProjetMultiThread/src/main/resources/PythonNotesForProfessionals.pdf");
        fichier.search("Iterating");
          fichier.Affichage();
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Temps d'exécution : " + elapsedTime + " millisecondes");
    }
}