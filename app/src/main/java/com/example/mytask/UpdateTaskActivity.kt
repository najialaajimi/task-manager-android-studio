package com.example.mytask

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.BaseColumns
import android.text.Editable
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mytask.db.DBOpenHelper
import com.example.mytask.utils.COLUMN_NAME_DATE
import com.example.mytask.utils.COLUMN_NAME_DESCRIPTION
import com.example.mytask.utils.COLUMN_NAME_STATUS
import com.example.mytask.utils.COLUMN_NAME_TIME
import com.example.mytask.utils.COLUMN_NAME_TITLE
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
// Classe principale pour l'activité de mise à jour d'une tâche
class UpdateTaskActivity : AppCompatActivity() {

    // Déclaration des variables pour les composants de l'interface utilisateur
    private lateinit var etUpdatedTitle: TextInputLayout
    private lateinit var etUpdatedDescription: TextInputLayout
    private lateinit var fabUpdate: ImageButton
    private lateinit var fabDelete: ImageButton
    private lateinit var backToMain: ImageButton
    private lateinit var calendar: Calendar
    private lateinit var etDate: TextView
    private lateinit var etTime: TextView
    // Variables pour stocker la date, l'heure et le statut de la tâche
    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var status: String = ""
    // Cases à cocher pour les statuts de la tâche
    private lateinit var checkBoxPending: CheckBox
    private lateinit var checkBoxInProgress: CheckBox
    private lateinit var checkBoxDone: CheckBox
    // Instance de la base de données pour la mise à jour et la suppression des tâches
    private val dbOpenHelper = DBOpenHelper(this)

    // Méthode appelée lors de la création de l'activité
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_task)
// Liaison des composants de l'interface utilisateur aux variables
        etUpdatedTitle = findViewById(R.id.edit_title)
        etUpdatedDescription = findViewById(R.id.edit_description)
        fabUpdate = findViewById(R.id.fab_update)
        backToMain = findViewById(R.id.fab_back_main)
        fabDelete = findViewById(R.id.fab_delete)

        checkBoxPending = findViewById(R.id.edit_pending)
        checkBoxInProgress = findViewById(R.id.edit_in_progress)
        checkBoxDone = findViewById(R.id.edit_done)

        etDate = findViewById(R.id.display_edit_date)
        etTime = findViewById(R.id.display_edit_time)
// Initialisation de l'objet Calendar pour la gestion des dates et heures
        calendar = Calendar.getInstance()
// Récupération des valeurs passées par l'intent
        val titleOld = intent.getStringExtra(COLUMN_NAME_TITLE)
        val descriptionOld = intent.getStringExtra(COLUMN_NAME_DESCRIPTION)
        val datepre = intent.getStringExtra(COLUMN_NAME_DATE)
        val timepre = intent.getStringExtra(COLUMN_NAME_TIME)
        val statusOld = intent.getStringExtra(COLUMN_NAME_STATUS)
// Affichage de la date et de l'heure initiales dans les TextView
        etDate.text = datepre
        etTime.text = timepre
// Initialisation des cases à cocher en fonction du statut initial de la tâch
        setStatusCheckBoxes(statusOld)
        // Définition des écouteurs de changement d'état des cases à cocher pour le statut
        checkBoxPending.setOnCheckedChangeListener { _, isChecked -> setStatus(isChecked, "Pending") }
        checkBoxInProgress.setOnCheckedChangeListener { _, isChecked -> setStatus(isChecked, "In Progress") }
        checkBoxDone.setOnCheckedChangeListener { _, isChecked -> setStatus(isChecked, "Done") }
// Configuration de la date dans le calendrier à partir de la date passée
        calendar = Calendar.getInstance()
        datepre?.let {
            val dateParts = it.split("-")
            val year = dateParts[0].toInt()
            val month = dateParts[1].toInt() - 1 // Month starts from 0
            val day = dateParts[2].toInt()
            calendar.set(year, month, day)
        }
// Configuration de l'heure dans le calendrier à partir de l'heure passée
        timepre?.let {
            val timeParts = it.split(":")
            val hour = timeParts[0].toInt()
            val minute = timeParts[1].toInt()
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)
        }
        // Configuration des boutons pour choisir la date et l'heure
        val btnPickDate = findViewById<Button>(R.id.btn_edit_pick_date)
        btnPickDate.setOnClickListener {
            showDatePicker()
        }

        val btnPickTime = findViewById<Button>(R.id.btn_edit_pick_time)
        btnPickTime.setOnClickListener {
            showTimePicker()
        }

// Initialisation des champs de texte avec les valeurs passées par l'intent
        if (!titleOld.isNullOrBlank()) {

            etUpdatedTitle.editText?.text =
                Editable.Factory.getInstance().newEditable(titleOld)
            etUpdatedDescription.editText?.text =
                Editable.Factory.getInstance().newEditable(descriptionOld)
// Journalisation des valeurs pour le débogage
            Log.d("UpdateTaskActivity", titleOld.toString())
            Log.d("UpdateTaskActivity", descriptionOld.toString())
            Log.d("UpdateTaskActivity", datepre.toString())
            Log.d("UpdateTaskActivity", timepre.toString())
        } else {
            Log.d("UpdateTaskActivity", "value was null")
            Toast.makeText(this, "Value was null", Toast.LENGTH_SHORT).show()
        }

