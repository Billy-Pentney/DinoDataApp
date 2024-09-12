package com.bp.dinodata

import com.bp.dinodata.data.quantities.Mass
import com.bp.dinodata.data.quantities.MassUnits
import org.junit.Test

import org.junit.Assert.*

/**
 * Verifies that Weight/Length units are correctly converted.
 * All tests are to within 1 decimal place (or more). *
 */
class MassQuantitiesUnitTest {
    @Test
    fun convert_TonnesToTonsImperial() {
        val tonnes = Mass.Tonnes(69f)
        val tons = tonnes.convert(MassUnits.TonsImperial)
        assertEquals(67.91f, tons.getValue(), 0.01f)
    }

    @Test
    fun convert_TonnesToTonsUS() {
        val tonnes = Mass.Tonnes(69f)
        val usTons = tonnes.convert(MassUnits.TonsUS)
        assertEquals(76.06f, usTons.getValue(), 0.01f)
    }

    @Test
    fun convert_TonnesToKg() {
        val tonnes = Mass.Tonnes(69.123f)
        val kg = tonnes.convert(MassUnits.Kilograms)
        assertEquals(69123f, kg.getValue(), 0.1f)
    }


    @Test
    fun convert_USTonsToImperialTons() {
        val usTons = Mass.TonsUS(69f)
        val impTons = usTons.convert(MassUnits.TonsImperial)
        assertEquals(61.6071f, impTons.getValue(), 0.01f)
    }

    @Test
    fun convert_USTonsToKg() {
        val tonsUs = Mass.TonsUS(6.9f)
        val kg = tonsUs.convert(MassUnits.Kilograms)
        assertEquals(6259.57f, kg.getValue(), 0.1f)
    }

    @Test
    fun convert_USTonsToTonnes() {
        val tonsUs = Mass.TonsUS(6.9f)
        val tonnes = tonsUs.convert(MassUnits.Tonnes)
        assertEquals(6.26f, tonnes.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToKg() {
        val tonsImp = Mass.TonsImperial(6.9f)
        val kg = tonsImp.convert(MassUnits.Kilograms)
        assertEquals(7010.72f, kg.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToUSTons() {
        val tonsImp = Mass.TonsImperial(6.9f)
        val tonsUs = tonsImp.convert(MassUnits.TonsUS)
        assertEquals(7.728f, tonsUs.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToTonnes() {
        val tonsImp = Mass.TonsImperial(6.9f)
        val tonnes = tonsImp.convert(MassUnits.Tonnes)
        assertEquals(7.011f, tonnes.getValue(), 0.1f)
    }

    @Test
    fun convert_KgToImperialTons() {
        val kg = Mass.Kg(690f)
        val impTons = kg.convert(MassUnits.TonsImperial)
        assertEquals(0.679103f, impTons.getValue(), 0.001f)
    }

    @Test
    fun convert_KgToUSTons() {
        val kg = Mass.Kg(690f)
        val usTons = kg.convert(MassUnits.TonsUS)
        assertEquals(0.760595f, usTons.getValue(), 0.001f)
    }

    @Test
    fun convert_KgToTonnes() {
        val kg = Mass.Kg(69000f)
        val tonnes = kg.convert(MassUnits.Tonnes)
        assertEquals(69f, tonnes.getValue(), 0.1f)
    }
}