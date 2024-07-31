package com.bp.dinodata.presentation.detail_genus

import androidx.compose.ui.graphics.Color
import com.bp.dinodata.R

interface IMarker{
    fun getX(): Float
    fun getY(): Float
    fun getMarkerColor(): Color
    fun getTextId(): Int?
}

interface IRegionMap {
    fun getMarker(key: String): IMarker?
    fun getAllKeys(): List<String>
    fun getRegionName(): String
}

class RegionMap(
    private val name: String,
    private val markers: Map<String,IMarker> = emptyMap()
): IRegionMap {
    constructor(
        name: String,
        vararg pairs: Pair<String, IMarker>
    ): this(name, pairs.toMap())

    override fun getRegionName(): String = name
    override fun getMarker(key: String): IMarker? = markers[key]
    override fun getAllKeys(): List<String> = markers.keys.toList()
}


/**
 * A marker refers to a point which is displayed on the global map. This could be a country
 * or state (or neither), but it must have a specific point.
 * @property x the latitude of the marker, ranges from 0.0-1.0
 * @property y the longitude of the marker, ranges from 0.0-1.0
 * @property textId (optional) the Id of the string for this region; this is used to display
 * localised country names e.g. "Russe" rather than "Russia"
 * @property color (optional) the color of this marker; by default, uses a slightly
 * translucent white.
 */
data class Marker(
    private val x: Float,
    private val y: Float,
    private val textId: Int? = null,
    private val color: Color = Color(0xBBFFFFFF)
): IMarker {
    override fun getX(): Float = x
    override fun getY(): Float = y
    override fun getMarkerColor(): Color = color
    override fun getTextId(): Int? = textId
}

object AtlasMarkers {

    const val AFRICA = "AFRICA"
    const val AMERICA = "AMERICA"
    const val EUROPE = "EUROPE"
    const val ASIA = "ASIA"
    const val OCEANIA = "OCEANIA"

    private val keyRedirect = mapOf(
        "United Kingdom" to "UK",
        "United States" to "USA",
        "North America" to "USA"
    )

    private val AMERICA_MAP = RegionMap(
        AMERICA,
        "Canada" to Marker(0.192f, 0.247f, R.string.location_canada),
        "USA" to Marker(0.225f, 0.35f, R.string.location_usa_full),
        "Mexico" to Marker(0.206f, 0.438f, R.string.location_mexico),
        "Argentina" to Marker(0.313f, 0.685f, R.string.location_argentina),
        "Brazil" to Marker(0.348f, 0.59f, R.string.location_brazil),
        "Uruguay" to Marker(0.325f, 0.695f, R.string.location_uruguay),
        "Venezuela" to Marker(0.29f, 0.507f, R.string.location_venezuela),
        "Colombia" to Marker(0.279f, 0.525f, R.string.location_colombia),
        "Chile" to Marker(0.29f, 0.69f, R.string.location_chile),
        "Peru" to Marker(0.28f, 0.6f, R.string.location_peru)
    )

    private val EUROPEAN_MAP = RegionMap(
        EUROPE,
        "UK" to Marker(0.468f, 0.267f, R.string.location_uk_full),
        "France" to Marker(0.477f, 0.30f, R.string.location_france),
        "Belgium" to Marker(0.483f, 0.28f, R.string.location_belgium),
        "Switzerland" to Marker(0.489f, 0.31f, R.string.location_switzerland),
        "Germany" to Marker(0.5f, 0.29f, R.string.location_germany),
        "Spain" to Marker(0.463f, 0.342f, R.string.location_spain),
        "Portugal" to Marker(0.452f, 0.339f, R.string.location_portugal),
        "Russia" to Marker(0.6f, 0.24f, R.string.location_russia),
        "Romania" to Marker(0.541f, 0.308f, R.string.location_romania),
        "Italy" to Marker(0.51f, 0.333f, R.string.location_italy),
        "Greece" to Marker(0.535f, 0.35f, R.string.location_greece),
        "Albania" to Marker(0.529f, 0.34f, R.string.location_albania),
        // SCANDINAVIA
        "Norway" to Marker(0.49f, 0.22f, R.string.location_norway),
        "Sweden" to Marker(0.51f, 0.24f, R.string.location_sweden),
        "Greenland" to Marker(0.36f, 0.15f, R.string.location_greenland),
        "Austria" to Marker(0.512f, 0.307f, R.string.location_austria),
        "Hungary" to Marker(0.526f, 0.311f, R.string.location_hungary),
    )

