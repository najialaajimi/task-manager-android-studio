// Package où se trouve le modèle TaskModel
package com.example.mytask.model

/**
 * Représente un modèle de tâche dans l'application.
 *
 * @property id Identifiant unique de la tâche.
 * @property title Titre de la tâche.
 * @property description Description détaillée de la tâche.
 * @property selectDate Date sélectionnée pour la tâche (format : yyyy-MM-dd).
 * @property selectTime Heure sélectionnée pour la tâche (format : HH:mm).
 * @property priority Priorité de la tâche (ex. : haute, moyenne, basse).
 * @property status Statut de la tâche (ex. : en cours, terminée, à faire).
 */
data class TaskModel(
    val id: Int,              // Identifiant unique de la tâche
    val title: String,        // Titre de la tâche
    val description: String,  // Description détaillée de la tâche
    val selectDate: String,   // Date sélectionnée pour la tâche
    val selectTime: String,   // Heure sélectionnée pour la tâche
    val priority: String,     // Priorité de la tâche
    val status: String        // Statut actuel de la tâche
)