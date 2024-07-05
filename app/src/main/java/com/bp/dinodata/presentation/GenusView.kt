package com.bp.dinodata.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.GenusBuilderImpl
import com.bp.dinodata.presentation.icons.DietIconThin
import com.bp.dinodata.presentation.icons.TimePeriodIcon
import com.bp.dinodata.presentation.utils.ConvertCreatureTypeToSilhouette
import com.bp.dinodata.theme.SurfaceGrey
import com.google.protobuf.DescriptorProtos.FieldDescriptorProto.Label


@Composable
fun LabelAttributeRow(
    label: String,
    value: String?,
    units: String? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween,
    valueStyle: FontStyle = FontStyle.Normal,
    valueTextAlign: TextAlign = TextAlign.End
) {
    Row (
        horizontalArrangement = horizontalArrangement,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$label: ", modifier=Modifier.alpha(0.6f))
        Text(
            value ?: "Unknown",
            fontStyle = valueStyle,
            textAlign = valueTextAlign
        )
        units?.let { Text(units, modifier=Modifier.alpha(0.6f)) }
    }
}

@Composable
fun LabelContentRow(
    label: String,
    valueContent: @Composable () -> Unit,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.SpaceBetween
) {
    Row (
        horizontalArrangement=horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("$label: ", modifier=Modifier.alpha(0.6f))
        valueContent.invoke()
    }
}

@Composable
fun GenusDetail(
    genus: Genus,
    modifier: Modifier = Modifier
) {
    val silhouetteId = ConvertCreatureTypeToSilhouette(genus.type)

    val horizontalPadding = 12.dp

    Surface(
        color = SurfaceGrey,
        contentColor = Color.White,
        modifier = modifier
    ) {
        LazyColumn (
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = Color.DarkGray
                    ),
                    elevation = CardDefaults.elevatedCardElevation(
                        defaultElevation = 10.dp
                    ),
                    shape = RoundedCornerShape(
                        topStart = 0f,
                        topEnd = 0f,
                        bottomEnd = 50f,
                        bottomStart = 50f
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = horizontalPadding)
                            .padding(top = 40.dp)
                    ) {
                        Image(
                            painterResource(id = silhouetteId),
                            contentDescription = null,
                            modifier = Modifier
                                .alpha(0.4f)
                                .padding(top = 10.dp, bottom = 0.dp)
                                .offset(x = 20.dp, y = 0.dp)
                                .fillMaxWidth(),
                            alignment = Alignment.CenterStart,
                            contentScale = ContentScale.Fit,
//                        colorFilter = ColorFilter.tint(Color.Green, BlendMode.Overlay)
                        )
                        Spacer(modifier.height(20.dp))
                        Text(
                            genus.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            fontStyle = FontStyle.Italic
                        )
                        genus.namePronunciation?.let {
                            Text(it, modifier = Modifier.alpha(0.6f))
                        }
                        Spacer(modifier = Modifier.height(15.dp))
                    }
                }
            }

            item {
                Column(
                    modifier = Modifier.padding(horizontal = horizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    LabelAttributeRow(
                        label = "Meaning",
                        value = genus.getNameMeaning(),
                        valueStyle = FontStyle.Italic
                    )
                    LabelAttributeRow(label = "Creature Type", value = genus.type.toString())

                    HorizontalDivider(
                        Modifier
                            .padding(8.dp)
                            .alpha(0.4f)
                    )

                    LabelContentRow(label = "Diet", valueContent = { DietIconThin(genus.diet) })
                    LabelAttributeRow(label = "Length (metres)", value = genus.getLength())
                    LabelAttributeRow(label = "Weight (tonnes)", value = genus.getWeight())

                    HorizontalDivider(
                        Modifier
                            .padding(8.dp)
                            .alpha(0.4f)
                    )

                    LabelContentRow(
                        label = "Time Period",
                        valueContent = { TimePeriodIcon(timePeriod = genus.timePeriod) },
                        horizontalArrangement = Arrangement.SpaceBetween
                    )
                    LabelAttributeRow(label = "Years Lived", value = genus.yearsLived)

                    Text("Taxonomy:", modifier = Modifier.alpha(0.6f))
                    ShowTaxonomicTree(genus = genus, modifier = Modifier.fillMaxWidth())
                }
            }
        }
    }
}

@Composable
fun ShowTaxonomicTree(
    genus: Genus,
    modifier: Modifier,
    internalCardPadding: PaddingValues = PaddingValues(16.dp)
) {

    val taxonomy = genus.getTaxonomicList()
    var tree = taxonomy[0]
    var indent = "└"
    for (taxon in taxonomy.drop(1)) {
        tree += "\n$indent $taxon"
        indent = "   $indent"
    }

    Card (
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray
        )
    ) {
        LazyRow (
            modifier = Modifier.padding(internalCardPadding)
                                .fillMaxWidth()
        ) {
            item {
                Column {
                    Text(tree)
                    Text(
                        "$indent ${genus.name}",
                        fontWeight = FontWeight.Bold,
                        color = Color.Yellow
                    )
                }
            }
        }
    }
}

@Preview(widthDp = 300, heightDp = 800)
@Composable
fun PreviewGenusDetail() {
    val acro = GenusBuilderImpl("Acrocanthosaurus")
        .setDiet("Carnivorous")
        .splitTimePeriodAndYears("Early Cretaceous, 113-110 mya")
        .setNamePronunciation("'ACK-row-CAN-thoh-SORE-us'")
        .setNameMeaning("high-spined lizard")
        .setLength("11-11.5")
        .setWeight("4.4-6.6")
        .setCreatureType("large theropod")
        .setTaxonomy("Dinosauria Saurischia Theropoda Carcharodontosauridae")
        .build()

    GenusDetail(acro)
}
