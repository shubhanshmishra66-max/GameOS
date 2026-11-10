
package com.gameos.launcher

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Background = Color(0xFF090610)
private val Panel = Color(0xFF171020)
private val NeonPurple = Color(0xFFB45CFF)
private val Muted = Color(0xFFB9AFC7)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                GameOSHome()
            }
        }
    }
}

data class ToolItem(
    val title: String,
    val subtitle: String,
    val icon: ImageVector
)

@Composable
fun GameOSHome() {
    val tools = listOf(
        ToolItem("Crosshair Studio", "Custom crosshair", Icons.Default.GpsFixed),
        ToolItem("Performance", "Device monitoring", Icons.Default.Bolt),
        ToolItem("Audio Control", "Sound shortcuts", Icons.Default.GraphicEq),
        ToolItem("Game Library", "Your installed games", Icons.Default.SportsEsports),
        ToolItem("Game Profiles", "Personal settings", Icons.Default.Settings),
        ToolItem("More Tools", "Coming soon", Icons.Default.Bolt)
    )

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF21102F), Background, Background)
                    )
                )
                .padding(20.dp)
        ) {
            Text(
                "GAMEOS",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 3.sp
            )

            Text(
                "ENTER THE GAME ZONE",
                color = NeonPurple,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Panel
                )
            ) {
                Column(Modifier.padding(18.dp)) {
                    Text(
                        "GAMING DASHBOARD",
                        color = Muted,
                        fontSize = 11.sp
                    )

                    Spacer(Modifier.height(8.dp))

                    Text(
                        "Your games. Your setup.",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        "Welcome to GameOS.",
                        color = Muted,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "GAMING TOOLBOX",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(tools) { item ->
                    ToolCard(item)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "GAMEOS • VERSION 1.0",
                    color = Muted,
                    fontSize = 10.sp
                )
                Text(
                    "BUILT FOR GAMERS",
                    color = NeonPurple,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
fun ToolCard(item: ToolItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(142.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Panel
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        Color(0xFF32184A),
                        RoundedCornerShape(13.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    item.icon,
                    contentDescription = item.title,
                    tint = NeonPurple
                )
            }

            Spacer(Modifier.height(12.dp))

            Text(
                item.title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(4.dp))

            Text(
                item.subtitle,
                color = Muted,
                fontSize = 10.sp
            )
        }
    }
}
