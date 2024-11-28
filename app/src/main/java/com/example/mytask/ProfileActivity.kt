// Déclaration du package dans lequel la classe est définie
package com.example.mytask

// Importation des classes nécessaires pour l'application Android
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

// La classe ProfileActivity hérite de AppCompatActivity, ce qui signifie qu'elle est une activité dans une application Android.
class ProfileActivity : AppCompatActivity() {

    // Déclaration de la variable `profileBcack` pour référencer le bouton de retour dans le profil
    private lateinit var profileBcack: ImageButton

    // La méthode `onCreate` est appelée lorsque l'activité est créée pour la première fois
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)  // Appel du constructeur de la classe parente
        enableEdgeToEdge()  // Active le mode d'affichage "edge-to-edge", qui permet d'utiliser tout l'écran, y compris les bords
        setContentView(R.layout.activity_profile)  // Définit la mise en page de l'activité à partir du fichier XML spécifié

        // Liaison du bouton de retour à la variable `profileBcack` en utilisant son ID
        profileBcack = findViewById(R.id.fab_profileView_back_main)

        // Définition d'un `OnClickListener` pour exécuter la méthode `profileBackToMain` lorsque le bouton est cliqué
        profileBcack.setOnClickListener {
            profileBackToMain()  // Appel de la méthode pour revenir à l'activité principale
        }
    }

    // Méthode pour naviguer vers l'activité principale
    private fun profileBackToMain() {
        // Création d'une intention explicite pour lancer `MainActivity`
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)  // Démarrage de l'activité spécifiée par l'intention
    }
}