// Configuration du bouton de mise à jour
        fabUpdate.setOnClickListener {
            updateData()
        }
// Configuration du bouton pour revenir à l'activité principale
        backToMain.setOnClickListener {
            fabBackToMain()
        }
// Configuration du bouton pour supprimer la tâche
        fabDelete.setOnClickListener {
            deleteTask()
        }
    }
    // Méthode pour initialiser les cases à cocher en fonction du statut existant de la tâche
    private fun setStatusCheckBoxes(statusOld: String?) {
        when (statusOld) {
            "Pending" -> checkBoxPending.isChecked = true
            "In Progress" -> checkBoxInProgress.isChecked = true
            "Done" -> checkBoxDone.isChecked = true
        }
    }
    // Méthode pour gérer le changement de statut lorsque l'une des cases est cochée
    private fun setStatus(isChecked: Boolean, newStatus: String) {
        if (isChecked) {
            status = newStatus
            when (newStatus) {
                "Pending" -> {
                    checkBoxInProgress.isChecked = false
                    checkBoxDone.isChecked = false
                }
                "In Progress" -> {
                    checkBoxPending.isChecked = false
                    checkBoxDone.isChecked = false
                }
                "Done" -> {
                    checkBoxPending.isChecked = false
                    checkBoxInProgress.isChecked = false
                }
            }
        }
    }
    // Méthode pour afficher le sélecteur de date
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
    // Méthode pour afficher le sélecteur d'heure
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
            true
        )
        timePickerDialog.show()
    }
    // Méthode pour mettre à jour les données de la tâche dans la base de données
    private fun updateData() {

        val id = intent.getIntExtra(BaseColumns._ID, 0).toString()
// Validation des champs de texte
        if (etUpdatedTitle.editText?.text.toString().isEmpty()) {
            etUpdatedTitle.error = "Please enter your Title"
            etUpdatedTitle.requestFocus()
            return
        }

        if (etUpdatedDescription.editText?.text.toString().isEmpty()) {
            etUpdatedDescription.error = "Please enter your Description"
            etUpdatedDescription.requestFocus()
            return
        }
        // Formatage de la date et de l'heure sélectionnées
        selectedDate = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        selectedTime = SimpleDateFormat("HH:mm").format(calendar.time)
// Vérifie si les champs requis ne sont pas vides avant de continuer
        if (notEmpty()) {
            // Mise à jour de la tâche dans la base de données via l'instance de dbOpenHelper
            dbOpenHelper.updateTask(
                id,
                etUpdatedTitle.editText?.text.toString(),
                etUpdatedDescription.editText?.text.toString(),
                selectedDate,
                selectedTime,
                status
            )
            // Affiche un message Toast pour indiquer que la mise à jour est réussie
            Toast.makeText(this, "Updated!", Toast.LENGTH_SHORT).show()

            // Crée un nouvel intent pour revenir à l'activité principale
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity) // Lance l'activité principale
            finish() // Termine l'activité courante
        }
    }

    // Méthode pour vérifier que les champs de titre et de description ne sont pas vides
    private fun notEmpty(): Boolean {
        return (etUpdatedTitle.editText?.text.toString().isNotEmpty() // Vérifie que le titre n'est pas vide
                && etUpdatedDescription.editText?.text.toString().isNotEmpty()) // Vérifie que la description n'est pas vide
    }

    // Méthode pour supprimer une tâche de la base de données
    private fun deleteTask() {
        // Récupération de l'ID de la tâche depuis les extras de l'intention
        val id = intent.getIntExtra(BaseColumns._ID, 0).toString()
        // Appel de la méthode de dbOpenHelper pour supprimer la tâche
        dbOpenHelper.deleteTask(id)
        // Affiche un message Toast pour indiquer que la tâche a été supprimée
        Toast.makeText(this, "Deleted!", Toast.LENGTH_SHORT).show()

        // Crée un nouvel intent pour revenir à l'activité principale
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity) // Lance l'activité principale
        finish() // Termine l'activité courante
    }

    // Méthode pour revenir à l'activité principale lorsque l'utilisateur clique sur le bouton flottant (FAB)
    private fun fabBackToMain() {
        val intentToMainActivity = Intent(this, MainActivity::class.java)
        startActivity(intentToMainActivity) // Lance l'activité principale
    }

    // Méthode pour mettre à jour le texte affiché dans le TextView de la date
    private fun updateDateTextView() {
        etDate = findViewById(R.id.display_edit_date) // Récupère la référence au TextView de la date
        etDate.text = "$selectedDate" // Met à jour le texte avec la date sélectionnée
    }

    // Méthode pour mettre à jour le texte affiché dans le TextView de l'heure
    private fun updateTimeTextView() {
        etTime = findViewById(R.id.display_edit_time) // Récupère la référence au TextView de l'heure
        etTime.text = "$selectedTime" // Met à jour le texte avec l'heure sélectionnée
    }

}