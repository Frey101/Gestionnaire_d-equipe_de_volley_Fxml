package com.example.volley_persofxml;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Application principale du tournoi de volley-ball.
 * Cette classe démarre l'application JavaFX et charge l'interface utilisateur.
 */
public class HelloApplication extends Application {

    /**
     * Point d'entrée de l'application.
     * @param args Arguments de ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Méthode appelée au démarrage de l'application JavaFX.
     * Configure la scène principale et affiche la fenêtre de l'application.
     * @param primaryStage La scène principale de l'application
     * @throws IOException En cas d'erreur lors du chargement du fichier FXML
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Charger le fichier FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));

            // Créer une instance du contrôleur
            HelloController controller = new HelloController();

            // Définir le contrôleur pour le chargeur FXML
            loader.setController(controller);

            // Charger la scène
            Parent root = loader.load();

            // Configurer la scène
            Scene scene = new Scene(root);

            // Configurer la fenêtre principale
            primaryStage.setTitle("Tournoi de Volley-Ball");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}