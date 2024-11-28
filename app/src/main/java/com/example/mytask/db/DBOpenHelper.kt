// Déclaration du package pour organiser les classes liées à la base de données
package com.example.mytask.db

// Importation des classes nécessaires pour manipuler les bases de données SQLite et les autres fonctionnalités Android
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.util.Log
import com.example.mytask.model.TaskModel
import com.example.mytask.utils.COLUMN_NAME_DATE
import com.example.mytask.utils.COLUMN_NAME_DESCRIPTION
import com.example.mytask.utils.COLUMN_NAME_TIME
import com.example.mytask.utils.COLUMN_NAME_TITLE
import com.example.mytask.utils.COLUMN_NAME_PRIORITY
import com.example.mytask.utils.COLUMN_NAME_STATUS
import com.example.mytask.utils.TABLE_NAME

// Requête SQL pour créer une table nommée TABLE_NAME avec des colonnes spécifiques
private const val SQL_CREATE_ENTRIES =
    "CREATE TABLE $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY," + // Colonne ID primaire
            "$COLUMN_NAME_TITLE TEXT," + // Colonne pour le titre
            "$COLUMN_NAME_DESCRIPTION TEXT," + // Colonne pour la description
            "$COLUMN_NAME_DATE TEXT," + // Colonne pour la date
            "$COLUMN_NAME_TIME TEXT," + // Colonne pour l'heure
            "$COLUMN_NAME_PRIORITY TEXT," + // Colonne pour la priorité
            "$COLUMN_NAME_STATUS TEXT)" // Colonne pour le statut

// Requête SQL pour supprimer la table si elle existe
private const val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS $TABLE_NAME"

// Classe DBOpenHelper pour gérer les opérations de la base de données SQLite
class DBOpenHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    companion object {
        // Version et nom de la base de données
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "TaskManager.dp"
    }

    // Création de la base de données
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_ENTRIES) // Exécution de la requête pour créer la table
    }
    // Gestion des mises à jour de la base de données
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_ENTRIES) // Suppression de l'ancienne table
        onCreate(db) // Création de la nouvelle table
    }
    // Gestion des rétrogradations de version de la base de données
    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }
    // Méthode pour ajouter une tâche dans la base de données
    fun addTask(title: String, description: String, selectedDate: String, selectedTime: String, priority: String, status: String) {

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME_TITLE, title)
            put(COLUMN_NAME_DESCRIPTION, description)
            put(COLUMN_NAME_DATE,selectedDate)
            put(COLUMN_NAME_TIME,selectedTime)
            put(COLUMN_NAME_PRIORITY, priority)
            put(COLUMN_NAME_STATUS, status)
        }
        db?.insert(TABLE_NAME, null, values)
        db.close()

    }
    // Méthode pour lire les tâches selon leur statut et priorité
    fun readTask(status: String, priority: String): MutableList<TaskModel> {
        val db = this.readableDatabase
        val cursorTask: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE TaskStatus = ? AND TaskPriority = ?", arrayOf(status, priority))
        val notesList: MutableList<TaskModel> = mutableListOf()

        if (cursorTask.moveToFirst()) {
            do {
                Log.d("DPOpenHelper", cursorTask.getString(0))
                notesList.add(
                    TaskModel(
                        cursorTask.getInt(0),
                        cursorTask.getString(1),
                        cursorTask.getString(2),
                        cursorTask.getString(3),
                        cursorTask.getString(4),
                        cursorTask.getString(5),
                        cursorTask.getString(6),
                    )
                )
            } while (cursorTask.moveToNext())
        }
        cursorTask.close()
        return notesList
    }
    // Méthode pour compter les tâches selon leur statut et priorité
    fun readTaskCount(status: String, priority: String): Int {
        val db = this.readableDatabase
        val cursorTask: Cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME WHERE TaskStatus = ? AND TaskPriority = ?", arrayOf(status, priority))
        var count = 0

        if (cursorTask.moveToFirst()) {
            count = cursorTask.getInt(0)
        }
        cursorTask.close()
        return count
    }
    // Méthode pour lire les tâches par statut
    fun readTaskByStatus(status: String): MutableList<TaskModel> {
        val db = this.readableDatabase
        val cursorTask: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE TaskStatus = ?", arrayOf(status))
        val notesList: MutableList<TaskModel> = mutableListOf()

        if (cursorTask.moveToFirst()) {
            do {
                Log.d("DPOpenHelper", cursorTask.getString(0))
                notesList.add(
                    TaskModel(
                        cursorTask.getInt(0),
                        cursorTask.getString(1),
                        cursorTask.getString(2),
                        cursorTask.getString(3),
                        cursorTask.getString(4),
                        cursorTask.getString(5),
                        cursorTask.getString(6),
                    )
                )
            } while (cursorTask.moveToNext())
        }
        cursorTask.close()
        return notesList
    }

    // Méthodes similaires pour lire par date ou seulement par date
    fun readTaskByDate(status: String, selectDate: String): MutableList<TaskModel> {
        val db = this.readableDatabase
        val cursorTask: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE TaskStatus = ? AND TaskDate = ?", arrayOf(status, selectDate))
        val notesList: MutableList<TaskModel> = mutableListOf()

        if (cursorTask.moveToFirst()) {
            do {
                Log.d("DPOpenHelper", cursorTask.getString(0))
                notesList.add(
                    TaskModel(
                        cursorTask.getInt(0),
                        cursorTask.getString(1),
                        cursorTask.getString(2),
                        cursorTask.getString(3),
                        cursorTask.getString(4),
                        cursorTask.getString(5),
                        cursorTask.getString(6)
                    )
                )
            } while (cursorTask.moveToNext())
        }
        cursorTask.close()
        return notesList
    }

    fun readTaskByOnlyDate(selectDate: String): MutableList<TaskModel> {
        val db = this.readableDatabase
        val cursorTask: Cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE TaskDate = ?", arrayOf(selectDate))
        val notesList: MutableList<TaskModel> = mutableListOf()

        if (cursorTask.moveToFirst()) {
            do {
                Log.d("DPOpenHelper", cursorTask.getString(0))
                notesList.add(
                    TaskModel(
                        cursorTask.getInt(0),
                        cursorTask.getString(1),
                        cursorTask.getString(2),
                        cursorTask.getString(3),
                        cursorTask.getString(4),
                        cursorTask.getString(5),
                        cursorTask.getString(6)
                    )
                )
            } while (cursorTask.moveToNext())
        }
        cursorTask.close()
        return notesList
    }
    // Méthode pour mettre à jour une tâche
    fun updateTask(
        id: String,
        title: String,
        description: String,
        selectedDate: String,
        selectedTime: String,
        status: String
    ) {

        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME_TITLE, title)
            put(COLUMN_NAME_DESCRIPTION, description)
            put(COLUMN_NAME_DATE,selectedDate)
            put(COLUMN_NAME_TIME,selectedTime)
            put(COLUMN_NAME_STATUS, status)
        }
        try {
            db?.update(TABLE_NAME, values, "_id = ?", arrayOf(id))
            db.close()
        } catch (e: Exception) {
            Log.d("DBOpenHelper", e.message.toString())
        }
    }
    // Méthode pour supprimer une tâche
    fun deleteTask(id: String) {

        val db = this.writableDatabase
        try {
            db?.delete(TABLE_NAME, "_id = ?", arrayOf(id))
            db.close()
        } catch (e: Exception) {
            Log.d("DBOpenHelper", e.message.toString())
        }

    }


}