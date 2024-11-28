package com.example.mytask

// Importation des dépendances nécessaires pour gérer les activités Android, les widgets, les RecyclerView et le traitement asynchrone
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.adapter.TaskAdapter
import com.example.mytask.db.DBOpenHelper
import com.example.mytask.model.TaskModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Déclaration de l'activité DoneActivity qui hérite de AppCompatActivity
class DoneActivity : AppCompatActivity() {

    // Déclaration des variables de l'interface utilisateur
    private lateinit var doneActBcack: ImageButton // Bouton pour revenir à l'activité principale
    private lateinit var doneRecyclerView: RecyclerView // RecyclerView pour afficher les tâches "Done"

    // Initialisation paresseuse de l'instance de la base de données
    private val dbOpenHelper by lazy { DBOpenHelper(this) }
    private lateinit var job: Job // Coroutine Job pour gérer les tâches asynchrones

    // Méthode appelée lors de la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Active le mode edge-to-edge pour une meilleure expérience utilisateur
        enableEdgeToEdge()
        setContentView(R.layout.activity_done)

        // Liaison des vues de l'interface utilisateur avec les ID dans le fichier XML
        doneActBcack = findViewById(R.id.fab_done_back_main)
        doneRecyclerView = findViewById(R.id.done_recycler_view)

        // Configure les paramètres de la RecyclerView
        setupRecyclerView(doneRecyclerView)

        // Lancement d'une coroutine pour charger les données des tâches "Done" depuis la base de données
        job = CoroutineScope(Dispatchers.Main).launch {
            // Affecte un adaptateur à la RecyclerView avec les tâches récupérées
            doneRecyclerView.adapter = TaskAdapter(this@DoneActivity, fetchTasksFromDatabase("Done"))
        }

        // Ajoute un listener au bouton pour revenir à l'activité principale
        doneActBcack.setOnClickListener {
            doneBackToMain()
        }
    }

    // Méthode pour revenir à l'activité principale
    private fun doneBackToMain() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity) // Démarre l'activité principale
    }

    // Méthode appelée lorsque l'activité est détruite
    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Annule la coroutine pour éviter les fuites de mémoire
    }

    // Méthode pour configurer les paramètres de la RecyclerView
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DoneActivity, LinearLayoutManager.VERTICAL, false) // Orientation verticale
            setHasFixedSize(true) // Optimisation des performances pour les éléments de taille fixe
            isVerticalScrollBarEnabled = false // Désactive la barre de défilement verticale
            isHorizontalScrollBarEnabled = false // Désactive la barre de défilement horizontale
        }
    }

    // Méthode suspendue pour récupérer les tâches "Done" depuis la base de données
    private suspend fun fetchTasksFromDatabase(status: String): MutableList<TaskModel> {
        // Change le contexte de la coroutine au thread IO pour effectuer des opérations d'entrée/sortie
        return withContext(Dispatchers.IO) {
            dbOpenHelper.readTaskByStatus(status) // Lecture des tâches ayant le statut "Done" depuis la base de données
        }
    }
}
