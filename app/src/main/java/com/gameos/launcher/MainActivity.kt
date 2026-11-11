package com.gameos.launcher

import android.app.*
import android.content.*
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.os.BatteryManager
import android.os.Handler
import android.os.Looper
import android.os.PowerManager

private val NeonPurple = ComposeColor(0xFFB45CFF)
private val DarkBackground = ComposeColor(0xFF090611)

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            GameOSDashboard(
                onStartOverlay = { startGamingOverlay() }
            )
        }
    }

    private fun startGamingOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            overlayPermissionLauncher.launch(intent)
        } else {
            launchOverlayService()
        }
    }

    private val overlayPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (Settings.canDrawOverlays(this)) {
                launchOverlayService()
            } else {
                Toast.makeText(
                    this,
                    "Overlay permission is required.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private fun launchOverlayService() {
        val intent = Intent(this, GameOverlayService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        Toast.makeText(this, "GameOS Toolbox started", Toast.LENGTH_SHORT).show()
    }
}

@Composable
fun GameOSDashboard(onStartOverlay: () -> Unit) {
    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = NeonPurple,
            background = DarkBackground,
            surface = ComposeColor(0xFF171020)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .padding(20.dp)
        ) {
            Text(
                "GAMEOS",
                color = NeonPurple,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Text(
                "YOUR GAMING CONTROL CENTER",
                color = ComposeColor.LightGray,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(28.dp))

            Text(
                "Gaming Dashboard",
                color = ComposeColor.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            FeatureCard("🎮  Floating Gaming Toolbox", "Open tools over your games")
            FeatureCard("🎯  Crosshair Studio", "Crosshair settings and design")
            FeatureCard("⚡  Performance Monitor", "Device information and monitoring")
            FeatureCard("🔊  Audio Control", "Quick access to sound settings")
            FeatureCard("🕹️  Game Library", "Your gaming hub")
            FeatureCard("🎨  Game Profiles", "Organise your gaming setup")

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onStartOverlay,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonPurple
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    "START FLOATING TOOLBOX",
                    color = ComposeColor.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                "GAMEOS V2.1 • BUILT FOR GAMERS",
                color = ComposeColor.Gray,
                fontSize = 10.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun FeatureCard(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = ComposeColor(0xFF171020)
        ),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(Modifier.padding(15.dp)) {
            Text(
                title,
                color = ComposeColor.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                description,
                color = ComposeColor.LightGray,
                fontSize = 12.sp
            )
        }
    }
}

class GameOverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private var floatingButton: TextView? = null
    private var toolboxPanel: LinearLayout? = null
    private val monitorHandler = Handler(Looper.getMainLooper())
private var monitorView: TextView? = null
    
private fun updateMonitor() {
    val activityManager =
        getSystemService(ACTIVITY_SERVICE) as ActivityManager

    val memoryInfo = ActivityManager.MemoryInfo()
    activityManager.getMemoryInfo(memoryInfo)

    val totalRam = memoryInfo.totalMem / (1024 * 1024)
    val availableRam = memoryInfo.availMem / (1024 * 1024)
    val usedRam = totalRam - availableRam

    val ramPercent = if (totalRam > 0) {
        usedRam * 100 / totalRam
    } else {
        0
    }

    val batteryManager =
        getSystemService(BATTERY_SERVICE) as BatteryManager

    val battery = batteryManager.getIntProperty(
        BatteryManager.BATTERY_PROPERTY_CAPACITY
    )

    val powerManager =
        getSystemService(POWER_SERVICE) as PowerManager

    val thermal = if (Build.VERSION.SDK_INT >= 29) {
        when (powerManager.currentThermalStatus) {
            PowerManager.THERMAL_STATUS_NONE -> "Normal"
            PowerManager.THERMAL_STATUS_LIGHT -> "Light"
            PowerManager.THERMAL_STATUS_MODERATE -> "Moderate"
            PowerManager.THERMAL_STATUS_SEVERE -> "Severe"
            PowerManager.THERMAL_STATUS_CRITICAL -> "Critical"
            PowerManager.THERMAL_STATUS_EMERGENCY -> "Emergency"
            PowerManager.THERMAL_STATUS_SHUTDOWN -> "Shutdown"
            else -> "Unknown"
        }
    } else {
        "Unavailable"
    }

    val batteryText =
        if (battery >= 0) "$battery%" else "Unavailable"

    monitorView?.text =
        "⚡ DEVICE MONITOR\n" +
        "RAM: $usedRam / $totalRam MB ($ramPercent% used)\n" +
        "BATTERY: $batteryText\n" +
        "THERMAL STATUS: $thermal"
}


private val monitorUpdater = object : Runnable {
    override fun run() {
        updateMonitor()

        if (toolboxPanel != null) {
            monitorHandler.postDelayed(this, 3000)
        }
    }
}

    companion object {
        private const val CHANNEL_ID = "gameos_overlay_channel"
        private const val NOTIFICATION_ID = 2101
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        createNotificationChannel()
        startOverlayForeground()
        showFloatingButton()
    }

    private fun startOverlayForeground() {
        val notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(this, CHANNEL_ID)
        } else {
            @Suppress("DEPRECATION")
            Notification.Builder(this)
        }
            .setContentTitle("GameOS is active")
            .setContentText("Floating Gaming Toolbox is running")
            .setSmallIcon(android.R.drawable.ic_media_play)
            .setOngoing(true)
            .build()

        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "GameOS Gaming Toolbox",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager =
                getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    private fun purpleBackground(): GradientDrawable {
        return GradientDrawable().apply {
            setColor(Color.rgb(25, 12, 38))
            cornerRadius = 28f
            setStroke(3, Color.rgb(180, 92, 255))
        }
    }

    private fun showFloatingButton() {
        val button = TextView(this).apply {
            text = "G"
            textSize = 20f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            background = GradientDrawable().apply {
                setColor(Color.rgb(135, 45, 210))
                shape = GradientDrawable.OVAL
                setStroke(3, Color.rgb(210, 150, 255))
            }
        }

        val params = WindowManager.LayoutParams(
            58,
            58,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 20
            y = 300
        }

        var startX = 0
        var startY = 0
        var touchX = 0f
        var touchY = 0f
        var moved = false

        button.setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startX = params.x
                    startY = params.y
                    touchX = event.rawX
                    touchY = event.rawY
                    moved = false
                    true
                }

                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - touchX).toInt()
                    val dy = (event.rawY - touchY).toInt()

                    if (kotlin.math.abs(dx) > 5 || kotlin.math.abs(dy) > 5) {
                        moved = true
                    }

                    params.x = startX + dx
                    params.y = startY + dy
                    windowManager.updateViewLayout(view, params)
                    true
                }

                MotionEvent.ACTION_UP -> {
                    if (!moved) toggleToolbox()
                    true
                }

                else -> true
            }
        }

        floatingButton = button
        windowManager.addView(button, params)
    }

    private fun toggleToolbox() {
        if (toolboxPanel == null) {
            showToolbox()
        } else {
            closeToolbox()
        }
    }

    private fun showToolbox() {
        val panel = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(22, 18, 22, 18)
            background = purpleBackground()
        }

        val title = TextView(this).apply {
            text = "GAMEOS TOOLBOX"
            textSize = 19f
            typeface = Typeface.DEFAULT_BOLD
            setTextColor(Color.rgb(210, 150, 255))
            setPadding(0, 0, 0, 16)
        }

        panel.addView(title)

        addToolButton(panel, "⚡  Performance") {
            showMessage("Performance monitor coming in the next update")
        }

        addToolButton(panel, "🔊  Audio Settings") {
            startActivity(
                Intent("android.settings.SOUND_SETTINGS").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            )
        }

        addToolButton(panel, "🎯  Crosshair Studio") {
            showMessage("Crosshair Studio is not enabled yet")
        }

        addToolButton(panel, "🕹️  Game Profiles") {
            showMessage("Game Profiles are coming soon")
        }

        addToolButton(panel, "✕  Close Toolbox") {
            closeToolbox()
        }

        val params = WindowManager.LayoutParams(
            260,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 110
        }

        toolboxPanel = panel
        windowManager.addView(panel, params)
    }

    private fun addToolButton(
        parent: LinearLayout,
        label: String,
        action: () -> Unit
    ) {
        val button = Button(this).apply {
            text = label
            textSize = 13f
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.rgb(75, 30, 110))
            setOnClickListener { action() }
        }

        parent.addView(
            button,
            LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 5
            }
        )
    }

    private fun closeToolbox() {
        toolboxPanel?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }
        toolboxPanel = null
    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        closeToolbox()

        floatingButton?.let {
            try {
                windowManager.removeView(it)
            } catch (_: Exception) {
            }
        }

        floatingButton = null
        super.onDestroy()
    }
}
