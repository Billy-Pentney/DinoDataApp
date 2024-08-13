package com.bp.dinodata.data.genus

object DebugGenusFactory {
    private val acro = GenusBuilder("Acrocanthosaurus")
        .setDiet("Carnivorous")
        .setTimePeriod("Late Cretaceous")
        .setCreatureType("large theropod")
        .build()
    private val trike = GenusBuilder("Triceratops")
        .setDiet("Herbivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("ceratopsian")
        .build()
    private val dipl = GenusBuilder("Diplodocus")
        .setDiet("Herbivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("sauropod")
        .build()
    private val edmon = GenusBuilder("Edmontosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("hadrosaur")
        .build()
    private val ptero = GenusBuilder("Pteranodon")
        .setDiet("Piscivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("pterosaur")
        .build()
    private val raptor = GenusBuilder("Velociraptor")
        .setDiet("Carnivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("small theropod")
        .build()
    private val ankylo = GenusBuilder("Ankylosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("ankylosaurid")
        .build()
    private val stego = GenusBuilder("Stegosaurus")
        .setDiet("Herbivorous")
        .setTimePeriod("Jurassic")
        .setCreatureType("Stegosaurid").build()
    private val spino = GenusBuilder("Spinosaurus").setDiet("Piscivorous")
        .setTimePeriod("Cretaceous")
        .setCreatureType("spinosaur").build()
    private val unkn = GenusBuilder("Othersaurus").setDiet("Nuts")
        .setTimePeriod("Other")
        .setCreatureType("other").build()

    private val styraco = GenusBuilder("Styracosaurus")
        .setDiet("Herbivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 70-65.5 mya")
        .setNamePronunciation("'sty-RAK-oh-SORE-us'")
        .setNameMeaning("spiked lizard")
        .setLength("5 metres")
        .setWeight("1 tonnes")
        .setCreatureType("ceratopsian")
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
        Raptor,
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
            GenusFactoryKey.Raptor -> raptor
            GenusFactoryKey.Pteranodon -> ptero
            GenusFactoryKey.Diplodocus -> dipl
            GenusFactoryKey.Acrocanthosaurus -> acro
        }
    }

    fun getAll(): List<IGenus> {
        return GenusFactoryKey.entries.map { getGenus(it) }
    }
}