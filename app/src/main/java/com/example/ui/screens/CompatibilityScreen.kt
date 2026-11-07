package com.example.ui.screens

import android.os.Build
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXSuccess

data class CompatibleDevice(
    val brand: String,
    val models: List<String>
)

@Composable
fun CompatibilityScreen(
    language: AppLanguage
) {
    var searchModel by remember { mutableStateOf("") }

    val deviceDatabase = remember {
        listOf(
            CompatibleDevice(
                brand = "Apple iPhone",
                models = listOf(
                    "iPhone 16, 16 Plus, 16 Pro, 16 Pro Max",
                    "iPhone 15, 15 Plus, 15 Pro, 15 Pro Max",
                    "iPhone 14, 14 Plus, 14 Pro, 14 Pro Max",
                    "iPhone 13, 13 mini, 13 Pro, 13 Pro Max",
                    "iPhone 12, 12 mini, 12 Pro, 12 Pro Max",
                    "iPhone 11, 11 Pro, 11 Pro Max",
                    "iPhone XS, XS Max, XR, SE (2e & 3e gén)"
                )
            ),
            CompatibleDevice(
                brand = "Samsung Galaxy",
                models = listOf(
                    "Galaxy S24, S24+, S24 Ultra",
                    "Galaxy S23, S23+, S23 Ultra, S23 FE",
                    "Galaxy S22, S22+, S22 Ultra",
                    "Galaxy S21, S21+, S21 Ultra, S20, S20+",
                    "Galaxy Z Fold 6, 5, 4, 3, 2",
                    "Galaxy Z Flip 6, 5, 4, 3",
                    "Galaxy Note 20, Note 20 Ultra",
                    "Galaxy A54 5G, A55 5G"
                )
            ),
            CompatibleDevice(
                brand = "Google Pixel",
                models = listOf(
                    "Pixel 9, 9 Pro, 9 Pro XL, 9 Pro Fold",
                    "Pixel 8, 8 Pro, 8a",
                    "Pixel 7, 7 Pro, 7a",
                    "Pixel 6, 6 Pro, 6a",
                    "Pixel 5, 5a, 4, 4 XL, 4a, 3, 3 XL"
                )
            ),
            CompatibleDevice(
                brand = "Autres Marques (Xiaomi, Motorola, Huawei)",
                models = listOf(
                    "Xiaomi 14, 14 Pro, 13, 13 Pro, 13T Pro, 12T Pro",
                    "Motorola Razr 40, Edge 40, G53 5G, G54 5G",
                    "Huawei P40, P40 Pro, Mate 40 Pro",
                    "Honor Magic 6 Pro, Magic 5 Pro, Magic 4 Pro"
                )
            )
        )
    }

    val currentPhoneModel = "${Build.MANUFACTURER.capitalize()} ${Build.MODEL}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("compatibility_screen")
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Live Device Detection Card
            item {
                Surface(
                    shape = RoundedCornerShape(22.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
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
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "Votre appareil détecté",
                                            fontSize = 12.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = SimXSuccess,
                                            modifier = Modifier.size(15.dp)
                                        )
                                    }
                                    Text(
                                        text = currentPhoneModel,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = SimXSuccess.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "Compatible",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SimXSuccess,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Votre appareil Android prend en charge les profils de carte eSIM Sim X via le gestionnaire standard GSMA SM-DP+.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Quick EID verification tip
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Comment vérifier manuellement ?",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Ouvrez le clavier d'appel de votre téléphone et tapez :",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Text(
                                text = "*#06#",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                fontFamily = FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Si un numéro EID (Embedded Identity Document) à 32 chiffres apparaît, votre téléphone est 100% compatible eSIM !",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Search filter
            item {
                OutlinedTextField(
                    value = searchModel,
                    onValueChange = { searchModel = it },
                    placeholder = { Text("Rechercher un modèle de smartphone...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            // Full devices catalogue
            items(deviceDatabase) { item ->
                val matchingModels = if (searchModel.isBlank()) item.models else {
                    item.models.filter { it.contains(searchModel, ignoreCase = true) }
                }

                if (matchingModels.isNotEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = item.brand,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            matchingModels.forEach { model ->
                                Row(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = SimXSuccess,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = model,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
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
