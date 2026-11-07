package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.model.UserEsim
import com.example.ui.components.QrCodeView
import com.example.ui.components.UserEsimCard
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyEsimsScreen(
    userEsims: List<UserEsim>,
    language: AppLanguage,
    onNavigateToExplore: () -> Unit,
    onNavigateToVirtualNumber: () -> Unit = {},
    onSimulateData: (String) -> Unit = {},
    onTopUpEsim: (String, Int, Int) -> Unit = { _, _, _ -> }
) {
    val context = LocalContext.current
    var selectedEsimForInstall by remember { mutableStateOf<UserEsim?>(null) }
    var selectedEsimForTopUp by remember { mutableStateOf<UserEsim?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("my_esims_screen")
    ) {
        if (userEsims.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF071426))
                            .border(2.5.dp, SimXCyanAccent, CircleShape)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.simx_logo),
                            contentDescription = "Sim X Logo",
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = "Sim X Global eSIM",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = Strings.get("no_esims", language),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onNavigateToExplore,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary)
                    ) {
                        Icon(imageVector = Icons.Default.Public, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = Strings.get("explore", language), fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SimXCyanAccent.copy(alpha = 0.35f)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, SimXCyanAccent, CircleShape)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.simx_logo),
                                    contentDescription = "Sim X Logo",
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Gestionnaire de Profils Sim X (${userEsims.size} actif${if (userEsims.size > 1) "s" else ""})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Itinérance mondiale instantanée 4G/5G haute vitesse",
                                    fontSize = 11.sp,
                                    color = SimXCyanAccent
                                )
                            }
                        }
                    }
                }

                if (userEsims.any { it.hasVirtualNumber }) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0x1810B981),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.4f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(onClick = onNavigateToVirtualNumber)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "📞", fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = Strings.get("virtual_number_title", language),
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = "Gérer vos appels, SMS et paramètres de ligne",
                                            fontSize = 11.sp,
                                            color = Color(0xFF99F6E4)
                                        )
                                    }
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = SimXBluePrimary
                                ) {
                                    Text(
                                        text = "Voir",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                items(userEsims, key = { it.id }) { esim ->
                    UserEsimCard(
                        esim = esim,
                        language = language,
                        onInstallClick = { selectedEsimForInstall = esim },
                        onVirtualNumberClick = onNavigateToVirtualNumber,
                        onSimulateData = {
                            onSimulateData(esim.id)
                            Toast.makeText(context, "+150 Mo consommés sur ${esim.countryName} !", Toast.LENGTH_SHORT).show()
                        },
                        onTopUpClick = {
                            selectedEsimForTopUp = esim
                        }
                    )
                }

                // APN Configuration Helper Card
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = "Paramètres APN Automatiques Sim X :",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "• Nom : Sim X\n• APN : global.simx.net\n• Nom d'utilisateur / Mot de passe : aucun\n• Itinérance des données : Activée (Obligatoire)",
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(90.dp))
                }
            }
        }
    }

    // Modal BottomSheet for eSIM QR Code Installation
    selectedEsimForInstall?.let { esim ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { selectedEsimForInstall = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp)
                    .testTag("install_esim_modal"),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${esim.flag} ${esim.countryName} · Sim X",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = Strings.get("ready_to_install", language),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = SimXCyanAccent
                        )
                    }
                    IconButton(onClick = { selectedEsimForInstall = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // High fidelity Canvas QR Code
                QrCodeView(
                    data = esim.lpaActivationCode,
                    size = 210.dp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = Strings.get("activation_code", language),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                // LPA string box
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = esim.lpaActivationCode,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("LPA Code", esim.lpaActivationCode)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(
                                    context,
                                    Strings.get("code_copied", language),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy LPA Code",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step-by-step installation instructions
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Guide d'installation Sim X :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = Strings.get("step1", language), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = Strings.get("step2", language), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = Strings.get("step3", language), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = { selectedEsimForInstall = null },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary)
                ) {
                    Text(text = "Compris !", fontWeight = FontWeight.Bold, color = Color.White)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal BottomSheet for eSIM Top-up
    selectedEsimForTopUp?.let { esim ->
        val topUpSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var selectedTopUpGb by remember { mutableStateOf(3) }
        var selectedTopUpCents by remember { mutableStateOf(667) }

        ModalBottomSheet(
            onDismissRequest = { selectedEsimForTopUp = null },
            sheetState = topUpSheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Recharger · ${esim.flag} ${esim.countryName}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Ajout immédiat de données sur votre profil Sim X",
                            fontSize = 12.sp,
                            color = SimXCyanAccent
                        )
                    }
                    IconButton(onClick = { selectedEsimForTopUp = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    Triple(1, 276, "$2.76"),
                    Triple(3, 667, "$6.67"),
                    Triple(5, 989, "$9.89")
                ).forEach { (gb, costCents, label) ->
                    val isSelected = selectedTopUpGb == gb
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) SimXBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                selectedTopUpGb = gb
                                selectedTopUpCents = costCents
                            }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "+$gb Go Data Sim X",
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = label,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isSelected) SimXCyanAccent else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        onTopUpEsim(esim.id, selectedTopUpGb, selectedTopUpCents)
                        selectedEsimForTopUp = null
                        Toast.makeText(context, "Recharge de +$selectedTopUpGb Go effectuée !", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary)
                ) {
                    Text(
                        text = "Valider la recharge (${String.format("$%.2f", selectedTopUpCents / 100.0)})",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
