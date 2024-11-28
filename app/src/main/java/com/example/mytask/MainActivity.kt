package com.example.mytask

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
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

// La classe MainActivity hérite de AppCompatActivity et sert de point d'entrée principal de l'application.
class MainActivity : AppCompatActivity() {

    // Déclaration des variables d'interface utilisateur pour les différents éléments de la vue.
    private lateinit var importantRecyclerView: RecyclerView
    private lateinit var pendingRecyclerView: RecyclerView
    private lateinit var meetingRecyclerView: RecyclerView
    private lateinit var fabCreate: ImageButton
    private lateinit var notificationBtn: ImageButton
    private lateinit var searchBtn: ImageButton
    private lateinit var profileBtn: ImageButton
    private lateinit var inProgressBtn: ImageButton
    private lateinit var doneBtn: ImageButton

    // Variables pour afficher les statistiques de tâches.
    private lateinit var importantDisplay: TextView
    private lateinit var toDoDisplay: TextView
    private lateinit var meetingDisplay: TextView

    // Instance de la base de données pour l'accès aux données.
    private val dbOpenHelper by lazy { DBOpenHelper(this) }
    // Variable pour gérer la coroutine de fond.
    private lateinit var job: Job

    // Méthode d'initialisation de l'activité.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Liaison des vues avec leurs identifiants respectifs.
        importantRecyclerView = findViewById(R.id.important_recycler_view)
        pendingRecyclerView = findViewById(R.id.pending_recycler_view)
        meetingRecyclerView = findViewById(R.id.meeting_recycler_view)

        importantDisplay = findViewById(R.id.countImportant)
        toDoDisplay = findViewById(R.id.countToDo)
        meetingDisplay = findViewById(R.id.countMeeting)

        fabCreate = findViewById(R.id.fab_create)
        notificationBtn = findViewById(R.id.notification_bell)
        searchBtn = findViewById(R.id.searchBtn)
        profileBtn = findViewById(R.id.profileBtn)
        doneBtn = findViewById(R.id.doneBtn)
        inProgressBtn = findViewById(R.id.inProgressBtn)

        // Configuration des RecyclerView pour l'affichage des listes de tâches.
        setupRecyclerView(importantRecyclerView)
        setupRecyclerView(pendingRecyclerView)
        setupRecyclerView(meetingRecyclerView)

        // Récupération du nombre de tâches par catégorie et mise à jour des affichages.
        val countImportant = dbOpenHelper.readTaskCount("Pending", "Important")
        importantDisplay.text = countImportant.toString()

        val countToDo = dbOpenHelper.readTaskCount("Pending", "To Do")
        toDoDisplay.text = countToDo.toString()

        val countMeeting = dbOpenHelper.readTaskCount("Pending", "Meeting")
        meetingDisplay.text = countMeeting.toString()

        // Lancement d'une coroutine pour récupérer les tâches de la base de données de manière asynchrone.
        job = CoroutineScope(Dispatchers.Main).launch {
            importantRecyclerView.adapter = TaskAdapter(this@MainActivity, fetchTasksFromDatabase("Pending", "Important"))
            pendingRecyclerView.adapter = TaskAdapter(this@MainActivity, fetchTasksFromDatabase("Pending", "To Do"))
            meetingRecyclerView.adapter = TaskAdapter(this@MainActivity, fetchTasksFromDatabase("Pending", "Meeting"))
        }

        // Configuration des boutons pour naviguer vers d'autres activités.
        fabCreate.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddTaskActivity::class.java))
            finish() // Termine l'activité actuelle après le démarrage de la nouvelle activité.
        }

        notificationBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, NotificationActivity::class.java))
            finish()
        }

        searchBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, SearchActivity::class.java))
            finish()
        }

        inProgressBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, InProgressActivity::class.java))
            finish()
        }

        doneBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, DoneActivity::class.java))
            finish()
        }

        profileBtn.setOnClickListener {
            startActivity(Intent(this@MainActivity, ProfileActivity::class.java))
            finish()
        }
    }

    // Méthode appelée lorsque l'activité est détruite pour annuler la coroutine en cours.
    override fun onDestroy() {
        super.onDestroy()
        job.cancel() // Annule la coroutine pour libérer des ressources.
    }

    // Méthode pour configurer un RecyclerView avec un layout manager horizontal.
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            isVerticalScrollBarEnabled = false // Désactive la barre de défilement verticale.
            isHorizontalScrollBarEnabled = false // Désactive la barre de défilement horizontale.
        }
    }

    // Méthode suspendue pour récupérer les tâches de la base de données sur un thread de fond.
    private suspend fun fetchTasksFromDatabase(category: String, type: String): MutableList<TaskModel> {
        return withContext(Dispatchers.IO) {
            dbOpenHelper.readTask(category, type) // Appel de la méthode de la base de données pour lire les tâches.
        }
    }
}
