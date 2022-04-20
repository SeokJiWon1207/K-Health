package com.example.k_health

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtil(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("record", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        prefs.edit().putString(key, str).apply()
    }

    fun getStringSet(key: String, defValue: MutableSet<String>): MutableSet<String> {
        return prefs.getStringSet(key, defValue)!!.toMutableSet()
    }

    fun setStringSet(key: String, set: MutableSet<String>) {
        prefs.edit().putStringSet(key, set).apply()
    }

    fun getAll() = prefs.all
}
