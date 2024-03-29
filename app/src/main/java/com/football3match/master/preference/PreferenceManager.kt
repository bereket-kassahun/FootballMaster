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
        return preference.getString(CURRENT_LANG, "en") ?: "en"
    }
    fun getBestTime(): Int{
        return preference.getInt(BEST_TIME, 20) ?: 0
    }
    fun getSoundEnabled(): Boolean{
        return preference.getBoolean(SOUND_ENABLED, true)
    }

    fun setCurrentLang(lang: String){
        editor.putString(CURRENT_LANG, lang)
        editor.apply()
    }
    fun setBestTime(time: Int){
        editor.putInt(BEST_TIME, time)
        editor.apply()
    }
    fun setSoundEnabled(value: Boolean){
        editor.putBoolean(SOUND_ENABLED, value)
        editor.apply()
    }
}