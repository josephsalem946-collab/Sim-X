package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.LoyaltyTier
import com.example.model.Strings
import com.example.model.UserProfile
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXGoldDark
import com.example.ui.theme.SimXGoldLight
import com.example.ui.theme.SimXSuccess
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun RewardsScreen(
    userProfile: UserProfile,
    oneFreeRemainingSeconds: Int,
    language: AppLanguage,
    onStartOneFree: () -> Unit,
    onRedeemPoints: () -> Boolean,
    onApplyPromoCode: (String) -> Result<String>
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isSimulatingAd by remember { mutableStateOf(false) }
    var promoInput by remember { mutableStateOf("") }
    var promoMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("rewards_screen")
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // One Free Sponsored 15 min WhatsApp / Messenger access
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(listOf(Color(0xFF0C2B6A), Color(0xFF041235)))
                        )
                        .border(1.dp, SimXCyanAccent.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(SimXCyanAccent.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Wifi,
                                        contentDescription = null,
                                        tint = SimXCyanAccent,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "One Free · Accès d'urgence",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "15 min d'internet WhatsApp & Messenger",
                                        fontSize = 11.sp,
                                        color = Color(0xFFCFDCFA)
                                    )
                                }
                            }

                            if (oneFreeRemainingSeconds > 0) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = SimXSuccess.copy(alpha = 0.2f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Timer,
                                            contentDescription = null,
                                            tint = SimXSuccess,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        val min = oneFreeRemainingSeconds / 60
                                        val sec = oneFreeRemainingSeconds % 60
                                        Text(
                                            text = String.format("%02d:%02d", min, sec),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = SimXSuccess
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = Strings.get("one_free_desc", language),
                            fontSize = 12.sp,
                            color = Color(0xFFBACAF0)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        if (oneFreeRemainingSeconds > 0) {
                            Text(
                                text = "✅ ${Strings.get("session_active", language)}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = SimXSuccess
                            )
                        } else {
                            Button(
                                onClick = {
                                    isSimulatingAd = true
                                    coroutineScope.launch {
                                        delay(2000) // Simulates 30s ad completion
                                        isSimulatingAd = false
                                        onStartOneFree()
                                        Toast.makeText(context, "Session 15 min One Free débloquée !", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                enabled = !isSimulatingAd,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("one_free_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SimXCyanAccent)
                            ) {
                                if (isSimulatingAd) {
                                    CircularProgressIndicator(
                                        color = Color(0xFF031633),
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Visionnage spot sponsorisé...",
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF031633)
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = null,
                                        tint = Color(0xFF031633),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = Strings.get("watch_ad", language),
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF031633)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Referral program (Parrainage $3 + $3)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🤝", fontSize = 24.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = Strings.get("referral_title", language),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Offrez 3 $ et recevez 3 $ de crédits",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = Strings.get("referral_desc", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Referral Code Box
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = Strings.get("your_code", language),
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = userProfile.referralCode,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    clipboard.setPrimaryClip(ClipData.newPlainText("Sim X Code", userProfile.referralCode))
                                    Toast.makeText(context, "Code copié !", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy")
                                }
                                IconButton(onClick = {
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, "Profite de 3 $ de crédit gratuit sur l'app Sim X avec mon code parrain : ${userProfile.referralCode} ! Télécharge Sim X : https://simx.net/app")
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Partager Sim X"))
                                }) {
                                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                                }
                            }
                        }
                    }
                }
            }

            // Loyalty Program (Club Sim X)
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, SimXGoldLight, CircleShape)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.simx_logo),
                                        contentDescription = "Club Sim X Emblem",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = userProfile.loyaltyTier.badge, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = Strings.get("loyalty_title", language),
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                    Text(
                                        text = "Niveau actuel : ${userProfile.loyaltyTier.name}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = SimXGoldDark
                                    )
                                }
                            }

                            Text(
                                text = "${userProfile.loyaltyPoints} pts",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = Strings.get("loyalty_desc", language),
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Progress towards Gold (300 pts)
                        val progress = (userProfile.loyaltyPoints / 300f).coerceIn(0f, 1f)
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = SimXGoldLight,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val ok = onRedeemPoints()
                                if (ok) {
                                    Toast.makeText(context, "100 points convertis en $1.00 !", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Vous avez besoin d'au moins 100 points.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            enabled = userProfile.loyaltyPoints >= 100,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary)
                        ) {
                            Text(
                                text = Strings.get("redeem_points", language),
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Promo Code Card
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = "Code Promotionnel :",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = promoInput,
                                onValueChange = { promoInput = it },
                                placeholder = { Text("Ex: BIENVENU ou SIMX2026") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val res = onApplyPromoCode(promoInput)
                                    res.onSuccess {
                                        promoMessage = it
                                        promoInput = ""
                                        Toast.makeText(context, it, Toast.LENGTH_LONG).show()
                                    }.onFailure {
                                        promoMessage = it.message
                                        Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                                    }
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary)
                            ) {
                                Text(text = Strings.get("apply_code", language), color = Color.White)
                            }
                        }

                        promoMessage?.let {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = it,
                                fontSize = 12.sp,
                                color = if (it.contains("validé")) SimXSuccess else MaterialTheme.colorScheme.error
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
