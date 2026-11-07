package com.example

import com.example.data.SimXCatalog
import com.example.data.SimXRepository
import com.example.model.PaymentGateway
import com.example.model.maskSensitive
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SimXRepositoryTest {

    private lateinit var repository: SimXRepository

    @Before
    fun setUp() {
        repository = SimXRepository()
    }

    @Test
    fun testWholesaleMargin15PercentCalculation() {
        // Wholesale 24000 (1/10000th USD = $2.40) -> 24000 * 11500 + 999999 / 1000000 = 276 cents ($2.76)
        val retailHaiti1Gb = SimXCatalog.calculateRetailCents(24000)
        assertEquals(276, retailHaiti1Gb)

        // Wholesale 58000 -> 58000 * 11500 + 999999 / 1000000 = 667 cents ($6.67)
        val retailHaiti3Gb = SimXCatalog.calculateRetailCents(58000)
        assertEquals(667, retailHaiti3Gb)
    }

    @Test
    fun testPurchasePlanDeductsBalanceAndCreditsLoyaltyPoints() {
        val initialUser = repository.userProfile.value
        val initialBalance = initialUser.balanceCents
        val initialPoints = initialUser.loyaltyPoints

        val country = SimXCatalog.countries.first { it.code == "HT" }
        val plans = SimXCatalog.getPlansForCountry(country.code)
        val plan = plans.first()

        val result = repository.purchasePlan(country, plan)
        assertTrue(result.isSuccess)

        val updatedUser = repository.userProfile.value
        assertEquals(initialBalance - plan.retailPriceCents, updatedUser.balanceCents)
        val expectedEarnedPoints = (plan.retailPriceCents / 100) * 5
        assertEquals(initialPoints + expectedEarnedPoints, updatedUser.loyaltyPoints)
    }

    @Test
    fun testPurchasePlanFailsOnInsufficientBalance() {
        val country = SimXCatalog.countries.first { it.code == "HT" }
        val expensivePlan = SimXCatalog.getPlansForCountry(country.code).last() // unlimited or expensive plan

        // Drain balance by attempting more than available
        val drainRepo = SimXRepository()
        drainRepo.eraseUserData() // balance becomes 0

        val result = drainRepo.purchasePlan(country, expensivePlan)
        assertFalse(result.isSuccess)
    }

    @Test
    fun testDepositIncreasesBalance() {
        val initialBalance = repository.userProfile.value.balanceCents
        val tx = repository.depositCredits(PaymentGateway.MONCASH, 25, "+509 3800 1234")

        assertEquals(initialBalance + 2500, repository.userProfile.value.balanceCents)
        assertEquals(2500, tx.amountCents)
        assertTrue(tx.referenceId.startsWith("MC-"))
    }

    @Test
    fun testGdprUserErasureAnonymizesPersonalIdentifiers() {
        repository.eraseUserData()
        val user = repository.userProfile.value

        assertTrue(user.isAnonymous)
        assertTrue(user.uid.startsWith("anon_"))
        assertTrue(user.email.contains("privacy.simx.net"))
        assertEquals("Utilisateur Anonyme", user.fullName)
        assertEquals(0, repository.userEsims.value.size)
    }

    @Test
    fun testTopUpEsimIncreasesDataAndDeductsBalance() {
        val initialEsim = repository.userEsims.value.first()
        val initialTotalMb = initialEsim.totalDataMb
        val initialBalance = repository.userProfile.value.balanceCents

        val res = repository.topUpEsim(initialEsim.id, 3, 667)
        assertTrue(res.isSuccess)

        val updatedEsim = repository.userEsims.value.first { it.id == initialEsim.id }
        assertEquals(initialTotalMb + (3 * 1024), updatedEsim.totalDataMb)
        assertEquals(initialBalance - 667, repository.userProfile.value.balanceCents)
    }

    @Test
    fun testSimulateDataUsageIncrementsUsedMb() {
        val initialEsim = repository.userEsims.value.first()
        val initialUsed = initialEsim.dataUsedMb

        repository.simulateDataUsage(initialEsim.id, 200)
        val updated = repository.userEsims.value.first { it.id == initialEsim.id }
        assertEquals(initialUsed + 200, updated.dataUsedMb)
    }

    @Test
    fun testAdminApproveDepositRequest() {
        val pendingReq = repository.pendingDepositRequests.value.first()
        val initialStatus = pendingReq.status
        assertEquals("PENDING", initialStatus)

        repository.approveDepositRequest(pendingReq.id)
        val updatedReq = repository.pendingDepositRequests.value.first { it.id == pendingReq.id }
        assertEquals("APPROVED", updatedReq.status)
    }

    @Test
    fun testAdminRejectDepositRequest() {
        val pendingReq = repository.pendingDepositRequests.value.last()
        repository.rejectDepositRequest(pendingReq.id)
        val updatedReq = repository.pendingDepositRequests.value.first { it.id == pendingReq.id }
        assertEquals("REJECTED", updatedReq.status)
    }

    @Test
    fun testAdminToggleUserSuspension() {
        val user = repository.userAccounts.value.first()
        val originalSuspension = user.isSuspended

        repository.toggleUserSuspension(user.uid)
        val updated = repository.userAccounts.value.first { it.uid == user.uid }
        assertEquals(!originalSuspension, updated.isSuspended)
    }

    @Test
    fun testAdminBroadcastNotification() {
        val initialCount = repository.broadcastNotifications.value.size
        repository.sendBroadcastNotification("Alerte Sim X", "Promo Caraïbes 2026", "Tous")
        assertEquals(initialCount + 1, repository.broadcastNotifications.value.size)
        assertEquals("Alerte Sim X", repository.broadcastNotifications.value.first().title)
    }

    @Test
    fun testAdminToggleGatewayBlockedAndDepositRestriction() {
        assertFalse(repository.adminConfig.value.isGatewayBlocked(PaymentGateway.MONCASH))

        // Block MonCash
        repository.toggleGatewayBlocked(PaymentGateway.MONCASH)
        assertTrue(repository.adminConfig.value.isGatewayBlocked(PaymentGateway.MONCASH))
        assertTrue(repository.adminConfig.value.blockedGateways.contains(PaymentGateway.MONCASH))

        // Attempting deposit with blocked gateway must throw IllegalStateException
        var exceptionThrown = false
        try {
            repository.depositCredits(PaymentGateway.MONCASH, 20, "Essai recharge bloquée")
        } catch (e: IllegalStateException) {
            exceptionThrown = true
            assertTrue(e.message?.contains("bloquée") == true)
        }
        assertTrue("Exception must be thrown when depositing via blocked gateway", exceptionThrown)

        // Other gateways should remain operational
        assertFalse(repository.adminConfig.value.isGatewayBlocked(PaymentGateway.STRIPE))
        val initialBalance = repository.userProfile.value.balanceCents
        val tx = repository.depositCredits(PaymentGateway.STRIPE, 15, "Recharge Stripe valide")
        assertEquals(initialBalance + 1500, repository.userProfile.value.balanceCents)
        assertEquals(1500, tx.amountCents)

        // Unblock MonCash
        repository.toggleGatewayBlocked(PaymentGateway.MONCASH)
        assertFalse(repository.adminConfig.value.isGatewayBlocked(PaymentGateway.MONCASH))

        // Deposit via MonCash now succeeds
        val moncashTx = repository.depositCredits(PaymentGateway.MONCASH, 10, "+509 3700 9988")
        assertEquals(1000, moncashTx.amountCents)
    }

    @Test
    fun testHaitianPaymentInitiationAndProxyMasking() {
        // Test MonCash initiation
        val moncashResult = repository.initiateHaitianPayment(
            gateway = PaymentGateway.MONCASH,
            amountUsd = 10,
            customerPhone = "+509 3844 5566"
        )
        assertTrue(moncashResult.isSuccess)
        val moncashOrder = moncashResult.getOrThrow()
        assertTrue(moncashOrder.orderId.startsWith("sx_ord_mc_"))
        assertEquals(1320L, moncashOrder.amountHtg)
        assertTrue(moncashOrder.paymentToken.startsWith("mc_tok_"))
        // Check that masked merchant string is returned and does not expose raw merchant number
        assertTrue(moncashOrder.maskedMerchantDisplay.contains("••••-7695"))
        assertFalse(moncashOrder.maskedMerchantDisplay.contains("+509 4687 7695"))

        // Test Natcash initiation
        val natcashResult = repository.initiateHaitianPayment(
            gateway = PaymentGateway.NATCASH,
            amountUsd = 20,
            customerPhone = "+509 3211 4455"
        )
        assertTrue(natcashResult.isSuccess)
        val natcashOrder = natcashResult.getOrThrow()
        assertTrue(natcashOrder.orderId.startsWith("sx_ord_nc_"))
        assertEquals(2640L, natcashOrder.amountHtg)
        assertTrue(natcashOrder.paymentToken.startsWith("nc_tok_"))
        assertTrue(natcashOrder.maskedMerchantDisplay.contains("••••-3281"))
        assertFalse(natcashOrder.maskedMerchantDisplay.contains("+509 3215 3281"))
    }

    @Test
    fun testHaitianPaymentCompletionCreditsWallet() {
        val initialBalance = repository.userProfile.value.balanceCents
        val orderResult = repository.initiateHaitianPayment(
            gateway = PaymentGateway.MONCASH,
            amountUsd = 25,
            customerPhone = "+509 3800 9999"
        )
        val order = orderResult.getOrThrow()

        val completionResult = repository.completeHaitianPayment(order.orderId)
        assertTrue(completionResult.isSuccess)
        val tx = completionResult.getOrThrow()

        assertEquals(initialBalance + 2500, repository.userProfile.value.balanceCents)
        assertEquals(2500, tx.amountCents)
        assertTrue(tx.referenceId.startsWith("MC-SX-"))
        assertEquals(com.example.model.PaymentProcessingStatus.SUCCESS, repository.activeHaitianPayment.value?.status)
    }

    @Test
    fun testMaskSensitiveExtension() {
        val phone1 = "+509 4687 7695"
        val masked1 = phone1.maskSensitive(4)
        assertEquals("+509 •••• 7695", masked1)

        val phone2 = "+509 3215 3281"
        val masked2 = phone2.maskSensitive(4)
        assertEquals("+509 •••• 3281", masked2)
    }

    @Test
    fun testBinancePayInitiationWithPrefixApp3() {
        val result = repository.initiateBinancePayment(50)
        assertTrue(result.isSuccess)

        val order = result.getOrThrow()
        assertEquals("APP3", order.appPrefix)
        assertTrue("merchantTradeNo must start with APP3_", order.merchantTradeNo.startsWith("APP3_"))
        assertTrue("prepayId must start with APP3_", order.prepayId.startsWith("APP3_"))
        assertEquals("USDT", order.currency)
        assertEquals(50, order.amountUsd)
        assertTrue(order.qrContent.contains(order.merchantTradeNo))
        assertTrue(order.deeplink.contains("merchantTradeNo=${order.merchantTradeNo}"))
        assertEquals(com.example.model.PaymentProcessingStatus.PENDING_CUSTOMER_APPROVAL, order.status)
    }

    @Test
    fun testBinancePayCompletionCreditsWallet() {
        val initialBalance = repository.userProfile.value.balanceCents
        val initResult = repository.initiateBinancePayment(30)
        val order = initResult.getOrThrow()

        val completionResult = repository.completeBinancePayment(order.merchantTradeNo)
        assertTrue(completionResult.isSuccess)

        val tx = completionResult.getOrThrow()
        assertEquals(initialBalance + 3000, repository.userProfile.value.balanceCents)
        assertEquals(3000, tx.amountCents)
        assertTrue(tx.referenceId.startsWith("APP3_"))
        assertEquals(com.example.model.PaymentProcessingStatus.SUCCESS, repository.activeBinanceOrder.value?.status)
    }

    @Test
    fun testBinancePayBlockedByAdminPreventsInitiation() {
        assertFalse(repository.adminConfig.value.isGatewayBlocked(PaymentGateway.CRYPTO_BINANCE))
        repository.toggleGatewayBlocked(PaymentGateway.CRYPTO_BINANCE)
        assertTrue(repository.adminConfig.value.isGatewayBlocked(PaymentGateway.CRYPTO_BINANCE))

        val result = repository.initiateBinancePayment(25)
        assertFalse(result.isSuccess)
    }

    @Test
    fun testWholesalePackageRetrievalAndOrdering() {
        val packages = repository.wholesalePackages.value
        assertTrue("Catalogue grossiste non vide", packages.isNotEmpty())
        val pkg = packages.first { it.packageCode == "ESIM_HT_1GB_7D" }
        assertEquals(2.00, pkg.costPrice, 0.001)
        assertEquals(2.76, pkg.sellingPrice, 0.001)

        val initialTxCount = repository.wholesaleTransactions.value.size
        val result = repository.orderWholesaleEsim(pkg.packageCode, pkg.costPrice, pkg.sellingPrice)
        assertTrue(result.isSuccess)

        val tx = result.getOrThrow()
        assertEquals("SUCCESS", tx.status)
        assertEquals("ESIM_HT_1GB_7D", tx.packageCode)
        assertEquals(2.00, tx.costPrice, 0.001)
        assertEquals(2.76, tx.sellingPrice, 0.001)
        assertTrue("ICCID doit commencer par 8901", tx.iccid.startsWith("8901"))
        assertEquals(initialTxCount + 1, repository.wholesaleTransactions.value.size)
    }

    @Test
    fun testWholesaleIdempotencyPreventsDuplicateOrders() {
        val testIdempotencyKey = "idemp_order_unique_4492"
        val firstResult = repository.orderWholesaleEsim(
            packageCode = "ESIM_CARIB_10GB_30D",
            costPrice = 16.00,
            sellingPrice = 21.85,
            idempotencyKey = testIdempotencyKey
        )
        assertTrue(firstResult.isSuccess)
        val firstTx = firstResult.getOrThrow()
        assertEquals(testIdempotencyKey, firstTx.transactionId)

        val countAfterFirst = repository.wholesaleTransactions.value.size

        // Replay with identical idempotency key
        val replayResult = repository.orderWholesaleEsim(
            packageCode = "ESIM_CARIB_10GB_30D",
            costPrice = 16.00,
            sellingPrice = 21.85,
            idempotencyKey = testIdempotencyKey
        )
        assertTrue(replayResult.isSuccess)
        val replayedTx = replayResult.getOrThrow()

        // Must return identical transaction record and ICCID, and NO duplicate record inserted
        assertEquals(firstTx.transactionId, replayedTx.transactionId)
        assertEquals(firstTx.iccid, replayedTx.iccid)
        assertEquals(countAfterFirst, repository.wholesaleTransactions.value.size)
    }

    @Test
    fun testAuditLogRecording() {
        val initialLogCount = repository.auditLogs.value.size
        assertTrue("Des logs initiaux existent", initialLogCount > 0)

        repository.logAdminAudit("TEST_AUDIT_ACTION_SECURITY_CHECK", "192.168.1.50")
        val updatedLogs = repository.auditLogs.value
        assertEquals(initialLogCount + 1, updatedLogs.size)

        val latestLog = updatedLogs.first()
        assertEquals("TEST_AUDIT_ACTION_SECURITY_CHECK", latestLog.action)
        assertEquals("192.168.1.50", latestLog.ipAddress)
        assertEquals("adm_super_01", latestLog.adminId)
    }

    @Test
    fun testVoiceAndSmsPlansAvailableInCatalogAndWhiteLabeled() {
        val haitiPlans = SimXCatalog.getPlansForCountry("HT")
        val voicePlans = haitiPlans.filter { it.supportsVoiceAndSms }
        assertTrue("Haiti doit avoir des forfaits Voix & SMS", voicePlans.isNotEmpty())

        val plan = voicePlans.first()
        assertTrue("Doit avoir des minutes d'appels", (plan.voiceMinutes ?: 0) > 0)
        assertTrue("Doit avoir des SMS", (plan.smsCount ?: 0) > 0)
        assertEquals("+509", plan.virtualPhoneNumberPrefix)

        // Strict White-Label assertion: No mention of wholesale provider in plans or countries
        SimXCatalog.countries.forEach { c ->
            assertFalse(c.nameFr.contains("eSIM Access", ignoreCase = true))
            assertFalse(c.nameEn.contains("eSIM Access", ignoreCase = true))
            val countryPlans = SimXCatalog.getPlansForCountry(c.code)
            countryPlans.forEach { p ->
                assertFalse(p.id.contains("esimaccess", ignoreCase = true))
                assertFalse(p.networkName.contains("esimaccess", ignoreCase = true))
            }
        }
    }

    @Test
    fun testPurchaseVoicePlanAllocatesVirtualPhoneNumberAndMinutes() {
        val country = SimXCatalog.countries.first { it.code == "HT" }
        val voicePlan = SimXCatalog.getPlansForCountry(country.code).first { it.supportsVoiceAndSms }

        // Top up test account balance to afford the 5GB Voice & SMS plan
        repository.depositCredits(PaymentGateway.MONCASH, 25, "Test Voix & SMS balance")

        val initialEsimsCount = repository.userEsims.value.size
        val result = repository.purchasePlan(country, voicePlan)
        assertTrue("L'achat du forfait Voix & SMS doit réussir", result.isSuccess)

        val newEsim = repository.userEsims.value.first()
        assertEquals(initialEsimsCount + 1, repository.userEsims.value.size)
        assertTrue("Doit supporter Voix & SMS", newEsim.supportsVoiceAndSms)
        assertTrue("hasVirtualNumber doit être true", newEsim.hasVirtualNumber)
        assertNotNull("Numéro virtuel non nul", newEsim.virtualPhoneNumber)
        assertTrue("Doit commencer par le préfixe +509", newEsim.virtualPhoneNumber!!.startsWith("+509"))
        assertEquals(voicePlan.voiceMinutes, newEsim.voiceMinutesRemaining)
        assertEquals(voicePlan.voiceMinutes, newEsim.voiceMinutesTotal)
        assertEquals(voicePlan.smsCount, newEsim.smsRemaining)
        assertEquals(voicePlan.smsCount, newEsim.smsTotal)
    }
}
