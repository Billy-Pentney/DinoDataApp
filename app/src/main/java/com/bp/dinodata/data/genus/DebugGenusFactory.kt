package com.bp.dinodata.data.genus

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.time_period.epochs.MesozoicEpochs

object DebugGenusFactory {
    private val acro = GenusBuilder("Acrocanthosaurus")
        .setDiet(Diet.Carnivore)
        .setTimePeriod("Late Cretaceous")
        .setCreatureType(CreatureType.LargeTheropod)
        .build()
    private val trike = GenusBuilder("Triceratops")
        .setDiet(Diet.Herbivore)
        .setTimePeriod("Cretaceous")
        .setCreatureType(CreatureType.Ceratopsian)
        .build()
    private val dipl = GenusBuilder("Diplodocus")
        .setDiet(Diet.Herbivore)
        .setTimePeriod("Jurassic")
        .setCreatureType(CreatureType.Sauropod)
        .build()
    private val edmon = GenusBuilder("Edmontosaurus")
        .setDiet(Diet.Herbivore)
        .setTimePeriod("Jurassic")
        .setCreatureType(CreatureType.Hadrosaur)
        .build()
    private val ptero = GenusBuilder(
        "Pteranodon",
        diet = Diet.Piscivore,
        timePeriods = mutableListOf(MesozoicEpochs.Jurassic),
        type = CreatureType.Pterosaur
    ).build()
    private val raptor = GenusBuilder("Velociraptor")
        .setDiet(Diet.Carnivore)
        .setTimePeriod("Cretaceous")
        .setCreatureType(CreatureType.Dromaeosaurid)
        .build()
    private val ankylo = GenusBuilder("Ankylosaurus")
        .setDiet(Diet.Herbivore)
        .setTimePeriod("Cretaceous")
        .setCreatureType(CreatureType.Ankylosaur)
        .build()
    private val stego = GenusBuilder("Stegosaurus")
        .setDiet(Diet.Herbivore)
        .setTimePeriod("Jurassic")
        .setCreatureType(CreatureType.Stegosaur).build()
    private val spino = GenusBuilder("Spinosaurus")
        .setDiet(Diet.Piscivore)
        .setTimePeriod("Cretaceous")
        .setCreatureType(CreatureType.Spinosaur).build()
    private val unkn = GenusBuilder("Othersaurus")
        .setDiet(Diet.Unknown)
        .setTimePeriod("Other")
        .setCreatureType(CreatureType.Other).build()

    private val styraco = GenusBuilder("Styracosaurus")
        .setDiet(Diet.Herbivore)
        .setTimePeriod("Early Cretaceous")
        .setNamePronunciation("'sty-RAK-oh-SORE-us'")
        .setNameMeaning("spiked lizard from some mythical place in the world")
        .setLength("5 metres")
        .setMass("1-1.5 tonnes")
        .setCreatureType(CreatureType.Ceratopsian)
        .setStartMya("75.5")
        .setEndMya("74.5")
        .setTaxonomy(listOf("Dinosauria", "Saurischia", "Ceratopsidae", "Centrosaurinae"))
        .setLocations(listOf("Canada", "USA"))
        .setFormations(listOf("Alberta Woodland Formation"))
        .setSpecies(
            listOf(
                mapOf(
                    "name" to "albertensis",
                    "discovered_by" to "Marsh",
                    "discovered_year" to "1885",
                    "is_type" to "true"
                )
            )
        )
        .build()

    enum class GenusFactoryKey {
        Edmontosaurus,
        Stegosaurus,
        Spinosaurus,
        Unknown,
        Ankylosaurus,
        Triceratops,
        Styracosaurus,
        Velociraptor,
        Pteranodon,
        Diplodocus,
        Acrocanthosaurus
    }

    fun getGenus(key: GenusFactoryKey): IGenus {
        return when (key) {
            GenusFactoryKey.Edmontosaurus -> edmon
            GenusFactoryKey.Stegosaurus -> stego
            GenusFactoryKey.Spinosaurus -> spino
            GenusFactoryKey.Unknown -> unkn
            GenusFactoryKey.Ankylosaurus -> ankylo
            GenusFactoryKey.Triceratops -> trike
            GenusFactoryKey.Styracosaurus -> styraco
            GenusFactoryKey.Velociraptor -> raptor
            GenusFactoryKey.Pteranodon -> ptero
            GenusFactoryKey.Diplodocus -> dipl
            GenusFactoryKey.Acrocanthosaurus -> acro
        }
    }

    fun getAll(): List<IGenus> {
        return GenusFactoryKey.entries.map { getGenus(it) }
    }
}