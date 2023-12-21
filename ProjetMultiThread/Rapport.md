
Projet de Recherche avec Threads et Structure de Données Non Bloquante
Le projet LierUnFichier utilise des threads et une structure de données non bloquante pour rechercher des phrases cibles dans un document PDF. Voici une explication détaillée du fonctionnement du projet, en mettant en évidence l'utilisation des threads et de la structure de données non bloquante.

Structure du Projet
Le projet est structuré en plusieurs parties :

1. Initialisation et Division du Document
   Classe LierUnFichier: Cette classe prend en entrée le chemin vers un fichier PDF. Elle initialise des variables pour stocker le chemin, les sections du document, et des compteurs pour le suivi du progrès. Elle utilise la bibliothèque Apache PDFBox pour charger le document PDF.
2. Division du Document en Sections
   Méthode divideIntoSections(): Cette méthode divise le document PDF en sections égales. Chaque section est représentée par un tableau de pages textuelles.
3. Recherche Concurrente avec Threads
   Méthode search(String targetPhrase): Cette méthode lance plusieurs threads pour effectuer la recherche de la phrase cible dans différentes sections du document de manière concurrente. La méthode utilise un ensemble de threads pour le suivi et la gestion des threads.

Méthode searchInSection(int indexSection, String targetPhrase): Cette méthode est exécutée par chaque thread. Elle parcourt les pages de la section attribuée au thread et recherche la phrase cible. Si la phrase est trouvée, elle met à jour un drapeau indiquant la découverte et ajoute les résultats dans une structure de données non bloquante (ConcurrentHashMap).

4. Méthodes auxiliaires
   Méthode extractPages(PDDocument document, int startPage, int endPage): Extrait le texte des pages d'une section spécifique du document.

Méthode extractTextFromPage(PDDocument document, int pageIndex): Extrait le texte d'une page spécifique du document.

Méthode containsPhrase(String text, String phrase): Vérifie si le texte de la page contient la phrase cible.

Méthode computeAbsolutePageIndex(int sectionIndex, int pageIndexInSection, int totalPages): Calcule l'index absolu d'une page dans le document complet en fonction de l'index de la section et de l'index de la page dans la section.

5. Affichage des Résultats
   Méthode Affichage(): Affiche les résultats de la recherche, indiquant le texte trouvé et les pages correspondantes.