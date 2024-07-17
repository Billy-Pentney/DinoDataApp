package com.bp.dinodata.data


enum class Epoch {
    Cretaceous, Jurassic, Triassic
}

enum class Subepoch {
    Early, Middle, Late
}

data class TimePeriod(
    val epoch: Epoch,
    private val subepoch: Subepoch? = null
)