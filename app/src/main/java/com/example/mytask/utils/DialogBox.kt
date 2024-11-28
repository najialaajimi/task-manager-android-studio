package com.example.mytask.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.BaseColumns
import android.util.Log
import android.widget.Toast
import com.example.mytask.MainActivity
import com.example.mytask.R
import com.example.mytask.UpdateTaskActivity
import com.example.mytask.db.DBOpenHelper
import com.example.mytask.model.TaskModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

// Classe utilitaire pour afficher des boîtes de dialogue contextuelles.
class DialogBox {

    // Constante utilisée pour identifier les messages de journalisation.
    private val TAG = "DialogBox"

    /**
     * Affiche une boîte de dialogue permettant de modifier une tâche.
     * @param context Le contexte de l'activité appelante.
     * @param note L'objet TaskModel contenant les informations de la tâche.
     */
    fun editDialog(context: Context, note: TaskModel) {

        // Crée une boîte de dialogue Material Design avec un titre, un message et une icône.
        val dialog = MaterialAlertDialogBuilder(context)
        dialog.setTitle("Edit") // Titre de la boîte de dialogue.
        dialog.setMessage("Do you want to update?") // Message de confirmation.
        dialog.setIcon(R.drawable.ic_baseline_edit_24) // Icône affichée dans la boîte de dialogue.

        // Journalisation des données de la tâche pour le débogage.
        Log.d(TAG, note.id.toString())
        Log.d(TAG, note.title)
        Log.d(TAG, note.description)
        Log.d(TAG, note.selectDate)
        Log.d(TAG, note.selectTime)

        // Action pour le bouton "Update".
        dialog.setPositiveButton("Update") { _, _ ->
            // Crée un intent pour lancer l'activité UpdateTaskActivity avec les détails de la tâche.
            val intent = Intent(context, UpdateTaskActivity::class.java).apply {
                putExtra(BaseColumns._ID, note.id) // Ajoute l'ID de la tâche.
                putExtra(COLUMN_NAME_TITLE, note.title) // Ajoute le titre de la tâche.
                putExtra(COLUMN_NAME_DESCRIPTION, note.description) // Ajoute la description.
                putExtra(COLUMN_NAME_DATE, note.selectDate) // Ajoute la date.
                putExtra(COLUMN_NAME_TIME, note.selectTime) // Ajoute l'heure.
            }
            context.startActivity(intent) // Démarre l'activité.
            (context as Activity).finish() // Termine l'activité courante.
        }

        // Action pour le bouton "Cancel".
        dialog.setNeutralButton("Cancel") { _, _ ->
            // Affiche un message Toast pour indiquer l'annulation.
            Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show()
        }

        dialog.create() // Crée la boîte de dialogue.
        dialog.setCancelable(false) // Empêche la fermeture de la boîte de dialogue en cliquant à l'extérieur.
        dialog.show() // Affiche la boîte de dialogue.
    }

    /**
     * Affiche une boîte de dialogue permettant de supprimer une tâche.
     * @param context Le contexte de l'activité appelante.
     * @param note L'objet TaskModel contenant les informations de la tâche.
     */
    fun deleteDialog(context: Context, note: TaskModel) {

        // Initialise l'assistant de base de données pour gérer la suppression.
        val dbOpenHelper = DBOpenHelper(context)

        // Crée une boîte de dialogue Material Design avec un titre, un message et une icône.
        val dialog = MaterialAlertDialogBuilder(context)
        dialog.setTitle("Delete") // Titre de la boîte de dialogue.
        dialog.setMessage("Do you really want to delete?") // Message de confirmation.
        dialog.setIcon(R.drawable.ic_baseline_delete_forever_24) // Icône affichée dans la boîte de dialogue.

        // Action pour le bouton "Delete".
        dialog.setPositiveButton("Delete") { _, _ ->
            // Journalisation de l'ID de la tâche pour le débogage.
            Log.d(TAG, note.id.toString())
            // Supprime la tâche de la base de données.
            dbOpenHelper.deleteTask(note.id.toString())

            // Affiche un message Toast pour confirmer la suppression.
            Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()

            // Retourne à l'écran principal en démarrant l'activité MainActivity.
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            (context as Activity).finish() // Termine l'activité courante.
        }

        // Action pour le bouton "Cancel".
        dialog.setNeutralButton("Cancel") { _, _ ->
            // Affiche un message Toast pour indiquer l'annulation.
            Toast.makeText(context, "Cancelled!", Toast.LENGTH_SHORT).show()
        }

        dialog.create() // Crée la boîte de dialogue.
        dialog.setCancelable(false) // Empêche la fermeture de la boîte de dialogue en cliquant à l'extérieur.
        dialog.show() // Affiche la boîte de dialogue.
    }

}
