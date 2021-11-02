package com.peanut.whut.smart

import androidx.preference.EditTextPreference
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

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

    fun buildPost(api: String, body: JSONObject, auth: String): Http {
        return Http()
            .setPost(
                api, body.toString()
                    .toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            )
            .setHeader("authorization", auth)
            .setHeader("x-mobile-id", "ce6d36d2-7d19-3a6e-82de-12c3be83ddb9")
            .setHeader(
                "user-agent",
                "MIX 2(Android/9) (com.midea.vm.washer/2.1.32) Weex/0.28.0.1 1080x2030"
            )
            .setHeader("x-app-version", "2.1.32")
            .setHeader("x-app-code", "CA")
    }

}