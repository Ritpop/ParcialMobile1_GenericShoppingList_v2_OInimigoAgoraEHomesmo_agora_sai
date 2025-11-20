package com.example.hypergenericlistforbuyingstuff

import android.app.Application
import com.example.hypergenericlistforbuyingstuff.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class IncrediblySuperGenericShoppingList : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@IncrediblySuperGenericShoppingList)
            modules(appModule)
        }
    }
}