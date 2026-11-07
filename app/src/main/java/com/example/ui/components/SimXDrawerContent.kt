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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.Strings
import com.example.model.UserProfile
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXGoldLight

@Composable
fun SimXDrawerContent(
    userProfile: UserProfile,
    language: AppLanguage,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onLanguageChange: (AppLanguage) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(320.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 24.dp)
            .verticalScroll(scrollState)
            .testTag("drawer_content")
    ) {
        // User Profile & Sim X Branding Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(listOf(SimXBluePrimary, SimXBlueDark))
                )
                .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(20.dp))
                .padding(16.dp)
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
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF071426))
                                .border(2.dp, SimXCyanAccent, CircleShape)
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
                                text = "Sim X",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = "Global eSIM Network",
                                fontSize = 11.sp,
                                color = SimXCyanAccent
                            )
                        }
                    }

                    // Loyalty Tier badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x33FFFFFF)
                    ) {
                        Text(
                            text = "${userProfile.loyaltyTier.badge} ${userProfile.loyaltyPoints} pts",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimXGoldLight,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = userProfile.fullName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = userProfile.phone.ifEmpty { userProfile.email },
                    fontSize = 12.sp,
                    color = Color(0xFFBACAF0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Solde : ${userProfile.formattedBalance()}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = SimXCyanAccent
                    )
                    Text(
                        text = userProfile.formattedHtg(),
                        fontSize = 11.sp,
                        color = Color(0xFFD3E0FC)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Main Navigation items
        DrawerItem(
            icon = Icons.Default.Public,
            label = Strings.get("explore", language),
            isSelected = currentRoute == "explore",
            onClick = {
                onNavigate("explore")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.SimCard,
            label = Strings.get("my_esims", language),
            isSelected = currentRoute == "my_esims",
            onClick = {
                onNavigate("my_esims")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Call,
            label = Strings.get("virtual_number_title", language),
            isSelected = currentRoute == "virtual_number",
            onClick = {
                onNavigate("virtual_number")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.AccountBalanceWallet,
            label = Strings.get("wallet", language),
            isSelected = currentRoute == "wallet",
            onClick = {
                onNavigate("wallet")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.CardGiftcard,
            label = Strings.get("rewards", language),
            isSelected = currentRoute == "rewards",
            onClick = {
                onNavigate("rewards")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.PhoneAndroid,
            label = Strings.get("compatibility", language),
            isSelected = currentRoute == "compatibility",
            onClick = {
                onNavigate("compatibility")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Person,
            label = Strings.get("profile", language),
            isSelected = currentRoute == "profile",
            onClick = {
                onNavigate("profile")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.AdminPanelSettings,
            label = "Panneau Administrateur",
            isSelected = currentRoute == "admin",
            onClick = {
                onNavigate("admin")
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))

        // Language selection
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Language,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Langue / Lang",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AppLanguage.entries.forEach { lang ->
                    val isCurrent = lang == language
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isCurrent) SimXBluePrimary else MaterialTheme.colorScheme.surfaceVariant
                            )
                            .clickable { onLanguageChange(lang) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${lang.flag} ${lang.code.uppercase()}",
                            fontSize = 12.sp,
                            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        Spacer(modifier = Modifier.height(16.dp))

        // Support & Legal
        DrawerItem(
            icon = Icons.Default.SupportAgent,
            label = Strings.get("support", language),
            isSelected = false,
            onClick = {
                onNavigate("support")
                onCloseDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Security,
            label = Strings.get("privacy", language),
            isSelected = false,
            onClick = {
                onNavigate("profile")
                onCloseDrawer()
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Footer version & White-label certification
        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .border(1.dp, SimXCyanAccent.copy(alpha = 0.6f), CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.simx_logo),
                        contentDescription = "Sim X Official",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Sim X Global eSIM · v1.0",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "MonCash · Natcash · Binance Pay (APP3)",
                        fontSize = 10.sp,
                        color = SimXCyanAccent.copy(alpha = 0.85f)
                    )
                }
            }
            Text(
                text = "Réseau mondial Sim X 100% Marque Blanche",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bg = if (isSelected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent
    val contentColor = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 2.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = contentColor
        )
    }
}
