package com.example.data

import com.example.model.Country
import com.example.model.EsimPlan
import com.example.model.Region

object SimXCatalog {

    /**
     * Marges et tarification serveur Sim X:
     * Prix de gros (en 1/10 000 USD) + 15% de marge brute Sim X, arrondi au centime supérieur.
     */
    fun calculateRetailCents(wholesaleUnits: Int): Int {
        return ((wholesaleUnits.toLong() * 11500L + 999999L) / 1000000L).toInt()
    }

    val countries: List<Country> = listOf(
        Country(
            code = "HT",
            nameFr = "Haïti",
            nameHt = "Ayiti",
            nameEn = "Haiti",
            flag = "🇭🇹",
            region = Region.CARIBBEAN,
            isPopular = true,
            startingPriceCents = calculateRetailCents(24000)
        ),
        Country(
            code = "US",
            nameFr = "États-Unis (USA)",
            nameHt = "Etazini (USA)",
            nameEn = "United States",
            flag = "🇺🇸",
            region = Region.NORTH_AMERICA,
            isPopular = true,
            startingPriceCents = calculateRetailCents(30000)
        ),
        Country(
            code = "DO",
            nameFr = "République Dominicaine",
            nameHt = "Repiblik Dominikèn",
            nameEn = "Dominican Republic",
            flag = "🇩🇴",
            region = Region.CARIBBEAN,
            isPopular = true,
            startingPriceCents = calculateRetailCents(26000)
        ),
        Country(
            code = "CA",
            nameFr = "Canada",
            nameHt = "Kanada",
            nameEn = "Canada",
            flag = "🇨🇦",
            region = Region.NORTH_AMERICA,
            isPopular = true,
            startingPriceCents = calculateRetailCents(28000)
        ),
        Country(
            code = "FR",
            nameFr = "France",
            nameHt = "Frans",
            nameEn = "France",
            flag = "🇫🇷",
            region = Region.EUROPE,
            isPopular = true,
            startingPriceCents = calculateRetailCents(27000)
        ),
        Country(
            code = "ES",
            nameFr = "Espagne",
            nameHt = "Espay",
            nameEn = "Spain",
            flag = "🇪🇸",
            region = Region.EUROPE,
            isPopular = false,
            startingPriceCents = calculateRetailCents(26000)
        ),
        Country(
            code = "MX",
            nameFr = "Mexique",
            nameHt = "Meksik",
            nameEn = "Mexico",
            flag = "🇲🇽",
            region = Region.LATIN_AMERICA,
            isPopular = true,
            startingPriceCents = calculateRetailCents(29000)
        ),
        Country(
            code = "BR",
            nameFr = "Brésil",
            nameHt = "Brezil",
            nameEn = "Brazil",
            flag = "🇧🇷",
            region = Region.LATIN_AMERICA,
            isPopular = false,
            startingPriceCents = calculateRetailCents(31000)
        ),
        Country(
            code = "JM",
            nameFr = "Jamaïque",
            nameHt = "Jamayik",
            nameEn = "Jamaica",
            flag = "🇯🇲",
            region = Region.CARIBBEAN,
            isPopular = false,
            startingPriceCents = calculateRetailCents(34000)
        ),
        Country(
            code = "BS",
            nameFr = "Bahamas",
            nameHt = "Bahamas",
            nameEn = "Bahamas",
            flag = "🇧🇸",
            region = Region.CARIBBEAN,
            isPopular = false,
            startingPriceCents = calculateRetailCents(33000)
        ),
        Country(
            code = "CO",
            nameFr = "Colombie",
            nameHt = "Kolonbi",
            nameEn = "Colombia",
            flag = "🇨🇴",
            region = Region.LATIN_AMERICA,
            isPopular = false,
            startingPriceCents = calculateRetailCents(28000)
        ),
        Country(
            code = "PA",
            nameFr = "Panama",
            nameHt = "Panama",
            nameEn = "Panama",
            flag = "🇵🇦",
            region = Region.LATIN_AMERICA,
            isPopular = false,
            startingPriceCents = calculateRetailCents(30000)
        ),
        Country(
            code = "GB",
            nameFr = "Royaume-Uni",
            nameHt = "Wayòm Ini",
            nameEn = "United Kingdom",
            flag = "🇬🇧",
            region = Region.EUROPE,
            isPopular = true,
            startingPriceCents = calculateRetailCents(27000)
        ),
        Country(
            code = "DE",
            nameFr = "Allemagne",
            nameHt = "Almay",
            nameEn = "Germany",
            flag = "🇩🇪",
            region = Region.EUROPE,
            isPopular = false,
            startingPriceCents = calculateRetailCents(26000)
        ),
        Country(
            code = "TR",
            nameFr = "Turquie",
            nameHt = "Tiki",
            nameEn = "Turkey",
            flag = "🇹🇷",
            region = Region.WORLD,
            isPopular = true,
            startingPriceCents = calculateRetailCents(25000)
        ),
        Country(
            code = "AE",
            nameFr = "Émirats Arabes Unis (Dubaï)",
            nameHt = "Emira Arab Ini (Doubayi)",
            nameEn = "United Arab Emirates",
            flag = "🇦🇪",
            region = Region.WORLD,
            isPopular = true,
            startingPriceCents = calculateRetailCents(35000)
        ),
        Country(
            code = "JP",
            nameFr = "Japon",
            nameHt = "Japon",
            nameEn = "Japan",
            flag = "🇯🇵",
            region = Region.WORLD,
            isPopular = false,
            startingPriceCents = calculateRetailCents(32000)
        ),
        Country(
            code = "MA",
            nameFr = "Maroc",
            nameHt = "Mawòk",
            nameEn = "Morocco",
            flag = "🇲🇦",
            region = Region.WORLD,
            isPopular = false,
            startingPriceCents = calculateRetailCents(33000)
        )
    )

