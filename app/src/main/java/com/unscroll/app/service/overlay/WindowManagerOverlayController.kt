package com.unscroll.app.service.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.unscroll.app.domain.model.Insult
import com.unscroll.app.domain.model.OverlayResponse
import com.unscroll.app.domain.model.SessionStats
import com.unscroll.app.ui.overlay.AmbientOverlayScreen
import com.unscroll.app.ui.theme.UnscrollTheme

class WindowManagerOverlayController(
    private val context: Context
) : OverlayController, LifecycleOwner, ViewModelStoreOwner, SavedStateRegistryOwner {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val mainHandler = Handler(Looper.getMainLooper())

    /**
     * Read from the monitoring poll on [kotlinx.coroutines.Dispatchers.Default] and
     * written on the main thread, so visibility must be explicit.
     */
    @Volatile
    private var composeView: ComposeView? = null

    @Volatile
    private var isShowing = false

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val store = ViewModelStore()
    private val savedStateRegistryController = SavedStateRegistryController.create(this)

    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore get() = store
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    init {
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
    }

    override fun showOverlay(
        stats: SessionStats,
        insult: Insult,
        onDismiss: (OverlayResponse) -> Unit
    ) {
        if (isShowing) return

        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        val view = ComposeView(context).apply {
            setViewTreeLifecycleOwner(this@WindowManagerOverlayController)
            setViewTreeViewModelStoreOwner(this@WindowManagerOverlayController)
            setViewTreeSavedStateRegistryOwner(this@WindowManagerOverlayController)

            setContent {
                UnscrollTheme {
                    AmbientOverlayScreen(
                        stats = stats,
                        insult = insult,
                        onResponseClick = { response ->
                            dismissOverlay()
                            onDismiss(response)
                        }
                    )
                }
            }
        }

        try {
            windowManager.addView(view, layoutParams)
            composeView = view
            isShowing = true
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Safe to call from any thread. The monitoring loop dismisses on lock from a
     * background dispatcher, and [WindowManager.removeView] must run on the thread
     * that added the view, so the removal is marshalled onto the main looper.
     *
     * The target view is captured up front so a queued removal can never tear down
     * a newer overlay that replaced it in the meantime.
     */
    override fun dismissOverlay() {
        val view = composeView ?: return
        if (!isShowing) return

        if (Looper.myLooper() == Looper.getMainLooper()) {
            removeOverlayView(view)
        } else {
            mainHandler.post { removeOverlayView(view) }
        }
    }

    private fun removeOverlayView(view: ComposeView) {
        try {
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
            if (view.parent != null) {
                windowManager.removeView(view)
            }
            lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (composeView === view) {
                composeView = null
                isShowing = false
            }
        }
    }

    override fun isOverlayShowing(): Boolean = isShowing
}
