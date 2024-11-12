package net.projectlcs.lcs

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.annotation.CallSuper
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner

abstract class AbstractLuaService : Service(),
    LifecycleOwner,
    SavedStateRegistryOwner, ViewModelStoreOwner {

    @Suppress("LeakingThis")
    private val lifecycleDispatcher = ServiceLifecycleDispatcher(this)

    @Suppress("LeakingThis")
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    override val lifecycle: Lifecycle = lifecycleDispatcher.lifecycle

    // region main lifecycle
    @CallSuper
    override fun onCreate() {
        savedStateRegistryController.performRestore(null)
        lifecycleDispatcher.onServicePreSuperOnCreate()
        super.onCreate()
    }

    @CallSuper
    @Suppress("OVERRIDE_DEPRECATION", "DEPRECATION")
    override fun onStart(intent: Intent?, startId: Int) {
        lifecycleDispatcher.onServicePreSuperOnStart()
        super.onStart(intent, startId)
    }

    @CallSuper
    override fun onDestroy() {
        lifecycleDispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override val viewModelStore: ViewModelStore = ViewModelStore()
}
