package com.example.volley_persofxml;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class HelloController implements Initializable {

    // Les constantes pour les dates d'inscription
    private static final LocalDate DATE_DEBUT_INSCRIPTIONS = LocalDate.of(2025, 5, 9);
    private static final LocalDate DATE_FIN_INSCRIPTIONS = LocalDate.of(2025, 5, 16);
    private static final int MAX_EQUIPES = 8;

    // Composants FXML
    @FXML private Label labelStatut;
    @FXML private ListView<String> listViewEquipes;
    @FXML private TextArea textAreaDetails;
    @FXML private TextField textFieldNomEquipe;
    @FXML private TextField textFieldJoueur1;
    @FXML private TextField textFieldJoueur2;
    @FXML private TextField textFieldJoueur3;
    @FXML private TextField textFieldJoueur4;
    @FXML private TextField textFieldJoueur5;
    @FXML private TextField textFieldJoueur6;
    @FXML private DatePicker datePickerTest;

    // Collections pour stocker les données
    private final List<Equipe> equipes = new ArrayList<>();
    private final Set<String> joueursTousEquipes = new HashSet<>();
    private LocalDate dateActuelle = LocalDate.now();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser la date
        datePickerTest.setValue(dateActuelle);

        // Mise à jour du statut des inscriptions
        mettreAJourStatutInscriptions();

        // Configurer les listeners
        listViewEquipes.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> afficherDetailsEquipe(newValue)
        );
    }

    /**
     * Met à jour le statut des inscriptions en fonction de la date actuelle.
     */
    private void mettreAJourStatutInscriptions() {
        if (dateActuelle.isBefore(DATE_DEBUT_INSCRIPTIONS)) {
            labelStatut.setText("Les inscriptions ouvriront le " +
                    DATE_DEBUT_INSCRIPTIONS.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else if (dateActuelle.isAfter(DATE_FIN_INSCRIPTIONS)) {
            labelStatut.setText("Les inscriptions sont terminées depuis le " +
                    DATE_FIN_INSCRIPTIONS.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        } else {
            int joursRestants = DATE_FIN_INSCRIPTIONS.compareTo(dateActuelle);
            labelStatut.setText("Inscriptions ouvertes ! Il reste " + joursRestants +
                    " jour(s) - " + equipes.size() + "/" + MAX_EQUIPES + " équipes inscrites");
        }
    }

    /**
     * Affiche les détails de l'équipe sélectionnée.
     */
    private void afficherDetailsEquipe(String nomEquipe) {
        if (nomEquipe == null) {
            textAreaDetails.clear();
            return;
        }

        for (Equipe equipe : equipes) {
            if (equipe.getNom().equals(nomEquipe)) {
                StringBuilder details = new StringBuilder();
                details.append("Équipe: ").append(equipe.getNom()).append("\n\n");
                details.append("Joueurs:\n");

                for (int i = 0; i < equipe.getJoueurs().size(); i++) {
                    details.append(i + 1).append(". ").append(equipe.getJoueurs().get(i)).append("\n");
                }

                details.append("\nDate d'inscription: ")
                        .append(equipe.getDateInscription().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

                textAreaDetails.setText(details.toString());
                return;
            }
        }
    }

    @FXML
    void actionChangerDate(ActionEvent event) {
        if (datePickerTest.getValue() != null) {
            dateActuelle = datePickerTest.getValue();
            mettreAJourStatutInscriptions();
        }
    }

    @FXML
    void actionInscrireEquipe(ActionEvent event) {
        try {
            // Vérifier si les inscriptions sont ouvertes
            if (dateActuelle.isBefore(DATE_DEBUT_INSCRIPTIONS) || dateActuelle.isAfter(DATE_FIN_INSCRIPTIONS)) {
                throw new InscriptionException("Les inscriptions ne sont pas ouvertes à cette date.");
            }

            // Vérifier si le nombre maximum d'équipes est atteint
            if (equipes.size() >= MAX_EQUIPES) {
                throw new InscriptionException("Le nombre maximum d'équipes (" + MAX_EQUIPES + ") est atteint.");
            }

            // Récupérer et valider le nom de l'équipe
            String nomEquipe = textFieldNomEquipe.getText().trim();
            if (nomEquipe.isEmpty()) {
                throw new InscriptionException("Le nom de l'équipe est obligatoire.");
            }

            // Vérifier si le nom de l'équipe est déjà utilisé
            for (Equipe equipe : equipes) {
                if (equipe.getNom().equalsIgnoreCase(nomEquipe)) {
                    throw new InscriptionException("Une équipe avec ce nom existe déjà.");
                }
            }

            // Récupérer et valider les noms des joueurs
            List<String> joueurs = new ArrayList<>();
            joueurs.add(validerJoueur(textFieldJoueur1.getText().trim()));
            joueurs.add(validerJoueur(textFieldJoueur2.getText().trim()));
            joueurs.add(validerJoueur(textFieldJoueur3.getText().trim()));
            joueurs.add(validerJoueur(textFieldJoueur4.getText().trim()));
            joueurs.add(validerJoueur(textFieldJoueur5.getText().trim()));
            joueurs.add(validerJoueur(textFieldJoueur6.getText().trim()));

            // Vérifier les doublons au sein de l'équipe
            Set<String> joueurSet = new HashSet<>(joueurs);
            if (joueurSet.size() < joueurs.size()) {
                throw new InscriptionException("Un joueur ne peut pas être inscrit deux fois dans la même équipe.");
            }

            // Vérifier si les joueurs sont déjà inscrits dans d'autres équipes
            List<String> joueursDejaInscrits = new ArrayList<>();
            for (String joueur : joueurs) {
                if (joueursTousEquipes.contains(joueur)) {
                    joueursDejaInscrits.add(joueur);
                }
            }

            if (!joueursDejaInscrits.isEmpty()) {
                throw new InscriptionException("Les joueurs suivants sont déjà inscrits dans une autre équipe : " +
                        String.join(", ", joueursDejaInscrits));
            }

            // Créer et ajouter la nouvelle équipe
            Equipe nouvelleEquipe = new Equipe(nomEquipe, joueurs, dateActuelle);
            equipes.add(nouvelleEquipe);

            // Ajouter les joueurs à l'ensemble des joueurs déjà inscrits
            joueursTousEquipes.addAll(joueurs);

            // Mettre à jour la liste des équipes
            listViewEquipes.getItems().add(nomEquipe);

            // Mettre à jour le statut
            mettreAJourStatutInscriptions();

            // Réinitialiser le formulaire
            actionReinitialiserFormulaire(event);

            // Afficher un message de succès
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Inscription réussie");
            alert.setHeaderText(null);
            alert.setContentText("L'équipe " + nomEquipe + " a été inscrite avec succès !");
            alert.showAndWait();

        } catch (InscriptionException e) {
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur d'inscription");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    /**
     * Valide le nom d'un joueur.
     */
    private String validerJoueur(String nom) throws InscriptionException {
        if (nom.isEmpty()) {
            throw new InscriptionException("Tous les joueurs doivent être renseignés.");
        }

        // On pourrait ajouter d'autres validations ici (longueur minimum, caractères autorisés, etc.)

        return nom;
    }

    @FXML
    void actionReinitialiserFormulaire(ActionEvent event) {
        textFieldNomEquipe.clear();
        textFieldJoueur1.clear();
        textFieldJoueur2.clear();
        textFieldJoueur3.clear();
        textFieldJoueur4.clear();
        textFieldJoueur5.clear();
        textFieldJoueur6.clear();
    }

    /**
     * Classe représentant une équipe.
     */
    private static class Equipe {
        private final String nom;
        private final List<String> joueurs;
        private final LocalDate dateInscription;

        public Equipe(String nom, List<String> joueurs, LocalDate dateInscription) {
            this.nom = nom;
            this.joueurs = new ArrayList<>(joueurs);
            this.dateInscription = dateInscription;
        }

        public String getNom() {
            return nom;
        }

        public List<String> getJoueurs() {
            return joueurs;
        }

        public LocalDate getDateInscription() {
            return dateInscription;
        }
    }

    /**
     * Exception spécifique pour les erreurs d'inscription.
     */
    private static class InscriptionException extends Exception {
        public InscriptionException(String message) {
            super(message);
        }
    }
}