    private val AFRICA_MAP = RegionMap(
        AFRICA,
        "Cameroon" to Marker(0.505f, 0.52f, R.string.location_cameroon),
        "Egypt" to Marker(0.548f, 0.408f, R.string.location_egypt),
        "Libya" to Marker(0.515f, 0.407f, R.string.location_libya),
        "Morocco" to Marker(0.454f, 0.382f, R.string.location_morocco),
        "Algeria" to Marker(0.48f, 0.411f, R.string.location_algeria),
        "Niger" to Marker(0.5f, 0.462f, R.string.location_niger),
        "South Africa" to Marker(0.535f, 0.685f, R.string.location_south_africa),
        "Madagascar" to Marker(0.599f, 0.64f, R.string.location_madagascar),
        "Tunisia" to Marker(0.495f, 0.375f, R.string.location_tunisia),
        "Zimbabwe" to Marker(0.555f, 0.625f, R.string.location_zimbabwe),
        "Malawi" to Marker(0.565f, 0.605f, R.string.location_malawi),
        "Lesotho" to Marker(0.548f, 0.677f, R.string.location_lesotho),
        "Tanzania" to Marker(0.566f, 0.567f, R.string.location_tanzania),
        "Western Sahara" to Marker(0.438f, 0.425f, R.string.location_western_sahara)
    )

    private val ASIA_MAP = RegionMap(
        ASIA,
        "Mongolia" to Marker(0.75f, 0.3f, R.string.location_mongolia),
        "Kazakhstan" to Marker(0.67f, 0.29f, R.string.location_kazakhstan),
        "Uzbekistan" to Marker(0.64f, 0.33f, R.string.location_uzbekistan),
        "Pakistan" to Marker(0.665f, 0.4f, R.string.location_pakistan),
        "India" to Marker(0.685f, 0.435f, R.string.location_india),
        "Laos" to Marker(0.745f, 0.44f, R.string.location_laos),
        "Jordan" to Marker(0.573f, 0.385f, R.string.location_jordan),
        "China" to Marker(0.74f, 0.384f, R.string.location_china),
        "Korea" to Marker(0.81f, 0.36f, R.string.location_korea),
        "Japan" to Marker(0.835f, 0.365f, R.string.location_japan),
        "Thailand" to Marker(0.743f, 0.47f, R.string.location_thailand)
    )


    private val OTHER_REGIONS = RegionMap(
        OCEANIA,
        "Australia" to Marker(0.83f, 0.65f, R.string.location_australia),
        "Antarctica" to Marker(0.589f, 0.932f, R.string.location_antarctica)
    )

    private val REGION_MAPS: List<IRegionMap> = listOf(
        AMERICA_MAP,
        EUROPEAN_MAP,
        AFRICA_MAP,
        ASIA_MAP,
        OTHER_REGIONS
    )

    private val allKeys = REGION_MAPS.flatMap { it.getAllKeys() }

