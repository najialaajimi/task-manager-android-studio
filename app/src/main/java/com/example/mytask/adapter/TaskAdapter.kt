// Package contenant l'adaptateur pour le RecyclerView
package com.example.mytask.adapter

// Import des bibliothèques nécessaires
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.provider.BaseColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.mytask.R
import com.example.mytask.UpdateTaskActivity
import com.example.mytask.model.TaskModel
import com.example.mytask.utils.COLUMN_NAME_DATE
import com.example.mytask.utils.COLUMN_NAME_DESCRIPTION
import com.example.mytask.utils.COLUMN_NAME_STATUS
import com.example.mytask.utils.COLUMN_NAME_TIME
import com.example.mytask.utils.COLUMN_NAME_TITLE

// Classe de l'adaptateur pour afficher une liste de tâches dans un RecyclerView
class TaskAdapter(
    private val context: Context, // Contexte de l'application ou de l'activité
    private val dataSet: List<TaskModel> // Liste des tâches à afficher
) : RecyclerView.Adapter<TaskAdapter.NoteViewHolder>() {

    // Classe interne pour gérer les vues individuelles dans le RecyclerView
    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textTitle: TextView = view.findViewById(R.id.text_title) // Titre de la tâche
        val textDescription: TextView = view.findViewById(R.id.text_description) // Description de la tâche
        val textDate: TextView = view.findViewById(R.id.viewDate) // Date sélectionnée pour la tâche
        val textTime: TextView = view.findViewById(R.id.viewTime) // Heure sélectionnée pour la tâche
        val textStatus: TextView = view.findViewById(R.id.viewStatus) // Statut de la tâche
        val cardView = view.findViewById<CardView>(R.id.mainCardView) // Conteneur de carte pour la tâche
    }

    // Méthode pour créer une nouvelle vue (appelée uniquement pour les nouvelles vues)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_recycler_single_item, parent, false) // Chargement de la vue à partir du layout XML
        return NoteViewHolder(adapterLayout)
    }

    // Méthode pour lier les données à une vue existante
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val item = dataSet[position] // Récupération de la tâche actuelle

        // Mise à jour des éléments visuels avec les données de la tâche
        holder.textTitle.text = item.title
        holder.textDescription.text = item.description
        holder.textDate.text = item.selectDate
        holder.textTime.text = item.selectTime
        holder.textStatus.text = item.status

        // Définir un clic sur la carte pour ouvrir l'activité de mise à jour de la tâche
        holder.cardView.setOnClickListener {
            val intent = Intent(context, UpdateTaskActivity::class.java).apply {
                // Ajouter les informations de la tâche en tant qu'extras dans l'intention
                putExtra(BaseColumns._ID, item.id)
                putExtra(COLUMN_NAME_TITLE, item.title)
                putExtra(COLUMN_NAME_DESCRIPTION, item.description)
                putExtra(COLUMN_NAME_DATE, item.selectDate)
                putExtra(COLUMN_NAME_TIME, item.selectTime)
                putExtra(COLUMN_NAME_STATUS, item.status)
            }
            // Démarrer l'activité de mise à jour et terminer l'activité actuelle
            context.startActivity(intent)
            (context as Activity).finish()
        }
    }

    // Retourner le nombre d'éléments dans la liste
    override fun getItemCount(): Int {
        return dataSet.size
    }
}
