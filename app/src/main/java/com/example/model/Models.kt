package com.example.model

enum class Region(val labelKey: String) {
    ALL("all_regions"),
    CARIBBEAN("caribbean"),
    NORTH_AMERICA("north_america"),
    EUROPE("europe"),
    LATIN_AMERICA("latin_america"),
    WORLD("asia_world")
}

data class Country(
    val code: String,
    val nameFr: String,
    val nameHt: String,
    val nameEn: String,
    val flag: String,
    val region: Region,
    val isPopular: Boolean = false,
    val networkSpeed: String = "5G / 4G LTE",
    val startingPriceCents: Int = 276
) {
    fun localizedName(lang: AppLanguage): String = when (lang) {
        AppLanguage.FR -> nameFr
        AppLanguage.HT -> nameHt
        AppLanguage.EN -> nameEn
    }
}

data class EsimPlan(
    val id: String,
    val countryCode: String,
    val dataGb: Int, // -1 means Unlimited
    val validityDays: Int,
    val wholesaleUnits: Int, // wholesale 1/10000th USD (strictly hidden from UI)
    val retailPriceCents: Int, // Calculated server-side wholesale * 1.15 rounded up
    val networkName: String = "Sim X",
    val supportsHotspot: Boolean = true,
    val supportsVoiceAndSms: Boolean = false,
    val voiceMinutes: Int = 0, // e.g. 100, 300, 500, or -1 for unlimited
    val smsCount: Int = 0, // e.g. 100, 300, or -1 for unlimited
    val virtualPhoneNumberPrefix: String? = null // e.g. "+1", "+509", "+33"
) {
    val isUnlimited: Boolean get() = dataGb <= 0
    val hasVoiceAndSms: Boolean get() = supportsVoiceAndSms && (voiceMinutes != 0 || smsCount != 0)

    fun formattedData(lang: AppLanguage): String =
        if (isUnlimited) Strings.get("unlimited", lang)
        else Strings.get("gb_format", lang, dataGb)

    fun formattedDays(lang: AppLanguage): String =
        Strings.get("days_format", lang, validityDays)

    fun formattedPrice(): String = String.format("$%.2f", retailPriceCents / 100.0)
    fun formattedHtg(rate: Double = 132.0): String =
        String.format("%.0f HTG", (retailPriceCents / 100.0) * rate)

    fun formattedVoice(lang: AppLanguage): String =
        if (voiceMinutes < 0) "Appels illimités" else "$voiceMinutes min d'appels"

    fun formattedSms(lang: AppLanguage): String =
        if (smsCount < 0) "SMS illimités" else "$smsCount SMS"
}

enum class EsimStatus {
    PREPARING,
    READY,
    ACTIVE,
    EXPIRED
}

data class UserEsim(
    val id: String,
    val planId: String,
    val countryCode: String,
    val countryName: String,
    val flag: String,
    val dataGb: Int,
    val validityDays: Int,
    val pricePaidCents: Int,
    val status: EsimStatus,
    val lpaActivationCode: String,
    val iccid: String,
    val purchaseTimestamp: Long,
    val dataUsedMb: Int = 0,
    val totalDataMb: Int = 1024,
    val networkBrand: String = "Sim X", // Network name is always Sim X
    val supportsVoiceAndSms: Boolean = false,
    val virtualPhoneNumber: String? = null,
    val voiceMinutesRemaining: Int = 0,
    val voiceMinutesTotal: Int = 0,
    val smsRemaining: Int = 0,
    val smsTotal: Int = 0
) {
    val hasVirtualNumber: Boolean get() = supportsVoiceAndSms && !virtualPhoneNumber.isNullOrBlank()
}

enum class TransactionType {
    PLAN_PURCHASE,
    DEPOSIT_MONCASH,
    DEPOSIT_NATCASH,
    DEPOSIT_STRIPE,
    DEPOSIT_GOOGLE_PLAY,
    DEPOSIT_CRYPTO,
    WELCOME_BONUS,
    REFERRAL_REWARD,
    LOYALTY_REDEEM
}

data class WalletTransaction(
    val id: String,
    val type: TransactionType,
    val title: String,
    val countryCode: String? = null,
    val amountCents: Int, // positive for credits, negative for debits
    val timestamp: Long,
    val referenceId: String,
    val status: String = "CONFIRMED",
    val gatewayName: String = "Sim X Ledger"
) {
    val isCredit: Boolean get() = amountCents > 0
    fun formattedAmount(): String {
        val sign = if (isCredit) "+" else "-"
        return "$sign$" + String.format("%.2f", kotlin.math.abs(amountCents) / 100.0)
    }
}

enum class LoyaltyTier(val minPoints: Int, val multiplier: Double, val badge: String) {
    BRONZE(0, 1.0, "🥉"),
    SILVER(100, 1.25, "🥈"),
    GOLD(300, 1.5, "🥇 VIP");

    companion object {
        fun fromPoints(points: Int): LoyaltyTier = when {
            points >= 300 -> GOLD
            points >= 100 -> SILVER
            else -> BRONZE
        }
    }
}

