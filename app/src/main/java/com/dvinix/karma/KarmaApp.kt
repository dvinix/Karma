package com.dvinix.karma

import android.app.Application
import com.dvinix.karma.data.local.KarmaDatabase

class KarmaApp : Application() {

    val database: KarmaDatabase by lazy {
        KarmaDatabase.getDatabase(this)
    }
    override fun onCreate() {
        super.onCreate()
        // Initialize things like DI, Logger, etc.
    }
}
