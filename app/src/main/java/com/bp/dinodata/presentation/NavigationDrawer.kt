package com.bp.dinodata.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemColors
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.theme.DinoDataTheme

enum class NavDrawerItem {
    CreatureList,
    Taxonomy,
    About
}

@Composable
fun MyNavigationDrawer(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    screenRouteState: State<String?>,
    navigateTo: (INavigationDrawerScreen) -> Unit,
    content: @Composable () -> Unit
) {
    val currentNavRoute by remember { screenRouteState }

    val colors = NavigationDrawerItemDefaults.colors(
        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
        selectedTextColor = MaterialTheme.colorScheme.onPrimary,
        selectedIconColor = MaterialTheme.colorScheme.onPrimary,
        unselectedContainerColor = MaterialTheme.colorScheme.secondaryContainer,
        unselectedTextColor = MaterialTheme.colorScheme.onBackground,
        unselectedIconColor = MaterialTheme.colorScheme.onBackground
    )

    val numItems = 3
    val scrimColor = Color(0xAA555555)

    val interactionSources = remember {
        Array(numItems) { MutableInteractionSource() }
    }

    val currentSelectedItem by remember {
        derivedStateOf {
            when (currentNavRoute) {
                Screen.ListGenus.route -> NavDrawerItem.CreatureList
                Screen.Taxonomy.route  -> NavDrawerItem.Taxonomy
                Screen.About.route     -> NavDrawerItem.About
                else                   -> null
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = scrimColor,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier= Modifier.height(50.dp))
                Image(
                    painterResource(id = R.mipmap.ic_launcher_v1_foreground),
                    null
                )
                Text(
                    text = stringResource(R.string.title_navigation_drawer),
                    fontSize = 22.sp,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                Column (
                    Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NavigationDrawerItem(
                        label = {
                            Text(
                                text = stringResource(R.string.title_creature_list),
                                fontWeight = if (currentSelectedItem == NavDrawerItem.CreatureList) {
                                    FontWeight.Bold
                                }
                                else {
                                    FontWeight.Normal
                                }
                            )
                        },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        selected = currentSelectedItem == NavDrawerItem.CreatureList,
                        onClick = {
                            Log.d("NavDrawer", "Go to ${Screen.ListGenus}!")
                            navigateTo(Screen.ListGenus)
                        },
                        colors = colors,
                        interactionSource = interactionSources[0]
                    )
                    NavigationDrawerItem(
                        label = {
                            Text(text = stringResource(R.string.screen_title_taxonomy),
                                fontWeight = if (currentSelectedItem == NavDrawerItem.Taxonomy) {
                                    FontWeight.Bold
                                }
                                else {
                                    FontWeight.Normal
                                }
                            ) },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.icon_filled_taxon_tree),
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        selected = currentSelectedItem == NavDrawerItem.Taxonomy,
                        onClick = {
                            Log.d("NavDrawer", "Go to ${Screen.Taxonomy}!")
                            navigateTo(Screen.Taxonomy)
                        },
                        colors = colors,
                        interactionSource = interactionSources[1]
                    )
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.screen_title_about),
                            fontWeight = if (currentSelectedItem == NavDrawerItem.About) {
                                FontWeight.Bold
                            }
                            else {
                                FontWeight.Normal
                            }) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        selected = currentSelectedItem == NavDrawerItem.About,
                        onClick = {
                            Log.d("NavDrawer", "Go to ${Screen.About}!")
                            navigateTo(Screen.About)
                        },
                        colors = colors,
                        interactionSource = interactionSources[2]
                    )
                }
            }
        }
    ) {
        content.invoke()
    }
}



@Preview
@Composable
fun PreviewNavigationDrawer() {
    val state = remember { mutableStateOf(Screen.ListGenus.route) }

    DinoDataTheme (darkTheme = true) {
        MyNavigationDrawer(
            drawerState = DrawerState(DrawerValue.Open),
            screenRouteState = state,
            navigateTo = {},
            content = {
                Surface(Modifier.fillMaxSize()) {

                }
            }
        )
    }
}