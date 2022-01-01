package com.peanut.whut.smart

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        LogService.__init__(this.cacheDir.path+"/log.txt")
        LogService.log("App started")
        SettingManager.__init__(this)
    }

}