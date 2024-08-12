package com.bp.dinodata.presentation.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bp.dinodata.R
import com.bp.dinodata.theme.DinoDataTheme

@Composable
fun AboutScreen(
    navigateBack: () -> Unit
) {
    Surface (
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            Modifier.padding(8.dp).fillMaxSize()
        ) {
            IconButton(
                onClick = navigateBack,
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    "navigate back"
                )
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            ) {
                Text(
                    "This is where i provide links to all the sources I've used.",
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(50.dp))
                Text("Images from:", fontSize = 14.sp, modifier = Modifier.alpha(0.7f))
                Text(
                    stringResource(R.string.source_phylopic),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(50.dp))
                Text("Genus Data from:", fontSize = 14.sp, modifier = Modifier.alpha(0.7f))
                Text(
                    stringResource(R.string.source_nhm),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    stringResource(R.string.source_wiki),
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewAboutScreen() {
    DinoDataTheme(darkTheme = true) {
        AboutScreen {}
    }
}