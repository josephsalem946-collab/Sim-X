package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ForwardToInbox
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PhoneForwarded
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Voicemail
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.model.UserEsim
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXGoldLight
import com.example.ui.theme.SimXNeonGreen
import com.example.ui.theme.SimXSlateNavy
import com.example.ui.theme.SimXSlateNavyVariant
import com.example.ui.theme.SimXSuccess

@Composable
fun VirtualNumberScreen(
    userEsims: List<UserEsim>,
    language: AppLanguage,
    onNavigateToExploreVoice: () -> Unit
) {
    val context = LocalContext.current
    val voiceEsims = remember(userEsims) { userEsims.filter { it.supportsVoiceAndSms && !it.virtualPhoneNumber.isNullOrBlank() } }

    var selectedEsimId by remember(voiceEsims) {
        mutableStateOf(voiceEsims.firstOrNull()?.id ?: "")
    }

    val activeEsim = voiceEsims.find { it.id == selectedEsimId } ?: voiceEsims.firstOrNull()

    // Telephony Settings toggles
    var callForwardingEnabled by remember { mutableStateOf(false) }
    var voicemailEnabled by remember { mutableStateOf(true) }
    var callerIdEnabled by remember { mutableStateOf(true) }
    var volteHdEnabled by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("virtual_number_screen")
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Header: Title and subtitle
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = Strings.get("virtual_number_title", language),
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = Strings.get("virtual_number_subtitle", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF071426))
                            .border(1.5.dp, SimXCyanAccent, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.simx_logo),
                            contentDescription = "Sim X",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }

            // eSIM Selector if multiple lines are available
            if (voiceEsims.size > 1) {
                item {
                    Text(
                        text = "Vos lignes actives (${voiceEsims.size}) :",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(voiceEsims) { esim ->
                            val isSelected = esim.id == activeEsim?.id
                            FilterChip(
                                selected = isSelected,
                                onClick = { selectedEsimId = esim.id },
                                label = {
                                    Text(
                                        text = "${esim.flag} ${esim.virtualPhoneNumber ?: esim.countryName}",
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = SimXBluePrimary,
                                    selectedLabelColor = Color.White
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                }
            }

            if (activeEsim != null && !activeEsim.virtualPhoneNumber.isNullOrBlank()) {
                val phoneNumber = activeEsim.virtualPhoneNumber

                // Main Virtual Phone Card
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        SimXSlateNavy,
                                        SimXSlateNavyVariant,
                                        Color(0xFF040A1A)
                                    )
                                )
                            )
                            .border(
                                BorderStroke(
                                    1.5.dp,
                                    Brush.horizontalGradient(
                                        listOf(SimXNeonGreen.copy(alpha = 0.6f), SimXCyanAccent.copy(alpha = 0.6f))
                                    )
                                ),
                                RoundedCornerShape(24.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Column {
                            // Top Card Bar: Carrier & Status Badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = activeEsim.flag, fontSize = 22.sp)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Ligne ${activeEsim.countryName}",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Sim X VoLTE HD Calling 5G",
                                            fontSize = 10.sp,
                                            color = SimXCyanAccent
                                        )
                                    }
                                }

                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SimXNeonGreen.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, SimXNeonGreen.copy(alpha = 0.3f))
                                ) {
                                    Text(
                                        text = "🟢 LIGNE ACTIVE",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SimXNeonGreen,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(18.dp))

                            Text(
                                text = "VOTRE NUMÉRO VIRTUEL SIM X :",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = Color(0xFF94A3B8)
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Large Number Display
                            Text(
                                text = phoneNumber,
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White,
                                modifier = Modifier.testTag("virtual_phone_number_text")
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            // Action buttons: Copy & Share
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("Sim X Virtual Number", phoneNumber))
                                        Toast.makeText(context, Strings.get("virtual_number_copied", language), Toast.LENGTH_SHORT).show()
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("copy_virtual_number_button")
                                ) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = Strings.get("copy_number", language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val sendIntent = Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, "Voici mon numéro international Sim X : $phoneNumber (Appels & SMS)")
                                            type = "text/plain"
                                        }
                                        context.startActivity(Intent.createChooser(sendIntent, "Partager mon numéro Sim X"))
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    border = BorderStroke(1.dp, SimXCyanAccent),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SimXCyanAccent),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(46.dp)
                                        .testTag("share_virtual_number_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = Strings.get("share_number", language), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                }
                            }
                        }
                    }
                }

                // Balance of Minutes and SMS
                item {
                    Text(
                        text = "Consommation Voix & SMS en cours :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Voice Minutes Card
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Call, contentDescription = null, tint = SimXCyanAccent, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "${activeEsim.voiceMinutesRemaining} min",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = Strings.get("calls_remaining", language),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val voiceProgress = if (activeEsim.voiceMinutesTotal > 0)
                                    (activeEsim.voiceMinutesRemaining.toFloat() / activeEsim.voiceMinutesTotal.toFloat()).coerceIn(0f, 1f)
                                else 1f
                                LinearProgressIndicator(
                                    progress = { voiceProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = SimXCyanAccent,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }

                        // SMS Count Card
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = MaterialTheme.colorScheme.surface,
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(imageVector = Icons.Default.Sms, contentDescription = null, tint = SimXNeonGreen, modifier = Modifier.size(20.dp))
                                    Text(
                                        text = "${activeEsim.smsRemaining} SMS",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SimXNeonGreen
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = Strings.get("sms_remaining", language),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                val smsProgress = if (activeEsim.smsTotal > 0)
                                    (activeEsim.smsRemaining.toFloat() / activeEsim.smsTotal.toFloat()).coerceIn(0f, 1f)
                                else 1f
                                LinearProgressIndicator(
                                    progress = { smsProgress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(5.dp)
                                        .clip(RoundedCornerShape(3.dp)),
                                    color = SimXNeonGreen,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                        }
                    }
                }

                // Telephony Settings / Paramètres de la Ligne
                item {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Strings.get("line_settings", language),
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Switch 1: Call Forwarding
                            SettingSwitchRow(
                                icon = Icons.Default.PhoneForwarded,
                                title = Strings.get("call_forwarding", language),
                                subtitle = "Rediriger les appels vers votre numéro principal",
                                isChecked = callForwardingEnabled,
                                onCheckedChange = { callForwardingEnabled = it }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                            // Switch 2: Voicemail
                            SettingSwitchRow(
                                icon = Icons.Default.Voicemail,
                                title = Strings.get("voicemail", language),
                                subtitle = "Enregistrement automatique des messages audio",
                                isChecked = voicemailEnabled,
                                onCheckedChange = { voicemailEnabled = it }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                            // Switch 3: Caller ID
                            SettingSwitchRow(
                                icon = Icons.Default.Visibility,
                                title = Strings.get("caller_id", language),
                                subtitle = "Transmettre mon numéro Sim X lors des appels sortants",
                                isChecked = callerIdEnabled,
                                onCheckedChange = { callerIdEnabled = it }
                            )

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))

                            // Switch 4: VoLTE HD
                            SettingSwitchRow(
                                icon = Icons.Default.Headphones,
                                title = Strings.get("hd_voice", language),
                                subtitle = "Codec audio haute définition large bande",
                                isChecked = volteHdEnabled,
                                onCheckedChange = { volteHdEnabled = it }
                            )
                        }
                    }
                }
            } else {
                // Empty state: No Voice & SMS eSIM active
                item {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(SimXBluePrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = SimXBluePrimary,
                                    modifier = Modifier.size(38.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Aucun numéro virtuel actif",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = Strings.get("no_virtual_number", language),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            Spacer(modifier = Modifier.height(18.dp))

                            Button(
                                onClick = onNavigateToExploreVoice,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Call, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = Strings.get("get_voice_plan", language),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // White-label Security & Carrier Note
            item {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = SimXSlateNavy.copy(alpha = 0.5f),
                    border = BorderStroke(1.dp, SimXSlateNavyVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = SimXCyanAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ligne Internationale Sécurisée Sim X",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Attribution instantanée conforme télécoms. Tous les flux voix et SMS sont chiffrés sur le réseau Sim X Core.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }
}

@Composable
private fun SettingSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = SimXBluePrimary
            )
        )
    }
}