data class UserProfile(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val balanceCents: Int, // 100 cents = $1.00 USD
    val loyaltyPoints: Int,
    val referralCode: String,
    val referredBy: String? = null,
    val isAnonymous: Boolean = false
) {
    val loyaltyTier: LoyaltyTier get() = LoyaltyTier.fromPoints(loyaltyPoints)
    fun formattedBalance(): String = String.format("$%.2f", balanceCents / 100.0)
    fun formattedHtg(rate: Double = 132.0): String =
        String.format("%.0f HTG", (balanceCents / 100.0) * rate)
}

enum class PaymentGateway(val displayName: String, val badge: String, val minAmountUsd: Int) {
    MONCASH("MonCash (Digicel)", "📱", 2),
    NATCASH("NatCash (Natcom)", "📲", 2),
    STRIPE("Carte / Apple & Google Pay", "💳", 5),
    GOOGLE_PLAY("Google Play Billing", "🛒", 5),
    CRYPTO_BINANCE("Binance Pay (Prefix APP3)", "🟡", 5)
}

data class BinancePayOrder(
    val merchantTradeNo: String,
    val prepayId: String,
    val appPrefix: String = "APP3",
    val amountUsd: Int,
    val currency: String = "USDT",
    val qrContent: String,
    val deeplink: String,
    val checkoutUrl: String,
    val status: PaymentProcessingStatus = PaymentProcessingStatus.IDLE,
    val statusMessage: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class AdminDepositRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userPhone: String,
    val gateway: PaymentGateway,
    val amountCents: Int,
    val transactionRef: String,
    val timestamp: Long,
    val status: String = "PENDING" // PENDING, APPROVED, REJECTED
) {
    fun formattedAmount(): String = String.format("$%.2f", amountCents / 100.0)
    fun formattedHtg(rate: Double = 132.0): String =
        String.format("%.0f HTG", (amountCents / 100.0) * rate)
}

data class AdminUserAccount(
    val uid: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val balanceCents: Int,
    val activeEsimsCount: Int,
    val isSuspended: Boolean = false,
    val createdAt: Long = System.currentTimeMillis() - 86400000L * 7
) {
    fun formattedBalance(): String = String.format("$%.2f", balanceCents / 100.0)
}

data class AdminConfig(
    val marginPercentage: Int = 15, // 15% wholesale margin
    val welcomeBonusUsd: Int = 3,
    val referralBonusUsd: Int = 3,
    val htgExchangeRate: Double = 132.0,
    val pushNotificationsEnabled: Boolean = true,
    val blockedGateways: Set<PaymentGateway> = emptySet()
) {
    fun isGatewayBlocked(gateway: PaymentGateway): Boolean = blockedGateways.contains(gateway)
    fun isGatewayEnabled(gateway: PaymentGateway): Boolean = !blockedGateways.contains(gateway)
}

data class AdminBroadcastNotification(
    val id: String,
    val title: String,
    val message: String,
    val targetAudience: String,
    val sentTimestamp: Long
)

enum class PaymentProcessingStatus {
    IDLE,
    INITIATING,
    PENDING_CUSTOMER_APPROVAL,
    PROCESSING_WEBHOOK,
    SUCCESS,
    FAILED
}

data class HaitianPaymentOrder(
    val orderId: String,
    val gateway: PaymentGateway,
    val amountUsd: Int,
    val amountHtg: Long,
    val customerPhone: String,
    val maskedMerchantDisplay: String,
    val paymentToken: String = "",
    val status: PaymentProcessingStatus = PaymentProcessingStatus.IDLE,
    val statusMessage: String = "",
    val transactionRef: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

fun String.maskSensitive(visibleTail: Int = 4): String {
    val clean = this.replace("\\s+".toRegex(), "")
    if (clean.length <= visibleTail) return "••••"
    val tail = clean.takeLast(visibleTail)
    val prefix = if (clean.startsWith("+509")) "+509 " else ""
    return "$prefix•••• $tail"
}

data class EsimWholesaleTransaction(
    val transactionId: String,
    val adminId: String,
    val packageCode: String,
    val iccid: String,
    val costPrice: Double,
    val sellingPrice: Double,
    val status: String = "SUCCESS",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun formattedCost(): String = String.format("$%.2f", costPrice)
    fun formattedSelling(): String = String.format("$%.2f", sellingPrice)
    fun formattedProfit(): String = String.format("+$%.2f", sellingPrice - costPrice)
}

data class AdminAuditLog(
    val logId: String,
    val adminId: String,
    val action: String,
    val ipAddress: String = "127.0.0.1",
    val timestamp: Long = System.currentTimeMillis()
)

data class EsimAccessWholesalePackage(
    val packageCode: String,
    val name: String,
    val countryCode: String,
    val dataMb: Int,
    val durationDays: Int,
    val costPrice: Double,
    val sellingPrice: Double,
    val coverage: String
) {
    fun formattedData(): String = if (dataMb >= 1024) "${dataMb / 1024} GB" else "${dataMb} MB"
    fun formattedMargin(): String = String.format("%.0f%%", ((sellingPrice - costPrice) / costPrice) * 100)
}

