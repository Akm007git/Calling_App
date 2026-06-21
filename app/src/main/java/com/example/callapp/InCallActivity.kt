package com.example.callapp

import android.os.Bundle
import android.telecom.Call
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.callapp.ui.theme.CallAppTheme
import kotlinx.coroutines.delay

private data class CallAction(
    val label: String,
    val glyph: String,
    val selected: Boolean = false,
    val enabled: Boolean = true,
    val onClick: () -> Unit = {},
)

class InCallActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallAppTheme(dynamicColor = false) {
                InCallScreen(onClose = { finish() })
            }
        }
    }
}

@Composable
private fun InCallScreen(onClose: () -> Unit) {
    var elapsedSeconds by remember { mutableIntStateOf(0) }
    var muted by remember { mutableStateOf(false) }
    var speaker by remember { mutableStateOf(false) }
    var held by remember { mutableStateOf(false) }
    var recording by remember { mutableStateOf(false) }
    var keypadOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            elapsedSeconds += 1
        }
    }

    val actions = listOf(
        CallAction("Speaker", "SPK", speaker) { speaker = !speaker },
        CallAction("Add call", "+") {},
        CallAction("Mute", "MIC", muted) {
            muted = !muted
            CallStateRepository.service?.setMuted(muted)
        },
        CallAction("Record", "REC", recording) { recording = !recording },
        CallAction("Keypad", "123") { keypadOpen = true },
        CallAction("Hold", "HLD", held) {
            held = !held
            CallStateRepository.currentCall?.let { call ->
                if (held) call.hold() else call.unhold()
            }
        },
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF56616B), Color(0xFF242C34), Color(0xFF0E1217)),
                )
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0x66070A0E))
        )
        Scaffold(
            contentWindowInsets = WindowInsets(0),
            containerColor = Color.Transparent,
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .statusBarsPadding()
                    .navigationBarsPadding()
                    .padding(horizontal = 22.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TopCallBar(elapsedSeconds = elapsedSeconds)

                Spacer(modifier = Modifier.height(18.dp))

                DarkCallCard(
                    keypadOpen = keypadOpen,
                    actions = actions,
                    onCloseKeypad = { keypadOpen = false },
                    onEnd = {
                        CallStateRepository.currentCall?.disconnect()
                        onClose()
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                SecondaryActions()
            }
        }
    }
}

@Composable
private fun TopCallBar(elapsedSeconds: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.09f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
        ) {
            Text(
                text = "‹",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 28.sp,
                fontWeight = FontWeight.Light,
                textAlign = TextAlign.Center,
                modifier = Modifier.size(44.dp).padding(top = 3.dp),
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Carolyn Statingham",
                color = Color.White.copy(alpha = 0.72f),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = formatDuration(elapsedSeconds),
                color = Color(0xFF2EC4A6),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
        }
        Surface(
            shape = CircleShape,
            color = Color.White.copy(alpha = 0.09f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
        ) {
            Text(
                text = "⋯",
                color = Color.White.copy(alpha = 0.82f),
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.size(44.dp).padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun DarkCallCard(
    keypadOpen: Boolean,
    actions: List<CallAction>,
    onCloseKeypad: () -> Unit,
    onEnd: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(650.dp),
        shape = RoundedCornerShape(34.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xF012171E)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 22.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CallerHero()

            Spacer(modifier = Modifier.height(20.dp))

            if (keypadOpen) {
                DarkCompactDialPad(onClose = onCloseKeypad)
            } else {
                PrimaryActionGrid(actions = actions)
            }

            Spacer(modifier = Modifier.weight(1f))

            EndCallButton(onClick = onEnd)
        }
    }
}

@Composable
private fun CallerHero() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(118.dp)
                .clip(RoundedCornerShape(34.dp))
                .background(
                    Brush.radialGradient(
                        listOf(Color(0xFF2EC4A6), Color(0xFF234840), Color(0xFF111821))
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(86.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(Color.White.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "AS",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Carolyn Statingham",
                color = Color.White,
                fontSize = 25.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Text(
                text = "+977 1111 222 444",
                color = Color.White.copy(alpha = 0.58f),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}

@Composable
private fun PrimaryActionGrid(actions: List<CallAction>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        actions.chunked(3).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                row.forEach { action ->
                    DarkActionButton(action = action)
                }
            }
        }
    }
}

@Composable
private fun DarkActionButton(action: CallAction) {
    val container = when {
        !action.enabled -> Color.White.copy(alpha = 0.07f)
        action.selected -> Color(0x332EC4A6)
        action.selected -> Color(0xFFBFEFE4)
        else -> Color.White.copy(alpha = 0.82f).copy(alpha = 0.08f)
    }
    val content = when {
        !action.enabled -> Color.White.copy(alpha = 0.28f)
        action.selected -> Color(0xFFBFEFE4)
        else -> Color.White.copy(alpha = 0.82f)
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(78.dp),
    ) {
        Surface(
            onClick = action.onClick,
            enabled = action.enabled,
            shape = RoundedCornerShape(22.dp),
            color = container,
            border = BorderStroke(1.dp, Color.White.copy(alpha = if (action.selected) 0.24f else 0.10f)),
            modifier = Modifier.size(64.dp),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = action.glyph,
                    color = content,
                    fontSize = if (action.glyph.length > 2) 15.sp else 23.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                )
            }
        }
        Text(
            text = action.label,
            color = content.copy(alpha = if (action.enabled) 0.74f else 0.34f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun SecondaryActions() {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.62f)),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MiniFooterAction("Merge", "Disabled", false)
            MiniFooterAction("Notes", "Open", true)
            MiniFooterAction("Transfer", "Soon", false)
        }
    }
}

@Composable
private fun MiniFooterAction(title: String, subtitle: String, enabled: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(92.dp)) {
        Text(
            text = title,
            color = if (enabled) Color.White else Color.White.copy(alpha = 0.28f),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = subtitle,
            color = Color.White.copy(alpha = 0.38f),
            fontSize = 11.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun DarkCompactDialPad(onClose: () -> Unit) {
    val rows = listOf(
        listOf("1", "2", "3"),
        listOf("4", "5", "6"),
        listOf("7", "8", "9"),
        listOf("*", "0", "#"),
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Keypad",
                color = Color(0xFF2EC4A6),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Surface(
                onClick = onClose,
                shape = RoundedCornerShape(999.dp),
                color = Color.White.copy(alpha = 0.12f),
            ) {
                Text(
                    text = "Done",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                )
            }
        }
        rows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(9.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { digit ->
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = RoundedCornerShape(16.dp),
                        color = Color.White.copy(alpha = 0.09f),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.10f)),
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = digit,
                                color = Color.White,
                                fontSize = 21.sp,
                                fontWeight = FontWeight.Medium,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EndCallButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(132.dp)
            .height(48.dp),
        shape = RoundedCornerShape(999.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD8335B)),
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(
            text = "End",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
