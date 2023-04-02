package com.lazday.moneykotlin.preferences

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager (context: Context) {

    private val PREFS_NAME = "money.pref"
    private var sharedPref: SharedPreferences
    val editor: SharedPreferences.Editor

    init {
        sharedPref = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        editor = sharedPref.edit()
    }

    fun put(key: String, value: String) {
        editor.putString(key, value)
            .apply()
    }

    fun put(key: String, value: Boolean) {
        editor.putBoolean(key, value)
            .apply()
    }

    fun put(key: String, value: Int) {
        editor.putInt(key, value)
                .apply()
    }

    fun getBoolean(key: String): Boolean {
        return sharedPref.getBoolean(key, false)
    }

    fun getString(key: String): String? {
        return sharedPref.getString(key, null)
    }

    fun getInt(key: String): Int {
        return sharedPref.getInt(key, 0)
    }

    fun clear() {
        editor.putBoolean("pref_is_login", false)
                .apply()
//        editor.clear()
//            .apply()
    }
}