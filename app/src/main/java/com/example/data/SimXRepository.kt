package com.example.data

import com.example.model.AdminAuditLog
import com.example.model.AdminBroadcastNotification
import com.example.model.AdminConfig
import com.example.model.AdminDepositRequest
import com.example.model.AdminUserAccount
import com.example.model.AppLanguage
import com.example.model.BinancePayOrder
import com.example.model.Country
import com.example.model.EsimAccessWholesalePackage
import com.example.model.EsimPlan
import com.example.model.EsimStatus
import com.example.model.EsimWholesaleTransaction
import com.example.model.HaitianPaymentOrder
import com.example.model.LoyaltyTier
import com.example.model.PaymentGateway
import com.example.model.PaymentProcessingStatus
import com.example.model.TransactionType
import com.example.model.UserEsim
import com.example.model.UserProfile
import com.example.model.WalletTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

class SimXRepository {

    private val scope = CoroutineScope(Dispatchers.Default)

    private val _userProfile = MutableStateFlow(
        UserProfile(
            uid = "u_" + UUID.randomUUID().toString().take(8),
            fullName = "Joseph Hollyventz Salem",
            email = "josephsalem946@gmail.com",
            phone = "+509 3712 8890",
            balanceCents = 1000, // $10.00 initial test balance
            loyaltyPoints = 120, // Silver tier
            referralCode = "SIMX-JOSEPH24"
        )
    )
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    private val _userEsims = MutableStateFlow<List<UserEsim>>(emptyList())
    val userEsims: StateFlow<List<UserEsim>> = _userEsims.asStateFlow()

    private val _transactions = MutableStateFlow<List<WalletTransaction>>(emptyList())
    val transactions: StateFlow<List<WalletTransaction>> = _transactions.asStateFlow()

    private val _selectedLanguage = MutableStateFlow(AppLanguage.FR)
    val selectedLanguage: StateFlow<AppLanguage> = _selectedLanguage.asStateFlow()

    private val _hideBalance = MutableStateFlow(false)
    val hideBalance: StateFlow<Boolean> = _hideBalance.asStateFlow()

    private val _currencyIsHtg = MutableStateFlow(false)
    val currencyIsHtg: StateFlow<Boolean> = _currencyIsHtg.asStateFlow()

    // Haitian Payment Order state for reactive modal and animated status
    private val _activeHaitianPayment = MutableStateFlow<HaitianPaymentOrder?>(null)
    val activeHaitianPayment: StateFlow<HaitianPaymentOrder?> = _activeHaitianPayment.asStateFlow()

    // Binance Pay Order state (Prefix APP3)
    private val _activeBinanceOrder = MutableStateFlow<BinancePayOrder?>(null)
    val activeBinanceOrder: StateFlow<BinancePayOrder?> = _activeBinanceOrder.asStateFlow()

    // One Free active session timer (remaining seconds)
    private val _oneFreeRemainingSeconds = MutableStateFlow(0)
    val oneFreeRemainingSeconds: StateFlow<Int> = _oneFreeRemainingSeconds.asStateFlow()
    private var oneFreeJob: Job? = null

    // Admin Back-Office State
    private val _adminConfig = MutableStateFlow(AdminConfig())
    val adminConfig: StateFlow<AdminConfig> = _adminConfig.asStateFlow()

    private val _pendingDepositRequests = MutableStateFlow<List<AdminDepositRequest>>(
        listOf(
            AdminDepositRequest(
                id = "req_mc_01",
                userId = "u_haiti_992",
                userName = "Jean-Baptiste Moïse",
                userPhone = "+509 3841 9920",
                gateway = PaymentGateway.MONCASH,
                amountCents = 2500, // $25.00
                transactionRef = "MC-89312048",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 18,
                status = "PENDING"
            ),
            AdminDepositRequest(
                id = "req_nc_02",
                userId = "u_diaspora_104",
                userName = "Marline Desrosiers",
                userPhone = "+509 3654 1120",
                gateway = PaymentGateway.NATCASH,
                amountCents = 5000, // $50.00
                transactionRef = "NC-44021983",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 45,
                status = "PENDING"
            ),
            AdminDepositRequest(
                id = "req_usdt_03",
                userId = "u_crypto_77",
                userName = "Alexandre Pierre",
                userPhone = "+1 305 842 1192",
                gateway = PaymentGateway.CRYPTO_BINANCE,
                amountCents = 10000, // $100.00
                transactionRef = "0x8f2a1b94e803c7d6124",
                timestamp = System.currentTimeMillis() - 1000L * 60 * 90,
                status = "PENDING"
            )
        )
    )
    val pendingDepositRequests: StateFlow<List<AdminDepositRequest>> = _pendingDepositRequests.asStateFlow()

