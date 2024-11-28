package com.example.mytask

// Importation des bibliothèques nécessaires pour le développement Android.
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

// Classe principale de l'activité pour afficher les tâches en cours.
class InProgressActivity : AppCompatActivity() {

    // Déclaration des variables pour l'interface utilisateur.
    private lateinit var inProgressBcack: ImageButton // Bouton pour revenir à l'activité principale.
    private lateinit var inProgressRecyclerView: RecyclerView // RecyclerView pour afficher la liste des tâches.

    // Instance de la classe DBOpenHelper pour interagir avec la base de données.
    private val dbOpenHelper by lazy { DBOpenHelper(this) }
    private lateinit var job: Job // Variable pour gérer la coroutine.

    // Méthode appelée lors de la création de l'activité.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Permet l'affichage sans bordures sur l'écran.
        setContentView(R.layout.activity_in_progress) // Définit le layout de l'activité.

        // Initialisation des composants de l'interface utilisateur.
        inProgressBcack = findViewById(R.id.fab_progress_back_main)
        inProgressRecyclerView = findViewById(R.id.in_progress_recycler_view)

        // Configuration de la RecyclerView.
        setupRecyclerView(inProgressRecyclerView)

        // Lancement d'une coroutine pour charger les tâches en cours depuis la base de données.
        job = CoroutineScope(Dispatchers.Main).launch {
            // Assigne l'adaptateur à la RecyclerView après le chargement des tâches.
            inProgressRecyclerView.adapter = TaskAdapter(this@InProgressActivity, fetchTasksFromDatabase("In Progress"))
        }

        // Configuration de l'écouteur d'événements pour le bouton de retour.
        inProgressBcack.setOnClickListener {
            inProgressBackToMain() // Appel de la méthode pour revenir à l'activité principale.
        }
    }

    // Méthode pour revenir à l'activité principale.
    private fun inProgressBackToMain() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity) // Démarre l'activité principale.
    }

    // Méthode appelée lorsque l'activité est détruite.
    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Annule la coroutine lorsque l'activité est détruite pour éviter les fuites de mémoire.
    }

    // Méthode pour configurer la RecyclerView.
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            // Configure le gestionnaire de mise en page pour afficher les éléments en verticale.
            layoutManager = LinearLayoutManager(this@InProgressActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true) // Améliore la performance en informant que la taille de la RecyclerView est fixe.
            // Désactive la barre de défilement verticale et horizontale.
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
        }
    }

    // Méthode suspendue pour récupérer les tâches de la base de données en fonction de leur statut.
    private suspend fun fetchTasksFromDatabase(status: String): MutableList<TaskModel> {
        return withContext(Dispatchers.IO) {
            // Appelle la méthode de la classe DBOpenHelper pour lire les tâches par statut.
            dbOpenHelper.readTaskByStatus(status)
        }
    }
}
