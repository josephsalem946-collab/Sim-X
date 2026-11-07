package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.ui.res.painterResource
import com.example.R
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BinancePayOrder
import com.example.model.HaitianPaymentOrder
import com.example.model.PaymentGateway
import com.example.model.PaymentProcessingStatus
import com.example.model.Strings
import com.example.model.UserProfile
import com.example.model.WalletTransaction
import com.example.model.maskSensitive
import com.example.ui.components.BinancePayProcessingView
import com.example.ui.components.QrCodeView
import com.example.ui.components.ReceiptDialog
import com.example.ui.theme.BinanceDark
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.MonCashRed
import com.example.ui.theme.NatcashBlue
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXError
import com.example.ui.theme.SimXGoldAccent
import com.example.ui.theme.SimXGoldDark
import com.example.ui.theme.SimXGoldLight
import com.example.ui.theme.SimXNavyDeep
import com.example.ui.theme.SimXNeonGreen
import com.example.ui.theme.SimXSlateBorder
import com.example.ui.theme.SimXSlateNavy
import com.example.ui.theme.SimXSlateNavyVariant
import com.example.ui.theme.SimXSuccess
import com.example.ui.theme.SimXWarning
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WalletScreen(
    userProfile: UserProfile,
    transactions: List<WalletTransaction>,
    hideBalance: Boolean,
    currencyIsHtg: Boolean,
    language: AppLanguage,
    blockedGateways: Set<PaymentGateway> = emptySet(),
    activeHaitianPayment: HaitianPaymentOrder? = null,
    activeBinanceOrder: BinancePayOrder? = null,
    onToggleHideBalance: () -> Unit,
    onToggleCurrency: () -> Unit,
    onDeposit: (PaymentGateway, Int, String) -> WalletTransaction,
    onInitiateHaitianPayment: ((PaymentGateway, Int, String) -> Result<HaitianPaymentOrder>)? = null,
    onCompleteHaitianPayment: ((String) -> Result<WalletTransaction>)? = null,
    onDismissHaitianPayment: (() -> Unit)? = null,
    onInitiateBinancePayment: ((Int) -> Result<BinancePayOrder>)? = null,
    onCompleteBinancePayment: ((String) -> Result<WalletTransaction>)? = null,
    onDismissBinancePayment: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var selectedGatewayForDeposit by remember { mutableStateOf<PaymentGateway?>(null) }
    var selectedTransactionForReceipt by remember { mutableStateOf<WalletTransaction?>(null) }
    var isUserPhoneRevealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SimXNavyDeep)
            .padding(horizontal = 16.dp)
            .testTag("wallet_screen")
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            // Balance Card (Mode Sombre Ardoise/Marine avec accents Néon)
            item {
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    SimXSlateNavy,
                                    SimXSlateNavyVariant,
                                    Color(0xFF070E22)
                                )
                            )
                        )
                        .border(
                            BorderStroke(
                                1.5.dp,
                                Brush.horizontalGradient(
                                    listOf(SimXNeonGreen.copy(alpha = 0.6f), SimXCyanAccent.copy(alpha = 0.5f))
                                )
                            ),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(22.dp)
                ) {
                    Column {
                        // Header de la carte : Solde + Toggles + Logo officiel Sim X
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF071426))
                                        .border(1.5.dp, SimXCyanAccent, CircleShape)
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.simx_logo),
                                        contentDescription = "Sim X Logo",
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = Strings.get("balance_label", language),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFFCBD5E1)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        // Toggle Masquer/Afficher le solde
                                        IconButton(
                                            onClick = onToggleHideBalance,
                                            modifier = Modifier
                                                .size(26.dp)
                                                .testTag("toggle_balance_button")
                                        ) {
                                            Icon(
                                                imageVector = if (hideBalance) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                contentDescription = "Toggle Balance",
                                                tint = SimXNeonGreen,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "WALLET eSIM PRO",
                                        fontSize = 8.5.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 1.sp,
                                        color = SimXCyanAccent
                                    )
                                }
                            }

                            // Bouton bascule de devise USD <-> HTG
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x3300FF66),
                                border = BorderStroke(1.dp, SimXNeonGreen.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .clickable(onClick = onToggleCurrency)
                                    .testTag("currency_switch_button")
                            ) {
                                Text(
                                    text = if (currencyIsHtg) "Gdes (HTG) 🇭🇹" else "USD ($) 💵",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SimXNeonGreen,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Affichage Principal du Solde
                        val displayMain = if (hideBalance) "••••••"
                        else if (currencyIsHtg) userProfile.formattedHtg()
                        else userProfile.formattedBalance()

                        val displaySub = if (hideBalance) "••••"
                        else if (currencyIsHtg) "≈ " + userProfile.formattedBalance()
                        else "≈ " + userProfile.formattedHtg()

                        Text(
                            text = displayMain,
                            fontSize = 34.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )

                        Text(
                            text = displaySub,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = SimXCyanAccent
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Info Ligne Sécurisée Utilisateur (Téléphone masqué avec toggle)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0x33000000))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.PhoneAndroid,
                                    contentDescription = null,
                                    tint = SimXCyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                val displayedUserPhone = if (isUserPhoneRevealed) userProfile.phone else userProfile.phone.maskSensitive(4)
                                Text(
                                    text = "Numéro lié : $displayedUserPhone",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFFE2E8F0)
                                )
                            }

                            IconButton(
                                onClick = { isUserPhoneRevealed = !isUserPhoneRevealed },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = if (isUserPhoneRevealed) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = "Toggle Phone Mask",
                                    tint = SimXCyanAccent,
                                    modifier = Modifier.size(15.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Boutons de rechargement rapide ($5, $10, $25, $50)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf(5, 10, 25, 50).forEach { amount ->
                                val defaultGateway = if (!blockedGateways.contains(PaymentGateway.MONCASH)) {
                                    PaymentGateway.MONCASH
                                } else {
                                    PaymentGateway.values().firstOrNull { !blockedGateways.contains(it) }
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(SimXSlateNavyVariant)
                                        .border(1.dp, SimXSlateBorder, RoundedCornerShape(12.dp))
                                        .clickable {
                                            if (defaultGateway != null) {
                                                selectedGatewayForDeposit = defaultGateway
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Passerelles en maintenance.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "+$$amount",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SimXNeonGreen
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 1 : Passerelles Haïtiennes Officielles (MonCash & Natcash)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = SimXNeonGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Passerelles Haïtiennes Sécurisées :",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFF8FAFC)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = SimXNeonGreen.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "PROXY SÉCURISÉ",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = SimXNeonGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Sélecteur Dédié MonCash Online (Digicel Haïti)
                HaitianGatewaySelectorCard(
                    gateway = PaymentGateway.MONCASH,
                    brandTitle = "MonCash Online",
                    operator = "Digicel Haïti",
                    maskedMerchant = "ID Marchand Sim X : MC-••••-7695",
                    accentColor = SimXNeonGreen,
                    brandBadgeColor = MonCashRed,
                    badgeText = "📱 DIGICEL",
                    isBlocked = blockedGateways.contains(PaymentGateway.MONCASH),
                    onClick = { selectedGatewayForDeposit = PaymentGateway.MONCASH }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Sélecteur Dédié Natcash Online (Natcom Haïti)
                HaitianGatewaySelectorCard(
                    gateway = PaymentGateway.NATCASH,
                    brandTitle = "Natcash Online",
                    operator = "Natcom Haïti",
                    maskedMerchant = "ID Marchand Sim X : NC-••••-3281",
                    accentColor = SimXGoldAccent,
                    brandBadgeColor = NatcashBlue,
                    badgeText = "📲 NATCOM",
                    isBlocked = blockedGateways.contains(PaymentGateway.NATCASH),
                    onClick = { selectedGatewayForDeposit = PaymentGateway.NATCASH }
                )
            }

            // SECTION 2 : Autres Moyens Internationaux
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Autres Moyens de Paiement Internationaux :",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    GatewayItemRow(
                        gateway = PaymentGateway.STRIPE,
                        subtitle = Strings.get("stripe_desc", language),
                        isBlocked = blockedGateways.contains(PaymentGateway.STRIPE),
                        onClick = { selectedGatewayForDeposit = PaymentGateway.STRIPE }
                    )
                    GatewayItemRow(
                        gateway = PaymentGateway.CRYPTO_BINANCE,
                        subtitle = Strings.get("crypto_desc", language),
                        isBlocked = blockedGateways.contains(PaymentGateway.CRYPTO_BINANCE),
                        onClick = { selectedGatewayForDeposit = PaymentGateway.CRYPTO_BINANCE }
                    )
                    GatewayItemRow(
                        gateway = PaymentGateway.GOOGLE_PLAY,
                        subtitle = Strings.get("play_billing_desc", language),
                        isBlocked = blockedGateways.contains(PaymentGateway.GOOGLE_PLAY),
                        onClick = { selectedGatewayForDeposit = PaymentGateway.GOOGLE_PLAY }
                    )
                }
            }

            // Historique / Grand Livre des Transactions
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Grand livre des transactions :",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFF8FAFC)
                    )
                    Text(
                        text = "${transactions.size} entrées",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            items(transactions, key = { it.id }) { tx ->
                TransactionRowItem(
                    tx = tx,
                    language = language,
                    onClick = { selectedTransactionForReceipt = tx }
                )
            }

            item {
                Spacer(modifier = Modifier.height(90.dp))
            }
        }
    }

    // Modal Sheet de Dépôt / Initiation Proxy pour la Passerelle Sélectionnée
    selectedGatewayForDeposit?.let { gateway ->
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        var depositAmountUsd by remember { mutableIntStateOf(10) }
        var customerPhoneInput by remember { mutableStateOf(userProfile.phone) }
        var isPhoneMaskedInModal by remember { mutableStateOf(true) }

        // Statuts du flux de paiement
        var processingStage by remember { mutableStateOf(PaymentProcessingStatus.IDLE) }
        var activeOrder by remember { mutableStateOf<HaitianPaymentOrder?>(null) }
        var activeBinancePayOrder by remember { mutableStateOf<BinancePayOrder?>(null) }
        var statusMessage by remember { mutableStateOf("") }
        var transactionResult by remember { mutableStateOf<WalletTransaction?>(null) }

        ModalBottomSheet(
            onDismissRequest = {
                if (processingStage == PaymentProcessingStatus.IDLE || processingStage == PaymentProcessingStatus.SUCCESS || processingStage == PaymentProcessingStatus.FAILED) {
                    selectedGatewayForDeposit = null
                }
            },
            sheetState = sheetState,
            containerColor = SimXSlateNavy,
            dragHandle = null
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp)
                    .testTag("deposit_gateway_modal")
            ) {
                // Header du modal
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = gateway.badge, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = gateway.displayName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = SimXNeonGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (gateway == PaymentGateway.MONCASH || gateway == PaymentGateway.NATCASH)
                                        "Proxy Sécurisé Sim X · SSL Chiffré"
                                    else
                                        "Passerelle Certifiée Sim X",
                                    fontSize = 12.sp,
                                    color = SimXNeonGreen
                                )
                            }
                        }
                    }

                    if (processingStage == PaymentProcessingStatus.IDLE || processingStage == PaymentProcessingStatus.SUCCESS || processingStage == PaymentProcessingStatus.FAILED) {
                        IconButton(onClick = { selectedGatewayForDeposit = null }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Si nous sommes dans l'étape initiale (Configuration du montant)
                if (processingStage == PaymentProcessingStatus.IDLE) {
                    // Sélection du montant
                    Text(
                        text = "Montant du rechargement :",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFCBD5E1)
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(5, 10, 25, 50, 100).forEach { amt ->
                            val isSelected = depositAmountUsd == amt
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) SimXBluePrimary else SimXSlateNavyVariant,
                                border = BorderStroke(
                                    1.dp,
                                    if (isSelected) SimXNeonGreen else SimXSlateBorder
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { depositAmountUsd = amt }
                            ) {
                                Text(
                                    text = "$$amt",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Calcul de conversion HTG en temps réel
                    val htgEquivalent = depositAmountUsd * 132
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(SimXSlateNavyVariant)
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Équivalent en Gourdes :",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Text(
                                text = "≈ $htgEquivalent HTG",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = SimXNeonGreen
                            )
                        }
                        Text(
                            text = "Taux : 1 USD = 132 HTG",
                            fontSize = 11.sp,
                            color = SimXCyanAccent
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Composants spécifiques selon la passerelle
                    when (gateway) {
                        PaymentGateway.MONCASH, PaymentGateway.NATCASH -> {
                            val maskedMerchantInfo = if (gateway == PaymentGateway.MONCASH) {
                                "ID Marchand Officiel : MC-••••-7695 (Sécurisé via Proxy)"
                            } else {
                                "ID Marchand Officiel : NC-••••-3281 (Sécurisé via Proxy)"
                            }

                            // Bannière de Sécurité & Confidentialité
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0x2200FF66),
                                border = BorderStroke(1.dp, SimXNeonGreen.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Security,
                                            contentDescription = null,
                                            tint = SimXNeonGreen,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Protection des données & Proxy Sécurisé",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = SimXNeonGreen
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = maskedMerchantInfo,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Les transactions transitent par un proxy intermédiaire sécurisé (.env). Aucun numéro de téléphone marchand n'est exposé.",
                                        fontSize = 10.sp,
                                        color = Color(0xFFCBD5E1)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Champ Téléphone Client avec Toggle Masquage
                            OutlinedTextField(
                                value = if (isPhoneMaskedInModal) customerPhoneInput.maskSensitive(4) else customerPhoneInput,
                                onValueChange = {
                                    if (!isPhoneMaskedInModal) customerPhoneInput = it
                                },
                                readOnly = isPhoneMaskedInModal,
                                label = { Text("Numéro client ${gateway.displayName}") },
                                trailingIcon = {
                                    IconButton(onClick = { isPhoneMaskedInModal = !isPhoneMaskedInModal }) {
                                        Icon(
                                            imageVector = if (isPhoneMaskedInModal) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = "Toggle Input Mask",
                                            tint = SimXCyanAccent
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = SimXNeonGreen,
                                    unfocusedBorderColor = SimXSlateBorder,
                                    focusedLabelColor = SimXNeonGreen,
                                    unfocusedLabelColor = Color(0xFF94A3B8),
                                    focusedTextColor = Color.White,
                                    unfocusedTextColor = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Code de validation envoyé automatiquement par SMS ou prompt USSD *202#.",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        PaymentGateway.STRIPE -> {
                            OutlinedTextField(
                                value = "4242 •••• •••• 4242",
                                onValueChange = {},
                                label = { Text("Carte bancaire chiffrée (Stripe 3DS)") },
                                modifier = Modifier.fillMaxWidth(),
                                enabled = false,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        PaymentGateway.CRYPTO_BINANCE -> {
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = BinanceDark,
                                border = BorderStroke(1.2.dp, BinanceYellow.copy(alpha = 0.6f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = "🟡", fontSize = 20.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Binance Pay Marchand",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = BinanceYellow.copy(alpha = 0.2f),
                                            border = BorderStroke(1.dp, BinanceYellow)
                                        ) {
                                            Text(
                                                text = "PRÉFIXE APP3",
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Black,
                                                color = BinanceYellow,
                                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Paiement direct en USDT instantané et sécurisé via l'API Marchand Binance Pay avec traçabilité préfixe APP3.",
                                        fontSize = 11.sp,
                                        color = Color(0xFFCBD5E1)
                                    )

                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(text = "Sous-Marchand :", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text(
                                                text = "APP3_SIMX_MERCHANT",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = Color.White,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(text = "Frais de réseau :", fontSize = 10.sp, color = Color(0xFF94A3B8))
                                            Text(
                                                text = "0.00 USDT (Gratuit)",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = SimXNeonGreen
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        PaymentGateway.GOOGLE_PLAY -> {
                            Text(
                                text = "Achat in-app sécurisé via le Google Play Store Billing v7.",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Bouton de lancement de paiement
                    Button(
                        onClick = {
                            if (gateway == PaymentGateway.MONCASH || gateway == PaymentGateway.NATCASH) {
                                processingStage = PaymentProcessingStatus.INITIATING
                                statusMessage = "Initiation de la requête proxy vers le backend..."

                                coroutineScope.launch {
                                    delay(900)
                                    // Appel du proxy
                                    val initResult = onInitiateHaitianPayment?.invoke(
                                        gateway,
                                        depositAmountUsd,
                                        customerPhoneInput
                                    )

                                    if (initResult != null && initResult.isSuccess) {
                                        activeOrder = initResult.getOrThrow()
                                        processingStage = PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL
                                        statusMessage = "En attente de validation client sur le téléphone..."
                                    } else {
                                        // Mode standard fallback si lambda non fourni
                                        activeOrder = HaitianPaymentOrder(
                                            orderId = "sx_ord_" + (if (gateway == PaymentGateway.MONCASH) "mc_" else "nc_") + System.currentTimeMillis().toString().takeLast(6),
                                            gateway = gateway,
                                            amountUsd = depositAmountUsd,
                                            amountHtg = (depositAmountUsd * 132).toLong(),
                                            customerPhone = customerPhoneInput,
                                            maskedMerchantDisplay = if (gateway == PaymentGateway.MONCASH) "MC-••••-7695" else "NC-••••-3281",
                                            status = PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL
                                        )
                                        processingStage = PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL
                                        statusMessage = "En attente de validation client..."
                                    }
                                }
                            } else if (gateway == PaymentGateway.CRYPTO_BINANCE) {
                                processingStage = PaymentProcessingStatus.INITIATING
                                statusMessage = "Génération de l'ordre Binance Pay avec préfixe APP3..."

                                coroutineScope.launch {
                                    delay(800)
                                    val binanceResult = onInitiateBinancePayment?.invoke(depositAmountUsd)
                                    if (binanceResult != null && binanceResult.isSuccess) {
                                        activeBinancePayOrder = binanceResult.getOrThrow()
                                    } else {
                                        val ts = System.currentTimeMillis()
                                        val rnd = java.util.UUID.randomUUID().toString().take(6).uppercase()
                                        val tradeNo = "APP3_${ts}_$rnd"
                                        activeBinancePayOrder = BinancePayOrder(
                                            merchantTradeNo = tradeNo,
                                            prepayId = "APP3_BPAY_" + java.util.UUID.randomUUID().toString().replace("-", "").take(16).uppercase(),
                                            appPrefix = "APP3",
                                            amountUsd = depositAmountUsd,
                                            currency = "USDT",
                                            qrContent = "binancepay://qr/d/$tradeNo",
                                            deeplink = "binancepay://pay?merchantTradeNo=$tradeNo",
                                            checkoutUrl = "https://pay.binance.com/checkout?tradeNo=$tradeNo",
                                            status = PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL,
                                            statusMessage = "Ordre Binance Pay ($tradeNo) créé avec succès."
                                        )
                                    }
                                    processingStage = PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL
                                    statusMessage = "Scannez le QR Code avec Binance ou ouvrez l'application."
                                }
                            } else {
                                // Traitement direct pour Stripe / Play
                                processingStage = PaymentProcessingStatus.INITIATING
                                coroutineScope.launch {
                                    delay(1200)
                                    val tx = onDeposit(gateway, depositAmountUsd, customerPhoneInput)
                                    transactionResult = tx
                                    processingStage = PaymentProcessingStatus.SUCCESS
                                    statusMessage = "Dépôt de $$depositAmountUsd validé avec succès !"
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("confirm_deposit_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = when (gateway) {
                                PaymentGateway.MONCASH -> MonCashRed
                                PaymentGateway.CRYPTO_BINANCE -> BinanceYellow
                                else -> SimXBluePrimary
                            }
                        )
                    ) {
                        Text(
                            text = when (gateway) {
                                PaymentGateway.MONCASH, PaymentGateway.NATCASH ->
                                    "Initier via Proxy Sécurisé · $$depositAmountUsd ($htgEquivalent HTG)"
                                PaymentGateway.CRYPTO_BINANCE ->
                                    "Payer avec Binance Pay (APP3) · $$depositAmountUsd.00 USDT"
                                else ->
                                    "Confirmer le dépôt · $$depositAmountUsd.00"
                            },
                            fontWeight = FontWeight.Bold,
                            color = if (gateway == PaymentGateway.CRYPTO_BINANCE) BinanceDark else Color.White
                        )
                    }
                } else if (gateway == PaymentGateway.CRYPTO_BINANCE) {
                    BinancePayProcessingView(
                        order = activeBinancePayOrder,
                        status = processingStage,
                        amountUsd = depositAmountUsd,
                        statusMessage = statusMessage,
                        onSimulateWebhookSuccess = {
                            processingStage = PaymentProcessingStatus.PROCESSING_WEBHOOK
                            statusMessage = "Vérification HMAC-SHA512 du webhook Binance Pay..."
                            coroutineScope.launch {
                                delay(1100)
                                val tradeNo = activeBinancePayOrder?.merchantTradeNo
                                val completion = if (tradeNo != null && onCompleteBinancePayment != null) {
                                    onCompleteBinancePayment(tradeNo).getOrNull()
                                } else {
                                    onDeposit(gateway, depositAmountUsd, tradeNo ?: "APP3")
                                }
                                transactionResult = completion
                                processingStage = PaymentProcessingStatus.SUCCESS
                                statusMessage = "Paiement de $$depositAmountUsd.00 USDT validé par le webhook Binance Pay (APP3) !"
                            }
                        },
                        onSimulateFailure = {
                            processingStage = PaymentProcessingStatus.FAILED
                            statusMessage = "Ordre Binance Pay expiré ou annulé par l'utilisateur."
                        },
                        onRetry = {
                            processingStage = PaymentProcessingStatus.IDLE
                            statusMessage = ""
                        },
                        onViewReceipt = {
                            transactionResult?.let { tx ->
                                selectedGatewayForDeposit = null
                                selectedTransactionForReceipt = tx
                            }
                        },
                        onDone = {
                            selectedGatewayForDeposit = null
                            onDismissBinancePayment?.invoke()
                        }
                    )
                } else {
                    // ÉTAPE DE SUIVI EN DIRECT & ANIMATIONS D'ÉTAT
                    HaitianPaymentProcessingView(
                        gateway = gateway,
                        status = processingStage,
                        order = activeOrder,
                        statusMessage = statusMessage,
                        amountUsd = depositAmountUsd,
                        amountHtg = depositAmountUsd * 132,
                        onSimulateCustomerApproval = {
                            processingStage = PaymentProcessingStatus.PROCESSING_WEBHOOK
                            statusMessage = "Vérification de la signature HMAC du webhook backend..."

                            coroutineScope.launch {
                                delay(1200)
                                val completion = if (activeOrder != null && onCompleteHaitianPayment != null) {
                                    onCompleteHaitianPayment(activeOrder!!.orderId).getOrNull()
                                } else {
                                    onDeposit(gateway, depositAmountUsd, customerPhoneInput)
                                }

                                transactionResult = completion
                                processingStage = PaymentProcessingStatus.SUCCESS
                                statusMessage = "Paiement confirmé par le webhook Sim X ! Solde rechargé."
                            }
                        },
                        onSimulateFailure = {
                            processingStage = PaymentProcessingStatus.FAILED
                            statusMessage = "Transaction refusée par le client ou délai expiré."
                        },
                        onRetry = {
                            processingStage = PaymentProcessingStatus.IDLE
                            statusMessage = ""
                        },
                        onViewReceipt = {
                            transactionResult?.let { tx ->
                                selectedGatewayForDeposit = null
                                selectedTransactionForReceipt = tx
                            }
                        },
                        onDone = {
                            selectedGatewayForDeposit = null
                            onDismissHaitianPayment?.invoke()
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // Modal de Reçu Officiel de Transaction
    selectedTransactionForReceipt?.let { tx ->
        ReceiptDialog(
            transaction = tx,
            language = language,
            onDismiss = { selectedTransactionForReceipt = null }
        )
    }
}

/**
 * Composant de Carte Dédiée pour les Passerelles Haïtiennes (MonCash & Natcash)
 */
@Composable
fun HaitianGatewaySelectorCard(
    gateway: PaymentGateway,
    brandTitle: String,
    operator: String,
    maskedMerchant: String,
    accentColor: Color,
    brandBadgeColor: Color,
    badgeText: String,
    isBlocked: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SimXSlateNavy,
        border = BorderStroke(
            1.2.dp,
            if (isBlocked) SimXError.copy(alpha = 0.5f) else accentColor.copy(alpha = 0.4f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isBlocked) {
                    Toast.makeText(
                        context,
                        "$brandTitle est temporairement suspendu par l'administration.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onClick()
                }
            }
            .testTag("haitian_gateway_${gateway.name.lowercase()}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = brandBadgeColor
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = brandTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = operator,
                            fontSize = 12.sp,
                            color = accentColor
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isBlocked) SimXError.copy(alpha = 0.2f) else accentColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = if (isBlocked) "SUSPENDU" else "PAYER EN HTG",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isBlocked) SimXError else accentColor,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Numéro de marchand masqué et statut de sécurité
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(SimXSlateNavyVariant)
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = maskedMerchant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFFCBD5E1)
                    )
                }

                Text(
                    text = "🔒 Proxy .env",
                    fontSize = 10.sp,
                    color = SimXCyanAccent
                )
            }
        }
    }
}

/**
 * Vue Dynamique d'Animation d'État pour le Flux de Paiement (Pending / Success / Failed)
 */
@Composable
fun HaitianPaymentProcessingView(
    gateway: PaymentGateway,
    status: PaymentProcessingStatus,
    order: HaitianPaymentOrder?,
    statusMessage: String,
    amountUsd: Int,
    amountHtg: Int,
    onSimulateCustomerApproval: () -> Unit,
    onSimulateFailure: () -> Unit,
    onRetry: () -> Unit,
    onViewReceipt: () -> Unit,
    onDone: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_transition")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(SimXSlateNavyVariant)
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En-tête des montants
        Text(
            text = "$amountHtg HTG ($$amountUsd.00 USD)",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = Color.White
        )
        if (order != null) {
            Text(
                text = "Commande : ${order.orderId}",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = SimXCyanAccent
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Indicateur visuel d'état animé
        when (status) {
            PaymentProcessingStatus.INITIATING -> {
                CircularProgressIndicator(
                    color = SimXCyanAccent,
                    modifier = Modifier.size(54.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Initiation du proxy sécurisé...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(SimXWarning.copy(alpha = 0.2f))
                        .border(2.dp, SimXWarning, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = "Pending",
                        tint = SimXWarning,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "EN ATTENTE DE VALIDATION",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SimXWarning
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Un SMS de confirmation a été envoyé sur votre numéro. Veuillez approuver la transaction ou composer le code USSD *202#.",
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Boutons d'interaction de simulation
                Button(
                    onClick = onSimulateCustomerApproval,
                    colors = ButtonDefaults.buttonColors(containerColor = SimXSuccess),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.Black)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Valider sur mon téléphone (Simuler Approbation)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onSimulateFailure,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SimXError),
                    border = BorderStroke(1.dp, SimXError),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Annuler la transaction")
                }
            }

            PaymentProcessingStatus.PROCESSING_WEBHOOK -> {
                CircularProgressIndicator(
                    color = SimXNeonGreen,
                    modifier = Modifier.size(54.dp),
                    strokeWidth = 3.dp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Vérification Webhook HMAC...",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SimXNeonGreen
                )
            }

            PaymentProcessingStatus.SUCCESS -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SimXSuccess.copy(alpha = 0.2f))
                        .border(2.dp, SimXSuccess, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Success",
                        tint = SimXSuccess,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "PAIEMENT VALIDÉ AVEC SUCCÈS !",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    color = SimXSuccess
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "+$amountUsd.00 ($amountHtg HTG) crédités dans votre portefeuille Sim X.",
                    fontSize = 13.sp,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onViewReceipt,
                    colors = ButtonDefaults.buttonColors(containerColor = SimXBluePrimary),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Receipt, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Afficher le reçu officiel", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDone,
                    border = BorderStroke(1.dp, SimXSlateBorder),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Terminer", color = Color.White)
                }
            }

            PaymentProcessingStatus.FAILED -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SimXError.copy(alpha = 0.2f))
                        .border(2.dp, SimXError, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Failed",
                        tint = SimXError,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "ÉCHEC DE LA TRANSACTION",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    color = SimXError
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = statusMessage.ifEmpty { "La transaction n'a pas pu être validée." },
                    fontSize = 12.sp,
                    color = Color(0xFFCBD5E1),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = SimXSlateBorder),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Réessayer", fontWeight = FontWeight.Bold)
                }
            }

            else -> {}
        }
    }
}

@Composable
fun GatewayItemRow(
    gateway: PaymentGateway,
    subtitle: String,
    isBlocked: Boolean = false,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (isBlocked) SimXSlateNavy.copy(alpha = 0.6f) else SimXSlateNavy,
        border = BorderStroke(
            1.dp,
            if (isBlocked) SimXError.copy(alpha = 0.5f) else SimXSlateBorder
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (isBlocked) {
                    Toast.makeText(
                        context,
                        "${gateway.displayName} est temporairement suspendu par l'administration.",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    onClick()
                }
            }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = gateway.badge, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = gateway.displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        if (gateway == PaymentGateway.CRYPTO_BINANCE) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BinanceYellow.copy(alpha = 0.2f),
                                border = BorderStroke(0.8.dp, BinanceYellow.copy(alpha = 0.6f))
                            ) {
                                Text(
                                    text = "PRÉFIXE APP3",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BinanceYellow,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        if (isBlocked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = SimXError.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "BLOQUÉ",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = SimXError,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = if (isBlocked) "Temporairement désactivé par l'administrateur" else subtitle,
                        fontSize = 11.sp,
                        color = if (isBlocked) SimXError else Color(0xFF94A3B8)
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (isBlocked) SimXError.copy(alpha = 0.1f) else SimXBluePrimary.copy(alpha = 0.2f)
            ) {
                Text(
                    text = if (isBlocked) "Suspendu" else "Recharger",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isBlocked) SimXError else SimXCyanAccent,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun TransactionRowItem(
    tx: WalletTransaction,
    language: AppLanguage,
    onClick: () -> Unit
) {
    val dateStr = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
        .format(Date(tx.timestamp))

    Surface(
        shape = RoundedCornerShape(14.dp),
        color = SimXSlateNavy,
        border = BorderStroke(
            1.dp,
            SimXSlateBorder.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (tx.isCredit) SimXNeonGreen.copy(alpha = 0.15f) else SimXCyanAccent.copy(alpha = 0.15f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (tx.isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                        contentDescription = null,
                        tint = if (tx.isCredit) SimXNeonGreen else SimXCyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = tx.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    Text(
                        text = "$dateStr · ${tx.gatewayName}",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = tx.formattedAmount(),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (tx.isCredit) SimXNeonGreen else Color.White
                )
                Text(
                    text = "Reçu ›",
                    fontSize = 11.sp,
                    color = SimXCyanAccent
                )
            }
        }
    }
}