    private val _userAccounts = MutableStateFlow<List<AdminUserAccount>>(
        listOf(
            AdminUserAccount(
                uid = "u_admin_001",
                fullName = "Joseph Hollyventz Salem (Propriétaire)",
                email = "josephsalem946@gmail.com",
                phone = "+509 3712 8890",
                balanceCents = 1000,
                activeEsimsCount = 1,
                isSuspended = false
            ),
            AdminUserAccount(
                uid = "u_client_002",
                fullName = "Marie-Claire Joseph",
                email = "marie.claire@gmail.com",
                phone = "+509 3654 1120",
                balanceCents = 3500,
                activeEsimsCount = 2,
                isSuspended = false
            ),
            AdminUserAccount(
                uid = "u_client_003",
                fullName = "Ronald Célestin",
                email = "rcelestin.travel@yahoo.fr",
                phone = "+509 3712 5501",
                balanceCents = 0,
                activeEsimsCount = 0,
                isSuspended = false
            ),
            AdminUserAccount(
                uid = "u_client_004",
                fullName = "Samuel Lindor",
                email = "sam.lindor99@gmail.com",
                phone = "+1 954 621 8840",
                balanceCents = 1500,
                activeEsimsCount = 1,
                isSuspended = true
            )
        )
    )
    val userAccounts: StateFlow<List<AdminUserAccount>> = _userAccounts.asStateFlow()

    private val _broadcastNotifications = MutableStateFlow<List<AdminBroadcastNotification>>(
        listOf(
            AdminBroadcastNotification(
                id = "notif_01",
                title = "Bienvenue sur Sim X !",
                message = "Profitez de nos forfaits Caraïbes & USA sans frais de roaming.",
                targetAudience = "Tous les utilisateurs",
                sentTimestamp = System.currentTimeMillis() - 86400000L
            )
        )
    )
    val broadcastNotifications: StateFlow<List<AdminBroadcastNotification>> = _broadcastNotifications.asStateFlow()

    // 1. eSIM Access Wholesale Packages (Catalogue Grossiste)
    private val _wholesalePackages = MutableStateFlow<List<EsimAccessWholesalePackage>>(
        listOf(
            EsimAccessWholesalePackage("ESIM_HT_1GB_7D", "Haïti 1 GB - 7 Jours", "HT", 1024, 7, 2.00, 2.76, "Haïti (Digicel & Natcom)"),
            EsimAccessWholesalePackage("ESIM_HT_3GB_30D", "Haïti 3 GB - 30 Jours", "HT", 3072, 30, 4.80, 6.67, "Haïti (Digicel & Natcom)"),
            EsimAccessWholesalePackage("ESIM_US_5GB_30D", "USA 5 GB - 30 Jours", "US", 5120, 30, 7.50, 10.35, "États-Unis (T-Mobile & AT&T)"),
            EsimAccessWholesalePackage("ESIM_CARIB_10GB_30D", "Caraïbes Pass 10 GB - 30 Jours", "CARIB", 10240, 30, 16.00, 21.85, "Haïti, Rep. Dom, Jamaïque, USA"),
            EsimAccessWholesalePackage("ESIM_GLOBAL_20GB_60D", "Global Pass 20 GB - 60 Jours", "GLOBAL", 20480, 60, 32.00, 42.50, "200+ Pays & Territoires")
        )
    )
    val wholesalePackages: StateFlow<List<EsimAccessWholesalePackage>> = _wholesalePackages.asStateFlow()

    // 2. Table `esim_transactions` (Grand livre des achats grossistes)
    private val _wholesaleTransactions = MutableStateFlow<List<EsimWholesaleTransaction>>(
        listOf(
            EsimWholesaleTransaction(
                transactionId = "tx_esim_wh_001",
                adminId = "adm_super_01",
                packageCode = "ESIM_HT_3GB_30D",
                iccid = "89014103211823901234",
                costPrice = 4.80,
                sellingPrice = 6.67,
                status = "SUCCESS",
                createdAt = System.currentTimeMillis() - 3600000L * 4
            )
        )
    )
    val wholesaleTransactions: StateFlow<List<EsimWholesaleTransaction>> = _wholesaleTransactions.asStateFlow()

