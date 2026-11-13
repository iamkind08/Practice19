package com.example.practice19_1

import android.content.Context
import android.content.SharedPreferences
import android.widget.EditText

object Transform {
    const val APP_PREFERENCES = "mysettings"
    const val LOGIN = "login"
    const val PASSWORD = "password"

    fun isStringNotNull(string: String?): Boolean {
        return !string.isNullOrEmpty()
    }

    fun saveUser(sp: SharedPreferences, login: String, password: String) {
        val editor = sp.edit()
        editor.putString(LOGIN, login)
        editor.putString(PASSWORD, password)
        editor.apply()
    }

    fun parseIntOrDefault(whatParse: String?, defaultValue: Int): Int {
        return whatParse?.toIntOrNull() ?: defaultValue
    }
}