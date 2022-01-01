package com.peanut.exercise.manager

import android.content.Context
import android.content.SharedPreferences

object SettingManager {
    private var sharedPreferences: SharedPreferences? = null

    fun __init__(context: Context) {
        if (null == sharedPreferences)
            sharedPreferences = context.getSharedPreferences(
                context.packageName + "_preferences",
                Context.MODE_PRIVATE
            )
    }

    /**
     * 封装一些方法
     */
    fun has(key: String,default:Boolean = false):Boolean = getValue(key,default)
    fun havent(key: String):Boolean = has(key).not()
    operator fun<T> set(key: String, value: T) = setValue(key, value)
    fun map(key: String):Set<String> = getValue(key, emptySet())
    fun plus(key: String,newValue:String){
        this@SettingManager[key] = map(key).plus(newValue)
    }
    operator fun plusAssign(pair: Pair<String,String>) {
        this@SettingManager[pair.first] = map(pair.first).plus(pair.second)
    }
    fun getString(key: String) = getValue(key,"")


    /**
     * 原始方法
     */
    fun <T> getValue(key: String, defaultValue: T,dependency: String? = null): T {
        if (dependency !=null && havent(dependency))
            return defaultValue
        @Suppress("UNCHECKED_CAST")
        return when (defaultValue) {
            is Boolean -> sharedPreferences!!.getBoolean(key, defaultValue) as T
            is String -> sharedPreferences!!.getString(key, defaultValue) as T
            is Int -> sharedPreferences!!.getInt(key, defaultValue) as T
            is Float -> sharedPreferences!!.getFloat(key, defaultValue) as T
            is Long -> sharedPreferences!!.getLong(key, defaultValue) as T
            else -> sharedPreferences!!.getStringSet(key, defaultValue as Set<String>) as T
        }
    }

    private fun <T> setValue(key: String, value: T) {
        @Suppress("UNCHECKED_CAST")
        when (value) {
            is Boolean -> sharedPreferences?.edit()?.putBoolean(key, value)?.apply()
            is String -> sharedPreferences?.edit()?.putString(key, value)?.apply()
            is Int -> sharedPreferences?.edit()?.putInt(key, value)?.apply()
            is Float -> sharedPreferences?.edit()?.putFloat(key, value)?.apply()
            is Long -> sharedPreferences?.edit()?.putLong(key, value)?.apply()
            else -> sharedPreferences?.edit()?.putStringSet(key, value as Set<String>)?.apply()
        }
    }
}