    // 3. Table `audit_logs` (Journaux d'audit de sécurité)
    private val _auditLogs = MutableStateFlow<List<AdminAuditLog>>(
        listOf(
            AdminAuditLog(
                logId = "log_001",
                adminId = "adm_super_01",
                action = "SYSTEM_INITIALIZED: eSIM Access Wholesaler Integration & Payment Gateways",
                ipAddress = "127.0.0.1",
                timestamp = System.currentTimeMillis() - 86400000L * 2
            ),
            AdminAuditLog(
                logId = "log_002",
                adminId = "adm_super_01",
                action = "PURCHASE_ESIM_WHOLESALE: ESIM_HT_3GB_30D (ICCID: 89014103••••)",
                ipAddress = "192.168.1.10",
                timestamp = System.currentTimeMillis() - 3600000L * 4
            )
        )
    )
    val auditLogs: StateFlow<List<AdminAuditLog>> = _auditLogs.asStateFlow()

    init {
        // Initial welcome bonus and sample transaction
        val welcomeTx = WalletTransaction(
            id = "tx_welcome_001",
            type = TransactionType.WELCOME_BONUS,
            title = "Bonus d'inscription Sim X",
            amountCents = 300,
            timestamp = System.currentTimeMillis() - 86400000L * 2,
            referenceId = "SX-BONUS-WELCOME",
            status = "CONFIRMED",
            gatewayName = "Sim X Rewards"
        )
        val depositTx = WalletTransaction(
            id = "tx_dep_002",
            type = TransactionType.DEPOSIT_MONCASH,
            title = "Dépôt MonCash Digicel",
            amountCents = 700,
            timestamp = System.currentTimeMillis() - 86400000L,
            referenceId = "MC-SX-89210482",
            status = "CONFIRMED",
            gatewayName = "MonCash Gateway"
        )
        _transactions.value = listOf(depositTx, welcomeTx)

        // Initial sample active eSIM with Voice & SMS support for testing out the UI immediately
        val initialEsim = UserEsim(
            id = "sx_esim_demo",
            planId = "US_VOICE_10GB_30D",
            countryCode = "US",
            countryName = "États-Unis",
            flag = "🇺🇸",
            dataGb = 10,
            validityDays = 30,
            pricePaidCents = 1350,
            status = EsimStatus.READY,
            lpaActivationCode = "LPA:1\$smdp.simx.net\$SX-8901410321182390-US-VOICE",
            iccid = "89014103211823901234",
            purchaseTimestamp = System.currentTimeMillis() - 3600000L * 3,
            dataUsedMb = 1240,
            totalDataMb = 10240,
            networkBrand = "Sim X",
            supportsVoiceAndSms = true,
            virtualPhoneNumber = "+1 (305) 842-7790",
            voiceMinutesRemaining = 185,
            voiceMinutesTotal = 300,
            smsRemaining = 240,
            smsTotal = 300
        )
        val haitiEsim = UserEsim(
            id = "sx_esim_haiti_demo",
            planId = "HT_VOICE_5GB_30D",
            countryCode = "HT",
            countryName = "Haïti",
            flag = "🇭🇹",
            dataGb = 5,
            validityDays = 30,
            pricePaidCents = 890,
            status = EsimStatus.ACTIVE,
            lpaActivationCode = "LPA:1\$smdp.simx.net\$SX-8901410321182390-HT-VOICE",
            iccid = "89014103211823905581",
            purchaseTimestamp = System.currentTimeMillis() - 86400000L,
            dataUsedMb = 640,
            totalDataMb = 5120,
            networkBrand = "Sim X",
            supportsVoiceAndSms = true,
            virtualPhoneNumber = "+509 3712 8890",
            voiceMinutesRemaining = 90,
            voiceMinutesTotal = 150,
            smsRemaining = 120,
            smsTotal = 150
        )
        _userEsims.value = listOf(initialEsim, haitiEsim)
    }

    fun setLanguage(language: AppLanguage) {
        _selectedLanguage.value = language
    }

    fun toggleHideBalance() {
        _hideBalance.update { !it }
    }

    fun toggleCurrency() {
        _currencyIsHtg.update { !it }
    }