    /*
    private val GENERATED_MAP = RegionMap("Other",
//        "Andorra" to Marker(0.4731f, 0.3093f, R.string.location_andorra),
//        "United Arab Emirates" to Marker(0.6308f, 0.427f, R.string.location_united_arab_emirates),
//        "Afghanistan" to Marker(0.6552f, 0.3684f, R.string.location_afghanistan),
//        "Antigua and Barbuda" to Marker(0.341f, 0.4643f, R.string.location_antigua_and_barbuda),
//        "Anguilla" to Marker(0.3364f, 0.4575f, R.string.location_anguilla),
//        "Albania" to Marker(0.5231f, 0.32f, R.string.location_albania),
//        "Armenia" to Marker(0.5891f, 0.3298f, R.string.location_armenia),
//        "Netherlands Antilles" to Marker(0.3275f, 0.4911f, R.string.location_netherlands_antilles),
//        "Angola" to Marker(0.5125f, 0.5471f, R.string.location_angola),
//        "Antarctica" to Marker(0.5423f, 0.931f, R.string.location_antarctica),
//        "Argentina" to Marker(0.3351f, 0.7095f, R.string.location_argentina),
//        "American Samoa" to Marker(0.0302f, 0.555f, R.string.location_american_samoa),
//        "Austria" to Marker(0.5013f, 0.2825f, R.string.location_austria),
//        "Australia" to Marker(0.8306f, 0.6448f, R.string.location_australia),
//        "Aruba" to Marker(0.3248f, 0.4893f, R.string.location_aruba),
//        "Azerbaijan" to Marker(0.5958f, 0.3297f, R.string.location_azerbaijan),
//        "Bosnia and Herzegovina" to Marker(0.5135f, 0.3037f, R.string.location_bosnia_and_herzegovina),
//        "Barbados" to Marker(0.3512f, 0.487f, R.string.location_barbados),
//        "Bangladesh" to Marker(0.7258f, 0.4307f, R.string.location_bangladesh),
//        "Belgium" to Marker(0.4717f, 0.2638f, R.string.location_belgium),
//        "Burkina Faso" to Marker(0.5035f, 0.5007f, R.string.location_burkina_faso),
//        "Bulgaria" to Marker(0.5352f, 0.3117f, R.string.location_bulgaria),
//        "Bahrain" to Marker(0.6196f, 0.4121f, R.string.location_bahrain),
//        "Burundi" to Marker(0.5351f, 0.5037f, R.string.location_burundi),
//        "Benin" to Marker(0.5124f, 0.501f, R.string.location_benin),
//        "Bermuda" to Marker(0.3161f, 0.3759f, R.string.location_bermuda),
//        "Brunei" to Marker(0.8109f, 0.5447f, R.string.location_brunei),
//        "Bolivia" to Marker(0.3103f, 0.582f, R.string.location_bolivia),
//        "Brazil" to Marker(0.3384f, 0.5718f, R.string.location_brazil),
//        "Bahamas" to Marker(0.2914f, 0.4161f, R.string.location_bahamas),
//        "Bhutan" to Marker(0.7217f, 0.4087f, R.string.location_bhutan),
//        "Bouvet Island" to Marker(0.5234f, 0.7942f, R.string.location_bouvet_island),
//        "Botswana" to Marker(0.5428f, 0.6123f, R.string.location_botswana),
//        "Belarus" to Marker(0.5292f, 0.2487f, R.string.location_belarus),
//        "Belize" to Marker(0.2712f, 0.4597f, R.string.location_belize),
//        "Canada" to Marker(0.1809f, 0.2327f, R.string.location_canada),
//        "Congo [DRC]" to Marker(0.5146f, 0.5064f, R.string.location_congo_drc),
//        "Central African Republic" to Marker(0.564f, 0.5193f, R.string.location_central_african_republic),
//        "Congo [Republic]" to Marker(0.4948f, 0.4836f, R.string.location_congo_republic),
//        "Switzerland" to Marker(0.4856f, 0.2856f, R.string.location_switzerland),
//        "Cook Islands" to Marker(0.065f, 0.5967f, R.string.location_cook_islands),
//        "Chile" to Marker(0.3114f, 0.6926f, R.string.location_chile),
//        "Cameroon" to Marker(0.5408f, 0.5136f, R.string.location_cameroon),
//        "China" to Marker(0.7481f, 0.3625f, R.string.location_china),
//        "Colombia" to Marker(0.3225f, 0.5345f, R.string.location_colombia),
//        "Costa Rica" to Marker(0.2919f, 0.5033f, R.string.location_costa_rica),
//        "Cuba" to Marker(0.2943f, 0.4363f, R.string.location_cuba),
//        "Cape Verde" to Marker(0.4407f, 0.4758f, R.string.location_cape_verde),
//        "Christmas Island" to Marker(0.7406f, 0.5554f, R.string.location_christmas_island),
//        "Cyprus" to Marker(0.5645f, 0.3567f, R.string.location_cyprus),
//        "Czech Republic" to Marker(0.5011f, 0.2694f, R.string.location_czech_republic),
//        "Germany" to Marker(0.4865f, 0.2609f, R.string.location_germany),
//        "Djibouti" to Marker(0.6146f, 0.4922f, R.string.location_djibouti),
//        "Denmark" to Marker(0.4783f, 0.2314f, R.string.location_denmark),
//        "Dominica" to Marker(0.3439f, 0.4739f, R.string.location_dominica),
//        "Dominican Republic" to Marker(0.3173f, 0.4535f, R.string.location_dominican_republic),
//        "Algeria" to Marker(0.4896f, 0.393f, R.string.location_algeria),
//        "Ecuador" to Marker(0.256f, 0.4965f, R.string.location_ecuador),
//        "Estonia" to Marker(0.5161f, 0.2202f, R.string.location_estonia),
//        "Egypt" to Marker(0.5669f, 0.4041f, R.string.location_egypt),
//        "Western Sahara" to Marker(0.4605f, 0.4301f, R.string.location_western_sahara),
//        "Eritrea" to Marker(0.6035f, 0.4725f, R.string.location_eritrea),
//        "Spain" to Marker(0.466f, 0.3377f, R.string.location_spain),
//        "Ethiopia" to Marker(0.6121f, 0.5074f, R.string.location_ethiopia),
//        "Finland" to Marker(0.5142f, 0.2011f, R.string.location_finland),
//        "Fiji" to Marker(0.9398f, 0.6013f, R.string.location_fiji),
//        "Micronesia" to Marker(0.9011f, 0.5331f, R.string.location_micronesia),
//        "Faroe Islands" to Marker(0.4336f, 0.2138f, R.string.location_faroe_islands),
//        "France" to Marker(0.4705f, 0.2882f, R.string.location_france),
//        "Gabon" to Marker(0.4845f, 0.4863f, R.string.location_gabon),
//        "United Kingdom" to Marker(0.4501f, 0.2518f, R.string.location_united_kingdom, Color.Red),
//        "Grenada" to Marker(0.3469f, 0.492f, R.string.location_grenada),
//        "Georgia" to Marker(0.5822f, 0.3166f, R.string.location_georgia),
//        "French Guiana" to Marker(0.3783f, 0.5412f, R.string.location_french_guiana),
//        "Guernsey" to Marker(0.459f, 0.286f, R.string.location_guernsey),
//        "Ghana" to Marker(0.5098f, 0.5256f, R.string.location_ghana),
//        "Gibraltar" to Marker(0.4667f, 0.3624f, R.string.location_gibraltar),
//        "Greenland" to Marker(0.3295f, 0.1521f, R.string.location_greenland),
//        "Gambia" to Marker(0.4663f, 0.4919f, R.string.location_gambia),
//        "Guinea" to Marker(0.4848f, 0.5128f, R.string.location_guinea),
//        "Guadeloupe" to Marker(0.3404f, 0.4647f, R.string.location_guadeloupe),
//        "Equatorial Guinea" to Marker(0.5417f, 0.5463f, R.string.location_equatorial_guinea),
//        "Greece" to Marker(0.5297f, 0.3322f, R.string.location_greece),
//        "Guatemala" to Marker(0.2683f, 0.4676f, R.string.location_guatemala),
//        "Guam" to Marker(0.8793f, 0.4976f, R.string.location_guam),
//        "Guinea-Bissau" to Marker(0.4685f, 0.5013f, R.string.location_guinea_bissau),
//        "Guyana" to Marker(0.3622f, 0.535f, R.string.location_guyana),
//        "Gaza Strip" to Marker(0.571f, 0.3785f, R.string.location_gaza_strip),
//        "Hong Kong" to Marker(0.7893f, 0.4416f, R.string.location_hong_kong),
//        "Heard Island and McDonald Islands" to Marker(0.7047f, 0.7965f, R.string.location_heard_island_and_mcdonald_islands),
//        "Honduras" to Marker(0.2794f, 0.4716f, R.string.location_honduras),
//        "Croatia" to Marker(0.5057f, 0.2966f, R.string.location_croatia),
//        "Haiti" to Marker(0.3115f, 0.4518f, R.string.location_haiti),
//        "Hungary" to Marker(0.5146f, 0.2853f, R.string.location_hungary),
//        "Indonesia" to Marker(0.7513f, 0.5009f, R.string.location_indonesia),
//        "Ireland" to Marker(0.4397f, 0.2625f, R.string.location_ireland),
//        "Israel" to Marker(0.5728f, 0.3804f, R.string.location_israel),
//        "Isle of Man" to Marker(0.4485f, 0.2583f, R.string.location_isle_of_man),
//        "India" to Marker(0.6996f, 0.447f, R.string.location_india),
//        "British Indian Ocean Territory" to Marker(0.6479f, 0.5268f, R.string.location_british_indian_ocean_territory),
//        "Iraq" to Marker(0.5933f, 0.3691f, R.string.location_iraq),
//        "Iran" to Marker(0.6203f, 0.3751f, R.string.location_iran),
//        "Iceland" to Marker(0.3987f, 0.1944f, R.string.location_iceland),
//        "Italy" to Marker(0.5025f, 0.3148f, R.string.location_italy),
//        "Jersey" to Marker(0.4605f, 0.2876f, R.string.location_jersey),
//        "Jamaica" to Marker(0.2994f, 0.456f, R.string.location_jamaica),
//        "Jordan" to Marker(0.5769f, 0.3832f, R.string.location_jordan),
//        "Japan" to Marker(0.8367f, 0.3655f, R.string.location_japan),
//        "Kenya" to Marker(0.5521f, 0.4856f, R.string.location_kenya),
//        "Kyrgyzstan" to Marker(0.6654f, 0.3276f, R.string.location_kyrgyzstan),
//        "Cambodia" to Marker(0.7765f, 0.497f, R.string.location_cambodia),
//        "Kiribati" to Marker(0.0216f, 0.4924f, R.string.location_kiribati),
//        "Comoros" to Marker(0.5811f, 0.5547f, R.string.location_comoros),
//        "Saint Kitts and Nevis" to Marker(0.3381f, 0.4624f, R.string.location_saint_kitts_and_nevis),
//        "North Korea" to Marker(0.8039f, 0.3401f, R.string.location_north_korea),
//        "South Korea" to Marker(0.8097f, 0.3656f, R.string.location_south_korea),
//        "Kuwait" to Marker(0.6077f, 0.3922f, R.string.location_kuwait),
//        "Cayman Islands" to Marker(0.2893f, 0.4475f, R.string.location_cayman_islands),
//        "Kazakhstan" to Marker(0.6373f, 0.2872f, R.string.location_kazakhstan),
//        "Laos" to Marker(0.7618f, 0.4546f, R.string.location_laos),
//        "Lebanon" to Marker(0.5722f, 0.3643f, R.string.location_lebanon),
//        "Saint Lucia" to Marker(0.3467f, 0.4826f, R.string.location_saint_lucia),
//        "Liechtenstein" to Marker(0.4886f, 0.2839f, R.string.location_liechtenstein),
//        "Sri Lanka" to Marker(0.7186f, 0.5205f, R.string.location_sri_lanka),
//        "Liberia" to Marker(0.4895f, 0.5332f, R.string.location_liberia),
//        "Lesotho" to Marker(0.5602f, 0.6547f, R.string.location_lesotho),
//        "Lithuania" to Marker(0.517f, 0.2398f, R.string.location_lithuania),
//        "Luxembourg" to Marker(0.4767f, 0.268f, R.string.location_luxembourg),
//        "Latvia" to Marker(0.5169f, 0.23f, R.string.location_latvia),
//        "Libya" to Marker(0.5322f, 0.405f, R.string.location_libya),
//        "Morocco" to Marker(0.467f, 0.3873f, R.string.location_morocco),
//        "Monaco" to Marker(0.4869f, 0.3032f, R.string.location_monaco),
//        "Moldova" to Marker(0.5375f, 0.2851f, R.string.location_moldova),
//        "Montenegro" to Marker(0.5193f, 0.3109f, R.string.location_montenegro),
//        "Madagascar" to Marker(0.5967f, 0.5949f, R.string.location_madagascar),
//        "Marshall Islands" to Marker(0.9552f, 0.5378f, R.string.location_marshall_islands),
//        "Mali" to Marker(0.4911f, 0.4696f, R.string.location_mali),
//        "Mongolia" to Marker(0.7349f, 0.2991f, R.string.location_mongolia),
//        "Macau" to Marker(0.788f, 0.4427f, R.string.location_macau),
//        "Northern Mariana Islands" to Marker(0.8765f, 0.4753f, R.string.location_northern_mariana_islands),
//        "Martinique" to Marker(0.3457f, 0.4784f, R.string.location_martinique),
//        "Mauritania" to Marker(0.4691f, 0.4489f, R.string.location_mauritania),
//        "Montserrat" to Marker(0.3403f, 0.4661f, R.string.location_montserrat),
//        "Malta" to Marker(0.5138f, 0.3492f, R.string.location_malta),
//        "Mauritius" to Marker(0.6263f, 0.6055f, R.string.location_mauritius),
//        "Maldives" to Marker(0.7042f, 0.5464f, R.string.location_maldives),
//        "Malawi" to Marker(0.5576f, 0.5613f, R.string.location_malawi),
//        "Mexico" to Marker(0.2273f, 0.4206f, R.string.location_mexico),
//        "Malaysia" to Marker(0.778f, 0.5447f, R.string.location_malaysia),
//        "Mozambique" to Marker(0.567f, 0.5927f, R.string.location_mozambique),
//        "Namibia" to Marker(0.5274f, 0.615f, R.string.location_namibia),
//        "New Caledonia" to Marker(0.9087f, 0.6242f, R.string.location_new_caledonia),
//        "Niger" to Marker(0.5181f, 0.454f, R.string.location_niger),
//        "Norfolk Island" to Marker(0.924f, 0.6715f, R.string.location_norfolk_island),
//        "Nigeria" to Marker(0.5293f, 0.5032f, R.string.location_nigeria),
//        "Nicaragua" to Marker(0.2847f, 0.4851f, R.string.location_nicaragua),
//        "Netherlands" to Marker(0.472f, 0.2546f, R.string.location_netherlands),
//        "Norway" to Marker(0.4709f, 0.207f, R.string.location_norway),
//        "Nepal" to Marker(0.7043f, 0.4027f, R.string.location_nepal),
//        "Nauru" to Marker(0.8892f, 0.507f, R.string.location_nauru),
//        "Niue" to Marker(0.0362f, 0.5826f, R.string.location_niue),
//        "New Zealand" to Marker(0.9554f, 0.7408f, R.string.location_new_zealand, Color.Blue),
//        "Oman" to Marker(0.6385f, 0.4383f, R.string.location_oman),
//        "Panama" to Marker(0.3011f, 0.5108f, R.string.location_panama),
//        "Peru" to Marker(0.2726f, 0.5394f, R.string.location_peru),
//        "French Polynesia" to Marker(0.0881f, 0.5777f, R.string.location_french_polynesia),
//        "Papua New Guinea" to Marker(0.8359f, 0.5371f, R.string.location_papua_new_guinea),
//        "Philippines" to Marker(0.8198f, 0.4975f, R.string.location_philippines),
//        "Pakistan" to Marker(0.6635f, 0.3891f, R.string.location_pakistan),
//        "Poland" to Marker(0.5083f, 0.2578f, R.string.location_poland),
//        "Saint Pierre and Miquelon" to Marker(0.3218f, 0.2929f, R.string.location_saint_pierre_and_miquelon),
//        "Pitcairn Islands" to Marker(0.1533f, 0.6213f, R.string.location_pitcairn_islands),
//        "Puerto Rico" to Marker(0.3272f, 0.457f, R.string.location_puerto_rico),
//        "Palestinian Territories" to Marker(0.5727f, 0.3752f, R.string.location_palestinian_territories),
//        "Portugal" to Marker(0.4556f, 0.3433f, R.string.location_portugal),
//        "Palau" to Marker(0.8594f, 0.5304f, R.string.location_palau),
//        "Paraguay" to Marker(0.3318f, 0.6239f, R.string.location_paraguay),
//        "Qatar" to Marker(0.6218f, 0.4155f, R.string.location_qatar),
//        "Réunion" to Marker(0.6219f, 0.6097f, R.string.location_réunion),
//        "Romania" to Marker(0.5303f, 0.2931f, R.string.location_romania),
//        "Serbia" to Marker(0.522f, 0.3036f, R.string.location_serbia),
//        "Russia" to Marker(0.7222f, 0.2148f, R.string.location_russia),
//        "Rwanda" to Marker(0.5334f, 0.4954f, R.string.location_rwanda),
//        "Saudi Arabia" to Marker(0.6075f, 0.4231f, R.string.location_saudi_arabia),
//        "Solomon Islands" to Marker(0.8818f, 0.5586f, R.string.location_solomon_islands),
//        "Seychelles" to Marker(0.6033f, 0.515f, R.string.location_seychelles),
//        "Sudan" to Marker(0.5812f, 0.4846f, R.string.location_sudan),
//        "Sweden" to Marker(0.4978f, 0.2104f, R.string.location_sweden),
//        "Singapore" to Marker(0.7861f, 0.5614f, R.string.location_singapore),
//        "Saint Helena" to Marker(0.4588f, 0.6349f, R.string.location_saint_helena),
//        "Slovenia" to Marker(0.504f, 0.2904f, R.string.location_slovenia),
//        "Svalbard and Jan Mayen" to Marker(0.4913f, 0.1107f, R.string.location_svalbard_and_jan_mayen),
//        "Slovakia" to Marker(0.5134f, 0.2766f, R.string.location_slovakia),
//        "Sierra Leone" to Marker(0.4811f, 0.5211f, R.string.location_sierra_leone),
//        "San Marino" to Marker(0.4999f, 0.3029f, R.string.location_san_marino),
//        "Senegal" to Marker(0.4674f, 0.4859f, R.string.location_senegal),
//        "Somalia" to Marker(0.6315f, 0.5312f, R.string.location_somalia),
//        "Suriname" to Marker(0.3708f, 0.5409f, R.string.location_suriname),
//        "São Tomé and Príncipe" to Marker(0.4704f, 0.4799f, R.string.location_são_tomé_and_príncipe),
//        "El Salvador" to Marker(0.274f, 0.4793f, R.string.location_el_salvador),
//        "Syria" to Marker(0.5794f, 0.3593f, R.string.location_syria),
//        "Swaziland" to Marker(0.5652f, 0.6374f, R.string.location_swaziland),
//        "Turks and Caicos Islands" to Marker(0.3097f, 0.4362f, R.string.location_turks_and_caicos_islands),
//        "Chad" to Marker(0.5483f, 0.4679f, R.string.location_chad),
//        "French Southern Territories" to Marker(0.6896f, 0.774f, R.string.location_french_southern_territories),
//        "Togo" to Marker(0.5138f, 0.5219f, R.string.location_togo),
//        "Thailand" to Marker(0.7624f, 0.4774f, R.string.location_thailand),
//        "Tajikistan" to Marker(0.6589f, 0.3405f, R.string.location_tajikistan),
//        "Tokelau" to Marker(0.0198f, 0.5242f, R.string.location_tokelau),
//        "Timor-Leste" to Marker(0.7911f, 0.5492f, R.string.location_timor_leste),
//        "Turkmenistan" to Marker(0.6283f, 0.3382f, R.string.location_turkmenistan),
//        "Tunisia" to Marker(0.5036f, 0.3604f, R.string.location_tunisia),
//        "Tonga" to Marker(0.0248f, 0.5941f, R.string.location_tonga),
//        "Turkey" to Marker(0.5649f, 0.3348f, R.string.location_turkey),
//        "Trinidad and Tobago" to Marker(0.3496f, 0.5012f, R.string.location_trinidad_and_tobago),
//        "Tuvalu" to Marker(0.9246f, 0.5465f, R.string.location_tuvalu),
//        "Taiwan" to Marker(0.8056f, 0.435f, R.string.location_taiwan),
//        "Tanzania" to Marker(0.5515f, 0.5217f, R.string.location_tanzania),
//        "Ukraine" to Marker(0.5436f, 0.2799f, R.string.location_ukraine),
//        "Uganda" to Marker(0.5995f, 0.551f, R.string.location_uganda),
//        "U.S. Minor Outlying Islands" to Marker(0.4578f, 0.4972f, R.string.location_us_minor_outlying_islands),
//        "United States" to Marker(0.23f, 0.344f, R.string.location_united_states),
//        "Uruguay" to Marker(0.349f, 0.6767f, R.string.location_uruguay),
//        "Uzbekistan" to Marker(0.6387f, 0.325f, R.string.location_uzbekistan),
//        "Vatican City" to Marker(0.5022f, 0.3146f, R.string.location_vatican_city),
//        "Saint Vincent and the Grenadines" to Marker(0.347f, 0.4879f, R.string.location_saint_vincent_and_the_grenadines),
//        "Venezuela" to Marker(0.3405f, 0.525f, R.string.location_venezuela),
//        "British Virgin Islands" to Marker(0.3321f, 0.4561f, R.string.location_british_virgin_islands),
//        "U.S. Virgin Islands" to Marker(0.3314f, 0.4565f, R.string.location_us_virgin_islands),
//        "Vietnam" to Marker(0.7834f, 0.4888f, R.string.location_vietnam),
//        "Vanuatu" to Marker(0.9061f, 0.5926f, R.string.location_vanuatu),
//        "Wallis and Futuna" to Marker(0.0113f, 0.5511f, R.string.location_wallis_and_futuna),
//        "Samoa" to Marker(0.0245f, 0.5519f, R.string.location_samoa),
//        "Kosovo" to Marker(0.5234f, 0.3117f, R.string.location_kosovo),
//        "Yemen" to Marker(0.6259f, 0.4717f, R.string.location_yemen),
//        "Mayotte" to Marker(0.5855f, 0.5604f, R.string.location_mayotte),
//        "South Africa" to Marker(0.5475f, 0.6594f, R.string.location_south_africa),
//        "Zambia" to Marker(0.5407f, 0.5597f, R.string.location_zambia),
//        "Zimbabwe" to Marker(0.5507f, 0.5938f, R.string.location_zimbabwe)
    )*/

    fun getPosition(location: String): IMarker? {
        val key = keyRedirect[location] ?: location
        for (map in REGION_MAPS) {
            val marker = map.getMarker(key)
            if (marker != null) {
                return marker
            }
        }
        return null
    }

    fun getAllKnownKeys(): List<String> {
        return allKeys
    }

    fun getKeysForRegion(name: String): List<String> {
        for (region in REGION_MAPS) {
            if (region.getRegionName() == name) {
                return region.getAllKeys()
            }
        }
        return emptyList()
    }
}