package com.example.mytask

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge // Permet l'affichage en mode "edge-to-edge" (affichage sans bordures)
import androidx.annotation.RequiresApi // Annotation pour indiquer que la méthode nécessite une version minimale de l'API Android
import androidx.appcompat.app.AppCompatActivity // Activité principale héritée de AppCompatActivity pour la compatibilité descendante
import androidx.recyclerview.widget.LinearLayoutManager // Pour gérer l'affichage en liste linéaire dans le RecyclerView
import androidx.recyclerview.widget.RecyclerView // Utilisé pour afficher une liste de tâches
import com.example.mytask.adapter.TaskAdapter // Adaptateur personnalisé pour le RecyclerView
import com.example.mytask.db.DBOpenHelper // Classe pour gérer la base de données SQLite
import com.example.mytask.model.TaskModel // Modèle de données pour une tâche
import kotlinx.coroutines.CoroutineScope // Pour exécuter des coroutines
import kotlinx.coroutines.Dispatchers // Pour spécifier le dispatcher (contexte d'exécution)
import kotlinx.coroutines.Job // Pour gérer le cycle de vie de la coroutine
import kotlinx.coroutines.launch // Pour lancer une coroutine
import kotlinx.coroutines.withContext // Pour changer le contexte d'exécution de la coroutine
import java.time.LocalDate // Pour manipuler la date actuelle

// Classe NotificationActivity héritée de AppCompatActivity
class NotificationActivity : AppCompatActivity() {

    // Déclaration des variables pour les éléments de l'interface utilisateur
    private lateinit var notifyBcack: ImageButton // Bouton pour revenir à l'activité principale
    private lateinit var viewDate: TextView // Texte pour afficher la date actuelle
    private lateinit var notifyRecyclerView: RecyclerView // RecyclerView pour afficher la liste des tâches

    // Instance de DBOpenHelper pour accéder à la base de données
    private val dbOpenHelper by lazy { DBOpenHelper(this) }
    // Variable pour gérer la coroutine
    private lateinit var job: Job

    // Méthode appelée lors de la création de l'activité
    @RequiresApi(Build.VERSION_CODES.O) // Indique que cette méthode nécessite l'API 26 ou plus
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Active le mode "edge-to-edge"
        setContentView(R.layout.activity_notification) // Définit le layout de l'activité

        // Récupération de la date actuelle
        val currentDate = LocalDate.now()

        // Liaison des widgets à leurs identifiants
        notifyBcack = findViewById(R.id.notify_back_main)
        viewDate = findViewById(R.id.displayDate)
        notifyRecyclerView = findViewById(R.id.notify_recycler_view)

        // Affichage de la date actuelle dans le TextView
        viewDate.text = "$currentDate"

        // Configuration de RecyclerView
        setupRecyclerView(notifyRecyclerView)

        // Lancement d'une coroutine pour charger les tâches depuis la base de données
        job = CoroutineScope(Dispatchers.Main).launch {
            notifyRecyclerView.adapter = TaskAdapter(this@NotificationActivity, fetchTasksFromDatabase("Pending", "$currentDate"))
        }

        // Action de retour à l'activité principale
        notifyBcack.setOnClickListener {
            notifyBackToMain()
        }
    }

    // Méthode pour revenir à l'activité principale
    private fun notifyBackToMain() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)
    }

    // Méthode appelée lors de la destruction de l'activité
    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Annule la coroutine lorsque l'activité est détruite pour éviter les fuites de mémoire
    }

    // Méthode pour configurer le RecyclerView
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@NotificationActivity, LinearLayoutManager.VERTICAL, false) // Configuration du gestionnaire de mise en page
            setHasFixedSize(true) // Indique que la taille du RecyclerView est fixe
            isVerticalScrollBarEnabled = false // Désactive la barre de défilement verticale
            isHorizontalScrollBarEnabled = false // Désactive la barre de défilement horizontale
        }
    }

    // Fonction suspendue pour récupérer les tâches de la base de données
    private suspend fun fetchTasksFromDatabase(status: String, date: String): MutableList<TaskModel> {
        return withContext(Dispatchers.IO) {
            dbOpenHelper.readTaskByDate(status, date) // Appel à la méthode de la base de données pour lire les tâches avec le statut et la date donnés
        }
    }
}
