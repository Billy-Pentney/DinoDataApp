package com.bp.dinodata.presentation.list_genus

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.bp.dinodata.data.Genus
import com.bp.dinodata.data.IDisplayInList
import com.bp.dinodata.presentation.detail_genus.LoadAsyncImageOrReserveDrawable
import com.bp.dinodata.presentation.icons.DietIconSquare
import com.bp.dinodata.presentation.utils.convertCreatureTypeToSilhouette

@Composable
fun GenusListItem(
    genus: IDisplayInList,
    onClick: () -> Unit = {},
    showDietText: Boolean = true
) {
    val silhouetteId = remember {
        convertCreatureTypeToSilhouette(genus.getCreatureType())
    }

    Surface(
        color = Color.DarkGray,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        onClick = onClick
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier.fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
                    .zIndex(1.0f)
            ) {
                DietIconSquare(diet = genus.getDiet())
                Text(
                    genus.getName(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    fontStyle = FontStyle.Italic,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
            Image(
                painter = painterResource(id = silhouetteId),
                contentDescription = "creature type",
                modifier = Modifier
                    .alpha(0.5f)
                    .zIndex(0f)
                    .padding(top = 5.dp)
                    .fillMaxHeight()
                    .fillMaxWidth(0.33f)
                    .absoluteOffset(x = 25.dp, y = 0.dp)
                    .clipToBounds(),
                alignment = Alignment.BottomStart,
                contentScale = ContentScale.FillHeight,
            )
        }
    }
}