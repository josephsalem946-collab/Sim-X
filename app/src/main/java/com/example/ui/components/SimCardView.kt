package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.EsimPlan
import com.example.model.EsimStatus
import com.example.model.Strings
import com.example.model.UserEsim
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXGoldDark
import com.example.ui.theme.SimXGoldLight
import com.example.ui.theme.SimXNeonGreen
import com.example.ui.theme.SimXSuccess

/**
 * Custom Shape creating the authentic SIM card beveled cut-corner on the top-right
 */
val SimCardCutShape = GenericShape { size, _ ->
    val cutSize = 28f * 2.5f // ~28dp cut corner
    val cornerRadius = 24f // ~8dp corner radius
    reset()
    // Top-left
    moveTo(0f, cornerRadius)
    quadraticBezierTo(0f, 0f, cornerRadius, 0f)
    // Top line up to cut
    lineTo(size.width - cutSize, 0f)
    // Cut diagonal to right line
    lineTo(size.width, cutSize)
    // Right line down to bottom-right
    lineTo(size.width, size.height - cornerRadius)
    quadraticBezierTo(size.width, size.height, size.width - cornerRadius, size.height)
    // Bottom line to bottom-left
    lineTo(cornerRadius, size.height)
    quadraticBezierTo(0f, size.height, 0f, size.height - cornerRadius)
    close()
}

/**
 * Realistic Gold SIM Contact Microchip
 */
@Composable
fun GoldSimChip(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(width = 44.dp, height = 34.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(SimXGoldLight, SimXGoldDark, Color(0xFF9E780E))
                )
            )
            .border(1.dp, Color(0x66000000), RoundedCornerShape(6.dp)),
        contentAlignment = Alignment.Center
    ) {
        // Internal circuit contact trace lines
        Column(
            modifier = Modifier.padding(3.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(Color(0x55000000))
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(14.dp)
                        .background(Color(0x55000000))
                )
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .border(1.dp, Color(0x55000000))
                )
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(14.dp)
                        .background(Color(0x55000000))
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.5.dp)
                    .background(Color(0x55000000))
            )
        }
    }
}

/**
 * Visual Sim X Card for Plan Selection
 */
