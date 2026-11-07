package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SimCard
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.SimXRepository
import com.example.model.Strings
import com.example.ui.components.SimXDrawerContent
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.CompatibilityScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.MyEsimsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.VirtualNumberScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.SimXBlueDark
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimXApp(
    repository: SimXRepository = remember { SimXRepository() }
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var currentScreen by remember { mutableStateOf("explore") }

    val userProfile by repository.userProfile.collectAsState()
    val userEsims by repository.userEsims.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val language by repository.selectedLanguage.collectAsState()
    val hideBalance by repository.hideBalance.collectAsState()
    val currencyIsHtg by repository.currencyIsHtg.collectAsState()
    val oneFreeRemainingSeconds by repository.oneFreeRemainingSeconds.collectAsState()
    val pendingDeposits by repository.pendingDepositRequests.collectAsState()
    val userAccounts by repository.userAccounts.collectAsState()
    val adminConfig by repository.adminConfig.collectAsState()
    val broadcastNotifications by repository.broadcastNotifications.collectAsState()
    val activeHaitianPayment by repository.activeHaitianPayment.collectAsState()
    val activeBinanceOrder by repository.activeBinanceOrder.collectAsState()
    val wholesalePackages by repository.wholesalePackages.collectAsState()
    val wholesaleTransactions by repository.wholesaleTransactions.collectAsState()
    val auditLogs by repository.auditLogs.collectAsState()

    // Handle back button: close drawer first, or return to explore screen
    BackHandler(enabled = drawerState.isOpen || currentScreen != "explore") {
        if (drawerState.isOpen) {
            coroutineScope.launch { drawerState.close() }
        } else {
            currentScreen = "explore"
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            SimXDrawerContent(
                userProfile = userProfile,
                language = language,
                currentRoute = currentScreen,
                onNavigate = { route ->
                    currentScreen = route
                },
                onLanguageChange = { newLang ->
                    repository.setLanguage(newLang)
                },
                onCloseDrawer = {
                    coroutineScope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { currentScreen = "explore" }
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .border(1.5.dp, SimXCyanAccent.copy(alpha = 0.8f), CircleShape)
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
                                        text = "Sim",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = " X",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Black,
                                        color = SimXCyanAccent
                                    )
                                }
                                Text(
                                    text = "GLOBAL eSIM",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 1.2.sp,
                                    color = SimXCyanAccent.copy(alpha = 0.85f)
                                )
                            }
                        }
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
                                }
                            },
                            modifier = Modifier.testTag("menu_drawer_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Open Drawer Menu",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    },
                    actions = {
                        // Balance Chip with eye toggle
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            ),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { currentScreen = "wallet" }
                        ) {
                            Row(
                                modifier = Modifier.padding(start = 10.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val displayBal = if (hideBalance) "••••"
                                else if (currencyIsHtg) userProfile.formattedHtg()
                                else userProfile.formattedBalance()

                                Text(
                                    text = displayBal,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                IconButton(
                                    onClick = { repository.toggleHideBalance() },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(
                                        imageVector = if (hideBalance) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = "Hide/Show Balance",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    NavigationBarItem(
                        selected = currentScreen == "explore",
                        onClick = { currentScreen = "explore" },
                        icon = {
                            Icon(imageVector = Icons.Default.Public, contentDescription = Strings.get("explore", language))
                        },
                        label = {
                            Text(text = Strings.get("explore", language), fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SimXCyanAccent,
                            selectedTextColor = SimXCyanAccent,
                            indicatorColor = SimXBluePrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_explore")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "my_esims",
                        onClick = { currentScreen = "my_esims" },
                        icon = {
                            Icon(imageVector = Icons.Default.SimCard, contentDescription = Strings.get("my_esims", language))
                        },
                        label = {
                            Text(text = Strings.get("my_esims", language), fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SimXCyanAccent,
                            selectedTextColor = SimXCyanAccent,
                            indicatorColor = SimXBluePrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_my_esims")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "wallet",
                        onClick = { currentScreen = "wallet" },
                        icon = {
                            Icon(imageVector = Icons.Default.AccountBalanceWallet, contentDescription = Strings.get("wallet", language))
                        },
                        label = {
                            Text(text = Strings.get("wallet", language), fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SimXCyanAccent,
                            selectedTextColor = SimXCyanAccent,
                            indicatorColor = SimXBluePrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_wallet")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "rewards",
                        onClick = { currentScreen = "rewards" },
                        icon = {
                            Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = Strings.get("rewards", language))
                        },
                        label = {
                            Text(text = Strings.get("rewards", language), fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SimXCyanAccent,
                            selectedTextColor = SimXCyanAccent,
                            indicatorColor = SimXBluePrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_rewards")
                    )

                    NavigationBarItem(
                        selected = currentScreen == "profile",
                        onClick = { currentScreen = "profile" },
                        icon = {
                            Icon(imageVector = Icons.Default.Person, contentDescription = Strings.get("profile", language))
                        },
                        label = {
                            Text(text = Strings.get("profile", language), fontSize = 11.sp)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = SimXCyanAccent,
                            selectedTextColor = SimXCyanAccent,
                            indicatorColor = SimXBluePrimary.copy(alpha = 0.2f)
                        ),
                        modifier = Modifier.testTag("nav_profile")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Crossfade(targetState = currentScreen, label = "screen_transition") { screen ->
                    when (screen) {
                        "explore" -> ExploreScreen(
                            userProfile = userProfile,
                            language = language,
                            onPurchasePlan = { country, plan ->
                                repository.purchasePlan(country, plan)
                                currentScreen = "my_esims"
                            },
                            onNavigateToWallet = {
                                currentScreen = "wallet"
                            }
                        )

                        "my_esims" -> MyEsimsScreen(
                            userEsims = userEsims,
                            language = language,
                            onNavigateToExplore = {
                                currentScreen = "explore"
                            },
                            onNavigateToVirtualNumber = {
                                currentScreen = "virtual_number"
                            },
                            onSimulateData = { esimId ->
                                repository.simulateDataUsage(esimId, 150)
                            },
                            onTopUpEsim = { esimId, gb, costCents ->
                                repository.topUpEsim(esimId, gb, costCents)
                            }
                        )

                        "virtual_number" -> VirtualNumberScreen(
                            userEsims = userEsims,
                            language = language,
                            onNavigateToExploreVoice = {
                                currentScreen = "explore"
                            }
                        )

                        "wallet" -> WalletScreen(
                            userProfile = userProfile,
                            transactions = transactions,
                            hideBalance = hideBalance,
                            currencyIsHtg = currencyIsHtg,
                            language = language,
                            blockedGateways = adminConfig.blockedGateways,
                            activeHaitianPayment = activeHaitianPayment,
                            activeBinanceOrder = activeBinanceOrder,
                            onToggleHideBalance = { repository.toggleHideBalance() },
                            onToggleCurrency = { repository.toggleCurrency() },
                            onDeposit = { gateway, amount, notes ->
                                repository.depositCredits(gateway, amount, notes)
                            },
                            onInitiateHaitianPayment = { gateway, amt, phone ->
                                repository.initiateHaitianPayment(gateway, amt, phone)
                            },
                            onCompleteHaitianPayment = { orderId ->
                                repository.completeHaitianPayment(orderId)
                            },
                            onDismissHaitianPayment = {
                                repository.dismissActiveHaitianPayment()
                            },
                            onInitiateBinancePayment = { amount ->
                                repository.initiateBinancePayment(amount)
                            },
                            onCompleteBinancePayment = { tradeNo ->
                                repository.completeBinancePayment(tradeNo)
                            },
                            onDismissBinancePayment = {
                                repository.dismissActiveBinancePayment()
                            }
                        )

                        "rewards" -> RewardsScreen(
                            userProfile = userProfile,
                            oneFreeRemainingSeconds = oneFreeRemainingSeconds,
                            language = language,
                            onStartOneFree = { repository.startOneFreeSession() },
                            onRedeemPoints = { repository.redeemPoints() },
                            onApplyPromoCode = { code -> repository.applyPromoCode(code) }
                        )

                        "profile" -> ProfileScreen(
                            userProfile = userProfile,
                            language = language,
                            onEraseData = { repository.eraseUserData() }
                        )

                        "compatibility" -> CompatibilityScreen(
                            language = language
                        )

                        "support" -> ProfileScreen(
                            userProfile = userProfile,
                            language = language,
                            onEraseData = { repository.eraseUserData() }
                        )

                        "admin" -> AdminPanelScreen(
                            userProfile = userProfile,
                            allTransactions = transactions,
                            allEsims = userEsims,
                            pendingDeposits = pendingDeposits,
                            userAccounts = userAccounts,
                            adminConfig = adminConfig,
                            sentNotifications = broadcastNotifications,
                            language = language,
                            wholesalePackages = wholesalePackages,
                            wholesaleTransactions = wholesaleTransactions,
                            auditLogs = auditLogs,
                            onApproveDeposit = { id -> repository.approveDepositRequest(id) },
                            onRejectDeposit = { id -> repository.rejectDepositRequest(id) },
                            onToggleUserSuspension = { uid -> repository.toggleUserSuspension(uid) },
                            onAdjustUserBalance = { uid, delta -> repository.adjustUserBalance(uid, delta) },
                            onToggleGatewayBlocked = { gw -> repository.toggleGatewayBlocked(gw) },
                            onUpdateAdminConfig = { cfg -> repository.updateAdminConfig(cfg) },
                            onSendNotification = { title, msg, target -> repository.sendBroadcastNotification(title, msg, target) },
                            onOrderWholesaleEsim = { code, cost, selling ->
                                repository.orderWholesaleEsim(code, cost, selling)
                            }
                        )

                        else -> ExploreScreen(
                            userProfile = userProfile,
                            language = language,
                            onPurchasePlan = { country, plan ->
                                repository.purchasePlan(country, plan)
                                currentScreen = "my_esims"
                            },
                            onNavigateToWallet = {
                                currentScreen = "wallet"
                            }
                        )
                    }
                }
            }
        }
    }
}
