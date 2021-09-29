package com.peanut.whut.smart

import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen

object Tools {
    const val prefsNAME = "com.peanut.whut.smart_preferences"

    fun PreferenceScreen.preference(
        key: String,
        title: String,
        summary: String = "",
        onclick: (() -> Unit)? = null
    ) {
        this.addPreference(Preference(this.context).apply {
            this.key = key
            this.title = title
            this.summary = summary
            this.isIconSpaceReserved = false
            this.onPreferenceClickListener = Preference.OnPreferenceClickListener {
                onclick?.invoke()
                true
            }
        })
    }

    fun PreferenceScreen.editPreference(key: String, title: String, summary: String = "") {
        this.addPreference(EditTextPreference(this.context).apply {
            this.key = key
            this.title = title
            this.dialogTitle = title
            this.summary = summary
            this.setSummaryProvider {
                if (this.text.isNullOrEmpty()) summary else this.text
            }
            this.isIconSpaceReserved = false
        })
    }

}