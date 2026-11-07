package com.example.ui.screens

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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AdminAuditLog
import com.example.model.AdminBroadcastNotification
import com.example.model.AdminConfig
import com.example.model.AdminDepositRequest
import com.example.model.AdminUserAccount
import com.example.model.AppLanguage
import com.example.model.EsimAccessWholesalePackage
import com.example.model.EsimWholesaleTransaction
import com.example.model.PaymentGateway
import com.example.model.Strings
import com.example.model.UserEsim
import com.example.model.UserProfile
import com.example.model.WalletTransaction
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXError
import com.example.ui.theme.SimXGoldLight
import com.example.ui.theme.SimXNeonGreen
import com.example.ui.theme.SimXSlateBorder
import com.example.ui.theme.SimXSlateNavy
import com.example.ui.theme.SimXSlateNavyVariant
import com.example.ui.theme.SimXSuccess
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class AdminTab(val title: String, val icon: String) {
    STATS("Synthèse", "📊"),
    WHOLESALE("Grossiste", "🌐"),
    GATEWAYS("Passerelles", "💳"),
    DEPOSITS("Dépôts", "⏳"),
    USERS("Utilisateurs", "👥"),
    CONFIG("Paramètres", "⚙️"),
    NOTIFICATIONS("Push", "📢")
}

