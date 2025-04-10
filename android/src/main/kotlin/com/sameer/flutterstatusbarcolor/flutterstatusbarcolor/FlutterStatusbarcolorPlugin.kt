package com.sameer.flutterstatusbarcolor.flutterstatusbarcolor

import android.animation.ValueAnimator
import android.app.Activity
import android.os.Build
import android.view.View
import android.view.WindowInsetsController
import androidx.annotation.NonNull
import androidx.core.view.WindowCompat
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result


class FlutterStatusbarcolorPlugin : FlutterPlugin, MethodCallHandler, ActivityAware {

    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel

    private var activity: Activity? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "plugins.sameer.com/statusbar")
        channel.setMethodCallHandler(this)
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (activity == null) return result.success(null)

        when (call.method) {
            "getstatusbarcolor" -> {
                var statusBarColor: Int = 0
                statusBarColor = activity!!.window.statusBarColor
                result.success(statusBarColor)
            }
            "setstatusbarcolor" -> {
                val statusBarColor: Int = call.argument("color")!!
                val animate: Boolean = call.argument("animate")!!
                if (animate) {
                    val colorAnim = ValueAnimator.ofArgb(activity!!.window.statusBarColor, statusBarColor)
                    colorAnim.addUpdateListener { anim -> activity!!.window.statusBarColor = anim.animatedValue as Int }
                    colorAnim.duration = 300
                    colorAnim.start()
                } else {
                    activity!!.window.statusBarColor = statusBarColor
                }
                result.success(null)
            }
            "setstatusbarwhiteforeground" -> {
                val usewhiteforeground: Boolean = call.argument("whiteForeground")!!
                val window = activity!!.window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !usewhiteforeground
                } else {
                    if (usewhiteforeground) {
                        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                    } else {
                        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                    }
                }
                result.success(null)
            }
            "getnavigationbarcolor" -> {
                var navigationBarColor: Int = 0
                navigationBarColor = activity!!.window.navigationBarColor
                result.success(navigationBarColor)
            }
            "setnavigationbarcolor" -> {
                val navigationBarColor: Int = call.argument("color")!!
                val animate: Boolean = call.argument("animate")!!
                @Suppress("DEPRECATION")
                if (animate) {
                    val colorAnim = ValueAnimator.ofArgb(activity!!.window.navigationBarColor, navigationBarColor)
                    colorAnim.addUpdateListener { anim -> activity!!.window.navigationBarColor = anim.animatedValue as Int }
                    colorAnim.duration = 300
                    colorAnim.start()
                } else {
                    @Suppress("DEPRECATION")
                    activity!!.window.navigationBarColor = navigationBarColor
                }
                result.success(null)
            }
            "setnavigationbarwhiteforeground" -> {
                val usewhiteforeground: Boolean = call.argument("whiteForeground")!!
                val window = activity!!.window
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = !usewhiteforeground
                    window.isNavigationBarContrastEnforced = true
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    @Suppress("DEPRECATION")
                    if (usewhiteforeground) {
                        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv()
                    } else {
                        window.decorView.systemUiVisibility = window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
                    }
                }
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivityForConfigChanges() {
        this.activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        this.activity = binding.activity
    }

    override fun onDetachedFromActivity() {
        this.activity = null
    }
}