    private val wholesaleTiers = mapOf(
        "HT" to listOf(24000, 58000, 86000, 140000, 220000),
        "US" to listOf(30000, 65000, 98000, 160000, 250000),
        "DO" to listOf(26000, 62000, 90000, 150000, 240000),
        "CA" to listOf(28000, 60000, 92000, 155000, 245000),
        "FR" to listOf(27000, 64000, 95000, 150000, 230000),
        "ES" to listOf(26000, 61000, 91000, 145000, 225000),
        "MX" to listOf(29000, 66000, 99000, 165000, 260000),
        "BR" to listOf(31000, 70000, 104000, 175000, 270000),
        "JM" to listOf(34000, 76000, 112000, 185000, 290000),
        "BS" to listOf(33000, 74000, 110000, 180000, 280000),
        "CO" to listOf(28000, 63000, 94000, 152000, 240000),
        "PA" to listOf(30000, 67000, 100000, 162000, 255000),
        "GB" to listOf(27000, 63000, 93000, 148000, 232000),
        "DE" to listOf(26000, 61000, 90000, 145000, 228000),
        "TR" to listOf(25000, 59000, 89000, 142000, 220000),
        "AE" to listOf(35000, 78000, 118000, 195000, 310000),
        "JP" to listOf(32000, 71000, 107000, 172000, 268000),
        "MA" to listOf(33000, 73000, 109000, 178000, 279000)
    )

    private val specs = listOf(
        Pair(1, 7),
        Pair(3, 30),
        Pair(5, 30),
        Pair(10, 30),
        Pair(20, 30)
    )

    fun getCountryPrefix(countryCode: String): String = when (countryCode.uppercase()) {
        "HT" -> "+509"
        "US" -> "+1"
        "CA" -> "+1"
        "FR" -> "+33"
        "GB" -> "+44"
        "DO" -> "+1"
        "ES" -> "+34"
        "MX" -> "+52"
        "DE" -> "+49"
        "BR" -> "+55"
        "CO" -> "+57"
        "TR" -> "+90"
        "AE" -> "+971"
        "JP" -> "+81"
        "MA" -> "+212"
        else -> "+1"
    }

