package com.example.mytask

// Importation des bibliothèques nécessaires
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mytask.db.DBOpenHelper
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.*

class AddTaskActivity : AppCompatActivity() {

    // Déclarations des variables pour les widgets de l'interface utilisateur
    private lateinit var etTitle: TextInputLayout
    private lateinit var etDescription: TextInputLayout
    private lateinit var fabSend: ImageButton
    private lateinit var fabBcack: ImageButton
    private lateinit var calendar: Calendar

    private lateinit var dateTextView: TextView
    private lateinit var timeTextView: TextView

    // Variables pour stocker les valeurs sélectionnées de date, heure, priorité et statut
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var priority: String = "Important"
    private var status: String = "Pending"

    // Déclarations des cases à cocher pour sélectionner la priorité
    private lateinit var checkBoxImportant: CheckBox
    private lateinit var checkBoxToDo: CheckBox
    private lateinit var checkBoxMeeting: CheckBox

    // Boutons pour choisir la date et l'heure
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button

    // Instance de la classe d'aide pour la base de données
    private val dbOpenHelper = DBOpenHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)

        // Initialisation des widgets
        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        fabSend = findViewById(R.id.fab_send)

        // Initialisation du calendrier à la date actuelle
        calendar = Calendar.getInstance()

        // Définit les valeurs par défaut de la date et de l'heure
        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        selectedTime = SimpleDateFormat("HH:mm").format(calendar.time)

        // Initialisation des cases à cocher
        checkBoxImportant = findViewById(R.id.important)
        checkBoxToDo = findViewById(R.id.to_do)
        checkBoxMeeting = findViewById(R.id.meeting)

        // Par défaut, la priorité "Important" est sélectionnée
        checkBoxImportant.isChecked = true

        // Gestionnaire d'événements pour les cases à cocher (priorité)
        checkBoxImportant.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                priority = "Important"
                checkBoxToDo.isChecked = false
                checkBoxMeeting.isChecked = false
            }
        }

        checkBoxToDo.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                priority = "To Do"
                checkBoxImportant.isChecked = false
                checkBoxMeeting.isChecked = false
            }
        }

        checkBoxMeeting.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                priority = "Meeting"
                checkBoxImportant.isChecked = false
                checkBoxToDo.isChecked = false
            }
        }

        // Affichage initial des valeurs de date et d'heure
        updateDateTextView()
        updateTimeTextView()

        // Configuration des boutons de sélection de date et d'heure
        btnPickDate = findViewById(R.id.btn_pick_date)
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        btnPickTime = findViewById(R.id.btn_pick_time)
        btnPickTime.setOnClickListener {
            showTimePicker()
        }

        // Configuration du bouton pour soumettre la tâche
        fabSend.setOnClickListener {
            fabSendData()
        }

        // Configuration du bouton pour revenir à l'activité principale
        fabBcack = findViewById(R.id.fab_back)
        fabBcack.setOnClickListener {
            fabBackToMain()
        }
    }

    // Affiche un sélecteur de date
    private fun showDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                calendar.set(year, monthOfYear, dayOfMonth)
                selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
                updateDateTextView()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    // Affiche un sélecteur d'heure
    private fun showTimePicker() {
        val timePickerDialog = TimePickerDialog(
            this,
            { _, hourOfDay, minute ->
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                selectedTime = SimpleDateFormat("HH:mm").format(calendar.time)
                updateTimeTextView()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true // Utilisation du format 24 heures
        )
        timePickerDialog.show()
    }

    // Retourne à l'activité principale
    private fun fabBackToMain() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)
    }

    // Valide et envoie les données de la tâche à la base de données
    private fun fabSendData() {
        if (etTitle.editText?.text.toString().isEmpty()) {
            etTitle.error = "Please enter your Title"
            etTitle.requestFocus()
            return
        }

        if (etDescription.editText?.text.toString().isEmpty()) {
            etDescription.error = "Please enter your Description"
            etDescription.requestFocus()
            return
        }

        // Mise à jour des valeurs de date et d'heure avant l'enregistrement
        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        selectedTime = SimpleDateFormat("HH:mm").format(calendar.time)

        // Ajout de la tâche à la base de données
        dbOpenHelper.addTask(
            etTitle.editText?.text.toString(),
            etDescription.editText?.text.toString(),
            selectedDate,
            selectedTime,
            priority,
            status
        )
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()

        // Retour à l'activité principale après l'ajout
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity)
        finish()
    }

    // Met à jour l'affichage de la date
    private fun updateDateTextView() {
        dateTextView = findViewById(R.id.display_data)
        dateTextView.text = selectedDate
    }

    // Met à jour l'affichage de l'heure
    private fun updateTimeTextView() {
        timeTextView = findViewById(R.id.display_time)
        timeTextView.text = selectedTime
    }
}
