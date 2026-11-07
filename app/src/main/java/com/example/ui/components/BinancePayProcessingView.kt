package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BinancePayOrder
import com.example.model.PaymentProcessingStatus
import com.example.ui.theme.BinanceDark
import com.example.ui.theme.BinanceYellow
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXError
import com.example.ui.theme.SimXNeonGreen
import com.example.ui.theme.SimXSlateBorder
import com.example.ui.theme.SimXSlateNavy
import com.example.ui.theme.SimXSlateNavyVariant
import com.example.ui.theme.SimXSuccess

/**
 * Interface interactive complète pour les paiements Binance Pay avec le préfixe marchand APP3
 */
@Composable
fun BinancePayProcessingView(
    order: BinancePayOrder?,
    status: PaymentProcessingStatus,
    amountUsd: Int,
    statusMessage: String,
    onSimulateWebhookSuccess: () -> Unit,
    onSimulateFailure: () -> Unit,
    onRetry: () -> Unit,
    onViewReceipt: () -> Unit,
    onDone: () -> Unit
) {
    val context = LocalContext.current

    val pulseTransition = rememberInfiniteTransition(label = "binance_pulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // En-tête Binance Pay Officiel avec préfixe APP3
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = BinanceDark,
            border = BorderStroke(1.5.dp, BinanceYellow.copy(alpha = 0.7f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🟡", fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Binance Pay Checkout",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Marchand Certifié Sim X",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    // Badge Préfixe APP3
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BinanceYellow.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, BinanceYellow)
                    ) {
                        Text(
                            text = "PRÉFIXE APP3",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = BinanceYellow,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Montant en USDT
                Text(
                    text = "$amountUsd.00 USDT",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = BinanceYellow
                )
                Text(
                    text = "Montant débité directement depuis votre compte Binance",
                    fontSize = 11.sp,
                    color = Color(0xFFCBD5E1)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ÉTATS DE TRAITEMENT
        when (status) {
            PaymentProcessingStatus.INITIATING -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BinanceDark)
                        .border(1.dp, BinanceYellow.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BinanceYellow,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Génération de l'ordre Binance Pay...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL -> {
                // Code QR Interactif Binance Pay
                val qrData = order?.qrContent ?: "binancepay://qr/d/APP3_DEMO"
                Box(
                    modifier = Modifier
                        .scale(pulseScale)
                        .padding(4.dp)
                ) {
                    QrCodeView(
                        data = qrData,
                        size = 160.dp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Informations sur le Trade Number avec préfixe APP3
                val tradeNo = order?.merchantTradeNo ?: "APP3_${System.currentTimeMillis()}"
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SimXSlateNavyVariant,
                    border = BorderStroke(1.dp, SimXSlateBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Numéro de Transaction (Trade No) :",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = tradeNo,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace,
                                    color = BinanceYellow
                                )
                            }
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("TradeNo", tradeNo)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Trade No copié : $tradeNo", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copier",
                                    tint = BinanceYellow,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        order?.prepayId?.let { prepayId ->
                            Text(
                                text = "Prepay ID : $prepayId",
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bouton d'ouverture DeepLink / Web Binance Pay
                Button(
                    onClick = {
                        val deeplink = order?.deeplink ?: "https://pay.binance.com"
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(deeplink))
                        try {
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Fallback navigateur web
                            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(order?.checkoutUrl ?: "https://pay.binance.com"))
                            try {
                                context.startActivity(webIntent)
                            } catch (_: Exception) {
                                Toast.makeText(context, "Lien Binance Pay : $deeplink", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BinanceYellow,
                        contentColor = BinanceDark
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ouvrir Binance Pay (App / Web)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bouton de simulation webhook en direct
                OutlinedButton(
                    onClick = onSimulateWebhookSuccess,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SimXNeonGreen.copy(alpha = 0.6f)),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = SimXNeonGreen
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Confirmer la réception du Webhook (PAID)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onSimulateFailure,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, SimXSlateBorder),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF94A3B8)
                    )
                ) {
                    Text(text = "Annuler l'ordre", fontSize = 12.sp)
                }
            }

            PaymentProcessingStatus.PROCESSING_WEBHOOK -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(BinanceDark)
                        .border(1.5.dp, BinanceYellow, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = BinanceYellow,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Vérification HMAC-SHA512 Binance Pay...",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Validation cryptographique du webhook reçu sur /api/payment/binance/webhook",
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF94A3B8)
                )
            }

            PaymentProcessingStatus.SUCCESS -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SimXSuccess.copy(alpha = 0.15f))
                        .border(2.dp, SimXSuccess, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = SimXSuccess,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Paiement Binance Pay Confirmé !",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Votre portefeuille a été crédité de $$amountUsd.00 USDT. Référence marchand : ${order?.merchantTradeNo ?: "APP3_OK"}",
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFCBD5E1)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onViewReceipt,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, BinanceYellow),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = BinanceYellow
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Receipt,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Reçu APP3", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Button(
                        onClick = onDone,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BinanceYellow,
                            contentColor = BinanceDark
                        )
                    ) {
                        Text(text = "Terminer", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }

            PaymentProcessingStatus.FAILED -> {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(SimXError.copy(alpha = 0.15f))
                        .border(2.dp, SimXError, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = SimXError,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Transaction Binance Pay Annulée",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = SimXError
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = statusMessage.ifEmpty { "La transaction n'a pas pu être validée ou a expiré." },
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFF94A3B8)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SimXSlateNavyVariant
                    )
                ) {
                    Text(text = "Réessayer avec un nouvel ordre", color = Color.White)
                }
            }

            PaymentProcessingStatus.IDLE -> {
                // Rien à afficher en idle
            }
        }
    }
}