    fun generateSampleVirtualNumber(countryCode: String): String {
        val rand = (1000 + (Math.random() * 8999).toInt())
        return when (countryCode.uppercase()) {
            "HT" -> "+509 3712 $rand"
            "US" -> "+1 (305) 842-$rand"
            "CA" -> "+1 (438) 621-$rand"
            "FR" -> "+33 6 44 91 ${rand.toString().take(2)} ${rand.toString().takeLast(2)}"
            "GB" -> "+44 7911 12$rand"
            "DO" -> "+1 (809) 552-$rand"
            else -> "+1 (305) 918-$rand"
        }
    }

    fun getPlansForCountry(countryCode: String): List<EsimPlan> {
        val tiers = wholesaleTiers[countryCode] ?: wholesaleTiers["US"]!!
        val prefix = getCountryPrefix(countryCode)

        // 1. Data-only Plans
        val dataPlans = specs.mapIndexed { index, (gb, days) ->
            val wholesale = tiers.getOrElse(index) { tiers.last() }
            val retail = calculateRetailCents(wholesale)
            EsimPlan(
                id = "${countryCode}_${gb}GB_${days}D",
                countryCode = countryCode,
                dataGb = gb,
                validityDays = days,
                wholesaleUnits = wholesale,
                retailPriceCents = retail,
                networkName = "Sim X",
                supportsHotspot = true,
                supportsVoiceAndSms = false
            )
        } + listOf(
            // Unlimited Data Plan
            EsimPlan(
                id = "${countryCode}_UNLIMITED_15D",
                countryCode = countryCode,
                dataGb = -1,
                validityDays = 15,
                wholesaleUnits = (tiers[2] * 2.2).toInt(),
                retailPriceCents = calculateRetailCents((tiers[2] * 2.2).toInt()),
                networkName = "Sim X",
                supportsHotspot = true,
                supportsVoiceAndSms = false
            )
        )

        // 2. Voice & SMS Plans with Included Virtual International Number (Strictly White-Label Sim X)
        val voicePlans = listOf(
            EsimPlan(
                id = "${countryCode}_VOICE_5GB_30D",
                countryCode = countryCode,
                dataGb = 5,
                validityDays = 30,
                wholesaleUnits = (tiers[2] * 1.35).toInt(),
                retailPriceCents = calculateRetailCents((tiers[2] * 1.35).toInt()),
                networkName = "Sim X",
                supportsHotspot = true,
                supportsVoiceAndSms = true,
                voiceMinutes = 300,
                smsCount = 300,
                virtualPhoneNumberPrefix = prefix
            ),
            EsimPlan(
                id = "${countryCode}_VOICE_10GB_30D",
                countryCode = countryCode,
                dataGb = 10,
                validityDays = 30,
                wholesaleUnits = (tiers[3] * 1.30).toInt(),
                retailPriceCents = calculateRetailCents((tiers[3] * 1.30).toInt()),
                networkName = "Sim X",
                supportsHotspot = true,
                supportsVoiceAndSms = true,
                voiceMinutes = 500,
                smsCount = 500,
                virtualPhoneNumberPrefix = prefix
            ),
            EsimPlan(
                id = "${countryCode}_VOICE_UNLIMITED_30D",
                countryCode = countryCode,
                dataGb = -1,
                validityDays = 30,
                wholesaleUnits = (tiers[4] * 1.45).toInt(),
                retailPriceCents = calculateRetailCents((tiers[4] * 1.45).toInt()),
                networkName = "Sim X",
                supportsHotspot = true,
                supportsVoiceAndSms = true,
                voiceMinutes = -1, // Unlimited
                smsCount = -1, // Unlimited
                virtualPhoneNumberPrefix = prefix
            )
        )

        return dataPlans + voicePlans
    }
}
