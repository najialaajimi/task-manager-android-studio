package com.example.mytask

// Importation des bibliothèques nécessaires pour l'application
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
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
import java.text.SimpleDateFormat
import java.util.Calendar

// Classe principale de l'activité de recherche, héritant d'AppCompatActivity pour la compatibilité avec les fonctionnalités Android modernes
class SearchActivity : AppCompatActivity() {

    // Déclaration des variables pour l'interface utilisateur
    private lateinit var searchBcack: ImageButton  // Bouton pour revenir à l'écran principal
    private lateinit var searchRecyclerView: RecyclerView  // RecyclerView pour afficher les tâches
    private lateinit var calendar: Calendar  // Objet Calendar pour gérer la sélection de la date
    private lateinit var btnPickDate: Button  // Bouton pour ouvrir le sélecteur de date
    private lateinit var dateTextView: TextView  // TextView pour afficher la date sélectionnée
    private var selectedDate: String = ""  // Variable pour stocker la date sélectionnée par l'utilisateur

    // Instance de la base de données, initialisée de manière paresseuse
    private val dbOpenHelper by lazy { DBOpenHelper(this) }
    // Variable pour gérer la coroutine de l'activité
    private lateinit var job: Job

    // Méthode appelée lors de la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()  // Active le mode d'affichage en plein écran sur les bords de l'appareil
        setContentView(R.layout.activity_search)  // Définit le layout de l'activité

        // Initialisation des composants de l'interface utilisateur
        searchBcack = findViewById(R.id.fab_search_back_main)
        searchRecyclerView = findViewById(R.id.search_recycler_view)

        // Initialisation du calendrier et de la date sélectionnée au format "yyyy-MM-dd"
        calendar = Calendar.getInstance()
        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        updateDateTextView()  // Met à jour la TextView avec la date sélectionnée

        // Configuration du RecyclerView pour afficher les tâches
        setupRecyclerView(searchRecyclerView)

        // Lancement de la coroutine pour charger les tâches de la base de données au démarrage de l'activité
        job = CoroutineScope(Dispatchers.Main).launch {
            searchRecyclerView.adapter = TaskAdapter(this@SearchActivity, fetchTasksFromDatabase("$selectedDate"))
        }

        // Configuration du bouton de retour à l'écran principal
        searchBcack.setOnClickListener {
            searchBackToMain()
        }

        // Initialisation du bouton de sélection de la date
        btnPickDate = findViewById(R.id.btn_pick_date)
        btnPickDate.setOnClickListener {
            showDatePicker()  // Affiche le sélecteur de date lorsqu'on clique sur le bouton
        }
    }

    // Méthode pour afficher le sélecteur de date et mettre à jour la date sélectionnée
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)  // Met à jour le calendrier avec la date choisie
                selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)  // Met à jour la date formatée
                updateDateTextView()  // Met à jour la TextView avec la nouvelle date sélectionnée
                updateRecyclerView(selectedDate)  // Met à jour l'affichage du RecyclerView avec les nouvelles tâches
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()  // Affiche le sélecteur de date
    }

    // Méthode pour revenir à l'écran principal
    private fun searchBackToMain() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)  // Lance MainActivity
    }

    // Méthode appelée lorsque l'activité est détruite, pour annuler la coroutine en cours
    override fun onDestroy() {
        super.onDestroy()
        job.cancel()  // Annule la coroutine pour libérer les ressources
    }

    // Méthode pour configurer le RecyclerView
    private fun setupRecyclerView(recyclerView: RecyclerView) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)  // Améliore les performances en indiquant que la taille du RecyclerView est fixe
            isVerticalScrollBarEnabled = false  // Désactive la barre de défilement verticale
            isHorizontalScrollBarEnabled = false  // Désactive la barre de défilement horizontale
        }
    }

    // Méthode suspendue pour récupérer les tâches de la base de données de manière asynchrone
    private suspend fun fetchTasksFromDatabase(date: String): MutableList<TaskModel> {
        return withContext(Dispatchers.IO) {
            dbOpenHelper.readTaskByOnlyDate(date)  // Accède à la base de données en utilisant le contexte IO
        }
    }

    // Méthode pour mettre à jour la TextView avec la date sélectionnée
    private fun updateDateTextView() {
        dateTextView = findViewById(R.id.display_data)
        dateTextView.text = "$selectedDate"  // Affiche la date sélectionnée
    }

    // Méthode pour mettre à jour le RecyclerView en rechargeant les tâches pour la nouvelle date
    private fun updateRecyclerView(selectedDate: String) {
        job = CoroutineScope(Dispatchers.Main).launch {
            searchRecyclerView.adapter = TaskAdapter(this@SearchActivity, fetchTasksFromDatabase(selectedDate))
        }
    }
}
