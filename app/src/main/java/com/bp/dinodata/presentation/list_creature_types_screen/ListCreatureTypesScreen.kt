package com.bp.dinodata.presentation.list_creature_types_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bp.dinodata.R
import com.bp.dinodata.data.CreatureType
import com.bp.dinodata.data.CreatureTypeInfo
import com.bp.dinodata.data.ICreatureTypeInfo
import com.bp.dinodata.presentation.detail_creature_type.DetailCreatureTypeDialog
import com.bp.dinodata.presentation.detail_creature_type.convertCreatureTypeToDescription
import com.bp.dinodata.presentation.detail_genus.card_fragments.CreatureTypeCard
import com.bp.dinodata.theme.DinoDataTheme


@Composable
fun ListCreatureTypesScreen(
    viewModel: IListCreatureTypesScreenViewModel,
    openNavDrawer: () -> Unit
) {
    val creatureTypeMap by viewModel.getCreatureTypeInfoMapState().collectAsState()

    ListCreatureTypesScreenContent(
        creatureTypeMap,
        openNavDrawer
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListCreatureTypesScreenContent(
    creatureTypes: Map<CreatureType, ICreatureTypeInfo>,
    openNavDrawer: () -> Unit,
    initiallyVisibleCreatureType: CreatureType? = null
) {
    var selectedCreatureType: CreatureType? by remember { mutableStateOf(initiallyVisibleCreatureType) }
    var dialogVisible by remember { mutableStateOf(selectedCreatureType != null) }

    // If we've picked a type, show the dialog
    if (selectedCreatureType in creatureTypes.keys && dialogVisible) {
        DetailCreatureTypeDialog(
            creatureTypeInfo = creatureTypes[selectedCreatureType]!!,
            onClose = { dialogVisible = false }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.title_creature_types)) },
                navigationIcon = {
                    IconButton(onClick = openNavDrawer) {
                        Icon(Icons.AutoMirrored.Filled.List,
                            stringResource(R.string.action_open_nav_drawer))
                    }
                }
            )
        }
    ) {
        pad ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(pad),
            contentPadding = PaddingValues(all = 16.dp)
        ) {
            items(creatureTypes.keys.toList()) {
                CreatureTypeCard(
                    type = it,
                    padding = PaddingValues(all=16.dp),
                    onClick = {
                        selectedCreatureType = it
                        dialogVisible = true
                    }
                )
            }
        }
    }
}


@Preview
@Composable
fun Preview_ListCreatureTypesScreen() {

    val typeInfoMap = CreatureType.entries.associateWith {
        val attr = convertCreatureTypeToDescription(creatureType = it)
        CreatureTypeInfo(
            it,
            attr.text,
            attr.attribution
        )
    }

    DinoDataTheme (darkTheme = true) {
        ListCreatureTypesScreenContent(
            creatureTypes = typeInfoMap,
            openNavDrawer = {},
            initiallyVisibleCreatureType = CreatureType.Ceratopsian
        )
    }
}