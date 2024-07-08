package com.bp.dinodata

import com.bp.dinodata.data.quantities.Units
import com.bp.dinodata.data.quantities.Weight
import org.junit.Test

import org.junit.Assert.*

/**
 * Verifies that Weight/Length units are correctly converted.
 * All tests are to within 1 decimal place (or more). *
 */
class WeightQuantitiesUnitTest {
    @Test
    fun convert_TonnesToTonsImperial() {
        val tonnes = Weight.Tonnes(69f)
        val tons = tonnes.convert(Units.Weight.TonsImperial)
        assertEquals(67.91f, tons.getValue(), 0.01f)
    }

    @Test
    fun convert_TonnesToTonsUS() {
        val tonnes = Weight.Tonnes(69f)
        val usTons = tonnes.convert(Units.Weight.TonsUS)
        assertEquals(76.06f, usTons.getValue(), 0.01f)
    }

    @Test
    fun convert_TonnesToKg() {
        val tonnes = Weight.Tonnes(69.123f)
        val kg = tonnes.convert(Units.Weight.Kg)
        assertEquals(69123f, kg.getValue(), 0.1f)
    }


    @Test
    fun convert_USTonsToImperialTons() {
        val usTons = Weight.TonsUS(69f)
        val impTons = usTons.convert(Units.Weight.TonsImperial)
        assertEquals(61.6071f, impTons.getValue(), 0.01f)
    }

    @Test
    fun convert_USTonsToKg() {
        val tonsUs = Weight.TonsUS(6.9f)
        val kg = tonsUs.convert(Units.Weight.Kg)
        assertEquals(6259.57f, kg.getValue(), 0.1f)
    }

    @Test
    fun convert_USTonsToTonnes() {
        val tonsUs = Weight.TonsUS(6.9f)
        val tonnes = tonsUs.convert(Units.Weight.Tonnes)
        assertEquals(6.26f, tonnes.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToKg() {
        val tonsImp = Weight.TonsImperial(6.9f)
        val kg = tonsImp.convert(Units.Weight.Kg)
        assertEquals(7010.72f, kg.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToUSTons() {
        val tonsImp = Weight.TonsImperial(6.9f)
        val tonsUs = tonsImp.convert(Units.Weight.TonsUS)
        assertEquals(7.728f, tonsUs.getValue(), 0.1f)
    }

    @Test
    fun convert_ImperialTonsToTonnes() {
        val tonsImp = Weight.TonsImperial(6.9f)
        val tonnes = tonsImp.convert(Units.Weight.Tonnes)
        assertEquals(7.011f, tonnes.getValue(), 0.1f)
    }

    @Test
    fun convert_KgToImperialTons() {
        val kg = Weight.Kg(690f)
        val impTons = kg.convert(Units.Weight.TonsImperial)
        assertEquals(0.679103f, impTons.getValue(), 0.001f)
    }

    @Test
    fun convert_KgToUSTons() {
        val kg = Weight.Kg(690f)
        val usTons = kg.convert(Units.Weight.TonsUS)
        assertEquals(0.760595f, usTons.getValue(), 0.001f)
    }

    @Test
    fun convert_KgToTonnes() {
        val kg = Weight.Kg(69000f)
        val tonnes = kg.convert(Units.Weight.Tonnes)
        assertEquals(69f, tonnes.getValue(), 0.1f)
    }
}