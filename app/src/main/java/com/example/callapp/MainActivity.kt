package com.example.callapp

import android.Manifest
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.callapp.ui.theme.CallAppTheme

private enum class HomeTab { Calls, Keypad, People }
private enum class CallFilter { All, Missed, Received }
private enum class CallType { Missed, Received, Outgoing }

private data class Caller(
    val name: String,
    val number: String,
    val initials: String,
    val type: CallType,
    val time: String,
    val subtitle: String,
    val accent: Color,
)

private val sampleCalls = listOf(
    Caller("Theresa Webb", "+1 (420) 425-0133", "TW", CallType.Received, "10:42", "Mobile", Color(0xFF2EC4A6)),
    Caller("Jenny Wilson", "+1 (560) 555-0100", "JW", CallType.Missed, "09:18", "Missed call", Color(0xFF4F8CFF)),
    Caller("Robert Fox", "+1 (704) 555-0127", "RF", CallType.Received, "Yesterday", "Work", Color(0xFFFFC75F)),
    Caller("Arlene McCoy", "+1 (584) 555-0102", "AM", CallType.Outgoing, "Sat", "Outgoing", Color(0xFF00B874)),
    Caller("Cameron Williamson", "+1 (505) 555-0148", "CW", CallType.Missed, "Fri", "Missed call", Color(0xFFE84D6A)),
)

private val favoritePeople = listOf(
    Caller("Ananya Sharma", "(900) 000-0000", "AS", CallType.Received, "Favorite", "Mobile", Color(0xFF2EC4A6)),
    Caller("Rahul Mehta", "+91 90000 00001", "RM", CallType.Received, "Team", "Work", Color(0xFFAAD9C5)),
    Caller("Priya Nair", "+91 90000 00002", "PN", CallType.Outgoing, "Home", "Family", Color(0xFF4F8CFF)),
    Caller("Design Studio", "+91 98765 43210", "DS", CallType.Received, "Work", "Saved", Color(0xFFFFC75F)),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CallAppTheme(dynamicColor = false) {
                DialerHome()
            }
        }
    }
}

@Composable
private fun DialerHome() {
    val context = LocalContext.current
    var number by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(HomeTab.Calls) }
    var callFilter by remember { mutableStateOf(CallFilter.All) }
    var selectedCaller by remember { mutableStateOf<Caller?>(null) }
    var isDefaultDialer by remember { mutableStateOf(context.isDefaultDialer()) }

    val roleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        isDefaultDialer = context.isDefaultDialer()
    }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { }

    LaunchedEffect(Unit) {
        val permissions = arrayOf(
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ANSWER_PHONE_CALLS,
        ).filter { permission ->
            ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()
        if (permissions.isNotEmpty()) permissionLauncher.launch(permissions)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Color(0xFF56616B), Color(0xFF242C34), Color(0xFF0E1217))))
    ) {
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
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                HomeHeader(
                    isDefaultDialer = isDefaultDialer,
                    onRequestDefault = { context.defaultDialerIntent()?.let(roleLauncher::launch) }
                )
                HomeTabs(activeTab = activeTab, onChange = { activeTab = it })

                AnimatedContent(
                    targetState = activeTab,
                    label = "home-tab",
                    modifier = Modifier.weight(1f),
                ) { tab ->
                    when (tab) {
                        HomeTab.Calls -> CallsScreen(
                            filter = callFilter,
                            onFilterChange = { callFilter = it },
                            onOpen = { selectedCaller = it },
                            onCall = { context.placeCall(it.number) },
                        )
                        HomeTab.Keypad -> KeypadScreen(
                            number = number,
                            onDigit = { number += it },
                            onDelete = { number = number.dropLast(1) },
                            onClear = { number = "" },
                            onCall = { context.placeCall(number) },
                        )
                        HomeTab.People -> PeopleScreen(
                            onOpen = { selectedCaller = it },
                            onCall = { context.placeCall(it.number) },
                        )
                    }
                }
            }
        }

        selectedCaller?.let { caller ->
            ContactDetailsOverlay(
                caller = caller,
                onDismiss = { selectedCaller = null },
                onCall = { context.placeCall(caller.number) },
            )
        }
    }
}

@Composable
private fun HomeHeader(isDefaultDialer: Boolean, onRequestDefault: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text("Phone", color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = if (isDefaultDialer) "Default phone app" else "Set as default to replace Google Phone",
                color = Color.White.copy(alpha = 0.54f),
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Surface(
            onClick = { if (!isDefaultDialer) onRequestDefault() },
            shape = RoundedCornerShape(14.dp),
            color = if (isDefaultDialer) Color.White.copy(alpha = 0.10f) else Color.White.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
        ) {
            Text(
                text = if (isDefaultDialer) "Ready" else "Default",
                color = if (isDefaultDialer) Color(0xFF07120F) else Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            )
        }
    }
}

