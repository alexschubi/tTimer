package xyz.alexschubi.ttimer.settings

import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.preference.PreferenceFragmentCompat
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.Preference
import androidx.preference.PreferenceManager
import xyz.alexschubi.ttimer.*


class FragmentSettings : PreferenceFragmentCompat(), ExitWithAnimation {
    override var posX: Int? = null
    override var posY: Int? = null
    var startPosX: Int = 0
    var startPosY: Int = 0
    override fun isToBeExitedWithAnimation(): Boolean = true

    companion object {
        @JvmStatic
        fun newInstance(startPos: IntArray? = null, exitPos: IntArray? = null): FragmentSettings = FragmentSettings().apply {
            if (exitPos != null && exitPos.size == 2) {
                posX = exitPos[0]
                posY = exitPos[1]
            }
            if (startPos != null && startPos.size == 2) {
                startPosX = startPos[0]
                startPosY = startPos[1]
            }
        }
    }
//TODO add DateFormat as Preference


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(startPosX, startPosY)

        mapplication.setTheme(R.style.tPreferenceTheme)
        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> view.setBackgroundColor(
                mapplication.resources.getColor(
                    R.color.d_background, mapplication.theme))
            AppCompatDelegate.MODE_NIGHT_YES -> view.setBackgroundColor(
                mapplication.resources.getColor(
                    R.color.n_background, mapplication.theme))
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                when (mapplication.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> { view.setBackgroundColor(
                        mapplication.resources.getColor(
                            R.color.n_background, mapplication.theme)) }
                    Configuration.UI_MODE_NIGHT_NO -> { view.setBackgroundColor(
                        mapplication.resources.getColor(
                            R.color.d_background, mapplication.theme)) }
                }
            }
            AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY -> {
                when (mapplication.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
                    Configuration.UI_MODE_NIGHT_YES -> { view.setBackgroundColor(
                        mapplication.resources.getColor(
                            R.color.n_background, mapplication.theme)) }
                    Configuration.UI_MODE_NIGHT_NO -> { view.setBackgroundColor(
                        mapplication.resources.getColor(
                            R.color.d_background, mapplication.theme)) }
                }
            }
        }
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener { sharedPreferences, key ->
            var mPrefs = localDB.preferencesDAO().getLast()
            if (key == "pref_firebase_enabled") { //TODO
                localDB.preferencesDAO().getLast().FirebaseEnabled = PreferenceManager.getDefaultSharedPreferences(
                    mapplication.applicationContext).getBoolean(key, false)
                Functions().applyFirebase()
                Log.d("Preferences", "applied Firebase settings")

                val bFirebase = PreferenceManager
                    .getDefaultSharedPreferences(mapplication.applicationContext).getBoolean(key, false)
                mPrefs = mPrefs.apply{FirebaseEnabled = bFirebase }
                localDB.preferencesDAO().update(mPrefs)

                Log.d("Preferences", "applied Sync settings")
            }
            if (key == "pref_sync_enabled") {
                val bSync = PreferenceManager
                    .getDefaultSharedPreferences(mapplication.applicationContext).getBoolean(key, false)
                mPrefs = mPrefs.apply{SyncEnabled = bSync }
                localDB.preferencesDAO().update(mPrefs)
                //TODO implement synchronisation
                Log.d("Preferences", "applied Sync settings")
            }
            if (key == "pref_notifications_enabled") {
                val bNotification = PreferenceManager
                    .getDefaultSharedPreferences(mapplication.applicationContext).getBoolean(key, true)
                mPrefs = mPrefs.apply{Notifications = bNotification }
                localDB.preferencesDAO().update(mPrefs)
                Functions().applyTheme()
                Log.d("Preferences", "apply Notification settings")
            }
            if (key == "pref_theme") {
                val sTheme = PreferenceManager
                    .getDefaultSharedPreferences(mapplication.applicationContext).getString(key, "followSystem")!!
                mPrefs = mPrefs.apply{Theme = sTheme}
                localDB.preferencesDAO().update(mPrefs)
                Functions().applyTheme()
                Log.d("Preferences", "apply Theme settings")
            }

        }

        /*val connectionButton = findPreference<Preference>("sync_connection_click")
        if (connectionButton != null) {
            connectionButton.setOnPreferenceClickListener {

                // TODO implement remote storage
                // password https://stackoverflow.com/a/4325239
                // webdav
                // save credentials secured
                true
            }
        }*/
    }
}