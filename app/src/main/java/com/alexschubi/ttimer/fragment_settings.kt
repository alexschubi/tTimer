package com.alexschubi.ttimer

import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintSet.GONE
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import kotlinx.android.synthetic.main.main_toolbar.view.*

class fragment_settings : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        suppActionBar.customView.b_settings.visibility = View.GONE
        suppActionBar.customView.b_back.visibility = View.VISIBLE

        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == "pref_firebase_enabled") {
                Functions().applyFirebase()
                Log.d("Preferences", "applied Firebase settings")
            }
            if (key == "pref_sync_enabled") {
                Log.d("Preferences", "applied Sync settings")
            }
            if (key == "pref_sync_connection") {
                Log.d("Preferences", "trying Sync connection")
                try {
                    //TODO sync
                    // TODO password https://stackoverflow.com/a/4325239
                    //  TODO webdav
                    //   TODO CSV
                } catch (e: Exception) {
                    Toast.makeText(mContext, "could not reach WebDAV-CSV-File", Toast.LENGTH_SHORT)
                }
            }
            if (key == "pref_notifications_enabled") {//TODO notifications
                Log.d("Preferences", "applied notification settings")
            }
            if (key == "pref_theme") {
                Functions().applyTheme()
                Log.d("Preferences", "applied Theme settings")
            }
        }

        suppActionBar.customView.b_back.setOnClickListener() {
            NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_fragment_settings_to_ItemList)
            suppActionBar.customView.b_settings.visibility = View.VISIBLE
            suppActionBar.customView.b_back.visibility = View.GONE
        }
    }
    
}