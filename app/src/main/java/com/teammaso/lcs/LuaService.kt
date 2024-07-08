package com.teammaso.lcs

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class LuaService: AccessibilityService() {
    companion object {
        var INSTANCE: LuaService? = null
    }

    val lua = LuaHandler.createInstance()

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    override fun onAccessibilityEvent(p0: AccessibilityEvent?) {

    }

    override fun onInterrupt() {

    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null
    }
}