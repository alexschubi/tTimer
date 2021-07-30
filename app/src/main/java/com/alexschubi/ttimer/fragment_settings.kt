package com.alexschubi.ttimer

import android.opengl.Visibility
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
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

        suppActionBar.customView.b_back.setOnClickListener() {
            NavHostFragment.findNavController(nav_host_fragment).navigate(R.id.action_fragment_settings_to_ItemList)
            suppActionBar.customView.b_settings.visibility = View.VISIBLE
            suppActionBar.customView.b_back.visibility = View.GONE
            Functions().applyFirebase()
        }
    }
}