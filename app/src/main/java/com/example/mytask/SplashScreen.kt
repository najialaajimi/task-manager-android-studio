// Déclaration du package de l'application
package com.example.mytask

// Importation des bibliothèques nécessaires pour l'application Android
import android.content.Intent // Pour naviguer vers une autre activité
import android.os.Bundle // Pour gérer les informations de l'état de l'activité
import android.os.Handler // Pour exécuter des tâches avec un délai
import android.os.Looper // Pour gérer l'exécution sur le thread principal
import androidx.activity.enableEdgeToEdge // Pour activer le mode "Edge-to-Edge"
import androidx.appcompat.app.AppCompatActivity // Pour utiliser les fonctionnalités de compatibilité de l'activité

// Déclaration de la classe SplashScreen qui hérite de AppCompatActivity
class SplashScreen : AppCompatActivity() {

    // Durée d'affichage de l'écran de démarrage en millisecondes (ici 2000 ms = 2 secondes)
    private val SPLASH_DISPLAY_LENGTH: Long = 2000

    // Méthode appelée lors de la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Appel de la méthode de la classe parente pour gérer la création
        enableEdgeToEdge() // Active le mode "Edge-to-Edge" pour que le contenu s'étende jusqu'aux bords de l'écran
        setContentView(R.layout.activity_splash_screen) // Définit le layout de l'écran de démarrage

        // Crée un objet Handler pour exécuter une tâche après un certain délai
        Handler(Looper.getMainLooper()).postDelayed({
            // Crée une intention pour lancer l'activité principale de l'application
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) // Démarre l'activité principale
            finish() // Termine l'activité actuelle (écran de démarrage) pour qu'elle ne soit plus dans la pile d'activités
        }, SPLASH_DISPLAY_LENGTH) // Spécifie la durée d'attente avant de lancer l'activité principale
    }
}
