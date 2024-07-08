package com.bp.dinodata

import com.bp.dinodata.data.GenusBuilderImpl
import org.junit.Test

import org.junit.Assert.*

/**
 * Verifies that the GenusBuilder extracts the correct information to build a correct Genus object. *
 */
class GenusBuilderUnitTest {
    @Test
    fun weight_parsedCorrectly() {
        val string = "11.2-20.1 tonnes"
        val gb = GenusBuilderImpl("test").setWeight(string)
        val genus = gb.build()
        assertEquals(string, genus.getWeight())
    }
}