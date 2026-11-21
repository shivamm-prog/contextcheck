package com.contextcheck.app.service

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.contextcheck.app.ui.MainActivity

class OverlayManager(private val context: Context) {

    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private var overlayView: View? = null
    private var autoDismissHandler = Handler(Looper.getMainLooper())
    private var autoDismissRunnable: Runnable? = null

    val canDrawOverlay: Boolean
        get() = Settings.canDrawOverlays(context)

    @SuppressLint("InflateParams")
    fun showVerificationPill(extractedText: String, onActionTapped: (String) -> Unit) {
        if (!canDrawOverlay) {
            // If overlay permission is not granted, launch MainActivity directly with the text
            launchVerificationDirectly(extractedText)
            return
        }

        // Remove any existing overlay view first
        removeOverlay()

        val layoutType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 36
            y = 220
        }

        // Programmatically create the ContextCheck floating pill with Sage Green branding
        val root = FrameLayout(context).apply {
            setPadding(8, 8, 8, 8)
        }

        val pillLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(32, 20, 32, 20)
            
            // Background drawable with rounded corners and Sage Green (#668F7A)
            val shape = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.RECTANGLE
                cornerRadius = 60f
                setColor(0xFF668F7A.toInt()) // Sage Green
                setStroke(2, 0xFFF8FAF7.toInt()) // Warm white stroke
            }
            background = shape
            elevation = 16f
        }

        val textView = TextView(context).apply {
            text = "✓ Verify with ContextCheck"
            setTextColor(0xFFF8FAF7.toInt()) // Warm White
            textSize = 13.5f
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.BOLD)
        }

        pillLayout.addView(textView)
        root.addView(pillLayout)

        // ONE-TAP TRIGGER: Clicking this pill immediately initiates verification
        pillLayout.setOnClickListener {
            removeOverlay()
            onActionTapped(extractedText)
            launchVerificationDirectly(extractedText)
        }

        try {
            windowManager.addView(root, params)
            overlayView = root

            // Auto dismiss after 7 seconds if user doesn't tap
            autoDismissRunnable = Runnable { removeOverlay() }
            autoDismissHandler.postDelayed(autoDismissRunnable!!, 7000)
        } catch (e: Exception) {
            // Fallback: direct launch if WindowManager fails
            launchVerificationDirectly(extractedText)
        }
    }

    fun removeOverlay() {
        autoDismissRunnable?.let { autoDismissHandler.removeCallbacks(it) }
        autoDismissRunnable = null

        overlayView?.let {
            try {
                windowManager.removeView(it)
            } catch (e: Exception) {
                // View might not be attached
            }
            overlayView = null
        }
    }

    private fun launchVerificationDirectly(text: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            action = MainActivity.ACTION_VERIFY_NOW
            putExtra(MainActivity.EXTRA_VERIFY_TEXT, text)
        }
        context.startActivity(intent)
    }
}
