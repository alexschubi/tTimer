package xyz.alexschubi.ttimer

import android.app.ActionBar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.add_toolbar.view.*

class fragment_settings : PreferenceFragmentCompat(), ExitWithAnimation{
    override var posX: Int? = null
    override var posY: Int? = null
    override fun isToBeExitedWithAnimation(): Boolean = true

    companion object {
        @JvmStatic
        fun newInstance(exit: IntArray? = null): fragment_settings = fragment_settings().apply {
            if (exit != null && exit.size == 2) {
                posX = exit[0]
                posY = exit[1]
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal()
        suppActionBar.setCustomView(R.layout.add_toolbar)
        suppActionBar.customView.b_back.setOnClickListener() {
            suppActionBar.setCustomView(R.layout.list_toolbar)
            parentFragmentManager.popBackStack()
        }
    }
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
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
    }
    
}