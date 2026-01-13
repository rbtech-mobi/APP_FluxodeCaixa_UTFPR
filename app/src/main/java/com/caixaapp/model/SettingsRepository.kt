package com.caixaapp.model

import android.content.Context
import org.json.JSONObject
import java.io.IOException

class SettingsRepository(private val context: Context) {

    /**
     * Reads the 'SecureLogin' parameter from the assets.
     * @return 1 for secure login, 0 for simple login. Defaults to 1 (secure) on error.
     */
    fun getSecureLoginStatus(): Int {
        return try {
            val jsonString =
                context.assets.open("parametros_app.json").bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)
            jsonObject.getInt("SecureLogin")
        } catch (ioException: IOException) {
            // Default to secure if the file is missing or can't be read
            1
        }
    }
}