@Composable
fun SimCardPlanItem(
    plan: EsimPlan,
    isSelected: Boolean,
    language: AppLanguage,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) SimXCyanAccent else Color(0x33FFFFFF)
    val backgroundBrush = if (isSelected) {
        Brush.linearGradient(listOf(Color(0xFF1E4CE6), Color(0xFF0C247A)))
    } else {
        Brush.linearGradient(listOf(Color(0xFF142E82), Color(0xFF0A1C54)))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(if (isSelected) 8.dp else 3.dp, SimCardCutShape)
            .clip(SimCardCutShape)
            .background(backgroundBrush)
            .border(if (isSelected) 2.5.dp else 1.dp, borderColor, SimCardCutShape)
            .clickable(onClick = onSelect)
            .testTag("plan_item_${plan.id}")
            .padding(horizontal = 18.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                GoldSimChip()
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = plan.formattedData(language),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0x3338B6FF)
                        ) {
                            Text(
                                text = "5G",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SimXCyanAccent,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                        if (plan.supportsVoiceAndSms) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SimXNeonGreen.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "VOIX & SMS",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = SimXNeonGreen,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                    if (plan.supportsVoiceAndSms) {
                        Text(
                            text = "📞 ${plan.formattedVoice(language)} · 💬 ${plan.formattedSms(language)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SimXNeonGreen
                        )
                        Text(
                            text = "📱 Numéro virtuel international inclus",
                            fontSize = 11.sp,
                            color = SimXCyanAccent
                        )
                    } else {
                        Text(
                            text = "${plan.formattedDays(language)} · Réseau Sim X",
                            fontSize = 13.sp,
                            color = Color(0xFFC7D5F7)
                        )
                        Text(
                            text = "Hotspot inclus · Zéro roaming",
                            fontSize = 11.sp,
                            color = Color(0xFF90A8DE)
                        )
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = plan.formattedPrice(),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected) SimXCyanAccent else Color.White
                )
                Text(
                    text = plan.formattedHtg(),
                    fontSize = 11.sp,
                    color = Color(0xFFBACAF0)
                )
                if (isSelected) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(SimXCyanAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color(0xFF031633),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * User's Active or Installed eSIM Card
 */
@Composable
fun UserEsimCard(
    esim: UserEsim,
    language: AppLanguage,
    onInstallClick: () -> Unit,
    onSimulateData: (() -> Unit)? = null,
    onTopUpClick: (() -> Unit)? = null,
    onVirtualNumberClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(6.dp, SimCardCutShape)
            .clip(SimCardCutShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(SimXBluePrimary, SimXBlueDark, Color(0xFF061545))
                )
            )
            .border(1.dp, Color(0x447C97FF), SimCardCutShape)
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    GoldSimChip()
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = "${esim.flag} ${esim.countryName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = if (esim.dataGb > 0) "${esim.dataGb} Go · ${esim.validityDays} jours"
                            else "Illimité · ${esim.validityDays} jours",
                            fontSize = 13.sp,
                            color = Color(0xFFCFDCFA)
                        )
                        Text(
                            text = "Réseau : ${esim.networkBrand} LTE/5G",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SimXCyanAccent
                        )
                    }
                }

                // Status Badge
                when (esim.status) {
                    EsimStatus.PREPARING -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x33FFB020))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(12.dp),
                                color = Color(0xFFFFB020),
                                strokeWidth = 1.5.dp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.get("preparing", language),
                                fontSize = 11.sp,
                                color = Color(0xFFFFECC2),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    EsimStatus.READY -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x3310B981))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = SimXSuccess,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Strings.get("ready_to_install", language),
                                fontSize = 11.sp,
                                color = Color(0xFF99F6E4),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    EsimStatus.ACTIVE -> {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x3338B6FF))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SignalCellularAlt,
                                contentDescription = null,
                                tint = SimXCyanAccent,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = Strings.get("active", language),
                                fontSize = 11.sp,
                                color = SimXCyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    EsimStatus.EXPIRED -> {
                        Text(
                            text = "Expirée",
                            fontSize = 11.sp,
                            color = Color(0xFFB0B0B0)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Data consumption bar if active or ready
            if (esim.totalDataMb > 0) {
                val progress = (esim.dataUsedMb.toFloat() / esim.totalDataMb.toFloat()).coerceIn(0f, 1f)
                val usedGb = String.format("%.2f", esim.dataUsedMb / 1024f)
                val totalGb = String.format("%.1f", esim.totalDataMb / 1024f)

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Consommation : $usedGb Go / $totalGb Go",
                            fontSize = 12.sp,
                            color = Color(0xFFE2EAF8)
                        )
                        Text(
                            text = "${((1f - progress) * 100).toInt()}% restant",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimXCyanAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = SimXCyanAccent,
                        trackColor = Color(0x33FFFFFF)
                    )
                }
            }

            // Virtual Phone Number callout (Voice & SMS)
            if (esim.supportsVoiceAndSms && !esim.virtualPhoneNumber.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SimXNeonGreen.copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SimXNeonGreen.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Call,
                                contentDescription = null,
                                tint = SimXNeonGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Numéro Virtuel : ${esim.virtualPhoneNumber}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Text(
                                    text = "${esim.voiceMinutesRemaining} min · ${esim.smsRemaining} SMS restants",
                                    fontSize = 10.sp,
                                    color = SimXCyanAccent
                                )
                            }
                        }
                        if (onVirtualNumberClick != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SimXBluePrimary,
                                modifier = Modifier.clickable(onClick = onVirtualNumberClick)
                            ) {
                                Text(
                                    text = "Gérer",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Bottom action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .border(1.dp, SimXCyanAccent, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.simx_logo),
                            contentDescription = "Sim X",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "ICCID : " + esim.iccid.take(10) + "...",
                        fontSize = 11.sp,
                        color = Color(0xFF88A2D8)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (onSimulateData != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x33FFFFFF),
                            modifier = Modifier.clickable(onClick = onSimulateData)
                        ) {
                            Text(
                                text = "+150 Mo test",
                                fontSize = 11.sp,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    if (onTopUpClick != null) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0x33FFB020),
                            modifier = Modifier.clickable(onClick = onTopUpClick)
                        ) {
                            Text(
                                text = "+ Recharge",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFECC2),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = SimXCyanAccent,
                        modifier = Modifier.clickable(onClick = onInstallClick)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.NetworkCheck,
                                contentDescription = null,
                                tint = Color(0xFF031633),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = Strings.get("install_button", language),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF031633)
                            )
                        }
                    }
                }
            }
        }
    }
}