@Composable
private fun HomeTabs(activeTab: HomeTab, onChange: (HomeTab) -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xD9161C23),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Row(modifier = Modifier.padding(5.dp), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
            HomeTab.entries.forEach { tab ->
                val selected = tab == activeTab
                val bg by animateColorAsState(if (selected) Color(0xFF26333B) else Color.Transparent, label = "tab")
                Text(
                    text = when (tab) {
                        HomeTab.Calls -> "Calls"
                        HomeTab.Keypad -> "Keypad"
                        HomeTab.People -> "People"
                    },
                    color = if (selected) Color(0xFF07120F) else Color.White.copy(alpha = 0.48f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .background(bg)
                        .clickable { onChange(tab) }
                        .padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun CallsScreen(
    filter: CallFilter,
    onFilterChange: (CallFilter) -> Unit,
    onOpen: (Caller) -> Unit,
    onCall: (Caller) -> Unit,
) {
    val calls = sampleCalls.filter { caller ->
        when (filter) {
            CallFilter.All -> true
            CallFilter.Missed -> caller.type == CallType.Missed
            CallFilter.Received -> caller.type == CallType.Received
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
    ) {
        item { FilterRow(filter = filter, onFilterChange = onFilterChange) }
        item { HighlightCallCard(caller = sampleCalls.first(), onOpen = { onOpen(sampleCalls.first()) }) }
        item { SectionTitle("Recent activity") }
        items(calls) { caller ->
            CallerRow(caller = caller, onOpen = { onOpen(caller) }, onCall = { onCall(caller) })
        }
    }
}

@Composable
private fun HighlightCallCard(caller: Caller, onOpen: () -> Unit) {
    Surface(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xE0181F27),
        border = BorderStroke(1.dp, Color(0x552EC4A6)),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(caller.initials, Color(0xFF2EC4A6), 46.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(caller.name, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(caller.number, color = Color.White.copy(alpha = 0.58f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
            Surface(shape = RoundedCornerShape(999.dp), color = Color(0x332EC4A6)) {
                Text("Now", color = Color(0xFFBFEFE4), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp))
            }
        }
    }
}

@Composable
private fun FilterRow(filter: CallFilter, onFilterChange: (CallFilter) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(9.dp)) {
        CallFilter.entries.forEach { option ->
            val selected = option == filter
            Surface(
                onClick = { onFilterChange(option) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) Color(0xFF2EC4A6) else Color.White.copy(alpha = 0.08f),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = when (option) {
                        CallFilter.All -> "All"
                        CallFilter.Missed -> "Missed"
                        CallFilter.Received -> "Received"
                    },
                    color = if (selected) Color(0xFF07120F) else Color.White.copy(alpha = 0.55f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 10.dp),
                )
            }
        }
    }
}

@Composable
private fun PeopleScreen(onOpen: (Caller) -> Unit, onCall: (Caller) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 12.dp),
    ) {
        item { FavoriteStrip() }
        item { SectionTitle("Saved people") }
        items(favoritePeople) { caller ->
            CallerRow(caller = caller, onOpen = { onOpen(caller) }, onCall = { onCall(caller) })
        }
    }
}

@Composable
private fun FavoriteStrip() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xE0181F27),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Avatar(initials = "AS", accent = Color(0xFF2EC4A6), size = 58.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text("Favorite", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("Ananya Sharma", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.SemiBold)
                Text("Tap any contact for full details", color = Color.White.copy(alpha = 0.55f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun KeypadScreen(
    number: String,
    onDigit: (String) -> Unit,
    onDelete: () -> Unit,
    onClear: () -> Unit,
    onCall: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DarkNumberPanel(number = number, onClear = onClear)
        BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
            val keyHeight = if (maxHeight < 470.dp) 58.dp else 70.dp
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Keypad(onDigit = onDigit, keyHeight = keyHeight)
                KeypadActions(onDelete = onDelete, onPlus = { onDigit("+") }, onCall = onCall)
            }
        }
    }
}

@Composable
private fun DarkNumberPanel(number: String, onClear: () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xE0141A21),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = number.ifBlank { "Enter number" },
                color = if (number.isBlank()) Color.White.copy(alpha = 0.42f) else Color.White,
                fontSize = if (number.length > 14) 25.sp else 32.sp,
                fontWeight = FontWeight.Light,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Secure dial", color = Color.White.copy(alpha = 0.48f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                if (number.isNotBlank()) {
                    Surface(onClick = onClear, shape = RoundedCornerShape(999.dp), color = Color.White.copy(alpha = 0.10f)) {
                        Text("Clear", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun Keypad(onDigit: (String) -> Unit, keyHeight: Dp) {
    val rows = listOf(
        listOf("1" to "", "2" to "ABC", "3" to "DEF"),
        listOf("4" to "GHI", "5" to "JKL", "6" to "MNO"),
        listOf("7" to "PQRS", "8" to "TUV", "9" to "WXYZ"),
        listOf("*" to "", "0" to "+", "#" to ""),
    )

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { (digit, letters) ->
                    DialKey(digit = digit, letters = letters, height = keyHeight, modifier = Modifier.weight(1f), onClick = { onDigit(digit) })
                }
            }
        }
    }
}

@Composable
private fun DialKey(digit: String, letters: String, height: Dp, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier.height(height),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xB8181F27),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
        onClick = onClick,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text(digit, color = Color.White.copy(alpha = 0.88f), fontSize = 28.sp, fontWeight = FontWeight.Light)
            Text(letters, color = Color.White.copy(alpha = 0.42f), fontSize = 10.sp, fontWeight = FontWeight.Bold, minLines = 1)
        }
    }
}

