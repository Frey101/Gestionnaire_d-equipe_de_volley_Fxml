import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

/**
 * Système de gestion d'inscriptions pour un tournoi de volley-ball
 */
public class TournoiVolleyball {
    private static final int MAX_EQUIPES = 8;
    private static final int JOUEURS_PAR_EQUIPE = 6;
    private static final LocalDate DATE_DEBUT_INSCRIPTIONS = LocalDate.of(2025, 5, 9);
    private static final LocalDate DATE_FIN_INSCRIPTIONS = LocalDate.of(2025, 5, 16);

    private ArrayList<Equipe> equipes;
    private Set<String> joueursInscrits;
    private Scanner scanner;
    private LocalDate dateActuelle;

    /**
     * Classe représentant une équipe de volley-ball
     */
    private class Equipe {
        private String nom;
        private ArrayList<String> joueurs;

        public Equipe(String nom) {
            this.nom = nom;
            this.joueurs = new ArrayList<>();
        }

        public String getNom() {
            return this.nom;
        }

        public ArrayList<String> getJoueurs() {
            return this.joueurs;
        }

        public void ajouterJoueur(String joueur) {
            this.joueurs.add(joueur);
        }

        @Override
        public String toString() {
            return "Équipe " + nom + " : " + String.join(", ", joueurs);
        }
    }

    /**
     * Exception personnalisée pour les erreurs d'inscription
     */
    public class InscriptionException extends Exception {
        public InscriptionException(String message) {
            super(message);
        }
    }

    /**
     * Constructeur de la classe TournoiVolleyball
     */
    public TournoiVolleyball() {
        this.equipes = new ArrayList<>();
        this.joueursInscrits = new HashSet<>();
        this.scanner = new Scanner(System.in);
        this.dateActuelle = LocalDate.now(); // Date du système
    }

    /**
     * Vérifier si les inscriptions sont ouvertes
     * @return true si les inscriptions sont ouvertes, false sinon
     */
    public boolean inscriptionsOuvertes() {
        return !dateActuelle.isBefore(DATE_DEBUT_INSCRIPTIONS) &&
                !dateActuelle.isAfter(DATE_FIN_INSCRIPTIONS);
    }

    /**
     * Ajouter une nouvelle équipe au tournoi
     * @throws InscriptionException si l'ajout est impossible
     */
    public void ajouterEquipe() throws InscriptionException {
        if (!inscriptionsOuvertes()) {
            throw new InscriptionException("Les inscriptions sont fermées. " +
                    "Elles sont ouvertes du " + DATE_DEBUT_INSCRIPTIONS.format(DateTimeFormatter.ISO_LOCAL_DATE) +
                    " au " + DATE_FIN_INSCRIPTIONS.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        if (equipes.size() >= MAX_EQUIPES) {
            throw new InscriptionException("Le nombre maximum d'équipes (" + MAX_EQUIPES + ") est atteint.");
        }

        System.out.print("Nom de l'équipe : ");
        String nomEquipe = scanner.nextLine().trim();

        // Vérifier si le nom d'équipe existe déjà
        for (Equipe e : equipes) {
            if (e.getNom().equalsIgnoreCase(nomEquipe)) {
                throw new InscriptionException("Une équipe avec ce nom existe déjà.");
            }
        }

        Equipe nouvelleEquipe = new Equipe(nomEquipe);

        System.out.println("Saisie des " + JOUEURS_PAR_EQUIPE + " joueurs :");

        for (int i = 0; i < JOUEURS_PAR_EQUIPE; i++) {
            System.out.print("Joueur " + (i + 1) + " : ");
            String nomJoueur = scanner.nextLine().trim();

            if (nomJoueur.isEmpty()) {
                throw new InscriptionException("Le nom du joueur ne peut pas être vide.");
            }

            if (joueursInscrits.contains(nomJoueur.toLowerCase())) {
                throw new InscriptionException("Le joueur '" + nomJoueur + "' est déjà inscrit dans une autre équipe.");
            }

            nouvelleEquipe.ajouterJoueur(nomJoueur);
            joueursInscrits.add(nomJoueur.toLowerCase());
        }

        equipes.add(nouvelleEquipe);
        System.out.println("Équipe '" + nomEquipe + "' ajoutée avec succès!");
    }

    /**
     * Afficher toutes les équipes inscrites
     */
    public void afficherEquipes() {
        if (equipes.isEmpty()) {
            System.out.println("Aucune équipe inscrite.");
            return;
        }

        System.out.println("\n--- Liste des équipes inscrites (" + equipes.size() + "/" + MAX_EQUIPES + ") ---");
        for (int i = 0; i < equipes.size(); i++) {
            System.out.println((i + 1) + ". " + equipes.get(i));
        }
    }

    /**
     * Définir la date actuelle (pour les tests)
     * @param date la nouvelle date
     */
    public void setDateActuelle(LocalDate date) {
        this.dateActuelle = date;
    }

    /**
     * Définir la date actuelle à partir d'une chaîne au format YYYY-MM-DD
     * @param dateStr la date au format YYYY-MM-DD
     * @throws DateTimeParseException si le format est incorrect
     */
    public void setDateActuelle(String dateStr) throws DateTimeParseException {
        this.dateActuelle = LocalDate.parse(dateStr);
    }

    /**
     * Menu principal du programme
     */
    public void menuPrincipal() {
        int choix = 0;

        do {
            System.out.println("\n=== TOURNOI DE VOLLEY-BALL ===");
            System.out.println("Date actuelle : " + dateActuelle.format(DateTimeFormatter.ISO_LOCAL_DATE));
            System.out.println("1. Inscrire une nouvelle équipe");
            System.out.println("2. Afficher les équipes inscrites");
            System.out.println("3. Modifier la date (pour test)");
            System.out.println("0. Quitter");
            System.out.print("Votre choix : ");

            try {
                choix = Integer.parseInt(scanner.nextLine());

                switch (choix) {
                    case 1:
                        try {
                            ajouterEquipe();
                        } catch (InscriptionException e) {
                            System.out.println("Erreur d'inscription : " + e.getMessage());
                        }
                        break;
                    case 2:
                        afficherEquipes();
                        break;
                    case 3:
                        System.out.print("Nouvelle date (YYYY-MM-DD) : ");
                        String nouvelleDate = scanner.nextLine();
                        try {
                            setDateActuelle(nouvelleDate);
                            System.out.println("Date modifiée avec succès.");
                        } catch (DateTimeParseException e) {
                            System.out.println("Format de date invalide. Utilisez le format YYYY-MM-DD.");
                        }
                        break;
                    case 0:
                        System.out.println("Au revoir !");
                        break;
                    default:
                        System.out.println("Choix invalide. Veuillez réessayer.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Veuillez entrer un nombre valide.");
            }
        } while (choix != 0);
    }


}