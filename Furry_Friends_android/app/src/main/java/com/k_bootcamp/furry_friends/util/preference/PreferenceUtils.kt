package com.k_bootcamp.furry_friends.util.preference

import android.app.Activity
import android.content.Context

class PreferenceUtils(context: Context) {
    private val prefName = "prefs"
    private val prefs = context.getSharedPreferences(prefName, Activity.MODE_PRIVATE)

    var session:String?
        get() = prefs.getString("session", null)
        set(value){
            prefs.edit().putString("session", value).apply()
        }
    var userId:String?
        get() = prefs.getString("userId", null)
        set(value) {
            prefs.edit().putString("userId", value).apply()
        }
    var email:String?
        get() = prefs.getString("email", null)
        set(value) {
            prefs.edit().putString("email", value).apply()
        }
    var name:String?
        get() = prefs.getString("name", null)
        set(value) {
            prefs.edit().putString("name", value).apply()
        }


    fun clear() {
        prefs.edit().clear().apply()
    }
}