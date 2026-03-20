package ua.kpi.ipze.part.providers

import android.annotation.SuppressLint
import android.content.Context

@SuppressLint("StaticFieldLeak")
class GlobalApplicationContext {
    companion object {
        lateinit var context: Context
            private set

        fun init(context: Context) {
            if (::context.isInitialized) return
            this.context = context.applicationContext
        }
    }
}