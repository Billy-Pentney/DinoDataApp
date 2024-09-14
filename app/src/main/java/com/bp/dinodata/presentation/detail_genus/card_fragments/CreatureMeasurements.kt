package com.bp.dinodata.presentation.detail_genus.card_fragments

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.IHasMeasurements
import com.bp.dinodata.presentation.detail_genus.QuantityComposable
import com.bp.dinodata.presentation.detail_genus.SectionLabelRow

@Composable
fun CreatureMeasurementsSection(
    genus: IHasMeasurements,
    iconModifier: Modifier = Modifier
) {
    val measurements = genus.getAllMeasurements()
    if (measurements.isEmpty()) {
        Log.d("CreatureMeasurements", "No measurements to display!")
        return
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        SectionLabelRow(labelText = stringResource(R.string.label_estimated_measurements))
        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            measurements.forEach { 
                QuantityComposable(quantity = it, modifier = Modifier.weight(1f))
            } 
        }
    }
}


