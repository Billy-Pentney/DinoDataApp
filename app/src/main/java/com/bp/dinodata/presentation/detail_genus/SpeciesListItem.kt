package com.bp.dinodata.presentation.detail_genus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.genus.ISpecies
import com.bp.dinodata.data.genus.species.SpeciesBuilder
import com.bp.dinodata.theme.DinoDataTheme
import com.bp.dinodata.theme.typeSpecies500

@Composable
fun SpeciesListItem(
    species: ISpecies,
    genusName: String? = null
) {
    val speciesName = species.getName().lowercase()
    val speciesNomial = StringBuilder()
    if (genusName != null) {
        speciesNomial.append(genusName[0].uppercase())
        speciesNomial.append(genusName.drop(1).lowercase())
        speciesNomial.append(" ")
    }
    speciesNomial.append(speciesName)

    val discoverer = species.getDiscoveryText()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    speciesNomial.toString(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.weight(1f)
                )
                if (species.isTypeSpecies()) {
                    Text(
                        stringResource(R.string.badge_species_type),
                        fontSize = 11.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines=1,
                        modifier = Modifier
                            .shadow(1.dp)
                            .background(
                                color = typeSpecies500,
                                shape = RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 0.dp)
                    )
                }
            }
            discoverer?.let {
                Text(
                    it,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.alpha(0.75f),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview(widthDp=250)
@Composable
fun PreviewSpeciesListItem() {
    val species = SpeciesBuilder("atokensis", genusName = "Acrocanthosaurus")
        .setTypeSpecies(true)
        .setDiscoverer("Marsh")
        .setYearOfDiscovery("1891")
        .build()

    Column {
        DinoDataTheme(darkTheme = false) {
            SpeciesListItem(
                species = species,
                genusName = "Acrocanthosaurus"
            )
        }
        DinoDataTheme(darkTheme = true) {
            SpeciesListItem(
                species = species,
                genusName = "Acrocanthosaurus"
            )
        }
    }
}
