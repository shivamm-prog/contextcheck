package com.contextcheck.app.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Context
import android.provider.Settings
import android.text.TextUtils
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class ContextCheckAccessibilityService : AccessibilityService() {

    private lateinit var overlayManager: OverlayManager
    private var lastExtractedText: String = ""
    private var lastExtractedTime: Long = 0

    override fun onCreate() {
        super.onCreate()
        overlayManager = OverlayManager(this)
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        // Ensure configuration is explicitly bound to WhatsApp packages
        val info = serviceInfo ?: AccessibilityServiceInfo()
        info.packageNames = SUPPORTED_PACKAGES
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or
                AccessibilityEvent.TYPE_VIEW_LONG_CLICKED or
                AccessibilityEvent.TYPE_VIEW_SELECTED or
                AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS or
                AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        serviceInfo = info
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return

        // 1. Strict package filtering: only act inside WhatsApp
        val packageName = event.packageName?.toString() ?: return
        if (packageName !in SUPPORTED_PACKAGES) return

        // 2. Only process on user-initiated actions (long clicks, selections, clicks)
        val eventType = event.eventType
        val isUserInteraction = eventType == AccessibilityEvent.TYPE_VIEW_LONG_CLICKED ||
                eventType == AccessibilityEvent.TYPE_VIEW_SELECTED ||
                (eventType == AccessibilityEvent.TYPE_VIEW_CLICKED && isMessageContainer(event.source))

        if (!isUserInteraction) return

        // 3. Extract permitted content from event node or active selection
        val sourceNode = event.source ?: rootInActiveWindow ?: return
        val extractedText = extractMessageText(sourceNode)

        if (!extractedText.isNullOrBlank() && extractedText.length > 5) {
            // Prevent duplicate triggers in rapid succession
            val currentTime = System.currentTimeMillis()
            if (extractedText == lastExtractedText && (currentTime - lastExtractedTime) < 2000) {
                return
            }
            lastExtractedText = extractedText
            lastExtractedTime = currentTime

            // 4. Show ContextCheck Action Pill: One Tap initiates verification
            overlayManager.showVerificationPill(extractedText) { selectedText ->
                // User confirmed action with ONE TAP
            }
        }
    }

    private fun isMessageContainer(node: AccessibilityNodeInfo?): Boolean {
        if (node == null) return false
        val viewId = node.viewIdResourceName?.lowercase() ?: ""
        return viewId.contains("message_text") ||
                viewId.contains("conversation_row") ||
                viewId.contains("entry") ||
                viewId.contains("bubble")
    }

    private fun extractMessageText(node: AccessibilityNodeInfo): String? {
        // Direct text on selected node
        if (!node.text.isNullOrBlank()) {
            return node.text.toString()
        }
        if (!node.contentDescription.isNullOrBlank()) {
            return node.contentDescription.toString()
        }

        // Search children nodes for message text
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val childText = extractMessageText(child)
            if (!childText.isNullOrBlank()) {
                return childText
            }
        }
        return null
    }

    override fun onInterrupt() {
        overlayManager.removeOverlay()
    }

    override fun onDestroy() {
        overlayManager.removeOverlay()
        super.onDestroy()
    }

    companion object {
        val SUPPORTED_PACKAGES = arrayOf("com.whatsapp", "com.whatsapp.w4b")

        /**
         * Checks whether the ContextCheck accessibility service is REAL-TIME enabled.
         */
        fun isServiceEnabled(context: Context): Boolean {
            val expectedComponentName = ComponentName(context, ContextCheckAccessibilityService::class.java)
            val enabledServicesSetting = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            ) ?: return false

            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)

            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                val enabledComponent = ComponentName.unflattenFromString(componentNameString)
                if (enabledComponent != null && enabledComponent == expectedComponentName) {
                    return true
                }
            }
            return false
        }
    }
}
