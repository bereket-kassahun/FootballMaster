package com.football3match.master.preference

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private var preference: SharedPreferences
    var editor: SharedPreferences.Editor

    val PREFERENCE_NAME = "SETTINGS"
    val CURRENT_LANG = "CURRENT_LANG"
    val SOUND_ENABLED = "SOUND_ENABLED"
    val BEST_TIME = "BEST_TIME"

    init {
        preference = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        editor = preference.edit()
    }

    fun getCurrentLang(): String{
        return preference.getString(CURRENT_LANG, "eng") ?: "eng"
    }
    fun getBestTime(): String{
        return preference.getString(BEST_TIME, "00:00") ?: "00:00"
    }
    fun getSoundEnabled(): Boolean{
        return preference.getBoolean(SOUND_ENABLED, false) ?: false
    }

    fun setCurrentLang(lang: String){
        editor.putString(CURRENT_LANG, lang)
        editor.apply()
    }
    fun setBestTime(time: String){
        editor.putString(BEST_TIME, time)
        editor.apply()
    }
    fun setSoundEnabled(value: Boolean){
        editor.putBoolean(SOUND_ENABLED, value)
        editor.apply()
    }
}