package net.projectlcs.lcs

import android.app.Application

class LCS : Application() {
    init {
        instance = this
    }

    companion object {
        lateinit var instance: LCS
            private set
    }
}