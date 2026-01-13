package com.caixaapp.util

import android.content.Context
import com.caixaapp.model.Person
import com.caixaapp.repository.FirebaseConfig
import org.json.JSONArray
import org.json.JSONObject

object JsonUtils {
    fun loadPeople(context: Context): List<Person> {
        val json = readAsset(context, "pessoas.json")
        val array = JSONArray(json)
        val people = mutableListOf<Person>()
        for (index in 0 until array.length()) {
            val item = array.getJSONObject(index)
            people.add(
                Person(
                    id = item.getString("id"),
                    nome = item.getString("nome"),
                    descricao = item.optString("descricao", null)
                )
            )
        }
        return people
    }

    fun loadRateio(context: Context): Map<String, Double> {
        val json = readAsset(context, "rateio_familia.json")
        val obj = JSONObject(json)
        val result = mutableMapOf<String, Double>()
        obj.keys().forEach { key ->
            result[key] = obj.getDouble(key)
        }
        return result
    }

    fun loadFirebaseConfig(context: Context): FirebaseConfig {
        val json = readAsset(context, "firebase_config.json")
        val obj = JSONObject(json)
        return FirebaseConfig(
            databaseUrl = obj.getString("databaseUrl"),
            enabled = obj.getBoolean("enabled")
        )
    }

    private fun readAsset(context: Context, fileName: String): String {
        return context.assets.open(fileName).bufferedReader().use { it.readText() }
    }
}
