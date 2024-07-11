package com.bp.dinodata.presentation.icons


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.data.Diet
import com.bp.dinodata.theme.Carnivore400
import com.bp.dinodata.theme.Carnivore700
import com.bp.dinodata.theme.Herbivore400
import com.bp.dinodata.theme.Herbivore700
import com.bp.dinodata.theme.MyGrey300
import com.bp.dinodata.theme.MyGrey600
import com.bp.dinodata.theme.MyGrey800
import com.bp.dinodata.theme.Piscivore400
import com.bp.dinodata.theme.Piscivore700

fun convertDietToImageResId(diet: Diet?): Int {
    return when(diet) {
        Diet.Carnivore -> R.drawable.diet_carn
        Diet.Herbivore -> R.drawable.diet_herb
        Diet.Piscivore -> R.drawable.diet_pisc
        else -> R.drawable.unkn
    }
}

@Composable
fun DietIconThin(
    diet: Diet?,
    showText: Boolean = true
) {
    val img = convertDietToImageResId(diet)

    val text = diet.toString()

    val iconBrush = convertDietToLinearBrush(diet)
    val iconShape = remember { RoundedCornerShape(10.dp) }

    Surface(
        color = MaterialTheme.colorScheme.onSurface,
        shape = iconShape,
        contentColor = Color.Black,
        modifier = Modifier.height(34.dp).width(IntrinsicSize.Min),
        shadowElevation = 4.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.padding(1.dp).background(
                brush = iconBrush,
                shape = RoundedCornerShape(9.dp)
            ).fillMaxSize()
        ) {
            Image(
                painter = painterResource(id = img),
                contentDescription = "diet_icon",
                modifier = Modifier.padding(4.dp)
                    .height(IntrinsicSize.Min)
            )
            if (showText) {
                Text(
                    text,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(end=8.dp),
                    style = TextStyle(
                        shadow = Shadow(
                            color = MyGrey800,
                            offset = Offset(1f, 1f),
                            blurRadius = 2f
                        )
                    )
                )
            }
        }
    }
}

private fun convertDietToLinearBrush(diet: Diet?): Brush {
    val iconBrush = Brush.linearGradient(
        when (diet) {
            Diet.Carnivore -> listOf(Carnivore400, Carnivore700)
            Diet.Herbivore -> listOf(Herbivore400, Herbivore700)
            Diet.Piscivore -> listOf(Piscivore400, Piscivore700)
            else -> listOf(MyGrey300, MyGrey600)
        }
    )
    return iconBrush
}

@Composable
fun DietIconSquare(diet: Diet?) {
    DietIconThin(
        diet = diet,
        showText = false
    )
}

@Composable
@Preview
fun PreviewDietIcon() {
    LazyColumn (verticalArrangement = Arrangement.spacedBy(4.dp)) {
        items(listOf(Diet.Carnivore, Diet.Piscivore, Diet.Herbivore, Diet.Unknown)) {
            Row (horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DietIconThin(diet = it)
                DietIconThin(diet = it, showText = false)
            }
        }
    }
}