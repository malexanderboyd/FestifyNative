package com.opensource.boyd.festifynative

import android.app.Application
import com.facebook.stetho.Stetho

/**
 * Created by Boyd on 10/14/2017.
 */
class FestifyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
    }

}