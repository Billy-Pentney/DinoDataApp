package com.bp.dinodata.data.attributions

open class ResourceAttribution(
    val source: String
) {
    data object ChatGPT: ResourceAttribution("ChatGPT")
    data object Phylopic: ResourceAttribution("Phylopic.org")
}