package com.bp.dinodata.presentation.icons


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
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
import com.bp.dinodata.theme.Piscivore400
import com.bp.dinodata.theme.Piscivore700

fun DietImageFromObject(diet: Diet?): Int {
    return when(diet) {
        Diet.Carnivore -> R.drawable.carn
        Diet.Herbivore -> R.drawable.herb
        Diet.Piscivore -> R.drawable.pisc
        else -> R.drawable.unkn
    }
}

@Composable
fun DietIconThin(
    diet: Diet?,
    showText: Boolean = true
) {

    val img = DietImageFromObject(diet)

    val text = diet.toString()

    val iconBrush = when(diet) {
        Diet.Carnivore -> Brush.linearGradient(listOf(Carnivore400, Carnivore700))
        Diet.Herbivore -> Brush.linearGradient(listOf(Herbivore400, Herbivore700))
        Diet.Piscivore -> Brush.linearGradient(listOf(Piscivore400, Piscivore700))
        else -> Brush.linearGradient(listOf(Color.Gray, Color.DarkGray))
    }
    val iconShape = remember { RoundedCornerShape(10.dp) }

    Surface(
        color = Color.White,
        shape = iconShape,
        contentColor = Color.Black,
        modifier = Modifier.height(36.dp).width(IntrinsicSize.Min)
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
                modifier = Modifier.padding(2.dp).height(IntrinsicSize.Min)
            )
            if (showText) {
                Text(
                    text,
                    fontWeight = FontWeight.Bold,
                    color=Color.White,
                    fontSize=14.sp,
                    modifier = Modifier.padding(end=8.dp)
                )
            }
        }
    }
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