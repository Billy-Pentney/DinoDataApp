package com.bp.dinodata.data.attributions

interface IResourceAttribution {
    fun describe(): String
}

sealed class ResourceAttribution: IResourceAttribution {
    data object ChatGPT: ResourceAttribution() {
        override fun describe(): String = "ChatGPT"
    }
    data object Phylopic: ResourceAttribution() {
        override fun describe(): String = "Phylopic.org"
    }
}