@Composable
private fun KeypadActions(onDelete: () -> Unit, onPlus: () -> Unit, onCall: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundUtilityButton("Del", onDelete)
        Button(
            modifier = Modifier.width(140.dp).height(56.dp),
            onClick = onCall,
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF24B394)),
            contentPadding = PaddingValues(0.dp),
        ) { Text("Call", color = Color(0xFF07120F), fontSize = 17.sp, fontWeight = FontWeight.SemiBold) }
        RoundUtilityButton("+", onPlus)
    }
}

@Composable
private fun RoundUtilityButton(label: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.size(56.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        onClick = onClick,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(label, color = Color.White.copy(alpha = 0.78f), fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 2.dp))
}

@Composable
private fun CallerRow(caller: Caller, onOpen: () -> Unit, onCall: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xE0181F27)),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.07f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(13.dp)
                .clip(RoundedCornerShape(18.dp))
                .clickable(onClick = onOpen),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Avatar(caller.initials, caller.accent, 50.dp)
            Column(modifier = Modifier.weight(1f)) {
                Text(caller.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${caller.subtitle}  ${caller.number}", color = statusColor(caller.type), fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(caller.time, color = Color.White.copy(alpha = 0.38f), fontSize = 11.sp, fontWeight = FontWeight.Medium)
                Surface(onClick = onCall, shape = RoundedCornerShape(999.dp), color = Color.White.copy(alpha = 0.08f), border = BorderStroke(1.dp, Color(0x442EC4A6))) {
                    Text("Call", color = Color(0xFFBFEFE4), fontSize = 12.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp))
                }
            }
        }
    }
}

@Composable
private fun Avatar(initials: String, accent: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(accent, Color(0xFF101820)))),
        contentAlignment = Alignment.Center,
    ) {
        Text(initials, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = (size.value * 0.34f).sp)
    }
}

@Composable
private fun ContactDetailsOverlay(caller: Caller, onDismiss: () -> Unit, onCall: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x99000000)),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            shape = RoundedCornerShape(34.dp),
            color = Color(0xF2171D24),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                Avatar(caller.initials, caller.accent, 88.dp)
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(caller.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Text(caller.number, color = Color.White.copy(alpha = 0.58f), fontSize = 15.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    DetailMetric("Type", caller.type.name.lowercase().replaceFirstChar { it.uppercase() }, Modifier.weight(1f))
                    DetailMetric("Last", caller.time, Modifier.weight(1f))
                    DetailMetric("Line", caller.subtitle, Modifier.weight(1f))
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Surface(onClick = onCall, shape = RoundedCornerShape(18.dp), color = Color(0xFF2EC4A6), modifier = Modifier.weight(1f).height(54.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("Call", color = Color(0xFF07120F), fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
                    }
                    Surface(onClick = onDismiss, shape = RoundedCornerShape(22.dp), color = Color.White.copy(alpha = 0.10f), modifier = Modifier.weight(1f).height(54.dp)) {
                        Box(contentAlignment = Alignment.Center) { Text("Close", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(shape = RoundedCornerShape(20.dp), color = Color.White.copy(alpha = 0.08f), modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(label, color = Color.White.copy(alpha = 0.44f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(value, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
    }
}

private fun statusColor(type: CallType): Color {
    return when (type) {
        CallType.Missed -> Color(0xFFFF5A70)
        CallType.Received -> Color(0xFF2EC4A6)
        CallType.Outgoing -> Color(0xFF8EA2FF)
    }
}

private fun Context.isDefaultDialer(): Boolean {
    val telecom = getSystemService(TelecomManager::class.java)
    return telecom.defaultDialerPackage == packageName
}

private fun Context.defaultDialerIntent(): Intent? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = getSystemService(RoleManager::class.java)
        if (roleManager.isRoleAvailable(RoleManager.ROLE_DIALER) && !roleManager.isRoleHeld(RoleManager.ROLE_DIALER)) {
            roleManager.createRequestRoleIntent(RoleManager.ROLE_DIALER)
        } else {
            null
        }
    } else {
        Intent(TelecomManager.ACTION_CHANGE_DEFAULT_DIALER).putExtra(
            TelecomManager.EXTRA_CHANGE_DEFAULT_DIALER_PACKAGE_NAME,
            packageName
        )
    }
}

private fun Context.placeCall(number: String) {
    if (number.isBlank()) return
    val uri = Uri.fromParts("tel", number, null)
    val hasCallPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED

    if (isDefaultDialer() && hasCallPermission) {
        getSystemService(TelecomManager::class.java).placeCall(uri, Bundle.EMPTY)
    } else {
        startActivity(Intent(Intent.ACTION_DIAL, uri))
    }
}
