package com.bp.dinodata.presentation.detail_genus

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.quantities.IDescribesLength
import com.bp.dinodata.data.quantities.IDescribesMass
import com.bp.dinodata.data.quantities.IQuantity
import com.bp.dinodata.data.quantities.Length
import com.bp.dinodata.data.quantities.LengthRange
import com.bp.dinodata.data.quantities.LengthUnits
import com.bp.dinodata.data.quantities.Mass
import com.bp.dinodata.data.quantities.MassRange
import com.bp.dinodata.data.quantities.MassUnits
import com.bp.dinodata.theme.DinoDataTheme

@Composable
fun GenericMeasurementComposable(
    weight: IQuantity,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            icon?.invoke()
            Text(
                weight.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.75f),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun LengthComposable(
    length: IDescribesLength,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        ) {
            Icon(
                painterResource(id = R.drawable.icon_filled_ruler),
                null
            )
            Text(
                length.toString(),
                textAlign = TextAlign.Center,
                modifier = Modifier.alpha(0.75f),
                fontWeight = FontWeight.SemiBold,
            )
        }
    }
}

@Composable
fun QuantityComposable(
    quantity: IQuantity,
    modifier: Modifier = Modifier
) {
    when (quantity) {
        is IDescribesLength ->
            GenericMeasurementComposable(
                weight = quantity,
                modifier=modifier,
                icon = {
                    Icon(
                        painterResource(id = R.drawable.icon_filled_ruler), null
                    )
                }
            )
        is IDescribesMass ->
            GenericMeasurementComposable(
                weight = quantity,
                modifier = modifier,
                icon = {
                    Icon(
                        painterResource(id = R.drawable.icon_filled_weight),
                        null
                    )
                }
            )
        else -> Text("No data")
    }
}


@Preview(widthDp = 300)
@Composable
fun Preview_WeightComposable() {
    val mass = Mass.make(2.3f, MassUnits.Tonnes)
    val massRange = MassRange(860f, 1011f, MassUnits.Kilograms)

    val length = Length.make(11.2f, LengthUnits.Metres)
    val lengthRange = LengthRange(3.5f, 4.5f, LengthUnits.Metres)

    val measures = listOf(
        mass,
        massRange,
        length,
        lengthRange
    )

    DinoDataTheme (darkTheme = true) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(measures) {
                QuantityComposable(
                    it,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}