package com.mor.avoidtherock

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class ScoreEntry(val name: String, val score: Int) : Comparable<ScoreEntry> {
    override fun compareTo(other: ScoreEntry): Int {
        // Sorts descending: Higher score comes first
        return other.score.compareTo(this.score)
    }
}

object ScoreManager {
    private const val PREFS_NAME = "GamePrefs"
    private const val KEY_SCORES = "SCORES_LIST"
    private const val MAX_SCORES = 10

    private val gson = Gson()

    // Loads existing scores, adds the new one, sorts them, and saves the top 10.
    fun addScore(context: Context, name: String, score: Int) {
        // 1. Get the current list of scores
        val currentScores = getTopScores(context).toMutableList()
        // 2. Add the new player's score
        currentScores.add(ScoreEntry(name, score))
        // 3. Sort the list (High to Low)
        currentScores.sort()
        // 4. Keep only the top 10 scores
        val top10 = currentScores.take(MAX_SCORES)
        // 5. Save the updated list back to SharedPreferences
        saveScores(context, top10)
    }


    // Convert the list of objects into a JSON string and save it.
    private fun saveScores(context: Context, list: List<ScoreEntry>) {
        val jsonString = gson.toJson(list)

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SCORES, jsonString).apply()
    }

    // Read the JSON string from storage and convert it back to a List of objects.
    fun getTopScores(context: Context): List<ScoreEntry> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonString = prefs.getString(KEY_SCORES, null) ?: return emptyList()

        val type = object : TypeToken<List<ScoreEntry>>() {}.type

        // Convert the JSON String back to a List object
        return gson.fromJson(jsonString, type)
    }
}