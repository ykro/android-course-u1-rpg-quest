package com.awa2027.rpgquest

import android.app.Application
import com.awa2027.rpgquest.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RpgQuestApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@RpgQuestApp)
            modules(appModule)
        }
    }
}
