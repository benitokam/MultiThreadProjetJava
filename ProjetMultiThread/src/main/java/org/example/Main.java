package org.example;

import org.example.recheche.LierUnFichier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        // Press Opt+Enter with your caret at the highlighted text to see how
        // IntelliJ IDEA suggests fixing it.


        long startTime = System.currentTimeMillis();
        LierUnFichier fichier = new LierUnFichier("/Users/mamadoucirecamara/Desktop/TP_Master/ProjetMultiThread/src/main/resources/PythonNotesForProfessionals.pdf");
        fichier.search("The difference between .keys() and .iterkeys(), .values() and .itervalues(), .items() and .iteritems() is\n" +
                "that the iter* methods are generators. Thus, the elements within the dictionary are yielded one by one as they are\n" +
                "evaluated. When a list object is returned, all of the elements are packed into a list and then returned for further\n" +
                "evaluation.");
        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;

        System.out.println("Temps d'exécution : " + elapsedTime + " millisecondes");
    }
}