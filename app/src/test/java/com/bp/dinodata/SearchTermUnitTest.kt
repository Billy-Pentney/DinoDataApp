package com.bp.dinodata

import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.Diet
import com.bp.dinodata.data.filters.ConjunctiveFilter
import com.bp.dinodata.data.genus.Genus
import com.bp.dinodata.data.genus.IGenus
import com.bp.dinodata.data.search.terms.BasicSearchTerm
import com.bp.dinodata.data.search.terms.CreatureTypeSearchTerm
import com.bp.dinodata.data.search.terms.DietSearchTerm
import org.junit.Assert.assertEquals
import org.junit.Test


class SearchTermUnitTest {

    private val testGenera: List<IGenus>

    private val spinosaurus = Genus(
        name = "Spinosaurus",
        diet = Diet.Piscivore,
        type = CreatureType.Spinosaur
    )
    private val triceratops = Genus(
        name = "Triceratops",
        diet = Diet.Herbivore,
        type = CreatureType.Ceratopsian
    )
    private val gigantspinosaurus = Genus(
        name = "Gigantspinosaurus",
        diet = Diet.Herbivore,
        type = CreatureType.Stegosaur
    )
    private val garrigatitan = Genus(
        name = "Garrigatitan",
        diet = Diet.Herbivore,
        type = CreatureType.Sauropod
    )
    private val tyrannotitan = Genus(
        name = "Tyrannotitan",
        diet = Diet.Carnivore,
        type = CreatureType.LargeTheropod
    )

    init {
        // For consistency, genera are sorted alphabetically
        testGenera = listOf(
            triceratops,
            spinosaurus,
            gigantspinosaurus,
            garrigatitan,
            tyrannotitan
        ).sortedBy { it.getName() }
    }

    @Test
    fun basicTermSimple() {
        val searchTerm = BasicSearchTerm("spino")
        val matches = searchTerm.applyTo(testGenera)
        assert(matches.size == 2)
        assertEquals(spinosaurus, matches.first())
    }

    @Test
    fun basicTermSuffix() {
        val searchTerm = BasicSearchTerm("tops")
        val matches = searchTerm.applyTo(testGenera)
        assert(matches.size == 1)
        assertEquals(triceratops, matches.first())
    }

    @Test
    fun basicRegexPartial() {
        val searchTerm = BasicSearchTerm("g*saurus")
        val matches = searchTerm.applyTo(testGenera)
        assert(matches.size == 1)
        assertEquals(gigantspinosaurus, matches.first())
    }

    @Test
    fun basicRegexSuffix() {
        val searchTerm = BasicSearchTerm("*titan$")
        val matches = searchTerm.applyTo(testGenera)
        assert(matches.size == 2)
        assertEquals(listOf(garrigatitan, tyrannotitan), matches)
    }

    @Test
    fun dietAndTypeSearch() {
        val dietSearch = DietSearchTerm("diet:herbivore+carnivore")
        val typeSearch = CreatureTypeSearchTerm("type:ceratopsian")
        val filter = ConjunctiveFilter(dietSearch, typeSearch)

        // All returned genera must be (herb OR carn) && Ceratopsian

        assertEquals(filter.size, 2)
        val matches = filter.applyTo(testGenera)
        val expected = listOf(triceratops)
//        assert(matches.size == expected.size)
        assertEquals(expected, matches)
    }

}