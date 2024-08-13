package com.bp.dinodata.presentation

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R

@Composable
fun MyNavigationDrawer(
    drawerState: DrawerState = rememberDrawerState(DrawerValue.Closed),
    screenRouteState: State<String?>,
    navigateTo: (INavigationDrawerScreen) -> Unit,
    content: @Composable () -> Unit
) {
    val currentNavRoute by remember { screenRouteState }

    ModalNavigationDrawer(
        drawerState = drawerState,
        scrimColor = Color(0xFF555555),
        drawerContent = {
            ModalDrawerSheet {
                Spacer(modifier= Modifier.height(50.dp))
                Image(
                    painterResource(id = R.mipmap.ic_launcher_v1_foreground),
                    null
                )
                Text(
                    "Navigation",
                    fontSize = 22.sp,
                    modifier = Modifier.padding(16.dp)
                )
                HorizontalDivider()
                Column (
                    Modifier.padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.title_creature_list)) },
                        icon = {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.List,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        selected = currentNavRoute == Screen.ListGenus.route,
                        onClick = {
                            Log.d("NavDrawer", "Go to ${Screen.ListGenus}!")
                            navigateTo(Screen.ListGenus)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.screen_title_taxonomy)) },
                        icon = {
                            Icon(
                                painterResource(id = R.drawable.icon_filled_taxon_tree),
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        selected = currentNavRoute == Screen.Taxonomy.route,
                        onClick = {
                            Log.d("NavDrawer", "Go to  ${Screen.Taxonomy}!")
                            navigateTo(Screen.Taxonomy)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text(text = stringResource(R.string.screen_title_about)) },
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null
                            )
                        },
                        shape = RoundedCornerShape(8.dp),
                        selected = currentNavRoute == Screen.About.route,
                        onClick = {
                            Log.d("NavDrawer", "Go to  ${Screen.About}!")
                            navigateTo(Screen.About)
                        }
                    )
                }
            }
        }
    ) {
        content.invoke()
    }
}