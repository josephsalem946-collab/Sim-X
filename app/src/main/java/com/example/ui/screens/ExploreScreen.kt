package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.SimXSlateNavyVariant
import com.example.ui.theme.SimXNeonGreen
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SimXCatalog
import com.example.model.AppLanguage
import com.example.model.Country
import com.example.model.EsimPlan
import com.example.model.Region
import com.example.model.Strings
import com.example.model.UserProfile
import com.example.ui.components.SimCardPlanItem
import com.example.ui.theme.SimXBluePrimary
import com.example.ui.theme.SimXCyanAccent
import com.example.ui.theme.SimXGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen(
    userProfile: UserProfile,
    language: AppLanguage,
    onPurchasePlan: (Country, EsimPlan) -> Unit,
    onNavigateToWallet: () -> Unit
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedRegion by remember { mutableStateOf(Region.ALL) }
    var selectedPlanType by remember { mutableStateOf("ALL") } // "ALL", "VOICE", "DATA"
    var selectedCountryForModal by remember { mutableStateOf<Country?>(null) }

    val filteredCountries = remember(searchQuery, selectedRegion, selectedPlanType, language) {
        SimXCatalog.countries.filter { country ->
            val matchesRegion = selectedRegion == Region.ALL || country.region == selectedRegion
            val matchesSearch = if (searchQuery.isBlank()) true else {
                country.code.contains(searchQuery, ignoreCase = true) ||
                        country.localizedName(language).contains(searchQuery, ignoreCase = true) ||
                        country.nameFr.contains(searchQuery, ignoreCase = true) ||
                        country.nameEn.contains(searchQuery, ignoreCase = true)
            }
            val matchesPlanType = when (selectedPlanType) {
                "VOICE" -> SimXCatalog.getPlansForCountry(country.code).any { it.supportsVoiceAndSms }
                "DATA" -> SimXCatalog.getPlansForCountry(country.code).any { !it.supportsVoiceAndSms }
                else -> true
            }
            matchesRegion && matchesSearch && matchesPlanType
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("explore_screen")
    ) {
        // Tagline / Subtitle
        Text(
            text = Strings.get("tagline", language),
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input"),
            placeholder = {
                Text(
                    text = Strings.get("search_country", language),
                    fontSize = 14.sp
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Region Filter Chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(Region.entries) { region ->
                val isSelected = region == selectedRegion
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedRegion = region },
                    label = {
                        Text(
                            text = Strings.get(region.labelKey, language),
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SimXBluePrimary,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Plan Type Filter Chips: All, Voice & SMS, Data only
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = selectedPlanType == "ALL",
                onClick = { selectedPlanType = "ALL" },
                label = {
                    Text(
                        text = Strings.get("all_plans", language) + " 🌐",
                        fontSize = 11.sp,
                        fontWeight = if (selectedPlanType == "ALL") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SimXBluePrimary,
                    selectedLabelColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_all_plans")
            )

            FilterChip(
                selected = selectedPlanType == "VOICE",
                onClick = { selectedPlanType = "VOICE" },
                label = {
                    Text(
                        text = Strings.get("voice_and_sms", language) + " 📞",
                        fontSize = 11.sp,
                        fontWeight = if (selectedPlanType == "VOICE") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SimXNeonGreen,
                    selectedLabelColor = Color(0xFF031633)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_voice_plans")
            )

            FilterChip(
                selected = selectedPlanType == "DATA",
                onClick = { selectedPlanType = "DATA" },
                label = {
                    Text(
                        text = Strings.get("data_only", language) + " ⚡",
                        fontSize = 11.sp,
                        fontWeight = if (selectedPlanType == "DATA") FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = SimXCyanAccent,
                    selectedLabelColor = Color(0xFF031633)
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("filter_data_plans")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Country List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(10.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SimXSlateNavyVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.horizontalGradient(listOf(SimXCyanAccent.copy(alpha = 0.6f), SimXNeonGreen.copy(alpha = 0.4f)))
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(58.dp)
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

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Sim X Global eSIM",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SimXNeonGreen.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "LIVE",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = SimXNeonGreen,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                            Text(
                                text = "Connexion internet instantanée dans 200+ pays sans itinérance.",
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = "⚡ Activation QR",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SimXCyanAccent
                                )
                                Text(
                                    text = "🇭🇹 MonCash & Natcash",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = SimXNeonGreen
                                )
                            }
                        }
                    }
                }
            }

            items(filteredCountries, key = { it.code }) { country ->
                CountryRowItem(
                    country = country,
                    language = language,
                    onClick = { selectedCountryForModal = country }
                )
            }
            item {
                Spacer(modifier = Modifier.height(80.dp)) // padding for bottom bar
            }
        }
    }

    // Modal Sheet for Plan Selection
    selectedCountryForModal?.let { country ->
        val allPlans = remember(country.code) { SimXCatalog.getPlansForCountry(country.code) }
        var modalPlanType by remember(country.code, selectedPlanType) { mutableStateOf(selectedPlanType) }
        val plans = remember(allPlans, modalPlanType) {
            when (modalPlanType) {
                "VOICE" -> allPlans.filter { it.supportsVoiceAndSms }
                "DATA" -> allPlans.filter { !it.supportsVoiceAndSms }
                else -> allPlans
            }
        }
        var selectedPlanId by remember(plans) { mutableStateOf(plans.firstOrNull()?.id ?: "") }
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = { selectedCountryForModal = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .testTag("plan_modal")
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = country.flag, fontSize = 32.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = country.localizedName(language),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.SignalCellularAlt,
                                    contentDescription = null,
                                    tint = SimXCyanAccent,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Réseau Sim X · Sans itinérance",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    IconButton(onClick = { selectedCountryForModal = null }) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = Strings.get("pick_plan", language),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Modal plan type selector chips
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
                ) {
                    FilterChip(
                        selected = modalPlanType == "ALL",
                        onClick = { modalPlanType = "ALL" },
                        label = { Text("Tous", fontSize = 11.sp, fontWeight = if (modalPlanType == "ALL") FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SimXBluePrimary,
                            selectedLabelColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = modalPlanType == "VOICE",
                        onClick = { modalPlanType = "VOICE" },
                        label = { Text("Voix & SMS 📞", fontSize = 11.sp, fontWeight = if (modalPlanType == "VOICE") FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SimXNeonGreen,
                            selectedLabelColor = Color(0xFF031633)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    FilterChip(
                        selected = modalPlanType == "DATA",
                        onClick = { modalPlanType = "DATA" },
                        label = { Text("Data ⚡", fontSize = 11.sp, fontWeight = if (modalPlanType == "DATA") FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SimXCyanAccent,
                            selectedLabelColor = Color(0xFF031633)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Plans List
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    items(plans) { plan ->
                        SimCardPlanItem(
                            plan = plan,
                            isSelected = plan.id == selectedPlanId,
                            language = language,
                            onSelect = { selectedPlanId = plan.id }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                val currentSelectedPlan = plans.find { it.id == selectedPlanId } ?: plans.firstOrNull() ?: allPlans.first()
                val canAfford = userProfile.balanceCents >= currentSelectedPlan.retailPriceCents

                if (!canAfford) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x22F59E0B))
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = Strings.get("short_balance", language),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = SimXBluePrimary,
                                modifier = Modifier.clickable {
                                    selectedCountryForModal = null
                                    onNavigateToWallet()
                                }
                            ) {
                                Text(
                                    text = "+ Recharger",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Buy Plan Action Button
                Button(
                    onClick = {
                        if (canAfford) {
                            onPurchasePlan(country, currentSelectedPlan)
                            selectedCountryForModal = null
                            Toast.makeText(
                                context,
                                Strings.get("plan_bought", language),
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            selectedCountryForModal = null
                            onNavigateToWallet()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("buy_plan_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (canAfford) SimXBluePrimary else Color(0xFFD97706)
                    )
                ) {
                    Text(
                        text = if (canAfford) Strings.get("buy_plan", language, currentSelectedPlan.formattedPrice())
                        else "Recharger pour acheter (${currentSelectedPlan.formattedPrice()})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun CountryRowItem(
    country: Country,
    language: AppLanguage,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag("country_row_${country.code}"),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = country.flag, fontSize = 30.sp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = country.localizedName(language),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (country.isPopular) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0x22F6D77A)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Popular",
                                    tint = SimXGoldLight,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Réseau Sim X 4G/5G · ${Strings.get("from_price", language, "$" + String.format("%.2f", country.startingPriceCents / 100.0))}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$" + String.format("%.2f", country.startingPriceCents / 100.0),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                )
            }
        }
    }
}
