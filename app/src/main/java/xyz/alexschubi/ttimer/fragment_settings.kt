package xyz.alexschubi.ttimer

import android.app.ActionBar
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceFragmentCompat
import kotlinx.android.synthetic.main.add_toolbar.view.*
import android.util.TypedValue




class fragment_settings : PreferenceFragmentCompat(), ExitWithAnimation{
    override var posX: Int? = null
    override var posY: Int? = null
    var startPosX: Int = 0
    var startPosY: Int = 0
    override fun isToBeExitedWithAnimation(): Boolean = true

    companion object {
        @JvmStatic
        fun newInstance(startPos: IntArray? = null, exitPos: IntArray? = null): fragment_settings = fragment_settings().apply {
            if (exitPos != null && exitPos.size == 2) {
                posX = exitPos[0]
                posY = exitPos[1]
            }
            if (startPos != null && startPos.size == 2) {
                startPosX = startPos[0]!!
                startPosY = startPos[1]!!
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.startCircularReveal(startPosX, startPosY)
        mapplication.setTheme(R.style.tPreferenceTheme)


        when (mapplication.resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK)) {
            Configuration.UI_MODE_NIGHT_YES -> {
                view.setBackgroundColor(mapplication.resources.getColor(R.color.tt_back_dark, mapplication.theme))
            }
            Configuration.UI_MODE_NIGHT_NO -> {
                view.setBackgroundColor(mapplication.resources.getColor(R.color.dn_fragment_background, mapplication.theme))
            }
            Configuration.UI_MODE_NIGHT_UNDEFINED -> { Log.i("Theme", "no UI-day/night-mode selected")}
        }

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
                    //Todo save credentials secured
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