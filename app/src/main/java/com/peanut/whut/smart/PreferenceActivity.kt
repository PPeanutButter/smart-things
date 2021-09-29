package com.peanut.whut.smart

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

open class PreferenceActivity : AppCompatActivity() {
    protected var onPreferenceLoad: ((PreferenceFragment)->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(findViewById(R.id.topAppBar))
        supportActionBar?.title = intent.getStringExtra("ActivityName")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, PreferenceFragment(onPreferenceLoad))
            .commit()
    }

    class PreferenceFragment(private val onLoad: ((PreferenceFragment)->Unit)? = null) : PreferenceFragmentCompat(){

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.empty, rootKey)
            onLoad?.invoke(this)
        }
    }

}