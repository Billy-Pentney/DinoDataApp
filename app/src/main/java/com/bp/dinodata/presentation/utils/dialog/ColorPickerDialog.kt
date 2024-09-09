package com.bp.dinodata.presentation.utils.dialog

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.bp.dinodata.R
import com.bp.dinodata.presentation.utils.ThemeConverter
import com.bp.dinodata.theme.DinoDataTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorPickerDialog(
    isVisibleState: State<Boolean>,
    initiallySelectedColor: String? = null,
    colorNames: List<String> = ThemeConverter.getNonNullColours(),
    onColorPicked: (String?) -> Unit,
    onClose: () -> Unit
) {
    var currentlySelectedColor by remember { mutableStateOf(initiallySelectedColor) }

    val dialogIsOpen by remember { isVisibleState }

    val gridState = rememberLazyGridState(initialFirstVisibleItemIndex = 0)
//    val currentGridScrollIndex = remember { derivedStateOf { gridState.firstVisibleItemIndex } }
//    val numGridRows = colorNames.size

    if (dialogIsOpen) {
        BasicAlertDialog(
            onDismissRequest = onClose,
            properties = DialogProperties(
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            )
        ) {
            val shape = RoundedCornerShape(16.dp)
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = shape,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxHeight(0.75f)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Filled.ColorLens, null, modifier=Modifier.alpha(0.8f))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            stringResource(R.string.dialog_title_color_selection),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface,
                        thickness = 1.dp,
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
                            .alpha(0.5f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        stringResource(R.string.desc_choose_a_color_for_genus),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Start,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier=Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        contentPadding = PaddingValues(vertical = 12.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            ).weight(1f).heightIn(max=400.dp),
                        state = gridState
                    ) {
                        items(colorNames) { colorName ->
                            ColorPickerSingleton(
                                onSelect = {
                                    Log.d("ColorPicker", "Picked color $it")
                                    // If the color changed
                                    if (currentlySelectedColor != it) {
                                        currentlySelectedColor = it
                                        onColorPicked(it)
                                    }
                                },
                                colorName = colorName,
                                isSelected = currentlySelectedColor == colorName
                            )
                        }
                        item {
                            val nullColor = ThemeConverter.DefaultColor.name
                            ColorPickerSingleton(
                                onSelect = {
                                    Log.d("ColorPicker", "Picked color $it")
                                    // If the color changed
                                    if (currentlySelectedColor != it) {
                                        currentlySelectedColor = it
                                        onColorPicked(it)
                                    }
                                },
                                colorName = nullColor,
                                isSelected = currentlySelectedColor == nullColor
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.End) {
                        Button(onClick = onClose) {
                            Text(
                                stringResource(R.string.action_done),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPickerSingleton(
    onSelect: (String) -> Unit,
    colorName: String,
    isSelected: Boolean
) {
    val theme = ThemeConverter.getTheme(colorName) ?: MaterialTheme.colorScheme
    val color = ThemeConverter.getPrimaryColor(colorName) ?: Color.Transparent

    val borderThickness = if (isSelected) 2.dp else 0.dp

    val gradientBrush = Brush.linearGradient(
        0.0f to Color(color.red*3, color.green*3, color.blue*3),
        0.5f to color,
        0.8f to Color(color.red / 2, color.green / 2, color.blue / 2)
    )
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                onSelect(colorName)
            },
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
        Text(colorName, fontSize = 12.sp)
    }
}



@Preview(heightDp = 600, widthDp = 400)
@Composable
fun PreviewColorPickerDialog() {
    DinoDataTheme (darkTheme = true) {
        ColorPickerDialog(
            onColorPicked = {},
            onClose = {},
            initiallySelectedColor = "PINK",
            isVisibleState = mutableStateOf(true)
        )
    }
}