@Composable
fun AdminPanelScreen(
    userProfile: UserProfile,
    allTransactions: List<WalletTransaction>,
    allEsims: List<UserEsim>,
    pendingDeposits: List<AdminDepositRequest>,
    userAccounts: List<AdminUserAccount>,
    adminConfig: AdminConfig,
    sentNotifications: List<AdminBroadcastNotification>,
    language: AppLanguage,
    wholesalePackages: List<EsimAccessWholesalePackage> = emptyList(),
    wholesaleTransactions: List<EsimWholesaleTransaction> = emptyList(),
    auditLogs: List<AdminAuditLog> = emptyList(),
    onApproveDeposit: (String) -> Unit,
    onRejectDeposit: (String) -> Unit,
    onToggleUserSuspension: (String) -> Unit,
    onAdjustUserBalance: (String, Int) -> Unit,
    onToggleGatewayBlocked: (PaymentGateway) -> Unit = {},
    onUpdateAdminConfig: (AdminConfig) -> Unit,
    onSendNotification: (String, String, String) -> Unit,
    onOrderWholesaleEsim: ((String, Double, Double) -> Unit)? = null
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(AdminTab.STATS) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("admin_panel_screen")
    ) {
        // Admin Master Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0xFF0F1E3D), Color(0xFF081226))
                    )
                )
                .border(1.5.dp, SimXGoldLight.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF071426))
                                .border(1.5.dp, SimXGoldLight, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.simx_logo),
                                contentDescription = "Sim X Admin Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Back-Office Admin",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF2E7D32)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Joseph H. Salem (josephsalem946@gmail.com)",
                                fontSize = 11.sp,
                                color = Color(0xFFBACAF0)
                            )
                        }
                    }

                    // Network status indicator
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x3338B6FF)
                    ) {
                        Text(
                            text = "Réseau Sim X 🟢",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimXCyanAccent,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal Tabs Bar
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(AdminTab.entries) { tab ->
                val isSelected = tab == currentTab
                val badgeCount = when (tab) {
                    AdminTab.DEPOSITS -> pendingDeposits.count { it.status == "PENDING" }
                    AdminTab.GATEWAYS -> adminConfig.blockedGateways.size
                    else -> 0
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { currentTab = tab },
                    label = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = tab.icon, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            if (badgeCount > 0) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = SimXError
                                ) {
                                    Text(
                                        text = badgeCount.toString(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SimXBluePrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (currentTab) {
            AdminTab.STATS -> AdminStatsTab(
                transactions = allTransactions,
                esims = allEsims,
                adminConfig = adminConfig
            )
            AdminTab.WHOLESALE -> AdminWholesaleTab(
                packages = wholesalePackages,
                transactions = wholesaleTransactions,
                auditLogs = auditLogs,
                onOrderPackage = { pkg ->
                    onOrderWholesaleEsim?.invoke(pkg.packageCode, pkg.costPrice, pkg.sellingPrice)
                    Toast.makeText(context, "Commande grossiste passée pour ${pkg.name} !", Toast.LENGTH_SHORT).show()
                }
            )
            AdminTab.GATEWAYS -> AdminGatewaysTab(
                adminConfig = adminConfig,
                onToggleGatewayBlocked = { gw ->
                    onToggleGatewayBlocked(gw)
                    val wasBlocked = adminConfig.isGatewayBlocked(gw)
                    val statusText = if (!wasBlocked) "suspendue" else "activée"
                    Toast.makeText(context, "${gw.displayName} : $statusText !", Toast.LENGTH_SHORT).show()
                }
            )
            AdminTab.DEPOSITS -> AdminDepositsTab(
                pendingDeposits = pendingDeposits,
                onApprove = { id ->
                    onApproveDeposit(id)
                    Toast.makeText(context, "Dépôt validé et crédité !", Toast.LENGTH_SHORT).show()
                },
                onReject = { id ->
                    onRejectDeposit(id)
                    Toast.makeText(context, "Dépôt refusé.", Toast.LENGTH_SHORT).show()
                }
            )
            AdminTab.USERS -> AdminUsersTab(
                users = userAccounts,
                onToggleSuspension = onToggleUserSuspension,
                onAdjustBalance = onAdjustUserBalance
            )
            AdminTab.CONFIG -> AdminConfigTab(
                adminConfig = adminConfig,
                onSave = { updated ->
                    onUpdateAdminConfig(updated)
                    Toast.makeText(context, "Configuration Sim X mise à jour !", Toast.LENGTH_SHORT).show()
                }
            )
            AdminTab.NOTIFICATIONS -> AdminNotificationsTab(
                sentNotifications = sentNotifications,
                onSend = { title, msg, audience ->
                    onSendNotification(title, msg, audience)
                    Toast.makeText(context, "Notification Push diffusée !", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }
}

/**
 * 0. eSIM Access Wholesaler Integration & Audit Logs Tab
 */
@Composable
private fun AdminWholesaleTab(
    packages: List<EsimAccessWholesalePackage>,
    transactions: List<EsimWholesaleTransaction>,
    auditLogs: List<AdminAuditLog>,
    onOrderPackage: (EsimAccessWholesalePackage) -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        // En-tête Statut Grossiste eSIM Access
        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = SimXSlateNavy,
                border = BorderStroke(1.2.dp, SimXNeonGreen.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🌐", fontSize = 22.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "eSIM Access API Grossiste",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = "Fournisseur en gros connecté",
                                    fontSize = 11.sp,
                                    color = SimXNeonGreen
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SimXNeonGreen.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, SimXNeonGreen)
                        ) {
                            Text(
                                text = "SÉCURISÉ (SERVER-ONLY)",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = SimXNeonGreen,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(text = "Access Code :", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "2273••••f78f (Masqué)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = Color.White
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(text = "Secret Key :", fontSize = 10.sp, color = Color(0xFF94A3B8))
                            Text(
                                text = "3b40••••b9f6 (Protégé .env)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                color = SimXCyanAccent
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Règle de sécurité : Les identifiants eSIM Access transitent exclusivement par le backend Node.js (/api/admin/esim/...). Le client ne reçoit jamais les secrets bruts.",
                        fontSize = 10.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        }

        // Section Catalogue Forfaits Grossiste
        item {
            Text(
                text = "Catalogue Grossiste & Marges Sim X (${packages.size} forfaits) :",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        items(packages) { pkg ->
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = SimXSlateNavyVariant,
                border = BorderStroke(1.dp, SimXSlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pkg.name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Code : ${pkg.packageCode} · Couverture : ${pkg.coverage}",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = SimXCyanAccent.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "Marge : ${pkg.formattedMargin()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SimXCyanAccent,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Column {
                                Text(text = "Prix Gros", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = String.format("$%.2f", pkg.costPrice),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                            Column {
                                Text(text = "Prix Vente", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = String.format("$%.2f", pkg.sellingPrice),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SimXNeonGreen
                                )
                            }
                        }

                        Button(
                            onClick = { onOrderPackage(pkg) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary),
                            modifier = Modifier.height(36.dp)
                        ) {
                            Text(text = "Acheter Gros (Idempotent)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Section Transactions Grossistes (Table `esim_transactions`)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grand Livre Grossiste (esim_transactions) :",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${transactions.size} ordres",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        items(transactions) { tx ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = SimXSlateNavy,
                border = BorderStroke(1.dp, SimXSlateBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = tx.packageCode,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SimXSuccess.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = tx.status,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = SimXSuccess,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ICCID : ${tx.iccid} · Idempotency Key : ${tx.transactionId.take(16)}...",
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF94A3B8)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Achat : ${tx.formattedCost()} · Vente : ${tx.formattedSelling()}",
                            fontSize = 11.sp,
                            color = Color(0xFFCBD5E1)
                        )
                        Text(
                            text = "Marge nette : ${tx.formattedProfit()}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimXNeonGreen
                        )
                    }
                }
            }
        }

        // Section Journaux d'Audit (Table `audit_logs`)
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Journaux d'Audit de Sécurité (audit_logs) :",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "${auditLogs.size} logs",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }

        items(auditLogs.take(10)) { log ->
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = SimXSlateNavyVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🛡️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.action,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                        Text(
                            text = "Admin : ${log.adminId} · IP : ${log.ipAddress} · ${dateFormat.format(Date(log.timestamp))}",
                            fontSize = 9.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }
    }
}

/**
 * 1. Financial Overview & Analytics
 */
@Composable
private fun AdminStatsTab(
    transactions: List<WalletTransaction>,
    esims: List<UserEsim>,
    adminConfig: AdminConfig
) {
    val totalVolumeCents = transactions.filter { it.isCredit }.sumOf { it.amountCents } + 18500
    val marginPercentage = adminConfig.marginPercentage
    val grossMarginCents = (totalVolumeCents * marginPercentage) / 100
    val paymentGatewayFeesCents = (totalVolumeCents * 0.035).toInt() // Estimated ~3.5%
    val netProfitCents = grossMarginCents - paymentGatewayFeesCents

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Indicateurs Financiers & Marge de Gros (15%) :",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "Volume Total",
                    value = String.format("$%.2f", totalVolumeCents / 100.0),
                    subtitle = "≈ ${(totalVolumeCents / 100 * adminConfig.htgExchangeRate).toInt()} HTG",
                    accentColor = SimXCyanAccent,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "Bénéfice Net",
                    value = String.format("$%.2f", netProfitCents / 100.0),
                    subtitle = "Marge ${marginPercentage}% nette",
                    accentColor = SimXSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminMetricCard(
                    title = "Marge Brute",
                    value = String.format("$%.2f", grossMarginCents / 100.0),
                    subtitle = "Sur forfaits vendus",
                    accentColor = SimXGoldLight,
                    modifier = Modifier.weight(1f)
                )
                AdminMetricCard(
                    title = "eSIMs Actives",
                    value = "${esims.size + 14} actives",
                    subtitle = "Couverture 200+ pays",
                    accentColor = Color(0xFFC084FC),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Répartition par Passerelle de Paiement :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    GatewayShareRow(name = "MonCash (Digicel)", volume = "$120.00", share = "42%")
                    GatewayShareRow(name = "NatCash (Natcom)", volume = "$75.00", share = "26%")
                    GatewayShareRow(name = "Stripe (Cartes / Apple Pay)", volume = "$50.00", share = "18%")
                    GatewayShareRow(name = "Binance Pay / Crypto", volume = "$30.00", share = "10%")
                    GatewayShareRow(name = "Google Play Billing", volume = "$10.00", share = "4%")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun AdminMetricCard(
    title: String,
    value: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Black, color = accentColor)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GatewayShareRow(name: String, volume: String, share: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = name, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
        Row {
            Text(text = volume, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "($share)", fontSize = 12.sp, color = SimXCyanAccent)
        }
    }
}

/**
 * 2. Pending Deposits & Withdrawals Validation Queue
 */
@Composable
private fun AdminDepositsTab(
    pendingDeposits: List<AdminDepositRequest>,
    onApprove: (String) -> Unit,
    onReject: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Demandes de dépôt à valider (MonCash, NatCash, Crypto) :",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (pendingDeposits.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aucune demande en attente.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            items(pendingDeposits) { req ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = req.gateway.badge, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = req.userName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "${req.userPhone} · ${req.gateway.displayName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = req.formattedAmount(),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SimXSuccess
                                )
                                Text(
                                    text = req.formattedHtg(),
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Réf Marchand : ${req.transactionRef}",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = SimXCyanAccent
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        if (req.status == "PENDING") {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { onApprove(req.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = SimXSuccess),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Valider", fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                OutlinedButton(
                                    onClick = { onReject(req.id) },
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SimXError),
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Refuser", fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (req.status == "APPROVED") Color(0x2210B981) else Color(0x22EF4444),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = if (req.status == "APPROVED") "✓ Dépôt validé et crédité" else "✗ Dépôt rejeté",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (req.status == "APPROVED") SimXSuccess else SimXError,
                                    modifier = Modifier.padding(8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 3. User Accounts & Balance Management
 */
@Composable
private fun AdminUsersTab(
    users: List<AdminUserAccount>,
    onToggleSuspension: (String) -> Unit,
    onAdjustBalance: (String, Int) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val filtered = users.filter {
        it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.email.contains(searchQuery, ignoreCase = true) ||
                it.phone.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Rechercher un client (nom, email, tel)...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        items(filtered) { user ->
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (user.isSuspended) SimXError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.fullName,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (user.isSuspended) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = SimXError
                                    ) {
                                        Text(
                                            text = "BLOQUÉ",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                            Text(
                                text = "${user.email} · ${user.phone}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = user.formattedBalance(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = SimXCyanAccent
                            )
                            Text(
                                text = "${user.activeEsimsCount} eSIM",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAdjustBalance(user.uid, 500) }
                        ) {
                            Text(
                                text = "+5.00 $",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SimXSuccess,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onAdjustBalance(user.uid, -500) }
                        ) {
                            Text(
                                text = "-5.00 $",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = SimXError,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (user.isSuspended) SimXSuccess.copy(alpha = 0.2f) else SimXError.copy(alpha = 0.2f),
                            modifier = Modifier
                                .weight(1.5f)
                                .clickable { onToggleSuspension(user.uid) }
                        ) {
                            Text(
                                text = if (user.isSuspended) "Débloquer" else "Bloquer",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (user.isSuspended) SimXSuccess else SimXError,
                                modifier = Modifier.padding(vertical = 6.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 4. Admin Configuration (Margin, Bonuses, FX rate)
 */
@Composable
private fun AdminConfigTab(
    adminConfig: AdminConfig,
    onSave: (AdminConfig) -> Unit
) {
    var margin by remember { mutableIntStateOf(adminConfig.marginPercentage) }
    var welcomeBonus by remember { mutableIntStateOf(adminConfig.welcomeBonusUsd) }
    var referralBonus by remember { mutableIntStateOf(adminConfig.referralBonusUsd) }
    var exchangeRate by remember { mutableStateOf(adminConfig.htgExchangeRate.toString()) }
    var pushEnabled by remember { mutableStateOf(adminConfig.pushNotificationsEnabled) }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Paramètres de Tarification & Règles Métier :",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Marge Commerciale Sim X : $margin %",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Appliquée automatiquement sur les prix grossistes fournisseur.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(10, 15, 20, 25).forEach { m ->
                            val isSel = margin == m
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SimXBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { margin = m }
                            ) {
                                Text(
                                    text = "$m %",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Bonus & Récompenses :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Bonus inscription : $welcomeBonus $", fontSize = 13.sp)
                        Row {
                            Button(onClick = { if (welcomeBonus > 1) welcomeBonus-- }, modifier = Modifier.size(36.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) { Text("-") }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(onClick = { welcomeBonus++ }, modifier = Modifier.size(36.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) { Text("+") }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Bonus parrainage : $referralBonus $", fontSize = 13.sp)
                        Row {
                            Button(onClick = { if (referralBonus > 1) referralBonus-- }, modifier = Modifier.size(36.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) { Text("-") }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(onClick = { referralBonus++ }, modifier = Modifier.size(36.dp), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) { Text("+") }
                        }
                    }
                }
            }
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Taux de change USD / HTG :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = exchangeRate,
                        onValueChange = { exchangeRate = it },
                        label = { Text("Taux (Gourdes par 1 USD)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        item {
            Button(
                onClick = {
                    val rate = exchangeRate.toDoubleOrNull() ?: 132.0
                    onSave(
                        adminConfig.copy(
                            marginPercentage = margin,
                            welcomeBonusUsd = welcomeBonus,
                            referralBonusUsd = referralBonus,
                            htgExchangeRate = rate,
                            pushNotificationsEnabled = pushEnabled
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary)
            ) {
                Text(text = "Sauvegarder les configurations", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

/**
 * 5. Push Notifications Broadcast Center
 */
@Composable
private fun AdminNotificationsTab(
    sentNotifications: List<AdminBroadcastNotification>,
    onSend: (String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var selectedAudience by remember { mutableStateOf("Tous les utilisateurs") }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        item {
            Text(
                text = "Diffuser une Notification Push aux Clients :",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Titre de la notification") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        label = { Text("Message de diffusion") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Audience cible :",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("Tous", "Haïti (+509)", "Clients eSIM").forEach { aud ->
                            val isSel = selectedAudience.contains(aud)
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) SimXBluePrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { selectedAudience = aud }
                            ) {
                                Text(
                                    text = aud,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(vertical = 6.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (title.isNotBlank() && message.isNotBlank()) {
                                onSend(title, message, selectedAudience)
                                title = ""
                                message = ""
                            }
                        },
                        enabled = title.isNotBlank() && message.isNotBlank(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SimXCyanAccent)
                    ) {
                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFF031633))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Diffuser la notification", fontWeight = FontWeight.Bold, color = Color(0xFF031633))
                    }
                }
            }
        }

        item {
            Text(
                text = "Historique des alertes diffusées :",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        items(sentNotifications) { notif ->
            val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault()).format(Date(notif.sentTimestamp))
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = notif.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = dateStr,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notif.message,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Cible : ${notif.targetAudience}",
                        fontSize = 10.sp,
                        color = SimXCyanAccent
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AdminGatewaysTab(
    adminConfig: AdminConfig,
    onToggleGatewayBlocked: (PaymentGateway) -> Unit
) {
    val allGateways = PaymentGateway.values().toList()
    val blockedCount = adminConfig.blockedGateways.size
    val activeCount = allGateways.size - blockedCount

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(14.dp),
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_gateways_tab")
    ) {
        item {
            // Header Card
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Contrôle des Passerelles de Paiement",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (blockedCount > 0) SimXError.copy(alpha = 0.15f) else SimXSuccess.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = if (blockedCount > 0) "$blockedCount bloquée(s)" else "Toutes actives",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (blockedCount > 0) SimXError else SimXSuccess,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Bloquez ou débloquez en direct les moyens de recharge pour les utilisateurs. Les passerelles suspendues apparaissent en état 'Maintenance' dans le portefeuille client et empêchent tout dépôt.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SimXSuccess.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SimXSuccess.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🟢", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Actives", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "$activeCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SimXSuccess)
                                }
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = SimXError.copy(alpha = 0.1f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, SimXError.copy(alpha = 0.3f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = "🔴", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = "Bloquées", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "$blockedCount", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = SimXError)
                                }
                            }
                        }
                    }
                }
            }
        }

        items(allGateways) { gw ->
            val isBlocked = adminConfig.isGatewayBlocked(gw)
            AdminGatewayControlCard(
                gateway = gw,
                isBlocked = isBlocked,
                onToggle = { onToggleGatewayBlocked(gw) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AdminGatewayControlCard(
    gateway: PaymentGateway,
    isBlocked: Boolean,
    onToggle: () -> Unit
) {
    val techInfo = when (gateway) {
        PaymentGateway.MONCASH -> "Digicel Haïti - API Proxy Marchand (ID: MC-••••-7695)"
        PaymentGateway.NATCASH -> "Natcom Haïti - API Proxy Marchand (ID: NC-••••-3281)"
        PaymentGateway.STRIPE -> "Stripe 3DS Secure - Cartes Visa/Mastercard (PCI-DSS)"
        PaymentGateway.CRYPTO_BINANCE -> "Binance Pay Marchand API (Préfixe APP3) & Webhook Automatisé"
        PaymentGateway.GOOGLE_PLAY -> "Google Play In-App Billing v7 (Achat Direct)"
    }

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isBlocked) SimXError.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = gateway.badge, fontSize = 28.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = gateway.displayName,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = techInfo,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Quick Switch Toggle
                Switch(
                    checked = !isBlocked,
                    onCheckedChange = { onToggle() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = SimXSuccess,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = SimXError
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBlocked) SimXError.copy(alpha = 0.15f) else SimXSuccess.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBlocked) "🔴 BLOQUÉ / MAINTENANCE" else "🟢 OPÉRATIONNEL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isBlocked) SimXError else SimXSuccess
                        )
                    }
                }

                // Explicit Action Button
                if (isBlocked) {
                    Button(
                        onClick = onToggle,
                        colors = ButtonDefaults.buttonColors(containerColor = SimXSuccess),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Débloquer",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Débloquer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = onToggle,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = SimXError),
                        border = androidx.compose.foundation.BorderStroke(1.dp, SimXError),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Block,
                            contentDescription = "Bloquer",
                            tint = SimXError,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Bloquer",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = SimXError
                        )
                    }
                }
            }
        }
    }
}