    /**
     * Achat de forfait eSIM:
     * - Vérifie le solde en centimes
     * - Débite le portefeuille de manière atomique
     * - Attribue 5 points de fidélité par $
     * - Crée l'eSIM Sim X avec code SM-DP+ LPA
     * - Statut passe de PREPARING à READY après un court délai de provisionnement
     */
    fun purchasePlan(country: Country, plan: EsimPlan): Result<UserEsim> {
        val user = _userProfile.value
        if (user.balanceCents < plan.retailPriceCents) {
            return Result.failure(IllegalStateException("Solde insuffisant"))
        }

        val esimId = "esim_" + UUID.randomUUID().toString().take(10)
        val lpaCode = "LPA:1\$smdp.simx.net\$SX-" +
                UUID.randomUUID().toString().take(8).uppercase() + "-" +
                UUID.randomUUID().toString().take(6).uppercase()
        val iccid = "8901" + (1000000000000000L + (Math.random() * 8999999999999999L).toLong())

        val virtualNumber = if (plan.supportsVoiceAndSms) {
            SimXCatalog.generateSampleVirtualNumber(country.code)
        } else null

        val newEsim = UserEsim(
            id = esimId,
            planId = plan.id,
            countryCode = country.code,
            countryName = country.nameFr,
            flag = country.flag,
            dataGb = plan.dataGb,
            validityDays = plan.validityDays,
            pricePaidCents = plan.retailPriceCents,
            status = EsimStatus.PREPARING,
            lpaActivationCode = lpaCode,
            iccid = iccid,
            purchaseTimestamp = System.currentTimeMillis(),
            dataUsedMb = 0,
            totalDataMb = if (plan.dataGb > 0) plan.dataGb * 1024 else 51200,
            networkBrand = "Sim X",
            supportsVoiceAndSms = plan.supportsVoiceAndSms,
            virtualPhoneNumber = virtualNumber,
            voiceMinutesRemaining = if (plan.voiceMinutes < 0) 9999 else plan.voiceMinutes,
            voiceMinutesTotal = if (plan.voiceMinutes < 0) 9999 else plan.voiceMinutes,
            smsRemaining = if (plan.smsCount < 0) 9999 else plan.smsCount,
            smsTotal = if (plan.smsCount < 0) 9999 else plan.smsCount
        )

        // Points earned (5 points per whole dollar)
        val earnedPoints = (plan.retailPriceCents / 100) * 5

        _userProfile.update { current ->
            current.copy(
                balanceCents = current.balanceCents - plan.retailPriceCents,
                loyaltyPoints = current.loyaltyPoints + earnedPoints
            )
        }

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            type = TransactionType.PLAN_PURCHASE,
            title = "Forfait Sim X ${country.flag} ${country.nameFr} (${plan.formattedData(_selectedLanguage.value)})",
            countryCode = country.code,
            amountCents = -plan.retailPriceCents,
            timestamp = System.currentTimeMillis(),
            referenceId = esimId,
            status = "CONFIRMED",
            gatewayName = "Sim X Core"
        )

        _userEsims.update { listOf(newEsim) + it }
        _transactions.update { listOf(tx) + it }

        // Transition from PREPARING to READY after 2.5 seconds
        scope.launch {
            delay(2500)
            _userEsims.update { list ->
                list.map { item ->
                    if (item.id == esimId) item.copy(status = EsimStatus.READY) else item
                }
            }
        }

