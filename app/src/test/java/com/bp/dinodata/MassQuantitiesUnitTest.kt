package com.bp.dinodata

import com.bp.dinodata.data.quantities.Mass
import com.bp.dinodata.data.quantities.MassUnit
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
        val tons = tonnes.convert(MassUnit.TON_IMPERIAL)
        assertEquals(67.91f, tons.getValue(), 0.01f)
    }

    @Test
    fun convert_TonnesToTonsUS() {
        val tonnes = Mass.Tonnes(69f)
        val usTons = tonnes.convert(MassUnit.TON_US)
        assertEquals(76.06f, usTons.getValue(), 0.01f)
    }

    @Test
    fun convert_TonnesToKg() {
        val tonnes = Mass.Tonnes(69.123f)
        val kg = tonnes.convert(MassUnit.KILOGRAM)
        assertEquals(69123f, kg.getValue(), 0.1f)
    }


    @Test
    fun convert_USTonsToImperialTons() {
        val usTons = Mass.TonsUS(69f)
        val impTons = usTons.convert(MassUnit.TON_IMPERIAL)
        assertEquals(61.6071f, impTons.getValue(), 0.01f)
    }

    @Test
    fun convert_USTonsToKg() {
        val tonsUs = Mass.TonsUS(6.9f)
        val kg = tonsUs.convert(MassUnit.KILOGRAM)
        assertEquals(6259.57f, kg.getValue(), 0.1f)
    }

    @Test
    fun convert_USTonsToTonnes() {
        val tonsUs = Mass.TonsUS(6.9f)
        val tonnes = tonsUs.convert(MassUnit.TONNE)
        assertEquals(6.26f, tonnes.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToKg() {
        val tonsImp = Mass.TonsImperial(6.9f)
        val kg = tonsImp.convert(MassUnit.KILOGRAM)
        assertEquals(7010.72f, kg.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToUSTons() {
        val tonsImp = Mass.TonsImperial(6.9f)
        val tonsUs = tonsImp.convert(MassUnit.TON_US)
        assertEquals(7.728f, tonsUs.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToTonnes() {
        val tonsImp = Mass.TonsImperial(6.9f)
        val tonnes = tonsImp.convert(MassUnit.TONNE)
        assertEquals(7.011f, tonnes.getValue(), 0.1f)
    }

    @Test
    fun convert_KgToImperialTons() {
        val kg = Mass.Kilograms(690f)
        val impTons = kg.convert(MassUnit.TON_IMPERIAL)
        assertEquals(0.679103f, impTons.getValue(), 0.001f)
    }

    @Test
    fun convert_KgToUSTons() {
        val kg = Mass.Kilograms(690f)
        val usTons = kg.convert(MassUnit.TON_US)
        assertEquals(0.760595f, usTons.getValue(), 0.001f)
    }

    @Test
    fun convert_KgToTonnes() {
        val kg = Mass.Kilograms(69000f)
        val tonnes = kg.convert(MassUnit.TONNE)
        assertEquals(69f, tonnes.getValue(), 0.1f)
    }
}