package com.bp.dinodata.presentation.detail_genus

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.bp.dinodata.R
import com.bp.dinodata.presentation.utils.ThemeConverter
import com.bp.dinodata.theme.DinoDataTheme

@Composable
fun ColorPickerDialog(
    selectedColor: String? = null,
    colorNames: List<String> = ThemeConverter.listOfColors,
    onColorPicked: (String?) -> Unit,
    onClose: () -> Unit
) {

    Dialog(
        onDismissRequest = onClose,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Column(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(16.dp)
                )
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Text("Color-Selection", fontWeight = FontWeight.Bold)
            }

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onSurface,
                thickness = 1.dp,
                modifier = Modifier
                    .fillMaxWidth(0.5f)
                    .alpha(0.5f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Adaptive(65.dp),
                contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.background(
                    MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(12.dp)
                )
            ) {
                items(colorNames) { colorName ->
                    val isSelected = (selectedColor == colorName)
                    ColorPickerSingleton(
                        onColorPicked,
                        colorName,
                        isSelected
                    )
                }

                item {
                    val isSelected = (selectedColor == null)
                    ColorPickerSingleton(
                        onColorPicked,
                        null,
                        isSelected
                    )
                }
            }

            Row(
                horizontalArrangement = Arrangement.End
            ) {
                Button(onClick = onClose) {
                    Text("Done", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}

@Composable
fun ColorPickerSingleton(
    onSelect: (String?) -> Unit,
    colorName: String?,
    isSelected: Boolean
) {
    val theme = ThemeConverter.getTheme(colorName) ?: MaterialTheme.colorScheme
    val color = ThemeConverter.getColor(colorName) ?: Color.Transparent
    val displayName = colorName ?: stringResource(id = R.string.text_color_none)

    val borderThickness =
        if (isSelected) 2.dp else 0.dp

    val gradientBrush = Brush.linearGradient(
        0.0f to Color(color.red*3, color.green*3, color.blue*3),
        0.5f to color,
        0.8f to Color(color.red / 2, color.green / 2, color.blue / 2)
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = { onSelect(colorName) },
            modifier = Modifier
                .background(gradientBrush, CircleShape)
                .border(
                    borderThickness,
                    MaterialTheme.colorScheme.onSurface,
                    CircleShape
                )
        ) {
            AnimatedVisibility (isSelected) {
                Icon(
                    Icons.Filled.Check,
                    stringResource(R.string.desc_select_this_color),
                    modifier = Modifier
                        .fillMaxSize(0.8f)
                        .padding(4.dp),
                    tint = theme.onSurface
                )
            }
        }
        Text(displayName, fontSize = 12.sp)
    }
}



@Preview
@Composable
fun PreviewColorPickerDialog() {
    DinoDataTheme (darkTheme = true) {
        ColorPickerDialog(
            onColorPicked = {},
            onClose = {},
            selectedColor = "PINK"
        )
    }
}