        return Result.success(newEsim)
    }

    /**
     * Dépôt de crédits dans le portefeuille
     */
    fun depositCredits(
        gateway: PaymentGateway,
        amountUsd: Int,
        referenceNotes: String = ""
    ): WalletTransaction {
        if (_adminConfig.value.isGatewayBlocked(gateway)) {
            throw IllegalStateException("La passerelle ${gateway.displayName} est actuellement bloquée par l'administrateur.")
        }
        val amountCents = amountUsd * 100
        val txType = when (gateway) {
            PaymentGateway.MONCASH -> TransactionType.DEPOSIT_MONCASH
            PaymentGateway.NATCASH -> TransactionType.DEPOSIT_NATCASH
            PaymentGateway.STRIPE -> TransactionType.DEPOSIT_STRIPE
            PaymentGateway.GOOGLE_PLAY -> TransactionType.DEPOSIT_GOOGLE_PLAY
            PaymentGateway.CRYPTO_BINANCE -> TransactionType.DEPOSIT_CRYPTO
        }

        val refId = when (gateway) {
            PaymentGateway.MONCASH -> "MC-" + (10000000 + (Math.random() * 89999999).toLong())
            PaymentGateway.NATCASH -> "NC-" + (10000000 + (Math.random() * 89999999).toLong())
            PaymentGateway.STRIPE -> "pi_simx_" + UUID.randomUUID().toString().take(12)
            PaymentGateway.GOOGLE_PLAY -> "GPA." + (1000 + (Math.random() * 9000).toInt()) + "-" + UUID.randomUUID().toString().take(8)
            PaymentGateway.CRYPTO_BINANCE -> "APP3_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().take(6).uppercase()
        }

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            type = txType,
            title = "Dépôt via ${gateway.displayName}",
            amountCents = amountCents,
            timestamp = System.currentTimeMillis(),
            referenceId = refId,
            status = "CONFIRMED",
            gatewayName = gateway.displayName
        )

        _userProfile.update { it.copy(balanceCents = it.balanceCents + amountCents) }
        _transactions.update { listOf(tx) + it }
        return tx
    }

    /**
     * Initiation d'un paiement sécurisé MonCash ou Natcash via le Proxy Backend
     * Conforme aux exigences de sécurité :
     * - Les vrais numéros de marchand ne transitent JAMAIS vers le client
     * - Génération d'un Order ID unique avec token de paiement
     * - Gestion des étapes : INITIATING -> PENDING_CUSTOMER_APPROVAL -> PROCESSING_WEBHOOK -> SUCCESS
     */
    fun initiateHaitianPayment(
        gateway: PaymentGateway,
        amountUsd: Int,
        customerPhone: String
    ): Result<HaitianPaymentOrder> {
        if (_adminConfig.value.isGatewayBlocked(gateway)) {
            return Result.failure(IllegalStateException("La passerelle ${gateway.displayName} est actuellement bloquée par l'administrateur."))
        }

        val orderId = "sx_ord_" + (if (gateway == PaymentGateway.MONCASH) "mc_" else "nc_") +
                UUID.randomUUID().toString().take(8)
        val rate = _adminConfig.value.htgExchangeRate
        val amountHtg = (amountUsd * rate).toLong()
        val maskedMerchant = when (gateway) {
            PaymentGateway.MONCASH -> "Digicel MonCash ID Marchand: MC-••••-7695 (Proxy Sécurisé)"
            PaymentGateway.NATCASH -> "Natcom Natcash ID Marchand: NC-••••-3281 (Proxy Sécurisé)"
            else -> "Passerelle Marchande Certifiée"
        }

        val order = HaitianPaymentOrder(
            orderId = orderId,
            gateway = gateway,
            amountUsd = amountUsd,
            amountHtg = amountHtg,
            customerPhone = customerPhone,
            maskedMerchantDisplay = maskedMerchant,
            paymentToken = (if (gateway == PaymentGateway.MONCASH) "mc_tok_" else "nc_tok_") + UUID.randomUUID().toString().replace("-", "").take(16),
            status = PaymentProcessingStatus.INITIATING,
            statusMessage = "Connexion sécurisée au serveur proxy Sim X...",
            timestamp = System.currentTimeMillis()
        )

        _activeHaitianPayment.value = order
        return Result.success(order)
    }

    fun updateHaitianPaymentStatus(orderId: String, status: PaymentProcessingStatus, message: String) {
        _activeHaitianPayment.update { current ->
            if (current?.orderId == orderId) {
                current.copy(status = status, statusMessage = message)
            } else current
        }
    }

    /**
     * Confirmation et validation finale du webhook pour le paiement haïtien
     */
    fun completeHaitianPayment(orderId: String): Result<WalletTransaction> {
        val currentOrder = _activeHaitianPayment.value
            ?: return Result.failure(IllegalStateException("Aucune commande de paiement en cours."))

        if (currentOrder.orderId != orderId) {
            return Result.failure(IllegalArgumentException("ID de commande non correspondant."))
        }

        val gateway = currentOrder.gateway
        val amountCents = currentOrder.amountUsd * 100
        val refId = when (gateway) {
            PaymentGateway.MONCASH -> "MC-SX-" + (10000000 + (Math.random() * 89999999).toLong())
            PaymentGateway.NATCASH -> "NC-SX-" + (10000000 + (Math.random() * 89999999).toLong())
            else -> "SX-" + UUID.randomUUID().toString().take(10).uppercase()
        }

        val tx = WalletTransaction(
            id = "tx_ht_" + UUID.randomUUID().toString().take(8),
            type = if (gateway == PaymentGateway.MONCASH) TransactionType.DEPOSIT_MONCASH else TransactionType.DEPOSIT_NATCASH,
            title = "Dépôt validé via ${gateway.displayName}",
            amountCents = amountCents,
            timestamp = System.currentTimeMillis(),
            referenceId = refId,
            status = "CONFIRMED",
            gatewayName = "${gateway.displayName} Proxy"
        )

        _userProfile.update { it.copy(balanceCents = it.balanceCents + amountCents) }
        _transactions.update { listOf(tx) + it }

        _activeHaitianPayment.update {
            it?.copy(
                status = PaymentProcessingStatus.SUCCESS,
                statusMessage = "Paiement de ${currentOrder.amountHtg} HTG ($${currentOrder.amountUsd}.00) confirmé avec succès !",
                transactionRef = refId
            )
        }

        return Result.success(tx)
    }

    fun dismissActiveHaitianPayment() {
        _activeHaitianPayment.value = null
    }

    /**
     * Initiation d'un paiement Binance Pay officiel avec le préfixe APP3
     */
    fun initiateBinancePayment(amountUsd: Int): Result<BinancePayOrder> {
        if (_adminConfig.value.isGatewayBlocked(PaymentGateway.CRYPTO_BINANCE)) {
            return Result.failure(IllegalStateException("Binance Pay est actuellement bloqué par l'administrateur."))
        }
        val timestamp = System.currentTimeMillis()
        val randomSuffix = UUID.randomUUID().toString().take(6).uppercase()
        val merchantTradeNo = "APP3_${timestamp}_${randomSuffix}"
        val prepayId = "APP3_BPAY_" + UUID.randomUUID().toString().replace("-", "").take(16).uppercase()
        val deeplink = "binancepay://pay?merchantTradeNo=$merchantTradeNo&prepayId=$prepayId"
        val checkoutUrl = "https://pay.binance.com/checkout?tradeNo=$merchantTradeNo"
        val qrContent = "binancepay://qr/d/$merchantTradeNo"

        val order = BinancePayOrder(
            merchantTradeNo = merchantTradeNo,
            prepayId = prepayId,
            appPrefix = "APP3",
            amountUsd = amountUsd,
            currency = "USDT",
            qrContent = qrContent,
            deeplink = deeplink,
            checkoutUrl = checkoutUrl,
            status = PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL,
            statusMessage = "Ordre Binance Pay ($merchantTradeNo) initié. Scannez avec l'application Binance."
        )

        _activeBinanceOrder.value = order
        return Result.success(order)
    }

    /**
     * Confirmation et validation du paiement Binance Pay (APP3)
     */
    fun completeBinancePayment(merchantTradeNo: String): Result<WalletTransaction> {
        val currentOrder = _activeBinanceOrder.value
            ?: return Result.failure(IllegalStateException("Aucune commande Binance Pay en cours."))

        if (currentOrder.merchantTradeNo != merchantTradeNo) {
            return Result.failure(IllegalArgumentException("Numéro de transaction Binance Pay non valide."))
        }

        val amountCents = currentOrder.amountUsd * 100
        val refId = currentOrder.merchantTradeNo

        val tx = WalletTransaction(
            id = "tx_bp_" + UUID.randomUUID().toString().take(8),
            type = TransactionType.DEPOSIT_CRYPTO,
            title = "Dépôt Binance Pay (APP3)",
            amountCents = amountCents,
            timestamp = System.currentTimeMillis(),
            referenceId = refId,
            status = "CONFIRMED",
            gatewayName = "Binance Pay Official"
        )

        _userProfile.update { it.copy(balanceCents = it.balanceCents + amountCents) }
        _transactions.update { listOf(tx) + it }

        _activeBinanceOrder.update {
            it?.copy(
                status = PaymentProcessingStatus.SUCCESS,
                statusMessage = "Paiement de ${currentOrder.amountUsd} USDT confirmé via Binance Pay (APP3) !",
                timestamp = System.currentTimeMillis()
            )
        }

        return Result.success(tx)
    }

    fun dismissActiveBinancePayment() {
        _activeBinanceOrder.value = null
    }

    /**
     * Lancer la session "One Free" (15 minutes de connexion d'urgence sponsorisée)
     */
    fun startOneFreeSession() {
        oneFreeJob?.cancel()
        _oneFreeRemainingSeconds.value = 15 * 60 // 15 minutes = 900 seconds
        oneFreeJob = scope.launch {
            while (_oneFreeRemainingSeconds.value > 0) {
                delay(1000)
                _oneFreeRemainingSeconds.update { kotlin.math.max(0, it - 1) }
            }
        }
    }

    /**
     * Échanger des points de fidélité (100 pts = $1.00 de crédit)
     */
    fun redeemPoints(): Boolean {
        val user = _userProfile.value
        if (user.loyaltyPoints < 100) return false

        _userProfile.update {
            it.copy(
                loyaltyPoints = it.loyaltyPoints - 100,
                balanceCents = it.balanceCents + 100
            )
        }

        val tx = WalletTransaction(
            id = "tx_" + UUID.randomUUID().toString().take(8),
            type = TransactionType.LOYALTY_REDEEM,
            title = "Échange de 100 points Fidélité",
            amountCents = 100,
            timestamp = System.currentTimeMillis(),
            referenceId = "REWARD-" + UUID.randomUUID().toString().take(6).uppercase(),
            status = "CONFIRMED",
            gatewayName = "Sim X Club"
        )
        _transactions.update { listOf(tx) + it }
        return true
    }

    /**
     * Utiliser un code promo
     */
    fun applyPromoCode(code: String): Result<String> {
        val clean = code.trim().uppercase()
        if (clean == "BIENVENU" || clean == "SIMX2026" || clean == "HAITI5") {
            val bonusCents = if (clean == "HAITI5") 500 else 300
            _userProfile.update { it.copy(balanceCents = it.balanceCents + bonusCents) }
            val tx = WalletTransaction(
                id = "tx_promo_" + UUID.randomUUID().toString().take(6),
                type = TransactionType.WELCOME_BONUS,
                title = "Code Promo appliqué : $clean",
                amountCents = bonusCents,
                timestamp = System.currentTimeMillis(),
                referenceId = "PROMO-$clean",
                status = "CONFIRMED",
                gatewayName = "Sim X Promos"
            )
            _transactions.update { listOf(tx) + it }
            return Result.success("Code $clean validé ! +$${bonusCents / 100}.00 ajoutés.")
        }
        return Result.failure(IllegalArgumentException("Code promo invalide ou expiré."))
    }

    /**
     * Droit à l'oubli / Suppression des données personnelles (RGPD)
     * Conforme aux tests d'audit2 : anonymise les identifiants, réinitialise le profil,
     * conserve les agrégats comptables sans données d'identité.
     */
    fun eraseUserData() {
        val anonId = "anon_" + UUID.randomUUID().toString().take(6)
        _userProfile.update {
            UserProfile(
                uid = anonId,
                fullName = "Utilisateur Anonyme",
                email = "$anonId@privacy.simx.net",
                phone = "+509 ********",
                balanceCents = 0,
                loyaltyPoints = 0,
                referralCode = "ANON-000",
                referredBy = null,
                isAnonymous = true
            )
        }
        _userEsims.update { emptyList() }
    }

    /**
     * Recharger des données sur une eSIM existante
     */
    fun topUpEsim(esimId: String, additionalGb: Int, costCents: Int): Result<Boolean> {
        val user = _userProfile.value
        if (user.balanceCents < costCents) {
            return Result.failure(IllegalStateException("Solde insuffisant pour la recharge"))
        }

        val targetEsim = _userEsims.value.find { it.id == esimId }
            ?: return Result.failure(IllegalArgumentException("eSIM introuvable"))

        _userProfile.update { current ->
            current.copy(
                balanceCents = current.balanceCents - costCents,
                loyaltyPoints = current.loyaltyPoints + ((costCents / 100) * 5)
            )
        }

        _userEsims.update { list ->
            list.map { esim ->
                if (esim.id == esimId) {
                    esim.copy(
                        dataGb = if (esim.dataGb > 0) esim.dataGb + additionalGb else esim.dataGb,
                        totalDataMb = esim.totalDataMb + (additionalGb * 1024),
                        status = EsimStatus.ACTIVE
                    )
                } else esim
            }
        }

        val tx = WalletTransaction(
            id = "tx_topup_" + UUID.randomUUID().toString().take(6),
            type = TransactionType.PLAN_PURCHASE,
            title = "Recharge +${additionalGb} Go (${targetEsim.countryName})",
            countryCode = targetEsim.countryCode,
            amountCents = -costCents,
            timestamp = System.currentTimeMillis(),
            referenceId = "TOPUP-" + UUID.randomUUID().toString().take(6).uppercase(),
            status = "CONFIRMED",
            gatewayName = "Sim X Core"
        )
        _transactions.update { listOf(tx) + it }
        return Result.success(true)
    }

    /**
     * Simulation de consommation de données
     */
    fun simulateDataUsage(esimId: String, mbToConsume: Int = 150) {
        _userEsims.update { list ->
            list.map { esim ->
                if (esim.id == esimId) {
                    val newUsed = kotlin.math.min(esim.totalDataMb, esim.dataUsedMb + mbToConsume)
                    esim.copy(
                        dataUsedMb = newUsed,
                        status = EsimStatus.ACTIVE
                    )
                } else esim
            }
        }
    }

    // --- Admin Operations ---

    fun approveDepositRequest(requestId: String) {
        val req = _pendingDepositRequests.value.find { it.id == requestId } ?: return
        _pendingDepositRequests.update { list ->
            list.map { if (it.id == requestId) it.copy(status = "APPROVED") else it }
        }
        if (req.userId == _userProfile.value.uid || req.userPhone == _userProfile.value.phone) {
            _userProfile.update { it.copy(balanceCents = it.balanceCents + req.amountCents) }
        }
        _userAccounts.update { list ->
            list.map {
                if (it.uid == req.userId || it.phone == req.userPhone) {
                    it.copy(balanceCents = it.balanceCents + req.amountCents)
                } else it
            }
        }
        val tx = WalletTransaction(
            id = "tx_admin_app_" + UUID.randomUUID().toString().take(6),
            type = when (req.gateway) {
                PaymentGateway.MONCASH -> TransactionType.DEPOSIT_MONCASH
                PaymentGateway.NATCASH -> TransactionType.DEPOSIT_NATCASH
                PaymentGateway.STRIPE -> TransactionType.DEPOSIT_STRIPE
                PaymentGateway.GOOGLE_PLAY -> TransactionType.DEPOSIT_GOOGLE_PLAY
                PaymentGateway.CRYPTO_BINANCE -> TransactionType.DEPOSIT_CRYPTO
            },
            title = "Dépôt approuvé : ${req.gateway.displayName}",
            amountCents = req.amountCents,
            timestamp = System.currentTimeMillis(),
            referenceId = req.transactionRef,
            status = "CONFIRMED",
            gatewayName = "Validation Admin Sim X"
        )
        _transactions.update { listOf(tx) + it }
    }

    fun rejectDepositRequest(requestId: String) {
        _pendingDepositRequests.update { list ->
            list.map { if (it.id == requestId) it.copy(status = "REJECTED") else it }
        }
    }

    fun toggleUserSuspension(uid: String) {
        _userAccounts.update { list ->
            list.map { if (it.uid == uid) it.copy(isSuspended = !it.isSuspended) else it }
        }
    }

    fun adjustUserBalance(uid: String, deltaCents: Int) {
        _userAccounts.update { list ->
            list.map {
                if (it.uid == uid) it.copy(balanceCents = kotlin.math.max(0, it.balanceCents + deltaCents))
                else it
            }
        }
        if (uid == _userProfile.value.uid) {
            _userProfile.update { it.copy(balanceCents = kotlin.math.max(0, it.balanceCents + deltaCents)) }
        }
    }

    fun toggleGatewayBlocked(gateway: PaymentGateway) {
        _adminConfig.update { current ->
            val updated = if (current.blockedGateways.contains(gateway)) {
                current.blockedGateways - gateway
            } else {
                current.blockedGateways + gateway
            }
            current.copy(blockedGateways = updated)
        }
    }

    fun updateAdminConfig(newConfig: AdminConfig) {
        _adminConfig.value = newConfig
    }

    fun sendBroadcastNotification(title: String, message: String, target: String) {
        val notif = AdminBroadcastNotification(
            id = "notif_" + UUID.randomUUID().toString().take(6),
            title = title,
            message = message,
            targetAudience = target,
            sentTimestamp = System.currentTimeMillis()
        )
        _broadcastNotifications.update { listOf(notif) + it }
        logAdminAudit("SEND_BROADCAST_NOTIFICATION: '$title' to $target")
    }

    /**
     * Achat d'une eSIM chez le grossiste avec contrôle d'idempotence et journal d'audit
     */
    fun orderWholesaleEsim(
        packageCode: String,
        costPrice: Double,
        sellingPrice: Double,
        idempotencyKey: String? = null
    ): Result<EsimWholesaleTransaction> {
        val txId = idempotencyKey ?: ("tx_esim_" + UUID.randomUUID().toString())

        // Contrôle d'idempotence
        val existing = _wholesaleTransactions.value.find { it.transactionId == txId }
        if (existing != null) {
            logAdminAudit("IDEMPOTENT_REPLAY_ESIM_ORDER: $txId")
            return Result.success(existing)
        }

        val iccid = "8901" + (1000000000000000L + (Math.random() * 8999999999999999L).toLong())
        val tx = EsimWholesaleTransaction(
            transactionId = txId,
            adminId = "adm_super_01",
            packageCode = packageCode,
            iccid = iccid,
            costPrice = costPrice,
            sellingPrice = sellingPrice,
            status = "SUCCESS",
            createdAt = System.currentTimeMillis()
        )

        _wholesaleTransactions.update { listOf(tx) + it }
        logAdminAudit("PURCHASE_ESIM_WHOLESALE: $packageCode (ICCID: ${iccid.take(8)}••••, Idempotency: $txId)")
        return Result.success(tx)
    }

    fun logAdminAudit(action: String, ipAddress: String = "127.0.0.1") {
        val entry = AdminAuditLog(
            logId = "log_" + UUID.randomUUID().toString().take(8),
            adminId = "adm_super_01",
            action = action,
            ipAddress = ipAddress,
            timestamp = System.currentTimeMillis()
        )
        _auditLogs.update { listOf(entry) + it }
    }
}

